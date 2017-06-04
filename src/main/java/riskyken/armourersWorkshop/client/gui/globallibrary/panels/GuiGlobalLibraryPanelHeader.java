package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelHeader extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/globalLibrary.png");
    
    public GuiGlobalLibraryPanelHeader(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
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
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        
        super.draw(mouseX, mouseY, partialTickTime);
        
        // TODO look in YggdrasilMinecraftSessionService
        
        String username = "player";
        GameProfile gameProfile = mc.thePlayer.getGameProfile();
        if (gameProfile != null) {
            username = gameProfile.getName();
            drawPlayerHead(username);
            this.fontRenderer.drawString(" - " + username, this.x + 24, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xAAFFAA);
        } else {
            this.fontRenderer.drawString("Not logged in.", this.x + 90, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFAAAA);
        }
        
        String titleText = ((GuiGlobalLibrary)parent).tileEntity.getBlockType().getLocalizedName();
        drawCenteredString(fontRenderer, titleText, x + (width / 2), this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFEEEEEE);
    }
    
    private void drawPlayerHead(String username) {
        ResourceLocation rl = AbstractClientPlayer.locationStevePng;
        rl = AbstractClientPlayer.getLocationSkin(username);
        AbstractClientPlayer.getDownloadImageSkin(rl, username);
        mc.renderEngine.bindTexture(rl);
        
        int sourceSize = 8;
        int targetSize = 16;
        
        this.func_152125_a(this.x + 5, this.y + 5, 8, 8, sourceSize, sourceSize, targetSize, targetSize, 64, 32);
        this.func_152125_a(this.x + 4, this.y + 4, 40, 8, sourceSize, sourceSize, targetSize + 2, targetSize + 2, 64, 32);
    }
}
