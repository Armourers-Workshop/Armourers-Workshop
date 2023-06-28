package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextView;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.util.Strings;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SkinEditLibraryPanel extends AbstractLibraryPanel {

    private UITextField textName;
    private UITextField textTags;
    private UITextView textDescription;

    private UIButton buttonUpdate;
    private UIButton buttonDelete;

    private ServerSkin entry;
    private GlobalSkinLibraryWindow.Page returnPage;

    public SkinEditLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.edit", GlobalSkinLibraryWindow.Page.SKIN_EDIT::equals);
        this.setup();
    }

    private void setup() {
        int width = bounds().getWidth();
        int height = bounds().getHeight();
        int inputWidth = width - 15 - 162;
        textName = addTextField(5, 15, inputWidth, 12, "enterName");
        textName.setMaxLength(80);

        textTags = addTextField(5, 45, inputWidth, 12, "enterTags");
        textTags.setMaxLength(32);

        textDescription = addTextView(5, 75, inputWidth, height - 75 - 40, "enterDescription");
        textDescription.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        textDescription.setMaxLength(255);

        addLabel(5, 5, inputWidth, 10, getDisplayText("skinName"));
        addLabel(5, 35, inputWidth, 10, getDisplayText("skinTags"));
        addLabel(5, 65, inputWidth, 10, getDisplayText("skinDescription"));

        buttonUpdate = addTextButton(5, height - 25, 100, 20, "buttonUpdate", SkinEditLibraryPanel::updateSkin);
        buttonUpdate.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);

        buttonDelete = addTextButton(width - 105, height - 25, 100, 20, "buttonDelete", SkinEditLibraryPanel::removeSkinPre);
        buttonDelete.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
    }

    @Override
    public void tick() {
        super.tick();
        if (textName != null) {
            buttonUpdate.setEnabled(Strings.isNotBlank(textName.value()));
        }
    }

    public void reloadData(ServerSkin entry, GlobalSkinLibraryWindow.Page returnPage) {
        this.entry = entry;
        this.returnPage = returnPage;
        this.textName.setValue(entry.getName());
        this.textDescription.setValue(entry.getDescription());
        this.textTags.setValue("");
    }

    private void updateSkin(UIControl button) {
        String name = textName.value().trim();
        String description = textDescription.value().trim();
        if (name.isEmpty()) {
            ModLog.warn("Can't set the skin name to empty");
            return;
        }
        // not change, ignore
        if (name.equals(entry.getName()) && description.equals(entry.getDescription())) {
            backToPage(false);
            return;
        }
        buttonUpdate.setEnabled(false);
        entry.update(name, description, (result, exception) -> {
            buttonUpdate.setEnabled(true);
            if (exception == null) {
                backToPage(false);
            }
        });
    }

    private void removeSkinPre(UIControl button) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(getDisplayText("dialog.delete.title"));
        dialog.setMessageColor(new UIColor(0xffff5555));
        dialog.setConfirmText(getDisplayText("dialog.delete.ok"));
        dialog.setCancelText(getDisplayText("dialog.delete.cancel"));
        dialog.setMessage(getDisplayText("dialog.delete.message", entry.getName()));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                removeSkin(button);
            }
        });
    }

    private void removeSkin(UIControl button) {
        entry.remove((result, exception) -> {
            if (exception == null) {
                backToPage(true);
            }
        });
    }

    private void backToPage(boolean removed) {
        if (removed) {
            router.skinDidChange(entry.getId(), null);
        } else {
            router.skinDidChange(entry.getId(), entry);
        }
        router.showPage(returnPage);
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
        textField.setMaxLength(255);
        addSubview(textField);
        return textField;
    }

    protected UILabel addLabel(int x, int y, int width, int height, NSString message) {
        UILabel label = new UILabel(new CGRect(x, y, width, height));
        label.setText(message);
        label.setTextColor(UIColor.WHITE);
        label.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(label);
        return label;
    }

    private UIButton addTextButton(int x, int y, int width, int height, String key, BiConsumer<SkinEditLibraryPanel, UIControl> handler) {
        UIButton button = new UIButton(new CGRect(x, y, width, height));
        button.setTitle(getDisplayText(key), UIControl.State.NORMAL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        return button;
    }
}
