package moe.plushie.armourers_workshop.common.data.type;

public class Rectangle_I_2D {
    
    public int x;
    public int y;
    public int width;
    public int height;
    
    public Rectangle_I_2D(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void grow(int width, int height) {
        this.width += width;
        this.height += height;
    }
    
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }
    
    public boolean intersects(Rectangle_I_2D rectangle) {
        if (x + width > rectangle.x & x < rectangle.x + rectangle.width) {
            if (y + height > rectangle.y & y < rectangle.y + rectangle.height) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInside(int x, int y) {
        if (x >= this.x & x < this.x + this.width) {
            if (y >= this.y & y < this.y + this.height) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected Rectangle_I_2D clone() throws CloneNotSupportedException {
        return new Rectangle_I_2D(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Rectangle_I_2D [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }
}
