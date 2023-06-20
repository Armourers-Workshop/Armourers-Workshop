package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.guide.AdvancedChestGuideRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AdvancedSkinCanvasView extends UIView {

//    private final GuideRendererManager rendererManager = new GuideRendererManager();
//    private final AdvancedSkinChestModel model = new AdvancedSkinChestModel();
    private final AdvancedChestGuideRenderer renderer = new AdvancedChestGuideRenderer();

    public AdvancedSkinCanvasView(CGRect frame) {
        super(frame);
    }
}
