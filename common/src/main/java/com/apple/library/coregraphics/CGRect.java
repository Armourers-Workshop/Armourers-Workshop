package com.apple.library.coregraphics;

import com.apple.library.uikit.UIEdgeInsets;

import java.util.Objects;

public class CGRect {

    public static final CGRect ZERO = new CGRect(0, 0, 0, 0);

    public int x;
    public int y;
    public int width;
    public int height;

    public CGRect(CGPoint point, CGSize size) {
        this(point.x, point.y, size.width, size.height);
    }

    public CGRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMinX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinY() {
        return y;
    }

    public int getMidX() {
        return x + width / 2;
    }

    public int getMidY() {
        return y + height / 2;
    }

    public int getMaxX() {
        return x + width;
    }

    public int getMaxY() {
        return y + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CGRect)) return false;
        CGRect that = (CGRect) o;
        return x == that.x && y == that.y && width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    public CGRect intersection(CGRect r) {
        int tx1 = this.x;
        int ty1 = this.y;
        int rx1 = r.x;
        int ry1 = r.y;
        long tx2 = tx1;
        tx2 += this.width;
        long ty2 = ty1;
        ty2 += this.height;
        long rx2 = rx1;
        rx2 += r.width;
        long ry2 = ry1;
        ry2 += r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
        if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
        return new CGRect(tx1, ty1, (int) tx2, (int) ty2);
    }

    public boolean intersects(CGRect rect) {
        return intersects(rect.x, rect.y, rect.width, rect.height);
    }

    public boolean intersects(double x, double y, double w, double h) {
        if (w <= 0 || h <= 0) {
            return false;
        }
        double x0 = getX();
        double y0 = getY();
        return (x + w > x0 && y + h > y0 && x < x0 + getWidth() && y < y0 + getHeight());
    }

    public CGRect offset(CGPoint point) {
        return offset(point.x, point.y);
    }

    public CGRect offset(int dx, int dy) {
        return new CGRect(x + dx, y + dy, width, height);
    }

    public CGRect insetBy(UIEdgeInsets insets) {
        return insetBy(insets.top, insets.left, insets.bottom, insets.right);
    }

    public CGRect insetBy(int top, int left, int bottom, int right) {
        int x0 = x + left;
        int x1 = x + width - right;
        int y0 = y + top;
        int y1 = y + height - bottom;
        return new CGRect(x0, y0, Math.max(x1 - x0, 0), Math.max(y1 - y0, 0));
    }

    public boolean contains(CGPoint point) {
        return contains(point.x, point.y);
    }

    public boolean contains(double x, double y) {
        double x0 = getX();
        double y0 = getY();
        return (x >= x0 && y >= y0 && x <= x0 + getWidth() && y <= y0 + getHeight());
    }

    private boolean contains(int x, int y) {
        int x0 = getX();
        int y0 = getY();
        return (x >= x0 && y >= y0 && x < x0 + getWidth() && y < y0 + getHeight());
    }

    @Override
    public String toString() {
        return String.format("(%d %d; %d %d)", x, y, width, height);
    }
}
