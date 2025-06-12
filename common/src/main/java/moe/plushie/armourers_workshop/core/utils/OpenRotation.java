package moe.plushie.armourers_workshop.core.utils;

public enum OpenRotation {
    NONE("none"),
    CLOCKWISE_90("clockwise_90"),
    CLOCKWISE_180("180"),
    COUNTERCLOCKWISE_90("counterclockwise_90");

    private final String id;

    OpenRotation(final String id) {
        this.id = id;
    }

    public OpenRotation getRotated(OpenRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> switch (this) {
                case NONE -> CLOCKWISE_180;
                case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_180 -> NONE;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
            };
            case COUNTERCLOCKWISE_90 -> switch (this) {
                case NONE -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_90 -> NONE;
                case CLOCKWISE_180 -> CLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
            };
            case CLOCKWISE_90 -> switch (this) {
                case NONE -> CLOCKWISE_90;
                case CLOCKWISE_90 -> CLOCKWISE_180;
                case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> NONE;
            };
            default -> this;
        };
    }

    public OpenDirection rotate(OpenDirection direction) {
        if (direction.axis() == OpenDirection.Axis.Y) {
            return direction;
        }
        return switch (this) {
            case CLOCKWISE_90 -> direction.clockWise();
            case CLOCKWISE_180 -> direction.opposite();
            case COUNTERCLOCKWISE_90 -> direction.counterClockWise();
            default -> direction;
        };
    }

    public int rotate(int i, int j) {
        return switch (this) {
            case CLOCKWISE_90 -> (i + j / 4) % j;
            case CLOCKWISE_180 -> (i + j / 2) % j;
            case COUNTERCLOCKWISE_90 -> (i + j * 3 / 4) % j;
            default -> i;
        };
    }
}
