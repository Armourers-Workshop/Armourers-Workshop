package com.apple.library.uikit;

import com.apple.library.foundation.NSString;

@SuppressWarnings("unused")
public class UIBarItem {

    private NSString title;
    private UIImage image = null;
    private UIEdgeInsets imageInsets = UIEdgeInsets.ZERO;
    private boolean isEnabled = true;
    private int tag;

    public NSString title() {
        return title;
    }

    public void setTitle(NSString title) {
        this.title = title;
    }

    public UIImage getImage() {
        return image;
    }

    public void setImage(UIImage image) {
        this.image = image;
    }

    public UIEdgeInsets imageInsets() {
        return imageInsets;
    }

    public void setImageInsets(UIEdgeInsets imageInsets) {
        this.imageInsets = imageInsets;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int tag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
