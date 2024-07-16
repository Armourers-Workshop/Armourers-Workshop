package moe.plushie.armourers_workshop.compatibility.api;

import java.util.HashMap;

public enum AbstractItemTransformType {

    NONE(0, "none"),
    THIRD_PERSON_LEFT_HAND(1, "thirdperson_lefthand"),
    THIRD_PERSON_RIGHT_HAND(2, "thirdperson_righthand"),
    FIRST_PERSON_LEFT_HAND(3, "firstperson_lefthand"),
    FIRST_PERSON_RIGHT_HAND(4, "firstperson_righthand"),
    HEAD(5, "head"),
    GUI(6, "gui"),
    GROUND(7, "ground"),
    FIXED(8, "fixed");

    private final int id;
    private final String name;

    AbstractItemTransformType(int id, String name) {
        this.id = id;
        this.name = name;
        Codec.BY_NAME.put(name, this);
    }

    public static AbstractItemTransformType byName(String name) {
        return Codec.BY_NAME.getOrDefault(name, NONE);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    private static class Codec {
        static final HashMap<String, AbstractItemTransformType> BY_NAME = new HashMap<>();
    }
}
