package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class SkinWardrobeBaseSetting extends UIView {

    private final String baseKey;

    public SkinWardrobeBaseSetting(String baseKey) {
        super(CGRect.ZERO);
        this.baseKey = baseKey;
    }

    public NSString getTitle() {
        return NSString.localizedString(baseKey);
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString(baseKey + "." + key, objects);
    }
}
