package me.lst.recordplus.objects;

public class MutableInt {
    private int value;

    public MutableInt(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void add(int amount) {
        this.value += amount;
    }

    public void increment() {
        ++this.value;
    }

    public int intValue() {
        return this.value;
    }
}