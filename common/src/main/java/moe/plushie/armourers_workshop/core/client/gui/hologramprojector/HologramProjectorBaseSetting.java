package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public abstract class HologramProjectorBaseSetting extends UIView {

    private final String baseKey;

    public HologramProjectorBaseSetting(String baseKey) {
        super(CGRect.ZERO);
        this.baseKey = baseKey;
        this.setContents(ModTextures.defaultWindowImage());
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        if (super.pointInside(point, event)) {
            return true;
        }
        return subviews().stream().anyMatch(subview -> subview.pointInside(convertPointToView(point, subview), event));
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