package moe.plushie.armourers_workshop.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.menu.SkinnableMenu;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

@SuppressWarnings({"unused"})
@Environment(value = EnvType.CLIENT)
public class SkinnableScreen extends AWAbstractContainerScreen<SkinnableMenu> {

    public SkinnableScreen(SkinnableMenu container, Inventory inventory, Component title) {
        super(container, inventory, container.getInventoryName());

        this.imageWidth = 176;
        this.imageHeight = container.getRow() * 18 + 125;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = imageHeight - 96;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        RenderUtils.bind(RenderUtils.TEX_COMMON);
        RenderUtils.drawContinuousTexturedBox(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128, 4, 4, 4, 4, 0);
        RenderUtils.blit(matrixStack, leftPos + 7, topPos + imageHeight - 85, 0, 180, 162, 76);
        List<Slot> slots = menu.slots;
        for (int i = 36; i < slots.size(); ++i) {
            Slot slot = slots.get(i);
            RenderUtils.blit(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1, 238, 0, 18, 18);
        }
    }

    @Override
    public boolean shouldRenderPluginScreen() {
        return true;
    }
}
