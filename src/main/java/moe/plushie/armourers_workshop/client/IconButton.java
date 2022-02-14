package moe.plushie.armourers_workshop.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IconButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int textureWidth;
    private final int textureHeight;

    private boolean isSelected = false;

    public IconButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.IPressable pressable, Button.ITooltip tooltip, ITextComponent title) {
        this(x, y, width, height, u, v, texture, 256, 256, pressable, tooltip, title);
    }

    public IconButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, int textureWidth, int textureHeight, Button.IPressable pressable, Button.ITooltip tooltip, ITextComponent title) {
        super(x, y, width, height, title, pressable, tooltip);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = u;
        this.yTexStart = v;
        this.resourceLocation = texture;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean getSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);
        int u = this.xTexStart + this.width * this.getState();
        int v = this.yTexStart;
        RenderSystem.enableDepthTest();
        blit(matrixStack, this.x, this.y, u, v, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    private int getState() {
        int state = 0;
        if (this.isHovered()) {
            state += 1;
        }
        if (isSelected) {
            state += 2;
        }
        return state;
    }
}