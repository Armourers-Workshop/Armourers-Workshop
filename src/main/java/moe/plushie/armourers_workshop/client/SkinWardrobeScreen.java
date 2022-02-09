package moe.plushie.armourers_workshop.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class SkinWardrobeScreen extends ContainerScreen<SkinWardrobeContainer> {

    private static final ResourceLocation TEXTURE_1 = SkinCore.resource("textures/gui/wardrobe/wardrobe-1.png");
    private static final ResourceLocation TEXTURE_2 = SkinCore.resource("textures/gui/wardrobe/wardrobe-2.png");

    private static final ResourceLocation INVENTORY_TEXTURE = SkinCore.resource("textures/gui/player_inventory.png");

    private final LivingEntity entity;
    private final PlayerEntity operator;
    private Vector3f v3 = new Vector3f(70 / 2, 111, 50);         // 71:111
    private int v4 = 180;
    private int v5 = 45;

    public SkinWardrobeScreen(LivingEntity entity, PlayerEntity operator) {
        super(new SkinWardrobeContainer(0, operator.inventory, operator), operator.inventory, TranslateUtils.translate("inventory.armourers_workshop:wardrobe"));

        this.imageWidth = 278;
        this.imageHeight = 250;

        this.operator = operator;
        this.operator.containerMenu = this.menu;

        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();

        int titleWidth = font.width(getTitle().getVisualOrderText());
        int titleHeight = font.lineHeight;
        this.titleLabelX = leftPos + (width - imageWidth - titleWidth) / 2;
        this.titleLabelY = topPos + (16 - titleHeight) / 2;
    }

    protected void slotClicked(@Nullable Slot slot, int index, int button, ClickType clickType) {
        if (slot != null) {
            index = slot.index;
        }
        menu.clicked(index, button, clickType, operator);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(TEXTURE_1);
        blit(matrixStack, leftPos, topPos, 0, 0, 256, 151);

        bindTexture(TEXTURE_2);
        blit(matrixStack, leftPos + 256, topPos, 0, 0, 22, 151);

        bindTexture(INVENTORY_TEXTURE);
        blit(matrixStack, leftPos + 51, topPos + 152, 0, 0, 176, 98);

        renderPlayer(matrixStack, mouseX, mouseY);
    }

    protected void renderPlayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        int x = leftPos + 8;
        int y = topPos + 27;
        int width = 71;
        int height = 111;
        boolean clipping = (x <= mouseX && mouseX <= x + width) && (y <= mouseY && mouseY <= y + height);

        if (clipping) {
            RenderUtils.enableScissor(x, y, width, height);
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x + (float) width / 2, y + height - 6, 50);
        RenderSystem.rotatef(-20, 0, 1, 0);
        RenderSystem.rotatef(45, 0, 1, 0);
        RenderSystem.translatef(0, 0, -50);

        InventoryScreen.renderEntityInInventory(0, 0, 45, 0, 0, entity);

        RenderSystem.popMatrix();

        if (clipping) {
            RenderUtils.disableScissor();
        }
    }


    private void bindTexture(ResourceLocation location) {
        Minecraft.getInstance().getTextureManager().bind(location);
    }
}
