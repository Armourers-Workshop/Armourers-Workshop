package moe.plushie.armourers_workshop.core.utils;

public enum OpenItemDisplayContext {

    NONE(0, "none"),
    THIRD_PERSON_LEFT_HAND(1, "thirdperson_lefthand"),
    THIRD_PERSON_RIGHT_HAND(2, "thirdperson_righthand"),
    FIRST_PERSON_LEFT_HAND(3, "firstperson_lefthand"),
    FIRST_PERSON_RIGHT_HAND(4, "firstperson_righthand"),
    HEAD(5, "head"), // or body
    GUI(6, "gui"),
    GROUND(7, "ground"),
    FIXED(8, "fixed");

    private final int id;
    private final String serializedName;

    OpenItemDisplayContext(int id, String serializedName) {
        this.id = id;
        this.serializedName = serializedName;
    }

    public static OpenItemDisplayContext byId(int id) {
        for (var value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return OpenItemDisplayContext.NONE;
    }

    public static OpenItemDisplayContext byName(String name) {
        for (var value : values()) {
            if (value.serializedName.equals(name)) {
                return value;
            }
        }
        return OpenItemDisplayContext.NONE;
    }

    public int id() {
        return id;
    }

    public String serializedName() {
        return serializedName;
    }

    public boolean isLeftHand() {
        return this == THIRD_PERSON_LEFT_HAND || this == FIRST_PERSON_LEFT_HAND;
    }

    public boolean isRightHand() {
        return this == THIRD_PERSON_RIGHT_HAND || this == FIRST_PERSON_RIGHT_HAND;
    }

    public boolean isFirstPerson() {
        return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
    }

    public boolean isThirdPerson() {
        return this == THIRD_PERSON_LEFT_HAND || this == THIRD_PERSON_RIGHT_HAND;
    }
}
