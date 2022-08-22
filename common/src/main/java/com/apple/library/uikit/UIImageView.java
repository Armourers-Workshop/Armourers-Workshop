package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.HighlightedDisplayable;

public class UIImageView extends UIView implements HighlightedDisplayable {

    private UIImage image;
    private UIImage highlightedImage;

    private boolean isHighlighted = false;

    public UIImageView(CGRect frame) {
        super(frame);
        this.setUserInteractionEnabled(false);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        UIImage currentImage = null;
        if (this.isHighlighted()) {
            currentImage = highlightedImage();
        }
        if (currentImage == null) {
            currentImage = image();
        }
        context.drawImage(currentImage, bounds());
    }

    public UIImage image() {
        return this.image;
    }

    public void setImage(UIImage image) {
        this.image = image;
    }

    public UIImage highlightedImage() {
        return this.highlightedImage;
    }

    public void setHighlightedImage(UIImage highlightedImage) {
        this.highlightedImage = highlightedImage;
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }
}
