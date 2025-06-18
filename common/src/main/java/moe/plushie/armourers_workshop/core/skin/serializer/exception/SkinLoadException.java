package moe.plushie.armourers_workshop.core.skin.serializer.exception;

public class SkinLoadException extends TranslatableException {

    private final Type type;

    public SkinLoadException(Type type, String message, Object... args) {
        super(message, args);
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public enum Type {
        NOT_FOUND,
        NOT_SUPPORTED_TYPE,
        NOT_SUPPORTED_CONTENT,
        NOT_EDITABLE;

        public SkinLoadException build(String message, Object... args) {
            return new SkinLoadException(this, "exception.armourers_workshop.load." + message, args);
        }
    }
}
