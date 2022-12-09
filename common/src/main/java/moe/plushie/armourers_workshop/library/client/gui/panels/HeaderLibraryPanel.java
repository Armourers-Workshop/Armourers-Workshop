package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIControl;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class HeaderLibraryPanel extends AbstractLibraryPanel {

    private final ArrayList<UIButton> rightButtons = new ArrayList<>();

    private final UIButton iconButtonHome = addRightButton(0, 0, "home", redirect(GlobalSkinLibraryWindow.Page.HOME));
    private final UIButton iconButtonMyFiles = addRightButton(0, 34, "myFiles", redirect(GlobalSkinLibraryWindow.Page.LIST_USER_SKINS));
    private final UIButton iconButtonUploadSkin = addRightButton(0, 51, "uploadSkin", "uploadSkinBan", redirect(GlobalSkinLibraryWindow.Page.SKIN_UPLOAD));
    private final UIButton iconButtonJoin = addRightButton(0, 68, "join", redirect(GlobalSkinLibraryWindow.Page.LIBRARY_JOIN));
    private final UIButton iconButtonInfo = addRightButton(0, 17, "info", redirect(GlobalSkinLibraryWindow.Page.LIBRARY_INFO));
    private final UIButton iconButtonModeration = addRightButton(0, 119, "moderation", redirect(GlobalSkinLibraryWindow.Page.LIBRARY_MODERATION));

    private PlayerTextureDescriptor playerTexture;

    public HeaderLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.header", p -> true);
        this.betaCheckUpdate();
    }

    @Override
    public void tick() {
        super.tick();
        betaCheckUpdate();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = bounds();
        int x2 = bounds.width - 4;
        for (UIButton button : rightButtons) {
            if (button.isHidden()) {
                continue;
            }
            CGRect frame = button.frame();
            button.setFrame(new CGRect(x2 - frame.width, (bounds.height - frame.height) / 2, frame.width, frame.height));
            x2 = button.frame().getMinX() - 2;
        }
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        this.renderPlayerProfile(context.poseStack, context.font.font(), Minecraft.getInstance().getUser().getGameProfile());
    }

    private void renderPlayerProfile(IPoseStack poseStack, Font font, GameProfile gameProfile) {
        if (playerTexture == null) {
            playerTexture = new PlayerTextureDescriptor(gameProfile);
        }
        RenderSystem.drawPlayerHead(poseStack, 5, 5, 16, 16, playerTexture);

        // White - not a member.
        // Yellow - Member not authenticated.
        // Green - Authenticated member.
        // Red - Missing profile info.
        CGRect rect = bounds();
        MutableComponent profile = Component.literal(" - ");
        profile.append(gameProfile.getName());
        int colour = 0xFFFFFF;
        if (!gameProfile.isLegacy()) {
            colour = 0xFFAAAA;
        }
        if (PlushieAuth.PLUSHIE_SESSION.hasServerId()) {
            colour = 0xFFFFAA;
        }
        if (PlushieAuth.PLUSHIE_SESSION.isAuthenticated()) {
            colour = 0xAAFFAA;
        }
        font.draw(poseStack.cast(), profile, 24, (rect.height - font.lineHeight) / 2f, colour);
    }

    private void betaCheckUpdate() {
        iconButtonHome.setHidden(false);
        iconButtonMyFiles.setHidden(true);
        iconButtonUploadSkin.setHidden(true);
        iconButtonJoin.setHidden(true);
        iconButtonInfo.setHidden(false);
        iconButtonModeration.setHidden(true);

        boolean isRemoteUser = PlushieAuth.isRemoteUser();
        boolean doneRemoteUserCheck = PlushieAuth.doneRemoteUserCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;

        if (doneRemoteUserCheck & !isRemoteUser) {
            iconButtonJoin.setHidden(false);
        }

        if (session.hasServerId()) {
            iconButtonMyFiles.setHidden(!isRemoteUser);
            iconButtonUploadSkin.setHidden(!isRemoteUser);
            iconButtonUploadSkin.setEnabled(session.hasPermission(PermissionSystem.PlushieAction.SKIN_UPLOAD));
            if (session.hasPermission(PermissionSystem.PlushieAction.GET_REPORT_LIST)) {
                iconButtonModeration.setHidden(false);
            }
        }

        setNeedsLayout();
    }

    private BiConsumer<HeaderLibraryPanel, UIControl> redirect(GlobalSkinLibraryWindow.Page page) {
        return (self, sender) -> {
            switch (page) {
                case HOME:
                    self.router.showNewHome();
                    break;
                case LIST_USER_SKINS:
                    if (PlushieAuth.PLUSHIE_SESSION.hasServerId()) {
                        self.router.showSkinList(PlushieAuth.PLUSHIE_SESSION.getServerId());
                    }
                    break;
                default:
                    self.router.showPage(page);
                    break;
            }
        };
    }

    private UIButton addRightButton(int u, int v, String key, BiConsumer<HeaderLibraryPanel, UIControl> handler) {
        return addRightButton(u, v, key, null, handler);
    }

    private UIButton addRightButton(int u, int v, String key, String key2, BiConsumer<HeaderLibraryPanel, UIControl> handler) {
        UIButton button = new UIButton(new CGRect(0, 0, 18, 18));
        button.setImage(ModTextures.iconImage(u, v, 16, 16, ModTextures.GLOBAL_SKIN_LIBRARY), UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.setTooltip(getDisplayText(key), UIControl.State.NORMAL);
        if (key2 != null) {
            button.setTooltip(getDisplayText(key2), UIControl.State.DISABLED);
        }
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        rightButtons.add(button);
        return button;
    }
}
