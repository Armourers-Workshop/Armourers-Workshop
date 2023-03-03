package moe.plushie.armourers_workshop.core.skin.exception;

public class SkinSaveException extends TranslatableException {

    private final Type type;

    public SkinSaveException(Type type, String message, Object... args) {
        super(message, args);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        MARKER_ERROR,
        NO_DATA,
        MISSING_PARTS,
        BED_AND_SEAT,
        INVALID_MULTIBLOCK;

        public SkinSaveException build(String message, Object... args) {
            return new SkinSaveException(this, "exception.armourers_workshop.save." + message, args);
        }
    }
}
