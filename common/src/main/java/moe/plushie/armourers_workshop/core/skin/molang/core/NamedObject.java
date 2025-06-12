package moe.plushie.armourers_workshop.core.skin.molang.core;

public class NamedObject {

    private String name;
    private NamedObject owner;

    public void setName(String name, NamedObject owner) {
        this.name = name;
        this.owner = owner;
    }

    public String name() {
        // this is root node.
        if (owner == null) {
            return name;
        }
        // the parent not name.
        var parentName = owner.name();
        if (parentName == null) {
            return name;
        }
        return parentName + "." + name;
    }

    @Override
    public String toString() {
        var name = name();
        if (name != null) {
            return name;
        }
        return "<unnamed>";
    }
}
