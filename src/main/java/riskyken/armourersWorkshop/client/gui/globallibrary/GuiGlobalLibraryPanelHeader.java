package riskyken.armourersWorkshop.client.gui.globallibrary;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class GuiGlobalLibraryPanelHeader extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/globalLibrary.png");
    
    public GuiGlobalLibraryPanelHeader(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiIconButton(parent, 0, this.x + this.width - 21, this.y + 5, 16, 16, "Home", BUTTON_TEXTURES).setIconLocation(0, 0, 16, 16));
        buttonList.add(new GuiIconButton(parent, 1, this.x + this.width - 42, this.y + 5, 16, 16, "Favourites", BUTTON_TEXTURES).setIconLocation(0, 17, 16, 16));
        buttonList.add(new GuiIconButton(parent, 2, this.x + this.width - 62, this.y + 5, 16, 16, "Friends", BUTTON_TEXTURES).setIconLocation(0, 34, 16, 16));
        buttonList.add(new GuiIconButton(parent, 3, this.x + this.width - 84, this.y + 5, 16, 16, "Upload Skin", BUTTON_TEXTURES).setIconLocation(0, 51, 16, 16));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            ((GuiGlobalLibrary)parent).switchScreen(Screen.HOME);
        }
        if (button.id == 1) {
            ((GuiGlobalLibrary)parent).switchScreen(Screen.FAVOURITES);
        }
        if (button.id == 2) {
            ((GuiGlobalLibrary)parent).switchScreen(Screen.FRIENDS);
        }
        if (button.id == 3) {
            ((GuiGlobalLibrary)parent).switchScreen(Screen.UPLOAD);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        
        GameProfile gameProfile = mc.thePlayer.getGameProfile();
        ResourceLocation rl = DefaultPlayerSkin.getDefaultSkinLegacy();
        if (gameProfile != null) {
            String name = gameProfile.getName();
            rl = AbstractClientPlayer.getLocationSkin(name);
            AbstractClientPlayer.getDownloadImageSkin(rl, name);
        }
        mc.renderEngine.bindTexture(rl);
        
        int size = 16;
        
        drawScaledCustomSizeModalRect(this.x + 5, this.y + 5, 8, 8, 8, 8, size, size, 64, 64);
        drawScaledCustomSizeModalRect(this.x + 4, this.y + 4, 40, 8, 8, 8, size + 2, size + 2, 64, 64);
        
        this.fontRenderer.drawString(" - RiskyKen", this.x + 24, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xAAFFAA);
        drawCenteredString(fontRenderer, ((GuiGlobalLibrary)parent).tileEntity.getBlockType().getLocalizedName(),x + (width / 2), this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFEEEEEE);
        super.drawScreen(mouseX, mouseY, partialTickTime);
    }
}
