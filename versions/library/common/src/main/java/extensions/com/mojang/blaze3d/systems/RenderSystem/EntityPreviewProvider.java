package extensions.com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.PoseStack;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

@Extension
@Available("[1.16, 1.19.4)")
public class EntityPreviewProvider {

    public static void renderEntityInInventory(@ThisClass Class<?> clazz, PoseStack poseStack, int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity) {
        InventoryScreen.renderEntityInInventory(x, y, scale, mouseX, mouseY, entity);
    }
}
