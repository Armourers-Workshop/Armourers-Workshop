package moe.plushie.armourers_workshop.api.math;

public interface IRectangle3i {

    int getX();

    int getY();

    int getZ();

    int getWidth();

    int getHeight();

    int getDepth();

    default int getMinX() {
        return this.getX();
    }

    default int getMinY() {
        return this.getY();
    }

    default int getMinZ() {
        return this.getZ();
    }

    default int getMidX() {
        return this.getX() + this.getWidth() / 2;
    }

    default int getMidY() {
        return this.getY() + this.getHeight() / 2;
    }

    default int getMidZ() {
        return this.getZ() + this.getDepth() / 2;
    }

    default int getMaxX() {
        return this.getX() + this.getWidth();
    }

    default int getMaxY() {
        return this.getY() + this.getHeight();
    }

    default int getMaxZ() {
        return this.getZ() + this.getDepth();
    }
}
