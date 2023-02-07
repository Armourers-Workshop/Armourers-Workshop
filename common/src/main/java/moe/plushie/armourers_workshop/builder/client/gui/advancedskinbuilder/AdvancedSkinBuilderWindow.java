package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.foundation.NSString;
import moe.plushie.armourers_workshop.builder.menu.AdvancedSkinBuilderMenu;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@Environment(value = EnvType.CLIENT)
public class AdvancedSkinBuilderWindow extends MenuWindow<AdvancedSkinBuilderMenu> {

//    private final NSWindow window = new NSWindow(new Rectangle2i(0, 0, 320, 240));

    public AdvancedSkinBuilderWindow(AdvancedSkinBuilderMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        AdvancedSkinCanvasView canvasView = new AdvancedSkinCanvasView(bounds());
        canvasView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(canvasView);
    }

//    @Override
//    protected void init() {
//        this.window.l(width, height);
//        this.imageWidth = window.bounds().width;
//        this.imageHeight = window.bounds().height;
//        super.init();
////        this.canvasView.init(minecraft, width, height);
//    }
//
//    @Override
//    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
//        super.render(poseStack, mouseX, mouseY, partialTicks);
////        this.canvasView.render(poseStack, mouseX, mouseY, partialTicks);
////        this.window.vanillaRender(this, poseStack, mouseX, mouseY, partialTicks);
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        return this.window.vanillaMouseClicked((int) mouseX, (int) mouseY, button);
//    }
//
//    @Override
//    public boolean mouseReleased(double mouseX, double mouseY, int button) {
//        return this.window.vanillaMouseReleased((int) mouseX, (int) mouseY, button);
//    }
//
//    @Override
//    public void mouseMoved(double mouseX, double mouseY) {
//        this.window.vanillaMouseMoved((int) mouseX, (int) mouseY);
//    }
}
