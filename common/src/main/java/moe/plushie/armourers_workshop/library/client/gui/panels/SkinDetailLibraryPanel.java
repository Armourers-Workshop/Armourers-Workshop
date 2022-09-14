package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGGradient;
import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIView;
import com.google.common.util.concurrent.FutureCallback;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.gui.widget.ReportDialog;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinItemList;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinRatingView;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.PlushieUser;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinReport;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskUserSkinRate;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskUserSkinRating;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Environment(value = EnvType.CLIENT)
public class SkinDetailLibraryPanel extends AbstractLibraryPanel {

    private UIButton buttonBack;
    private UIButton buttonDownload;
    private UIButton buttonUserSkins;
    private UIButton buttonEditSkin;
    private UIButton buttonReportSkin;

    private SkinRatingView buttonStarRating = new SkinRatingView(CGRect.ZERO);

    private int userRating = 0;
    private boolean doneRatingCheck = false;

    private Font font;
    private CGGradient gradient;

    private CGRect skinInfoFrame = CGRect.ZERO;
    private CGRect previewFrame = CGRect.ZERO;
    private CGRect userFrame = CGRect.ZERO;

    private NSString message;
    private SkinItemList.Entry entry;
    private GlobalSkinLibraryWindow.Page returnPage;
    private PlayerTextureDescriptor playerTexture = PlayerTextureDescriptor.EMPTY;

