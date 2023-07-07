package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HologramProjectorInventorySetting extends HologramProjectorBaseSetting {

    private final UIView slotView = new UIView(new CGRect(0, 0, 18, 18));

    public HologramProjectorInventorySetting(HologramProjectorBlockEntity entity) {
        super("inventory.armourers_workshop.hologram-projector.inventory");
        this.setFrame(new CGRect(0, 0, 176, 40));
        this.slotView.setContents(UIImage.of(ModTextures.COMMON).uv(238, 0).build());
        this.addSubview(slotView);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        slotView.setCenter(new CGPoint(rect.width / 2, 15 + slotView.bounds().height / 2));
    }
}
