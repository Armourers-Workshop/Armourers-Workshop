package riskyken.armourersWorkshop.api.common.skin;

import riskyken.armourersWorkshop.api.common.IRectangle3D;

public class Rectangle3D implements IRectangle3D {

    private final int x;
    private final int y;
    private final int z;
    private final int width;
    private final int height;
    private final int depth;
    
    public Rectangle3D(int x, int y, int z, int width, int height, int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }
}
