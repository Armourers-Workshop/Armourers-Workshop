package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelHeader extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibGuiResources.GUI_GLOBAL_LIBRARY);

    private GuiIconButton iconButtonHome;
    private GuiIconButton iconButtonFavourites;
    private GuiIconButton iconButtonMyFiles;
    private GuiIconButton iconButtonUploadSkin;
    private GuiIconButton iconButtonJoinBeta;
    private GuiIconButton iconButtonInfo;

    public GuiGlobalLibraryPanelHeader(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary) parent).getGuiName();
        buttonList.clear();

        iconButtonHome = new GuiIconButton(parent, 0, this.x + this.width - 21, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.home"), BUTTON_TEXTURES).setIconLocation(0, 0, 16, 16);
        // iconButtonFavourites = new GuiIconButton(parent, 1, this.x + this.width - 41,
        // this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName,
        // "header.favourites"), BUTTON_TEXTURES).setIconLocation(0, 17, 16, 16);
        iconButtonMyFiles = new GuiIconButton(parent, 2, this.x + this.width - 41, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.myFiles"), BUTTON_TEXTURES).setIconLocation(0, 34, 16, 16);
        iconButtonUploadSkin = new GuiIconButton(parent, 3, this.x + this.width - 61, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.uploadSkin"), BUTTON_TEXTURES).setIconLocation(0, 51, 16, 16);
        iconButtonJoinBeta = new GuiIconButton(parent, 4, this.x + this.width - 41, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.joinBeta"), BUTTON_TEXTURES).setIconLocation(0, 68, 16, 16);
        iconButtonInfo = new GuiIconButton(parent, 5, this.x + this.width - 81, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.info"), BUTTON_TEXTURES).setIconLocation(0, 17, 16, 16);

        iconButtonUploadSkin.setDisableText(GuiHelper.getLocalizedControlName(guiName, "header.uploadSkinBan"));

        buttonList.add(iconButtonHome);
        // buttonList.add(iconButtonFavourites);
        buttonList.add(iconButtonMyFiles);
        buttonList.add(iconButtonUploadSkin);
        buttonList.add(iconButtonJoinBeta);
        buttonList.add(iconButtonInfo);
        betaCheckUpdate();
    }

    @Override
    public void update() {
        super.update();
        betaCheckUpdate();
    }

    private void betaCheckUpdate() {
        boolean inBeta = PlushieAuth.isRemoteUser();
        boolean doneBetaCheck = PlushieAuth.doneRemoteUserCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;
        if (inBeta) {
            iconButtonInfo.x = this.x + this.width - 81;
        } else {
            iconButtonInfo.x = this.x + this.width - 61;
        }
        // iconButtonFavourites.visible = inBeta;
        if (session.hasServerId()) {
            iconButtonMyFiles.visible = inBeta;
            iconButtonUploadSkin.visible = inBeta;
            iconButtonUploadSkin.enabled = session.hasPermission(PlushieAction.SKIN_UPLOAD);
        } else {
            iconButtonMyFiles.visible = false;
            iconButtonUploadSkin.visible = false;
            iconButtonJoinBeta.visible = true;
        }
        if (doneBetaCheck) {
            iconButtonJoinBeta.visible = !inBeta;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == iconButtonHome) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.HOME);
            ((GuiGlobalLibrary) parent).panelHome.updateSkinPanels();
        }
        if (button == iconButtonFavourites) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.FAVOURITES);
        }
        if (button == iconButtonMyFiles) {
            GuiGlobalLibrary guiGlobalLibrary = (GuiGlobalLibrary) parent;
            int serverId = PlushieAuth.PLUSHIE_SESSION.getServerId();
            ((GuiGlobalLibrary) parent).panelUserSkins.clearResults();
            ((GuiGlobalLibrary) parent).switchScreen(Screen.USER_SKINS);
            ((GuiGlobalLibrary) parent).panelUserSkins.switchToUser(serverId);

        }
        if (button == iconButtonUploadSkin) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.UPLOAD);
        }
        if (button == iconButtonJoinBeta) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.JOIN_BETA);
        }
        if (button == iconButtonInfo) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.INFO);
            ((GuiGlobalLibrary) parent).panelInfo.updateInfo();
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);

        String username = "player";
        GameProfile gameProfile = mc.player.getGameProfile();
        if (gameProfile != null) {
            username = gameProfile.getName();
            GuiHelper.drawPlayerHead(x + 4, y + 4, 16, username);
            this.fontRenderer.drawString(" - " + username, this.x + 24, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xAAFFAA);
        } else {
            this.fontRenderer.drawString("Not logged in.", this.x + 90, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFAAAA);
        }
        String titleText = ((GuiGlobalLibrary) parent).getGuiName();
        drawCenteredString(fontRenderer, GuiHelper.getLocalizedControlName(titleText, "name"), x + (width / 2), this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFEEEEEE);
        super.draw(mouseX, mouseY, partialTickTime);
    }

    private void drawPlayerHead(String username) {
        ResourceLocation rl = DefaultPlayerSkin.getDefaultSkinLegacy();
        rl = AbstractClientPlayer.getLocationSkin(username);
        AbstractClientPlayer.getDownloadImageSkin(rl, username);
        mc.renderEngine.bindTexture(rl);

        int sourceSize = 8;
        int targetSize = 16;

        this.drawScaledCustomSizeModalRect(this.x + 5, this.y + 5, 8, 8, sourceSize, sourceSize, targetSize, targetSize, 64, 32);
        this.drawScaledCustomSizeModalRect(this.x + 4, this.y + 4, 40, 8, sourceSize, sourceSize, targetSize + 2, targetSize + 2, 64, 32);
    }
}
