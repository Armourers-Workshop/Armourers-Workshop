package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;

public class Joint implements IJoint {

    private int id;
    private Joint parent;
    private final String name;

    public Joint(String name) {
        this.name = name;
        this.id = 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setParent(Joint parent) {
        this.parent = parent;
    }

    @Override
    public Joint getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Joint)) return false;
        Joint joint2 = (Joint) o;
        return name.equals(joint2.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + "[" + id + "]";
    }
}