    public SkinDetailLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.skinInfo", GlobalSkinLibraryWindow.Page.SKIN_DETAIL::equals);
        this.setup();
    }

    private void setup() {
        font = Minecraft.getInstance().font;;
        gradient = new CGGradient(UIColor.rgba(0x22888888), CGPoint.ZERO, UIColor.rgba(0x22CCCCCC), CGPoint.ZERO);

        CGRect bounds = bounds();
        int minX = 2;
        int maxX = bounds.width - 2;
        int midX = minX + 185 + 2;

        buttonUserSkins = addTextButton(minX + 2, 4, 26, 26, "", SkinDetailLibraryPanel::searchUser);
        buttonUserSkins.addSubview(new HeadView(new CGRect(5, 5, 16, 16)));

        buttonStarRating.setFrame(new CGRect(191, 4, 16, 16));
        buttonStarRating.setMaxValue(10);
        buttonStarRating.setValue(userRating);
        buttonStarRating.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinDetailLibraryPanel::updateSkinRating);
        addSubview(buttonStarRating);

        buttonDownload = addTextButton(midX + 2, bounds.height - 38, 76, 16, "downloadSkin", SkinDetailLibraryPanel::downloadSkin);
        buttonDownload.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);

        buttonReportSkin = addTextButton(maxX - 80, bounds.height - 38, 76, 16, "button.report_skin", SkinDetailLibraryPanel::reportSkinPre);
        buttonReportSkin.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);

        buttonBack = addTextButton(minX, bounds.height - 18, 80, 16, "back", SkinDetailLibraryPanel::backToHome);
        buttonBack.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);

        buttonEditSkin = addTextButton(maxX - 80, bounds.height - 18, 80, 16, "editSkin", SkinDetailLibraryPanel::editSkin);
        buttonEditSkin.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = bounds();
        skinInfoFrame = new CGRect(2, 34, 185, bounds.height - 54);
        previewFrame = new CGRect(189, 2, bounds.width - 189 - 2, bounds.height - 22);
        userFrame = new CGRect(2, 2, 185, 30);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.buttonUserSkins == null) {
            return;
        }
        this.buttonEditSkin.setHidden(true);
        if (isOwner()) {
            this.buttonEditSkin.setHidden(!PlushieAuth.PLUSHIE_SESSION.hasPermission(PermissionSystem.PlushieAction.SKIN_OWNER_EDIT));
        } else {
            this.buttonEditSkin.setHidden(!PlushieAuth.PLUSHIE_SESSION.hasPermission(PermissionSystem.PlushieAction.SKIN_MOD_EDIT));
        }
        this.buttonDownload.setHidden(!PlushieAuth.PLUSHIE_SESSION.hasPermission(PermissionSystem.PlushieAction.SKIN_DOWNLOAD));
    }

    public void reloadData(SkinItemList.Entry entry, GlobalSkinLibraryWindow.Page returnPage) {
        this.returnPage = returnPage;
        this.userRating = 0;
        this.doneRatingCheck = false;
        this.buttonDownload.setEnabled(true);
        this.reloadUI(entry);
        this.updateSkinJson();
        if (PlushieAuth.isRemoteUser()) {
            this.checkIfLiked();
        }
    }

    public void reloadUI(SkinItemList.Entry entry) {
        this.entry = entry;
        this.message = getMessage();
        this.playerTexture = PlayerTextureDescriptor.EMPTY;
        this.updateLikeButtons();
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        drawSkinInfo(context, skinInfoFrame);
        drawPreviewBox(context, previewFrame);
        drawUserbox(context, userFrame);
    }

    public void drawUserbox(CGGraphicsContext context, CGRect rect) {
        context.fillRect(gradient, rect);
        if (playerTexture.isEmpty()) {
            PlushieUser user = GlobalSkinLibraryUtils.getUserInfo(entry.userId);
            if (user != null) {
                playerTexture = new PlayerTextureDescriptor(new GameProfile(null, user.getUsername()));
            }
        }
        if (Strings.isNotBlank(playerTexture.getName())) {
            font.draw(context.poseStack, getDisplayText("uploader", playerTexture.getName()).chars(), rect.x + 32, rect.y + 12, 0xffeeeeee);
            RenderSystem.enableAlphaTest();
        }
    }

    public void drawSkinInfo(CGGraphicsContext context, CGRect rect) {
        context.fillRect(gradient, rect);
        if (message == null) {
            return;
        }
        RenderSystem.addClipRect(convertRectToView(rect, null));
        RenderSystem.drawText(context.poseStack, font, message.component(), rect.x + 2, rect.y + 2, rect.width - 4, 0, 0xffeeeeee);
        RenderSystem.removeClipRect();
    }

    public void drawPreviewBox(CGGraphicsContext context, CGRect rect) {
        context.fillRect(gradient, rect);
        BakedSkin bakedSkin = BakedSkin.of(entry.descriptor);
        if (bakedSkin != null) {
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkin(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, rect.x, rect.y, 100, rect.width, rect.height, 20, 45, 0, context.poseStack, buffers);
            buffers.endBatch();
        }
    }

    private UIButton addTextButton(int x, int y, int width, int height, String key, BiConsumer<SkinDetailLibraryPanel, UIControl> handler) {
        NSString title = new NSString("");
        if (!key.isEmpty()) {
            title = getDisplayText(key);
        }
        UIButton button = new UIButton(new CGRect(x, y, width, height));
        button.setTitle(title, UIControl.State.NORMAL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        return button;
    }

    private void backToHome(UIControl button) {
        router.showPage(returnPage);
    }

    private void searchUser(UIControl button) {
        PlushieUser plushieUser = GlobalSkinLibraryUtils.getUserInfo(entry.userId);
        if (plushieUser != null) {
            router.showSkinList(entry.userId);
        }
    }

    private void editSkin(UIControl button) {
        router.showSkinEdit(entry, returnPage);
    }

    private void updateSkinRating(UIControl button) {
        setSkinRating(buttonStarRating.getValue());
    }

    private void reportSkinPre(UIControl button) {
        GlobalTaskSkinReport.SkinReport.SkinReportType[] reportTypes = GlobalTaskSkinReport.SkinReport.SkinReportType.values();
        ReportDialog dialog = new ReportDialog();
        dialog.setTitle(getDisplayText("dialog.report_skin.title"));
        dialog.setMessageColor(new UIColor(0x7f0000));
        dialog.setMessage(getDisplayText("dialog.report_skin.label.report_warning"));
        dialog.setPlaceholder(getDisplayText("dialog.report_skin.optional_message"));
        dialog.setReportTypes(Arrays.stream(reportTypes).map(t -> new NSString(TranslateUtils.title(t.getLangKey()))).collect(Collectors.toList()));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                GlobalTaskSkinReport.SkinReport.SkinReportType reportType = reportTypes[dialog.getReportType()];
                GlobalTaskSkinReport.SkinReport report = new GlobalTaskSkinReport.SkinReport(entry.id, reportType, dialog.getText());
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

    private void downloadSkin(UIControl button) {
        int skinId = entry.id;
        String idString = String.format("%04d", skinId);
        String skinName = entry.name;
        File path = new File(EnvironmentManager.getSkinLibraryDirectory(), "downloads");
        File target = new File(path, SkinIOUtils.makeFileNameValid(idString + " - " + skinName + ".armour"));
        buttonDownload.setEnabled(false);
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
        buttonStarRating.setHidden(true);
        if (doneRatingCheck) {
            buttonStarRating.setValue(userRating);
            buttonStarRating.setHidden(false);
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

    private NSString getMessage() {
        NSMutableString message = new NSMutableString("");

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

    private class HeadView extends UIView {

        public HeadView(CGRect frame) {
            super(frame);
        }

        @Override
        public void render(CGPoint point, CGGraphicsContext context) {
            super.render(point, context);
            RenderSystem.enableAlphaTest();
            RenderSystem.drawPlayerHead(context.poseStack, 0, 0, 16, 16, playerTexture);
        }
    }
}
