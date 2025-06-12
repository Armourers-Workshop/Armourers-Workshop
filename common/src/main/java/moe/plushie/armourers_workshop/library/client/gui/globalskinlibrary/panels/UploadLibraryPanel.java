package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextView;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibrarySettingWindow;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import moe.plushie.armourers_workshop.library.network.UploadSkinPacket;
import moe.plushie.armourers_workshop.library.network.UploadSkinPrePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class UploadLibraryPanel extends AbstractLibraryPanel {

    private UILabel warningLabel;
    private UITextField textName;
    private UITextField textTags;
    private UITextView textDescription;
    private UIButton buttonUpload;
    private UICheckBox fileOptionsBox;

    private NSString error = null;
    private boolean isUploading = false;

    private final GlobalSkinLibrary library = GlobalSkinLibrary.getInstance();

    public UploadLibraryPanel() {
        super("skin-library-global.upload", GlobalSkinLibraryWindow.Page.SKIN_UPLOAD::equals);
        this.setup();
    }

    private void setup() {
        float width = bounds().width();
        float height = bounds().height();

        float inputWidth = width - 15 - 162;

        textName = addTextField(5, 15, inputWidth, 16, "enterName");
        textName.setMaxLength(80);

        textTags = addTextField(5, 45, inputWidth, 16, "enterTags");
        textTags.setMaxLength(32);

        textDescription = addTextView(5, 75, inputWidth, height - 75 - 40 - 12, "enterDescription");
        textDescription.setMaxLength(255);

        buttonUpload = addTextButton(28, height - 28, 96, 18, "buttonUpload", UploadLibraryPanel::upload);
        buttonUpload.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        buttonUpload.setEnabled(false);

        fileOptionsBox = new UICheckBox(new CGRect(5, textDescription.frame().maxY() + 6, inputWidth, 10));
        fileOptionsBox.setTitle(getDisplayText("fileOptions"));
        fileOptionsBox.setTitleColor(UIColor.WHITE);
        fileOptionsBox.setSelected(GlobalSkinLibrarySettingWindow.hasChanges());
        fileOptionsBox.addTarget(this, UIControl.Event.VALUE_CHANGED, UploadLibraryPanel::changeFileOptions);
        fileOptionsBox.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        addSubview(fileOptionsBox);

        addLabel(5, 5, inputWidth, 10, getDisplayText("skinName"));
        addLabel(5, 35, inputWidth, 10, getDisplayText("skinTags"));
        addLabel(5, 65, inputWidth, 10, getDisplayText("skinDescription"));

        warningLabel = addLabel(width - 162 - 5, 5, 162, height - 90, getWarningMessage());
        warningLabel.setNumberOfLines(0);
        warningLabel.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
        warningLabel.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);

        var bg1 = new UIImageView(new CGRect(width - 18 * 9 - 5, height - 82, 162, 76));
        bg1.setImage(UIImage.of(ModTextures.GLOBAL_SKIN_LIBRARY).uv(0, 180).build());
        bg1.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
        bg1.setOpaque(true);
        insertViewAtIndex(bg1, 0);

        var bg2 = new UIImageView(new CGRect(5, height - 28, 18, 18));
        var bg3 = new UIImageView(new CGRect(129, height - 32, 26, 26));
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
        boolean flags = Strings.isNotBlank(textName.text()) && !SkinDescriptor.of(getInputStack()).isEmpty() && !isUploading;
        this.buttonUpload.setEnabled(flags);
    }

    private UITextField addTextField(float x, float y, float width, float height, String key) {
        var textField = new UITextField(new CGRect(x, y, width, height));
        textField.setPlaceholder(getDisplayText(key));
        textField.setMaxLength(255);
        textField.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(textField);
        return textField;
    }

    private UITextView addTextView(float x, float y, float width, float height, String key) {
        var textField = new UITextView(new CGRect(x, y, width, height));
        textField.setPlaceholder(getDisplayText(key));
        textField.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        textField.setMaxLength(255);
        addSubview(textField);
        return textField;
    }

    private UILabel addLabel(float x, float y, float width, float height, NSString message) {
        var label = new UILabel(new CGRect(x, y, width, height));
        label.setText(message);
        label.setTextColor(UIColor.WHITE);
        label.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(label);
        return label;
    }

    private UIButton addTextButton(float x, float y, float width, float height, String key, BiConsumer<UploadLibraryPanel, UIControl> handler) {
        var button = new UIButton(new CGRect(x, y, width, height));
        button.setTitle(getDisplayText(key), UIControl.State.NORMAL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        return button;
    }

    private void changeFileOptions(UIControl sender) {
        var dialog = new GlobalSkinLibrarySettingWindow();
        dialog.setTitle(getDisplayText("setting.title"));
        dialog.sizeToFit();
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                GlobalSkinLibrarySettingWindow.setChanges(dialog.properties());
                fileOptionsBox.setSelected(GlobalSkinLibrarySettingWindow.hasChanges());
            }
        });
        fileOptionsBox.setSelected(GlobalSkinLibrarySettingWindow.hasChanges());
    }

    private void upload(UIControl sender) {
        var descriptor = SkinDescriptor.of(getInputStack());
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin == null) {
            onUploadFailed(getDisplayText("error.notSkin"));
            return;
        }
        if (Strings.isBlank(textName.text())) {
            onUploadFailed(getDisplayText("error.notSkinName"));
            return;
        }
        // we can't upload a readonly skin.
        var uploadOptions = getUploadOptions(bakedSkin.skin());
        if (bakedSkin.skin().settings().isEncrypted()) {
            onUploadFailed(getDisplayText("error.notSupported"));
            return;
        }
        if (isUploading) {
            return;
        }
        this.isUploading = true;
        // we need to check this user the global skin upload permission in the server first.
        NetworkManager.sendToServer(new UploadSkinPrePacket(), (result, exception) -> Minecraft.getInstance().execute(() -> {
            if (exception != null || result == null || !result) {
                onUploadFailed(getDisplayText("error.notPermission"));
                return;
            }
            // upload now
            library.uploadSkin(textName.text().trim(), textDescription.text().trim(), bakedSkin.skin(), uploadOptions, (result1, exception1) -> {
                if (exception1 != null) {
                    onUploadFailed(new NSString(exception1.toString()));
                } else {
                    onUploadFinish();
                }
            });
        }));
    }

    private SkinFileOptions getUploadOptions(Skin skin) {
        // only upgrade the version if necessary.
        var options = GlobalSkinLibrarySettingWindow.fileOptions();
        if (skin.fileVersion() < SkinSerializer.Versions.V20 && options.fileVersion() < SkinSerializer.Versions.V20) {
            return null;
        }
        options.setCompressed(true); // the global skin library always compress.
        return options;
    }

    private void onUploadFinish() {
        textName.setText("");
        textTags.setText("");
        textDescription.setText("");
        isUploading = false;
        router.showNewHome();
        NetworkManager.sendToServer(new UploadSkinPacket());
    }

    private void onUploadFailed(NSString message) {
        error = message;
        isUploading = false;
        if (warningLabel != null) {
            warningLabel.setText(getWarningMessage());
        }
    }

    private NSString getWarningMessage() {
        var message = new NSMutableString("");
        message.append(getDisplayText("label.upload_warning"));
        message.append("\n\n");

        if (error != null) {
            message.append("§cError: " + error.contents() + "§r");
            message.append("\n\n");
        }
        return message;
    }

    private ItemStack getInputStack() {
        return getMenu().map(GlobalSkinLibraryMenu::inputStack).orElse(ItemStack.EMPTY);
    }

    private Optional<GlobalSkinLibraryMenu> getMenu() {
        if (router != null) {
            return Optional.ofNullable(router.menu());
        }
        return Optional.empty();
    }
}
