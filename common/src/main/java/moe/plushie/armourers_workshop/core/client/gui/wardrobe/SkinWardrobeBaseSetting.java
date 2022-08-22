package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public abstract class SkinWardrobeBaseSetting extends UIView {

    private final String baseKey;

    public SkinWardrobeBaseSetting(String baseKey) {
        super(CGRect.ZERO);
        this.baseKey = baseKey;
    }

    public NSString getTitle() {
        return new NSString(TranslateUtils.title(baseKey));
    }

    protected NSString getDisplayText(String key) {
        return new NSString(TranslateUtils.title(baseKey + "." + key));
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return new NSString(TranslateUtils.title(baseKey + "." + key, objects));
    }
}
