package moe.plushie.armourers_workshop.core.utils;

public enum OpenEquipmentSlot {

    MAINHAND(Type.HAND, 0, 0, 0, "mainhand"),
    OFFHAND(Type.HAND, 1, 0, 5, "offhand"),
    FEET(Type.HUMANOID_ARMOR, 0, 1, 1, "feet"),
    LEGS(Type.HUMANOID_ARMOR, 1, 1, 2, "legs"),
    CHEST(Type.HUMANOID_ARMOR, 2, 1, 3, "chest"),
    HEAD(Type.HUMANOID_ARMOR, 3, 1, 4, "head"),
    BODY(Type.ANIMAL_ARMOR, 0, 1, 6, "body");

    private final Type type;
    private final String serializedName;

    private final int index;
    private final int countLimit;
    private final int filterFlag;

    OpenEquipmentSlot(Type type, int index, int countLimit, int filterFlag, String serializedName) {
        this.type = type;
        this.index = index;
        this.countLimit = countLimit;
        this.filterFlag = filterFlag;
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    public Type type() {
        return type;
    }

    public int index() {
        return index;
    }

    public int filterFlag() {
        return filterFlag;
    }

    public boolean isHand() {
        return type == Type.HAND;
    }

    public boolean isArmor() {
        return type != Type.HAND;
    }

    public enum Type {
        HAND,
        HUMANOID_ARMOR,
        ANIMAL_ARMOR;
    }
}
