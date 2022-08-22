package com.apple.library.uikit;

import com.apple.library.foundation.NSString;

public class UIComboItem {

    public final NSString title;
    public final UIImage image;

    public boolean isEnabled;

    public UIComboItem(NSString title) {
        this(null, title, true);
    }

    public UIComboItem(NSString title, boolean isEnabled) {
        this(null, title, isEnabled);
    }

    public UIComboItem(UIImage image, NSString title) {
        this(image, title, true);
    }

    public UIComboItem(UIImage image, NSString title, boolean isEnabled) {
        this.image = image;
        this.title = title;
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
