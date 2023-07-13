package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.init.ModTextures;

public class AdvancedBoneSkinPanel extends AdvancedSkinPanel {

    private final AdvancedSkinBuilderBlockEntity blockEntity;

    public AdvancedBoneSkinPanel(AdvancedSkinBuilderBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.setBackgroundColor(UIColor.ORANGE);
        this.barItem.setImage(UIImage.of(ModTextures.TAB_ICONS).uv(240, 0).fixed(16, 16).build());
    }
}
