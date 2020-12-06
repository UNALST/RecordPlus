package me.lst.recordplus;

import me.lst.recordplus.objects.ActionData;
import me.lst.recordplus.objects.AsyncTaskTimer;
import me.lst.recordplus.objects.MutableBoolean;
import me.lst.recordplus.util.NumberUtils;
import me.lst.recordplus.util.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Recorder implements Listener {
    protected final Map<String, Consumer<ActionData>> actionHandlers = new HashMap<>();

    protected Map<Entity, Recording> recordedEntities = new ConcurrentHashMap<>();
    protected Map<Entity, List<EntityTracker>> trackedEntities = new ConcurrentHashMap<>();
    protected Set<Player> viewers = ConcurrentHashMap.newKeySet();

    protected MutableInt tick = new MutableInt(0);

    protected Plugin plugin;
    protected Configuration configuration;
    protected RecordingStorage storage;

    public Recorder(Plugin plugin, Configuration configuration, RecordingStorage storage) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.storage = storage;

        this.initHandlers();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            this.tick(new HashMap<>(this.trackedEntities));
            this.tick.increment();
        }, 0L, 1L);
    }

    public void tick(Map<Entity, List<EntityTracker>> trackedEntities) {
        if (!this.configuration.getRecordNearby()) {
            return;
        }
        for (Map.Entry<Entity, List<EntityTracker>> entry : trackedEntities.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            Entity entity = entry.getKey();
            EntityTracker tracker = entry.getValue().get(0);

            int fireTicks = entity.getFireTicks();

            if (fireTicks <= 0 && tracker.lastFireTicks > 0) {
                this.recordAction(tracker, "C 0");
            } else if (fireTicks > 0 && tracker.lastFireTicks <= 0) {
                this.recordAction(tracker, "C 1");
            }
            tracker.lastFireTicks = fireTicks;

            EntityType type = entity.getType();

            if (type != EntityType.PLAYER && (!(entity instanceof Projectile) || type.name().endsWith("ARROW"))) {
                String move = this.toMoveString(entity, entity.getLocation());

                if (!move.equals(tracker.lastMoved)) {
                    this.recordAction(entity, move);
                }
                tracker.lastMoved = move;
            }
            if (!tracker.recordNearby) {
                continue;
            }
            Set<Entity> newLastNearby = new HashSet<>();

            for (Entity nearby : entity.getNearbyEntities(this.configuration.getRecordNearbyHorizontalDistance(), this.configuration.getRecordNearbyVerticalDistance(), this.configuration.getRecordNearbyHorizontalDistance())) {
                if (!(nearby instanceof LivingEntity) && !(nearby instanceof Vehicle)) {
                    continue;
                }
                if (!tracker.lastNearby.remove(nearby)) {
                    this.startRecording(nearby, tracker.recording);

                    if (nearby instanceof Ageable && !((Ageable) nearby).isAdult()) {
                        this.recordAction(nearby, "B");
                    }
                }
                newLastNearby.add(nearby);
            }
            for (Entity nearby : tracker.lastNearby) {
                this.stopTracking(nearby, tracker.recording.id);
            }
            tracker.lastNearby = newLastNearby;
        }
    }

    protected void setHandler(String action, Consumer<ActionData> handler) {
        this.actionHandlers.put(action, handler);
    }

    protected abstract void initHandlers();

    protected abstract void applySkin(Player player, EntityTracker tracker);

    public abstract void initDataWatcher(EntityTracker tracker);

    public void playRecording(Player viewer, Recording recording) {
        String[] locationData = recording.targetLocation.split(" ");

        World world = Bukkit.getWorld(locationData[0]);

        if (world == null) {
            viewer.sendMessage(StringUtils.format("&cWorld '%s' could not be found for recording!", locationData[0]));
            return;
        }
        viewer.sendMessage(StringUtils.format("&6Playing recording &e%s&6...", recording.id));
        this.viewers.add(viewer);

        double x = Double.parseDouble(locationData[1]);
        double y = Double.parseDouble(locationData[2]);
        double z = Double.parseDouble(locationData[3]);
        float yaw = Float.parseFloat(locationData[4]);
        float pitch = Float.parseFloat(locationData[5]);

        Location location = new Location(world, x, y, z, yaw, pitch);

        if (world != viewer.getWorld() || location.distance(viewer.getLocation()) > this.configuration.getPlaybackDistanceRequired()) {
            viewer.teleport(location);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.startPlaying(viewer, recording), 10L);
        } else {
            this.startPlaying(viewer, recording);
        }
    }

    private void startPlaying(Player viewer, Recording recording) {
        for (EntityTracker tracker : recording.entities) {
            this.initDataWatcher(tracker);
        }
        int startingTick = -Configuration.SPAWN_DELAY_TICKS;

        MutableInt tick = new MutableInt(startingTick);

        AsyncTaskTimer task = new AsyncTaskTimer(this.plugin);

        task.setTask(() -> {
            if (tick.intValue() > recording.tickDuration) {
                Consumer<ActionData> remove = this.actionHandlers.get("R");

                for (EntityTracker tracker : recording.entities) {
                    remove.accept(new ActionData(tracker, null, null, viewer, new MutableBoolean(false)));
                }
                viewer.sendMessage(StringUtils.format("&6Finished playing recording &e%s&6!", recording.id));
                this.viewers.remove(viewer);

                task.cancel();
                return;
            }
            for (EntityTracker tracker : recording.entities) {
                List<String> actions = tracker.ticks.get(tick.intValue());

                if (actions == null) {
                    continue;
                }
                MutableBoolean sendMetadata = new MutableBoolean(false);

                for (String action : actions) {
                    String[] data = action.split(" ");

                    Consumer<ActionData> handler = this.actionHandlers.get(data[0]);

                    if (handler != null) { // Action possibly not yet implemented for NMS version
                        handler.accept(new ActionData(tracker, action, data, viewer, sendMetadata));
                    } else if (this.configuration.isVerbose()) {
                        viewer.sendMessage(StringUtils.format("Could not find action '%s', skipping...", data[0]));
                        this.plugin.getLogger().info("Could not find action '" + data[0] + "', skipping...");
                    }
                }
                if (sendMetadata.getValue()) {
                    this.actionHandlers.get("METADATA").accept(new ActionData(tracker, null, null, viewer, sendMetadata));
                }
            }
            tick.increment();
        });
    }

    public Set<Player> getViewers() {
        return this.viewers;
    }

    public int getTick() {
        return this.tick.intValue();
    }

    protected abstract void handleStartRecording(Entity entity, Recording recording);

    public void startRecording(Entity entity, Recording recording) {
        boolean isNew = recording == null;
        UUID uuid = entity.getUniqueId();
        Location location = entity.getLocation();
        String locationString = location.getWorld().getName() + " " + this.toLocationString(location);

        if (isNew) {
            recording = new Recording();
            recording.date = System.currentTimeMillis();
            recording.id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            recording.targetLocation = locationString;

            recording.entities = new ArrayList<>();

            recording.targetUUID = uuid.toString();
            recording.tickStarted = this.getTick();
        }
        boolean existed = false;
        EntityTracker tracker = null;

        for (EntityTracker tracker1 : recording.entities) {
            if (!tracker1.entityUUID.equals(uuid)) {
                continue;
            }
            tracker = tracker1;
            existed = true;
            break;
        }
        if (!existed) {
            tracker = new EntityTracker();
            tracker.entityUUID = uuid;
            tracker.entityId = Integer.MAX_VALUE - entity.getEntityId();
            tracker.name = entity.getName();
            tracker.type = entity.getType().name();
            tracker.isLiving = entity instanceof LivingEntity;
            
            tracker.ticks = new HashMap<>();
            
            if (entity instanceof Player) {
                this.applySkin((Player) entity, tracker);
            } else {
                tracker.skin = "";
            }
            tracker.lastItems = new ArrayList<>();
            tracker.lastNearby = new HashSet<>();
            tracker.recordNearby = isNew;
            
            tracker.recording = recording;
            recording.entities.add(tracker);
        }
        this.trackedEntities.computeIfAbsent(entity, k -> new ArrayList<>()).add(tracker);

        Entity vehicle = entity.getVehicle();

        if (vehicle != null) {
            this.recordVehicle(entity, vehicle);
        }
        this.recordAction(tracker, "SPAWN " + locationString);

        if (entity instanceof Player && ((Player) entity).isSneaking()) {
            this.recordAction(tracker, "S 1");
        }
        this.handleStartRecording(entity, recording);

        if (isNew) {
            this.recordedEntities.put(entity, recording);
        }
    }

    public void startRecording(Entity entity) {
        this.startRecording(entity, null);
    }

    public void stopRecording(Entity entity) {
        Recording recording = this.recordedEntities.remove(entity);

        if (recording == null) {
            return;
        }
        recording.tickDuration = this.getTick() - recording.tickStarted;
        this.stopTracking(entity, recording.id);

        this.storage.save(recording);
    }

    public void stopTracking(Entity entity, String id) {
        List<EntityTracker> trackers = this.trackedEntities.get(entity);

        if (trackers == null) {
            return;
        }
        Iterator<EntityTracker> trackersIterator = trackers.iterator();

        while (trackersIterator.hasNext()) {
            EntityTracker tracker = trackersIterator.next();

            if (tracker.recording.id.equals(id)) {
                this.recordAction(tracker, "R", true);
                trackersIterator.remove();
            }
        }
        if (trackers.isEmpty()) {
            this.trackedEntities.remove(entity);
        }
    }

    public boolean isRecording(Entity entity) {
        return this.recordedEntities.containsKey(entity);
    }

    public void recordAction(EntityTracker tracker, String action, boolean checkExisting) {
        int tick = this.getTick() - tracker.recording.tickStarted;

        if (action.startsWith("SPAWN")) {
            tick -= Configuration.SPAWN_DELAY_TICKS;
        }
        List<String> actions = tracker.ticks.get(tick);

        if (actions == null) {
            actions = new ArrayList<>();
            actions.add(action);
            tracker.ticks.put(tick, actions);
            return;
        }
        if (checkExisting) {
            for (String action1 : actions) {
                if (action1.equals(action)) {
                    return;
                }
            }
        }
        actions.add(action);
    }

    public void recordAction(Entity entity, String action, boolean checkExisting) {
        List<EntityTracker> trackers = this.trackedEntities.get(entity);

        if (trackers != null && !trackers.isEmpty()) {
            this.recordAction(trackers, action, checkExisting);
        }
    }

    public void recordAction(Entity entity, String action) {
        this.recordAction(entity, action, false);
    }

    public void recordAction(EntityTracker tracker, String action) {
        this.recordAction(tracker, action, false);
    }

    public void recordAction(List<EntityTracker> trackers, String action) {
        for (EntityTracker tracker : trackers) {
            this.recordAction(tracker, action);
        }
    }

    public void recordAction(List<EntityTracker> trackers, String action, boolean checkExisting) {
        for (EntityTracker tracker : trackers) {
            this.recordAction(tracker, action, checkExisting);
        }
    }

    public String toLocationString(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        if (this.configuration.getRoundDecimals()) {
            return StringUtils.asString(NumberUtils.format(x), NumberUtils.format(y), NumberUtils.format(z), NumberUtils.format(yaw), NumberUtils.format(pitch));
        } else {
            return StringUtils.asString(x, y, z, yaw, pitch);
        }
    }

    public String toMoveString(Entity entity, Location location) {
        return "M " + this.toLocationString(location) + " " + (entity.isOnGround() ? 1 : 0);
    }

    public void recordMove(Entity entity, Location to,  List<EntityTracker> trackers) {
        this.recordAction(trackers, this.toMoveString(entity, to));
    }

    public void recordVehicle(Entity entered, Entity vehicle) {
        this.recordAction(entered, "MNT " + (Integer.MAX_VALUE - vehicle.getEntityId()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.stopRecording(player);
        this.viewers.remove(player);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        this.stopRecording(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.stopRecording(event.getEntity());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        List<EntityTracker> trackers = this.trackedEntities.remove(entity);

        if (trackers == null || trackers.isEmpty()) {
            return;
        }
        this.recordMove(entity, entity.getLocation(), trackers);
        this.recordAction(trackers, "D");
        this.stopTracking(entity, trackers.get(0).recording.id);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        List<EntityTracker> trackers = this.trackedEntities.get(player);

        if (trackers == null || trackers.isEmpty()) {
            return;
        }
        Location to = event.getTo();

        if (to != null) {
            this.recordMove(player, to, trackers);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        List<EntityTracker> trackers = this.trackedEntities.get(player);

        if (trackers == null || trackers.isEmpty()) {
            return;
        }
        Location to = event.getTo();

        if (to != null && event.getFrom().getWorld() == to.getWorld()) {
            this.recordMove(player, to, trackers);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        this.recordAction(entity, "A 1");

        if (entity.getFireTicks() > 0) {
            return;
        }
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.LAVA) {
            this.recordAction(entity, "C 1");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageBlock(EntityDamageByBlockEvent event) {
        this.recordAction(event.getEntity(), "A 1");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        this.recordAction(event.getEntity(), "A 1");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event) {
        Entity entity = event.getIgnitingEntity();

        if (entity != null) {
            this.recordAction(entity, "A 1");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnimation(PlayerAnimationEvent event) {
        this.recordAction(event.getPlayer(), "A 0");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        this.recordAction(event.getPlayer(), event.isSneaking() ? "S 1" : "S 0");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();

        if (!(source instanceof Entity)) {
            return;
        }
        Entity shooter = (Entity) source;
        List<EntityTracker> trackers = this.trackedEntities.get(shooter);

        if (trackers == null || trackers.isEmpty()) {
            return;
        }
        Set<String> ids = new HashSet<>();

        for (EntityTracker tracker : trackers) {
            if (ids.add(tracker.recording.id)) {
                this.startRecording(projectile, tracker.recording);
            }
        }
        Vector velocity = projectile.getVelocity();
        double x = velocity.getX();
        double y = velocity.getY();
        double z = velocity.getZ();

        if (this.configuration.getRoundDecimals()) {
            this.recordAction(projectile, StringUtils.asString((Object) "V", NumberUtils.format(x), NumberUtils.format(y), NumberUtils.format(z)));
        } else {
            this.recordAction(projectile, StringUtils.asString((Object) "V", x, y, z));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnter(VehicleEnterEvent event) {
        this.recordVehicle(event.getEntered(), event.getVehicle());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExit(VehicleExitEvent event) {
        this.recordAction(event.getExited(), "UNMNT");
    }
}