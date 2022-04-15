package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWConfirmDialog;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinDelete;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinEdit;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.widget.SkinFileList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.logging.log4j.util.Strings;

@OnlyIn(Dist.CLIENT)
public class GlobalLibrarySkinEditPanel extends GlobalLibraryAbstractPanel {

    private AWTextField textName;
    private AWTextField textTags;
    private AWTextField textDescription;

    private ExtendedButton buttonUpdate;
    private ExtendedButton buttonDelete;

    private SkinFileList.Entry entry;
    private GlobalSkinLibraryScreen.Page returnPage;

    public GlobalLibrarySkinEditPanel() {
        super("inventory.armourers_workshop.skin-library-global.edit", GlobalSkinLibraryScreen.Page.SKIN_EDIT::equals);
    }

    @Override
    protected void init() {
        super.init();

        int inputWidth = width - 15 - 162;
        this.textName = addTextField(leftPos + 5, topPos + 35, inputWidth, 12, "enterName");
        this.textName.setMaxLength(80);

        this.textTags = addTextField(leftPos + 5, topPos + 65, inputWidth, 12, "enterTags");
        this.textTags.setMaxLength(32);

        this.textDescription = addTextField(leftPos + 5, topPos + 95, inputWidth, height - 95 - 40, "enterDescription");
        this.textDescription.setMaxLength(255);
        this.textDescription.setSingleLine(false);

        this.addLabel(leftPos + 5, topPos + 25, inputWidth, 10, getDisplayText("skinName"));
        this.addLabel(leftPos + 5, topPos + 55, inputWidth, 10, getDisplayText("skinTags"));
        this.addLabel(leftPos + 5, topPos + 85, inputWidth, 10, getDisplayText("skinDescription"));

        this.buttonUpdate = addButton(new ExtendedButton(leftPos + 5, topPos + height - 25, 100, 20, getDisplayText("buttonUpdate"), this::updateSkin));
        this.buttonDelete = addButton(new ExtendedButton(leftPos + width - 105, topPos + height - 25, 100, 20, getDisplayText("buttonDelete"), this::removeSkinPre));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.textName != null) {
            this.buttonUpdate.active = Strings.isNotBlank(textName.getValue());
        }
    }

    public void reloadData(SkinFileList.Entry entry, GlobalSkinLibraryScreen.Page returnPage) {
        this.entry = entry;
        this.returnPage = returnPage;
        this.textName.setValue(entry.name);
        this.textDescription.setValue(entry.description);
        this.textTags.setValue("");
    }

    @Override
    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
    }

    private void updateSkin(Button button) {
        String name = textName.getValue().trim();
        String description = textDescription.getValue().trim();
        if (name.isEmpty()) {
            ModLog.warn("Can't set the skin name to empty");
            return;
        }
        // not change, ignore
        if (name.equals(entry.name) && description.equals(entry.description)) {
            backToPage(false);
            return;
        }
        buttonUpdate.active = false;
        new GlobalTaskSkinEdit(entry.id, name, description, isModerator()).createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getInstance().execute(() -> {
                    buttonUpdate.active = true;
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

    private void removeSkinPre(Button button) {
        AWConfirmDialog dialog = new AWConfirmDialog(getDisplayText("dialog.delete.title"));
        dialog.setMessageColor(0xffff5555);
        dialog.setConfirmText(getDisplayText("dialog.delete.ok"));
        dialog.setCancelText(getDisplayText("dialog.delete.cancel"));
        dialog.setMessage(getDisplayText("dialog.delete.message", entry.name));
        router.showDialog(dialog, r -> {
            if (!r.isCancelled()) {
                removeSkin(button);
            }
        });
    }

    private void removeSkin(Button button) {
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
        // TODO: reload all data
//       if (returnScreen == Screen.HOME) {
//            ((GuiGlobalLibrary) parent).panelHome.updateSkinPanels();
//        }
//        if (returnScreen == Screen.SEARCH) {
//            ((GuiGlobalLibrary) parent).panelSearchResults.refresh();
//        }
//        if (returnScreen == Screen.USER_SKINS) {
//            ((GuiGlobalLibrary) parent).panelUserSkins.refresh();
//        }
//        ((GuiGlobalLibrary) parent).switchScreen(returnScreen);
        router.showPage(returnPage);
    }

    private AWTextField addTextField(int x, int y, int width, int height, String key) {
        AWTextField textField = new AWTextField(font, x, y, width, height, getDisplayText(key));
        textField.setMaxLength(255);
        addWidget(textField);
        return textField;
    }

    private boolean isModerator() {
        if (entry != null) {
            return !PlushieAuth.PLUSHIE_SESSION.isOwner(entry.userId);
        }
        return true;
    }
}
