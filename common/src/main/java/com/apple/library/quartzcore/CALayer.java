package com.apple.library.quartzcore;

public class CALayer {

    protected float _cornerRadius = 0;
    protected boolean _masksToBounds = false;

    protected float _borderWidth = 0;
    protected int _borderColor;

    protected float _opacity = 1;

    protected boolean _isHidden = false;
    protected boolean _isOpaque = false;

    protected Object _contents;

    public void setCornerRadius(float cornerRadius) {
        _cornerRadius = cornerRadius;
    }

    public float cornerRadius() {
        return _cornerRadius;
    }


    public void setMasksToBounds(boolean masksToBounds) {
        _masksToBounds = masksToBounds;
    }

    public boolean masksToBounds() {
        return _masksToBounds;
    }

    public boolean isHidden() {
        return _isHidden;
    }

    public void setHidden(boolean isHidden) {
        _isHidden = isHidden;
    }

    public boolean isOpaque() {
        return _isOpaque;
    }

    public void setOpaque(boolean opaque) {
        _isOpaque = opaque;
    }

    public float opacity() {
        return _opacity;
    }

    public void setOpacity(float opacity) {
        _opacity = opacity;
    }

    public void setBorderColor(int borderColor) {
        _borderColor = borderColor;
    }

    public int borderColor() {
        return _borderColor;
    }

    public void setBorderWidth(float borderWidth) {
        _borderWidth = borderWidth;
    }

    public float borderWidth() {
        return _borderWidth;
    }

    public void setContents(Object contents) {
        _contents = contents;
    }

    public Object contents() {
        return _contents;
    }

}
