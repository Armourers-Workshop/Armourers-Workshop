package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.menu.AdvancedSkinBuilderMenu;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

@Environment(value = EnvType.CLIENT)
public class AdvancedSkinBuilderScreen extends AWAbstractContainerScreen<AdvancedSkinBuilderMenu> {

    private final AdvancedSkinCanvasView canvasView = new AdvancedSkinCanvasView(TextComponent.EMPTY);

    public AdvancedSkinBuilderScreen(AdvancedSkinBuilderMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 240;
    }

    @Override
    protected void init() {
        this.imageWidth = width;
        this.imageHeight = height;
        super.init();
        this.canvasView.init(minecraft, width, height);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.canvasView.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
