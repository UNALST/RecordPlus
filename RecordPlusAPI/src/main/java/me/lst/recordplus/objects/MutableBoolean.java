package me.lst.recordplus.objects;

public class MutableBoolean {
    private boolean value;

    public MutableBoolean(boolean value) {
        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }
}
