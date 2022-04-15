package moe.plushie.armourers_workshop.library.gui.panels;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageExtendedButton;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;

public class GlobalLibraryHeaderPanel extends GlobalLibraryAbstractPanel {

    private final ArrayList<Button> rightButtons = new ArrayList<>();
    private PlayerTextureDescriptor playerTexture;

    private AWImageButton iconButtonHome;
    private AWImageButton iconButtonMyFiles;
    private AWImageButton iconButtonUploadSkin;
    private AWImageButton iconButtonJoin;
    private AWImageButton iconButtonInfo;
    private AWImageButton iconButtonModeration;

    public GlobalLibraryHeaderPanel() {
        super("inventory.armourers_workshop.skin-library-global.header", p -> true);
    }

    @Override
    protected void init() {
        this.rightButtons.clear();
        super.init();

        this.iconButtonHome = addRightButton(0, 0, 18, 18, "home", redirect(GlobalSkinLibraryScreen.Page.HOME));
        this.iconButtonMyFiles = addRightButton(0, 34, 18, 18, "myFiles", redirect(GlobalSkinLibraryScreen.Page.USER_SKINS));
        this.iconButtonUploadSkin = addRightButton(0, 51, 18, 18, "uploadSkin", "uploadSkinBan", redirect(GlobalSkinLibraryScreen.Page.SKIN_UPLOAD));

        this.iconButtonJoin = addRightButton(0, 68, 18, 18, "join", redirect(GlobalSkinLibraryScreen.Page.JOIN));
        this.iconButtonInfo = addRightButton(0, 17, 18, 18, "info", redirect(GlobalSkinLibraryScreen.Page.INFO));

        this.iconButtonModeration = addRightButton(0, 119, 18, 18, "moderation", redirect(GlobalSkinLibraryScreen.Page.HOME));

        this.betaCheckUpdate();
    }

    @Override
    public void tick() {
        super.tick();
        this.betaCheckUpdate();
    }

    private void betaCheckUpdate() {
        iconButtonHome.visible = true;
        iconButtonMyFiles.visible = false;
        iconButtonUploadSkin.visible = false;
        iconButtonJoin.visible = false;
        iconButtonInfo.visible = true;
        iconButtonModeration.visible = false;

        boolean isRemoteUser = PlushieAuth.isRemoteUser();
        boolean doneRemoteUserCheck = PlushieAuth.doneRemoteUserCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;

        if (doneRemoteUserCheck & !isRemoteUser) {
            iconButtonJoin.visible = true;
        }

        if (session.hasServerId()) {
            iconButtonMyFiles.visible = isRemoteUser;
            iconButtonUploadSkin.visible = isRemoteUser;
            iconButtonUploadSkin.setEnabled(session.hasPermission(PermissionSystem.PlushieAction.SKIN_UPLOAD));
            if (session.hasPermission(PermissionSystem.PlushieAction.GET_REPORT_LIST)) {
                iconButtonModeration.visible = true;
            }
        }

        rightButtons.forEach(b -> b.visible = true); // test

        int x2 = leftPos + width - 4;
        for (Button button : rightButtons) {
            if (!button.visible) {
                continue;
            }
            button.x = x2 - button.getWidth();
            button.y = topPos + (height - button.getHeight()) / 2;
            x2 = button.x - 2;
        }
    }

    private Button.IPressable redirect(GlobalSkinLibraryScreen.Page page) {
        return button -> {
            switch (page) {
                case HOME:
                    router.showNewHome();
                    break;
                case USER_SKINS:
                    if (PlushieAuth.PLUSHIE_SESSION.hasServerId()) {
                        router.showSkinList(PlushieAuth.PLUSHIE_SESSION.getServerId());
                    }
                    break;
                default:
                    router.showPage(page);
                    break;
            }
        };
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        if (minecraft != null) {
            this.renderPlayerProfile(matrixStack, minecraft.player);
        }
    }

    private void renderPlayerProfile(MatrixStack matrixStack, ClientPlayerEntity player) {
        if (player == null) {
            return;
        }
        if (playerTexture == null) {
            playerTexture = new PlayerTextureDescriptor(player.getGameProfile());
        }
        RenderUtils.drawPlayerHead(matrixStack, leftPos + 5, topPos + 5, 16, 16, playerTexture);

        // White - not a member.
        // Yellow - Member not authenticated.
        // Green - Authenticated member.
        // Red - Missing profile info.
        StringTextComponent profile = new StringTextComponent(" - ");
        profile.append(player.getDisplayName());
        int colour = 0xFFFFFF;
        if (!player.getGameProfile().isLegacy()) {
            colour = 0xFFAAAA;
        }
        if (PlushieAuth.PLUSHIE_SESSION.hasServerId()) {
            colour = 0xFFFFAA;
        }
        if (PlushieAuth.PLUSHIE_SESSION.isAuthenticated()) {
            colour = 0xAAFFAA;
        }
        font.draw(matrixStack, profile, leftPos + 24, topPos + (height - font.lineHeight) / 2f, colour);
    }

    private AWImageButton addRightButton(int u, int v, int width, int height, String key, Button.IPressable handler) {
        return addRightButton(u, v, width, height, key, null, handler);
    }

    private AWImageButton addRightButton(int u, int v, int width, int height, String key, String key2, Button.IPressable handler) {
        ITextComponent tooltip = getDisplayText(key);
        AWImageButton button = new AWImageExtendedButton(0, 0, width, height, u, v, RenderUtils.TEX_GLOBAL_SKIN_LIBRARY, handler, this::addHoveredButton, tooltip);
        if (key2 != null) {
            button.setDisabledMessage(getDisplayText(key2));
        }
        button.setIconSize(16, 16);
        addButton(button);
        rightButtons.add(button);
        return button;
    }
}
