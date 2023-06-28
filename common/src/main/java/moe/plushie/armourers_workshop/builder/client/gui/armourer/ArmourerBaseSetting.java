package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArmourerBaseSetting extends UIView {

    private final String baseKey;

    public ArmourerBaseSetting(String baseKey) {
        super(CGRect.ZERO);
        this.baseKey = baseKey;
    }

    public void init() {
    }

    public void reloadData() {
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
