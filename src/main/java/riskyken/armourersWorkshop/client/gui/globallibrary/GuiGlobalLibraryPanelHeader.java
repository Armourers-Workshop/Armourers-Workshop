package riskyken.armourersWorkshop.client.gui.globallibrary;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

public class GuiGlobalLibraryPanelHeader extends GuiPanel {

    public GuiGlobalLibraryPanelHeader(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiIconButton(parent, 0, this.x + this.width - 21, this.y + 5, 16, 16, "Friends", optionsBackground));
        buttonList.add(new GuiIconButton(parent, 0, this.x + this.width - 42, this.y + 5, 16, 16, "Favourites", optionsBackground));
        buttonList.add(new GuiIconButton(parent, 0, this.x + this.width - 62, this.y + 5, 16, 16, "Collections", optionsBackground));
        buttonList.add(new GuiIconButton(parent, 0, this.x + this.width - 84, this.y + 5, 16, 16, "Upload Skin", optionsBackground));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        
        GameProfile gameProfile = mc.thePlayer.getGameProfile();
        ResourceLocation rl = AbstractClientPlayer.locationStevePng;
        if (gameProfile != null) {
            String name = gameProfile.getName();
            rl = AbstractClientPlayer.getLocationSkin(name);
            AbstractClientPlayer.getDownloadImageSkin(rl, name);
        }
        mc.renderEngine.bindTexture(rl);
        
        int size = 16;
        this.drawTexturedModalRectScaled(this.x + 5, this.y + 5, 8, 8, 8, 8, size, size);
        this.drawTexturedModalRectScaled(this.x + 4, this.y + 4, 40, 8, 8, 8, size + 2, size + 2);
        this.fontRenderer.drawString(" - RiskyKen", this.x + 24, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xAAFFAA);
        drawCenteredString(fontRenderer, ((GuiGlobalLibrary)parent).tileEntity.getBlockType().getLocalizedName(),x + (width / 2), this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFEEEEEE);
    }
    
    public void drawTexturedModalRectScaled (int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
        float f = 1F / 64;
        float f1 = 1F / 32;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + tarHeight), (double)this.zLevel, (double)((float)(u + 0) * f), (double)((float)(v + srcHeight) * f1));
        tessellator.addVertexWithUV((double)(x + tarWidth), (double)(y + tarHeight), (double)this.zLevel, (double)((float)(u + srcWidth) * f), (double)((float)(v + srcHeight) * f1));
        tessellator.addVertexWithUV((double)(x + tarWidth), (double)(y + 0), (double)this.zLevel, (double)((float)(u + srcWidth) * f), (double)((float)(v + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
        tessellator.draw();
    }
}
