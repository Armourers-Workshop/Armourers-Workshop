package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWExtendedButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWReportDialog;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinFileList;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinRatingButton;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.PlushieUser;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinReport;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskUserSkinRate;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskUserSkinRating;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

@Environment(value = EnvType.CLIENT)
public class SkinDetailLibraryPanel extends AbstractLibraryPanel {

    private AWExtendedButton buttonBack;
    private AWExtendedButton buttonDownload;
    private AWExtendedButton buttonUserSkins;
    private AWExtendedButton buttonEditSkin;
    private AWExtendedButton buttonReportSkin;

    private SkinRatingButton buttonStarRating;

    private int userRating = 0;
    private boolean doneRatingCheck = false;

    private Component message;
    private SkinFileList.Entry entry;
    private GlobalSkinLibraryScreen.Page returnPage;
    private PlayerTextureDescriptor playerTexture = PlayerTextureDescriptor.EMPTY;

    public SkinDetailLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.skinInfo", GlobalSkinLibraryScreen.Page.SKIN_DETAIL::equals);
    }

    @Override
    protected void init() {
        super.init();

        int minX = leftPos + 2;
        int maxX = leftPos + width - 2;
        int midX = minX + 185 + 2;

        this.buttonUserSkins = addTextButton(minX + 2, topPos + 4, 26, 26, "", this::searchUser);

        this.buttonStarRating = new SkinRatingButton(leftPos + 191, topPos + 4, 16, 16, this::updateSkinRating);
        this.buttonStarRating.setValue(userRating);
        this.addButton(buttonStarRating);

        this.buttonDownload = addTextButton(midX + 2, topPos + height - 38, 76, 16, "downloadSkin", this::downloadSkin);
        this.buttonReportSkin = addTextButton(maxX - 80, topPos + height - 38, 76, 16, "button.report_skin", this::reportSkinPre);

        this.buttonBack = addTextButton(minX, topPos + height - 18, 80, 16, "back", this::backToHome);
        this.buttonEditSkin = addTextButton(maxX - 80, topPos + height - 18, 80, 16, "editSkin", this::editSkin);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.buttonUserSkins == null) {
            return;
        }
        this.buttonEditSkin.visible = false;
        if (isOwner()) {
            this.buttonEditSkin.visible = PlushieAuth.PLUSHIE_SESSION.hasPermission(PermissionSystem.PlushieAction.SKIN_OWNER_EDIT);
        } else {
            this.buttonEditSkin.visible = PlushieAuth.PLUSHIE_SESSION.hasPermission(PermissionSystem.PlushieAction.SKIN_MOD_EDIT);
        }
        this.buttonDownload.visible = PlushieAuth.PLUSHIE_SESSION.hasPermission(PermissionSystem.PlushieAction.SKIN_DOWNLOAD);
    }

    public void reloadData(SkinFileList.Entry entry, GlobalSkinLibraryScreen.Page returnPage) {
        this.returnPage = returnPage;
        this.userRating = 0;
        this.doneRatingCheck = false;
        this.buttonDownload.active = true;
        this.reloadUI(entry);
        this.updateSkinJson();
        if (PlushieAuth.isRemoteUser()) {
            this.checkIfLiked();
        }
    }

    public void reloadUI(SkinFileList.Entry entry) {
        this.entry = entry;
        this.message = getMessage();
        this.playerTexture = PlayerTextureDescriptor.EMPTY;
        this.updateLikeButtons();
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackgroundLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        this.drawSkinInfo(matrixStack, leftPos + 2, topPos + 34, 185, height - 54, mouseX, mouseY, partialTicks);
        this.drawPreviewBox(matrixStack, leftPos + 189, topPos + 2, width - 189 - 2, height - 22, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        // the user's avatar needs to be rendering on button top, so it needs to be post-rendered.
        this.drawUserbox(matrixStack, leftPos + 2, topPos + 2, 185, 30, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // all content will render on the background layer
    }

    public void drawUserbox(PoseStack matrixStack, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        fillGradient(matrixStack, boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        if (playerTexture.isEmpty()) {
            PlushieUser user = GlobalSkinLibraryUtils.getUserInfo(entry.userId);
            if (user != null) {
                playerTexture = new PlayerTextureDescriptor(new GameProfile(null, user.getUsername()));
            }
        }
        if (Strings.isNotBlank(playerTexture.getName())) {
            font.draw(matrixStack, getDisplayText("uploader", playerTexture.getName()), boxX + 32, boxY + 12, 0xffeeeeee);
            RenderSystem.enableAlphaTest();
        }
        RenderUtils.drawPlayerHead(matrixStack, boxX + 7, boxY + 7, 16, 16, playerTexture);
    }

    public void drawSkinInfo(PoseStack matrixStack, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        fillGradient(matrixStack, boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        if (message == null) {
            return;
        }
        RenderUtils.enableScissor(boxX, boxY, boxWidth, boxHeight);
        RenderUtils.drawText(matrixStack, font, message, boxX + 2, boxY + 2, boxWidth - 4, 0, 0xffeeeeee);
        RenderUtils.disableScissor();
    }

    public void drawPreviewBox(PoseStack matrixStack, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        fillGradient(matrixStack, boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        BakedSkin bakedSkin = BakedSkin.of(entry.descriptor);
        if (bakedSkin != null) {
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkin(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, boxX, boxY, 100, boxWidth, boxHeight, 160, 45, 0, matrixStack, buffers);
            buffers.endBatch();
        }
    }

    private AWExtendedButton addTextButton(int x, int y, int width, int height, String key, Button.OnPress handler) {
        Component title = TextComponent.EMPTY;
        if (!key.isEmpty()) {
            title = getDisplayText(key);
        }
        AWExtendedButton button = new AWExtendedButton(x, y, width, height, title, handler);
        addButton(button);
        return button;
    }

    private void backToHome(Button button) {
        router.showPage(returnPage);
    }

    private void searchUser(Button button) {
        PlushieUser plushieUser = GlobalSkinLibraryUtils.getUserInfo(entry.userId);
        if (plushieUser != null) {
            router.showSkinList(entry.userId);
        }
    }

    private void editSkin(Button button) {
        router.showSkinEdit(entry, returnPage);
    }

    private void updateSkinRating(Button button) {
        setSkinRating(buttonStarRating.getValue());
    }

    private void reportSkinPre(Button button) {
        GlobalTaskSkinReport.SkinReport.SkinReportType[] reportTypes = GlobalTaskSkinReport.SkinReport.SkinReportType.values();
        AWReportDialog dialog = new AWReportDialog(getDisplayText("dialog.report_skin.title"));
        dialog.setMessageColor(0xff7f0000);
        dialog.setMessage(getDisplayText("dialog.report_skin.label.report_warning"));
        dialog.setPlaceholderText(getDisplayText("dialog.report_skin.optional_message"));
        dialog.setReportTypes(Arrays.stream(reportTypes).map(t -> TranslateUtils.title(t.getLangKey())).collect(Collectors.toList()));
        router.showDialog(dialog, r -> {
            if (!r.isCancelled()) {
                GlobalTaskSkinReport.SkinReport.SkinReportType reportType = reportTypes[r.getReportType()];
                GlobalTaskSkinReport.SkinReport report = new GlobalTaskSkinReport.SkinReport(entry.id, reportType, r.getText());
                reportSkin(report);
            }
        });
    }

    private void reportSkin(GlobalTaskSkinReport.SkinReport report) {
        ModLog.debug("report skin: '{}', text: '{}', type: {}", report.getSkinId(), report.getMessage(), report.getReportType());
        new GlobalTaskSkinReport(report).createTaskAndRun(new FutureCallback<GlobalTaskSkinReport.SkinReportResult>() {

            @Override
            public void onSuccess(GlobalTaskSkinReport.SkinReportResult result) {
                ModLog.debug("skin report sent.");
                // NO-OP
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void downloadSkin(Button button) {
        int skinId = entry.id;
        String idString = String.format("%04d", skinId);
        String skinName = entry.name;
        File path = new File(EnvironmentManager.getSkinLibraryDirectory(), "downloads");
        File target = new File(path, SkinIOUtils.makeFileNameValid(idString + " - " + skinName + ".armour"));
        buttonDownload.active = false;
        SkinLoader.getInstance().loadSkin(entry.descriptor.getIdentifier(), (skin, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            if (SkinIOUtils.saveSkinToFile(target, skin)) {
                SkinLibraryManager.getClient().getLocalSkinLibrary().reload();
            }
        });
    }

    private void updateLikeButtons() {
        buttonStarRating.visible = false;
        if (doneRatingCheck) {
            buttonStarRating.setValue(userRating);
            buttonStarRating.visible = true;
        }
    }

    private void checkIfLiked() {
        new GlobalTaskUserSkinRating(entry.id).createTaskAndRun(new FutureCallback<GlobalTaskUserSkinRating.UserSkinRatingResult>() {

            @Override
            public void onSuccess(GlobalTaskUserSkinRating.UserSkinRatingResult result) {
                Minecraft.getInstance().execute(() -> {
                    if (result.getResult() == GlobalTaskResult.SUCCESS) {
                        userRating = result.getRating();
                        doneRatingCheck = true;
                        reloadUI(entry);
                    } else {
                        ModLog.warn(result.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setSkinRating(int rating) {
        boolean isNew = userRating == 0;
        userRating = rating;
        new GlobalTaskUserSkinRate(entry.id, rating).createTaskAndRun(new FutureCallback<GlobalTaskUserSkinRate.UserSkinRateResult>() {

            @Override
            public void onSuccess(GlobalTaskUserSkinRate.UserSkinRateResult result) {
                Minecraft.getInstance().execute(() -> {
                    if (result.getResult() == GlobalTaskResult.SUCCESS) {
                        if (isNew) {
                            entry.ratingCount += 1;
                        }
                        entry.rating = result.getNewRating();
                        reloadUI(entry);
                    } else {
                        ModLog.warn(result.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateSkinJson() {
//        // TODO: imp
//        new GlobalTaskGetSkinInfo(entry.id).createTaskAndRun(new FutureCallback<JsonObject>() {
//
//            @Override
//            public void onSuccess(JsonObject result) {
//                if (result == null) {
//                    return;
//                }
//                entry = new SkinFileList.Entry(result);
//                Minecraft.getInstance().execute(() -> reloadUI(entry));
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                t.printStackTrace();
//            }
//        });
    }

    private boolean isOwner() {
        if (entry != null) {
            return PlushieAuth.PLUSHIE_SESSION.isOwner(entry.userId);
        }
        return false;
    }

    private Component getMessage() {
        TextComponent message = new TextComponent("");

        message.append(getDisplayText("title"));
        message.append("\n\n");

        if (entry == null) {
            return message;
        }

        message.append(getDisplayText("name"));
        message.append(" ");
        message.append(entry.name);
        message.append("\n\n");

        if (entry.showsDownloads) {
            message.append(getDisplayText("downloads"));
            message.append(" ");
            message.append("" + entry.downloads);
            message.append("\n\n");
        }

        if (entry.showsRating) {
            message.append(getDisplayText("rating"));
            message.append(" ");
            message.append(String.format("(%d) %.1f/10.0", entry.ratingCount, entry.rating));
            message.append("\n\n");
        }

        BakedSkin bakedSkin = BakedSkin.of(entry.descriptor);
        if (bakedSkin != null && bakedSkin.getSkin() != null) {
            message.append(getDisplayText("author"));
            message.append(" ");
            message.append(bakedSkin.getSkin().getAuthorName());
            message.append("\n\n");
        }

        if (entry.showsGlobalId) {
            message.append(getDisplayText("global_id"));
            message.append(" ");
            message.append("" + entry.id);
            message.append("\n\n");
        }

        if (Strings.isNotBlank(entry.description)) {
            message.append(getDisplayText("description"));
            message.append(" ");
            message.append(entry.description);
            message.append("\n\n");
        }

        return message;
    }
}
