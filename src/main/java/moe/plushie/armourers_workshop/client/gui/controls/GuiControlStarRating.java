package moe.plushie.armourers_workshop.client.gui.controls;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControlStarRating extends GuiButtonExt {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_RATING);

    private int maxRating;
    private int rating;

    public GuiControlStarRating(int xPos, int yPos) {
        super(-1, xPos, yPos, 16, 16, "");
        setMaxRating(10);
        setRating(7);
    }

    public void setMaxRating(int maxRating) {
        this.width = maxRating * 8;
        this.maxRating = maxRating;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public void setRating(int rating) {
        this.rating = MathHelper.clamp(rating, 0, maxRating);
    }

    public int getRating() {
        return rating;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            setRating(MathHelper.floor((mouseX + 8 - x) / 8F));
            return true;
        }
        return false;
    }
    
    private int getRatingAtPos(int mouseX, int mouseY) {
        return MathHelper.clamp(MathHelper.floor((mouseX + 8 - x) / 8F), 0, maxRating);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }

        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);

        mc.renderEngine.bindTexture(TEXTURE);
        int rating = getRating();
        for (int i = 0; i < (getMaxRating() / 2); i++) {
            drawTexturedModalRect(x + i * 16, y, 32, 0, 16, 16);
        }
        
        if (k == 2) {
            rating = getRatingAtPos(mouseX, mouseY);
        }
        
        int stars = MathHelper.floor(rating / 2F);
        int halfStar = rating % 2;
        for (int i = 0; i < stars; i++) {
            drawTexturedModalRect(x + i * 16, y, 0, 0, 16, 16);
        }
        if (halfStar == 1) {
            drawTexturedModalRect(x + stars * 16, y, 0, 0, 8, 16);
        }

        // GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46,
        // this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
    }
}
