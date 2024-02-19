package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.gui.screens.inventory.InventoryScreen;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import manifold.ext.rt.api.auto;

@Extension
@Available("[1.21, )")
public class ABI {

    public static void renderEntityInInventoryFollowsMouse(@ThisClass Class<?> clazz, GuiGraphics guiGraphics, CGRect rect, int scale, Vector3f rotate, CGPoint focus, LivingEntity livingEntity) {
        auto ctm = guiGraphics.pose();
        ctm.pushPose();
        ctm.translate(rect.getWidth() / 2, rect.getHeight() / 2, 50);
        ctm.mulPose(Vector3f.XP.rotationDegrees(rotate.getX()));
        ctm.mulPose(Vector3f.YP.rotationDegrees(rotate.getY()));
        ctm.translate(0, 0, -50);
        RenderSystem.setExtendedScissorFlags(1);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, 0, 0, 0, 0, scale, 0.0625f, focus.x, focus.y, livingEntity);
        RenderSystem.setExtendedScissorFlags(0);
        ctm.popPose();
    }
}
