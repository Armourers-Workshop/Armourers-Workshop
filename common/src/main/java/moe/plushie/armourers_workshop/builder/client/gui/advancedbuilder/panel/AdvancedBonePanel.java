package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.init.ModTextures;

public class AdvancedBonePanel extends AdvancedPanel {

    public AdvancedBonePanel(DocumentEditor editor) {
        super(editor);
        this.barItem.setImage(UIImage.of(ModTextures.TAB_ICONS).uv(16, 128).fixed(16, 16).build());
    }
}
