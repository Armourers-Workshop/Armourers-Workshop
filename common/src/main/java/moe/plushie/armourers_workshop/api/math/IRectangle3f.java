package moe.plushie.armourers_workshop.api.math;

public interface IRectangle3f {

    float getX();

    float getY();

    float getZ();

    float getWidth();

    float getHeight();

    float getDepth();

    default float getMinX() {
        return this.getX();
    }

    default float getMinY() {
        return this.getY();
    }

    default float getMinZ() {
        return this.getZ();
    }

    default float getMidX() {
        return this.getX() + this.getWidth() / 2;
    }

    default float getMidY() {
        return this.getY() + this.getHeight() / 2;
    }

    default float getMidZ() {
        return this.getZ() + this.getDepth() / 2;
    }

    default float getMaxX() {
        return this.getX() + this.getWidth();
    }

    default float getMaxY() {
        return this.getY() + this.getHeight();
    }

    default float getMaxZ() {
        return this.getZ() + this.getDepth();
    }
}
