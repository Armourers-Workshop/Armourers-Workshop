package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinItemList;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinDelete;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinEdit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.util.Strings;

import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class SkinEditLibraryPanel extends AbstractLibraryPanel {

    private UITextField textName;
    private UITextField textTags;
    private UITextView textDescription;

    private UIButton buttonUpdate;
    private UIButton buttonDelete;

    private SkinItemList.Entry entry;
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

    public void reloadData(SkinItemList.Entry entry, GlobalSkinLibraryWindow.Page returnPage) {
        this.entry = entry;
        this.returnPage = returnPage;
        this.textName.setValue(entry.name);
        this.textDescription.setValue(entry.description);
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
        if (name.equals(entry.name) && description.equals(entry.description)) {
            backToPage(false);
            return;
        }
        buttonUpdate.setEnabled(false);
        new GlobalTaskSkinEdit(entry.id, name, description, isModerator()).createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getInstance().execute(() -> {
                    buttonUpdate.setEnabled(true);
                    if (result.has("valid") & result.has("action")) {
                        String action = result.get("action").getAsString();
                        boolean valid = result.get("valid").getAsBoolean();
                        if (action.equals("user-skin-edit")) {
                            entry.name = name;
                            entry.description = description;
                            backToPage(false);
                        } else {
                            ModLog.warn("Server send unknown action: " + action);
                        }
                    } else {
                        ModLog.error("Server returned invalid responce.");
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void removeSkinPre(UIControl button) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(getDisplayText("dialog.delete.title"));
        dialog.setMessageColor(new UIColor(0xffff5555));
        dialog.setConfirmText(getDisplayText("dialog.delete.ok"));
        dialog.setCancelText(getDisplayText("dialog.delete.cancel"));
        dialog.setMessage(getDisplayText("dialog.delete.message", entry.name));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                removeSkin(button);
            }
        });
    }

    private void removeSkin(UIControl button) {
        new GlobalTaskSkinDelete(entry.id, isModerator()).createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getInstance().execute(() -> {
                    if (result.has("valid") & result.has("action")) {
                        String action = result.get("action").getAsString();
                        boolean valid = result.get("valid").getAsBoolean();
                        if (action.equals("user-skin-delete")) {
                            backToPage(true);
                        } else {
                            ModLog.warn("Server send unknown action: " + action);
                        }
                    } else {
                        ModLog.error("Server returned invalid response.");
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void backToPage(boolean removed) {
        if (removed) {
            router.skinDidChange(entry.id, null);
        } else {
            router.skinDidChange(entry.id, entry);
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

    private boolean isModerator() {
        if (entry != null) {
            return !PlushieAuth.PLUSHIE_SESSION.isOwner(entry.userId);
        }
        return true;
    }
}
