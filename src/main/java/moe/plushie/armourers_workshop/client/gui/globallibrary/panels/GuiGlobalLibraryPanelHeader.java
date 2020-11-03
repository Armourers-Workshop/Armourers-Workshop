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
import net.minecraft.client.renderer.GlStateManager;
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
    private GuiIconButton iconButtonJoin;
    private GuiIconButton iconButtonInfo;
    private GuiIconButton iconButtonModeration;
    private GuiIconButton iconButtonProfile;

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
        iconButtonUploadSkin.setDisableText(GuiHelper.getLocalizedControlName(guiName, "header.uploadSkinBan"));
        iconButtonJoin = new GuiIconButton(parent, 4, this.x + this.width - 41, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.join"), BUTTON_TEXTURES).setIconLocation(0, 68, 16, 16);
        iconButtonInfo = new GuiIconButton(parent, 5, this.x + this.width - 81, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.info"), BUTTON_TEXTURES).setIconLocation(0, 17, 16, 16);
        iconButtonModeration = new GuiIconButton(parent, -1, this.x, this.y + 4, 18, 18, GuiHelper.getLocalizedControlName(guiName, "header.moderation"), BUTTON_TEXTURES).setIconLocation(0, 119, 16, 16);
        iconButtonProfile = new GuiIconButton(parent, -1, this.x + 1, this.y + 1, 24, 24, GuiHelper.getLocalizedControlName(guiName, "header.my_profile"), BUTTON_TEXTURES);

        buttonList.add(iconButtonHome);
        // buttonList.add(iconButtonFavourites);
        buttonList.add(iconButtonMyFiles);
        buttonList.add(iconButtonUploadSkin);
        buttonList.add(iconButtonJoin);
        buttonList.add(iconButtonInfo);
        buttonList.add(iconButtonModeration);
        // buttonList.add(iconButtonProfile);

        betaCheckUpdate();
    }

    @Override
    public void update() {
        super.update();
        betaCheckUpdate();
    }

    private void betaCheckUpdate() {
        int padButton = 20;
        int buttonOffsetX = this.x + this.width - 21;

        iconButtonHome.visible = true;
        iconButtonMyFiles.visible = false;
        iconButtonUploadSkin.visible = false;
        iconButtonJoin.visible = false;
        iconButtonInfo.visible = true;
        iconButtonModeration.visible = false;

        iconButtonHome.x = buttonOffsetX;
        buttonOffsetX -= padButton;

        boolean isRemoteUser = PlushieAuth.isRemoteUser();
        boolean doneRemoteUserCheck = PlushieAuth.doneRemoteUserCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;

        if (doneRemoteUserCheck & !isRemoteUser) {
            iconButtonJoin.visible = true;
            iconButtonJoin.x = buttonOffsetX;
            buttonOffsetX -= padButton;
        }

        if (session.hasServerId()) {
            iconButtonMyFiles.visible = isRemoteUser;
            iconButtonMyFiles.x = buttonOffsetX;
            buttonOffsetX -= padButton;

            iconButtonUploadSkin.visible = isRemoteUser;
            iconButtonUploadSkin.enabled = session.hasPermission(PlushieAction.SKIN_UPLOAD);
            iconButtonUploadSkin.x = buttonOffsetX;
            buttonOffsetX -= padButton;

            // iconButtonFavourites.visible = true;
            // iconButtonFavourites.x = buttonOffsetX;
            // buttonOffsetX -= padButton;

            if (session.hasPermission(PlushieAction.GET_REPORT_LIST)) {
                iconButtonModeration.visible = true;
                iconButtonModeration.x = buttonOffsetX;
                buttonOffsetX -= padButton;
            }
        }

        iconButtonInfo.x = buttonOffsetX;
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
        if (button == iconButtonJoin) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.JOIN);
        }
        if (button == iconButtonInfo) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.INFO);
            ((GuiGlobalLibrary) parent).panelInfo.updateInfo();
        }
        if (button == iconButtonModeration) {
            ((GuiGlobalLibrary) parent).switchScreen(Screen.MODERATION);
        }
        if (button == iconButtonProfile) {
            ((GuiGlobalLibrary) parent).panelProfile.setProfileTarget(null);
            ((GuiGlobalLibrary) parent).switchScreen(Screen.PROFILE);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        String titleText = ((GuiGlobalLibrary) parent).getGuiName();
        drawCenteredString(fontRenderer, GuiHelper.getLocalizedControlName(titleText, "name"), x + (width / 2), this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, 0xFFEEEEEE);
        super.draw(mouseX, mouseY, partialTickTime);

        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    protected void drawbuttons(int mouseX, int mouseY, float partialTickTime) {
        if (insideCheck & !isInside(mouseX, mouseY)) {
            mouseX = -10;
            mouseY = -10;
        }
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX, mouseY, partialTickTime);
        }

        // Colours.
        // White - not a member.
        // Yellow - Member not authenticated.
        // Green - Authenticated member.
        // Red - Missing profile info.
        String username = "MissingNo";
        int colour = 0xFFFFFF;
        if (PlushieAuth.PLUSHIE_SESSION.hasServerId()) {
            colour = 0xFFFFAA;
        }
        if (PlushieAuth.PLUSHIE_SESSION.isAuthenticated()) {
            colour = 0xAAFFAA;
        }
        GameProfile gameProfile = mc.player.getGameProfile();
        if (gameProfile != null) {
            username = gameProfile.getName();
            GuiHelper.drawPlayerHead(x + 4, y + 4, 16, username);
            this.fontRenderer.drawString(" - " + username, this.x + 24, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, colour);
        } else {
            GuiHelper.drawPlayerHead(x + 4, y + 4, 16, null);
            colour = 0xFFAAAA;
            this.fontRenderer.drawString(" - " + username, this.x + 24, this.y + (height / 2) - fontRenderer.FONT_HEIGHT / 2, colour);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);

        for (int i = 0; i < buttonList.size(); i++) {
            if (buttonList.get(i) instanceof GuiIconButton) {
                ((GuiIconButton) buttonList.get(i)).drawRollover(mc, mouseX, mouseY);
            }
        }
    }

    private void drawPlayerHead(String username) {
        ResourceLocation rl = DefaultPlayerSkin.getDefaultSkinLegacy();
        if (username != null) {
            rl = AbstractClientPlayer.getLocationSkin(username);
            AbstractClientPlayer.getDownloadImageSkin(rl, username);
        }
        mc.renderEngine.bindTexture(rl);

        int sourceSize = 8;
        int targetSize = 16;

        this.drawScaledCustomSizeModalRect(this.x + 5, this.y + 5, 8, 8, sourceSize, sourceSize, targetSize, targetSize, 64, 32);
        this.drawScaledCustomSizeModalRect(this.x + 4, this.y + 4, 40, 8, sourceSize, sourceSize, targetSize + 2, targetSize + 2, 64, 32);
    }
}
