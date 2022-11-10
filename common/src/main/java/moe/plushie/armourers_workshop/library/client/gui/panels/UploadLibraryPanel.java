package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinUpload;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import moe.plushie.armourers_workshop.library.network.UploadSkinPacket;
import moe.plushie.armourers_workshop.library.network.UploadSkinPrePacket;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class UploadLibraryPanel extends AbstractLibraryPanel {

    private UILabel warningLabel;
    private UITextField textName;
    private UITextField textTags;
    private UITextView textDescription;
    private UIButton buttonUpload;

    private String error = null;
    private boolean isUploading = false;

    public UploadLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.upload", GlobalSkinLibraryWindow.Page.SKIN_UPLOAD::equals);
        this.setup();
    }

    private void setup() {
        int width = bounds().getWidth();
        int height = bounds().getHeight();

        int inputWidth = width - 15 - 162;

        textName = addTextField(5, 15, inputWidth, 16, "enterName");
        textName.setMaxLength(80);

        textTags = addTextField(5, 45, inputWidth, 16, "enterTags");
        textTags.setMaxLength(32);

        textDescription = addTextView(5, 75, inputWidth, height - 75 - 40, "enterDescription");
        textDescription.setMaxLength(255);

        buttonUpload = addTextButton(28, height - 28, 96, 18, "buttonUpload", UploadLibraryPanel::upload);
        buttonUpload.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        buttonUpload.setEnabled(false);

        addLabel(5, 5, inputWidth, 10, getDisplayText("skinName"));
        addLabel(5, 35, inputWidth, 10, getDisplayText("skinTags"));
        addLabel(5, 65, inputWidth, 10, getDisplayText("skinDescription"));

        warningLabel = addLabel(width - 162 - 5, 5, 162, height - 90, getWarningMessage());
        warningLabel.setNumberOfLines(0);
        warningLabel.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
        warningLabel.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);

        UIImageView bg1 = new UIImageView(new CGRect(width - 18 * 9 - 5, height - 82, 162, 76));
        bg1.setImage(UIImage.of(ModTextures.GLOBAL_SKIN_LIBRARY).uv(0, 180).build());
        bg1.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
        bg1.setOpaque(true);
        insertViewAtIndex(bg1, 0);

        UIImageView bg2 = new UIImageView(new CGRect(5, height - 28, 18, 18));
        UIImageView bg3 = new UIImageView(new CGRect(129, height - 32, 26, 26));
        bg2.setOpaque(true);
        bg3.setOpaque(true);
        bg2.setImage(UIImage.of(ModTextures.GLOBAL_SKIN_LIBRARY).uv(0, 162).build());
        bg3.setImage(UIImage.of(ModTextures.GLOBAL_SKIN_LIBRARY).uv(18, 154).build());
        bg2.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        bg3.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        insertViewAtIndex(bg2, 0);
        insertViewAtIndex(bg3, 0);
    }

    @Override
    public void didMoveToWindow() {
        super.didMoveToWindow();
        this.getMenu().ifPresent(container -> container.setVisible(window() != null));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.buttonUpload == null) {
            return;
        }
        boolean flags = Strings.isNotBlank(textName.value()) && !SkinDescriptor.of(getInputStack()).isEmpty() && !isUploading;
        this.buttonUpload.setEnabled(flags);
    }

    private UITextField addTextField(int x, int y, int width, int height, String key) {
        UITextField textField = new UITextField(new CGRect(x, y, width, height));
        textField.setPlaceholder(getDisplayText(key));
        textField.setMaxLength(255);
        textField.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(textField);
        return textField;
    }

    private UITextView addTextView(int x, int y, int width, int height, String key) {
        UITextView textField = new UITextView(new CGRect(x, y, width, height));
        textField.setPlaceholder(getDisplayText(key));
        textField.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        textField.setMaxLength(255);
        addSubview(textField);
        return textField;
    }

    private UILabel addLabel(int x, int y, int width, int height, NSString message) {
        UILabel label = new UILabel(new CGRect(x, y, width, height));
        label.setText(message);
        label.setTextColor(UIColor.WHITE);
        label.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(label);
        return label;
    }

    private UIButton addTextButton(int x, int y, int width, int height, String key, BiConsumer<UploadLibraryPanel, UIControl> handler) {
        UIButton button = new UIButton(new CGRect(x, y, width, height));
        button.setTitle(getDisplayText(key), UIControl.State.NORMAL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        return button;
    }

    private void upload(UIControl sender) {
        BakedSkin bakedSkin = BakedSkin.of(getInputStack());
        if (bakedSkin == null) {
            onUploadFailed("Skin missing.");
            return;
        }

        if (Strings.isBlank(textName.value())) {
            onUploadFailed("Skin name missing.");
            return;
        }

        if (isUploading) {
            return;
        }

        this.isUploading = true;
        // we need to check this user the global skin upload permission in the server first.
        NetworkManager.sendToServer(new UploadSkinPrePacket(), (result, exception) -> Minecraft.getInstance().execute(() -> {
            if (exception != null || result == null || !result) {
                onUploadFailed("Authentication failed.");
                return;
            }
            // upload now
            GameProfile gameProfile = Minecraft.getInstance().getUser().getGameProfile();
            Thread thread = new Thread(() -> uploadSkin(gameProfile, bakedSkin.getSkin()));
            thread.start();
        }));
    }

    public void uploadSkin(GameProfile profile, Skin skin) {
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        if (!plushieSession.isAuthenticated()) {
            JsonObject jsonObject = PlushieAuth.authenticateUser(profile.getName(), profile.getId().toString());
            plushieSession.authenticate(jsonObject);
        }

        if (!plushieSession.isAuthenticated()) {
            Minecraft.getInstance().execute(() -> onUploadFailed("Authentication failed."));
            return;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SkinIOUtils.saveSkinToStream(outputStream, skin);
        byte[] fileBytes = outputStream.toByteArray();
        StreamUtils.closeQuietly(outputStream);
        new GlobalTaskSkinUpload(fileBytes, textName.value().trim(), textDescription.value().trim()).createTaskAndRun(new FutureCallback<GlobalTaskSkinUpload.Result>() {

            @Override
            public void onSuccess(GlobalTaskSkinUpload.Result result) {
                Minecraft.getInstance().execute(() -> {
                    if (result.getResult() == GlobalTaskResult.SUCCESS) {
                        onUploadFinish();
                    } else {
                        onUploadFailed(result.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Minecraft.getInstance().execute(() -> onUploadFailed(t.toString()));
            }
        });
    }

    private void onUploadFinish() {
        textName.setValue("");
        textTags.setValue("");
        textDescription.setValue("");
        isUploading = false;
        router.showNewHome();
        NetworkManager.sendToServer(new UploadSkinPacket());
    }

    private void onUploadFailed(String message) {
        error = message;
        isUploading = false;
        if (warningLabel != null) {
            warningLabel.setText(getWarningMessage());
        }
    }

    private NSString getWarningMessage() {
        NSMutableString message = new NSMutableString("");
        message.append(getDisplayText("label.upload_warning"));
        message.append("\n\n");

        if (Strings.isNotBlank(error)) {
            message.append("§cError: " + error + "§r");
            message.append("\n\n");
        }
        return message;
    }

    private ItemStack getInputStack() {
        return getMenu().map(GlobalSkinLibraryMenu::getInputStack).orElse(ItemStack.EMPTY);
    }

    private Optional<GlobalSkinLibraryMenu> getMenu() {
        if (router != null) {
            return Optional.ofNullable(router.menu());
        }
        return Optional.empty();
    }
}
