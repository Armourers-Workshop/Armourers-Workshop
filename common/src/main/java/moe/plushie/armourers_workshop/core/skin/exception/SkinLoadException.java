package moe.plushie.armourers_workshop.core.skin.exception;

public class SkinLoadException extends TranslatableException {

    private final Type type;

    public SkinLoadException(Type type, String message, Object... args) {
        super(message, args);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        NOT_FOUND;

        public SkinLoadException build(String message, Object... args) {
            return new SkinLoadException(this, "exception.armourers_workshop.load." + message, args);
        }
    }
}
