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
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.gui.widget.ReportDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.Toast;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinRatingView;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.ReportType;
import moe.plushie.armourers_workshop.library.data.impl.ServerPermission;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import moe.plushie.armourers_workshop.library.data.impl.ServerUser;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
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

    private final SkinRatingView buttonStarRating = new SkinRatingView(CGRect.ZERO);

    private int userRating = 0;
    private boolean doneRatingCheck = false;

    private Font font;
    private CGGradient gradient;

    private CGRect skinInfoFrame = CGRect.ZERO;
    private CGRect previewFrame = CGRect.ZERO;
    private CGRect userFrame = CGRect.ZERO;

    private NSString message;
    private ServerSkin entry;
    private GlobalSkinLibraryWindow.Page returnPage;
    private PlayerTextureDescriptor playerTexture = PlayerTextureDescriptor.EMPTY;

    private final GlobalSkinLibrary library = GlobalSkinLibrary.getInstance();

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
        ServerUser user = GlobalSkinLibrary.getInstance().getUser();
        this.buttonEditSkin.setHidden(true);
        if (entry != null && user.equals(entry.getUser())) {
            this.buttonEditSkin.setHidden(!user.hasPermission(ServerPermission.SKIN_OWNER_EDIT));
        } else {
            this.buttonEditSkin.setHidden(!user.hasPermission(ServerPermission.SKIN_MOD_EDIT));
        }
        this.buttonDownload.setHidden(!user.hasPermission(ServerPermission.SKIN_DOWNLOAD));
    }

    public void reloadData(ServerSkin entry, GlobalSkinLibraryWindow.Page returnPage) {
        this.returnPage = returnPage;
        this.userRating = 0;
        this.doneRatingCheck = false;
        this.buttonDownload.setEnabled(true);
        this.reloadUI(entry);
        this.updateSkinJson();
        if (GlobalSkinLibrary.getInstance().getUser().isMember()) {
            this.checkIfLiked();
        }
    }

    public void reloadUI(ServerSkin entry) {
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
            ServerUser user = entry.getUser();
            if (!user.getName().isEmpty()) {
                playerTexture = new PlayerTextureDescriptor(new GameProfile(null, user.getName()));
            }
        }
        if (Strings.isNotBlank(playerTexture.getName())) {
            font.draw(context.poseStack.cast(), getDisplayText("uploader", playerTexture.getName()).chars(), rect.x + 32, rect.y + 12, 0xffeeeeee);
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
        BakedSkin bakedSkin = BakedSkin.of(entry.getDescriptor());
        if (bakedSkin != null) {
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkin(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, rect.x, rect.y, 100, rect.width, rect.height, 20, 45, 0, 0, 0xf000f0, context.poseStack, buffers);
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
        router.showSkinList(entry.getUser());
    }

    private void editSkin(UIControl button) {
        router.showSkinEdit(entry, returnPage);
    }

    private void updateSkinRating(UIControl button) {
        setSkinRating(buttonStarRating.getValue());
    }

    private void reportSkinPre(UIControl button) {
        ReportType[] reportTypes = ReportType.values();
        ReportDialog dialog = new ReportDialog();
        dialog.setTitle(getDisplayText("dialog.report_skin.title"));
        dialog.setMessageColor(new UIColor(0x7f0000));
        dialog.setMessage(getDisplayText("dialog.report_skin.label.report_warning"));
        dialog.setPlaceholder(getDisplayText("dialog.report_skin.optional_message"));
        dialog.setReportTypes(Arrays.stream(reportTypes).map(t -> new NSString(TranslateUtils.title(t.getLangKey()))).collect(Collectors.toList()));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                ReportType reportType = reportTypes[dialog.getReportType()];
                reportSkin(dialog.getText(), reportType);
            }
        });
    }

    private void reportSkin(String message, ReportType reportType) {
        ModLog.debug("report skin: '{}', text: '{}', type: {}", entry.getId(), message, reportType);
        entry.report(message, reportType, (result, exception) -> {
            if (exception == null) {
                ModLog.debug("skin report sent.");
            }
        });
    }

    private void downloadSkin(UIControl button) {
        String skinId = entry.getId();
        String idString = leftZeroPadding(skinId, 5);
        String skinName = entry.getName();
        File path = new File(EnvironmentManager.getSkinLibraryDirectory(), "downloads");
        File target = new File(path, SkinIOUtils.makeFileNameValid(idString + " - " + skinName + ".armour"));
        buttonDownload.setEnabled(false);
        // yep, we directly download and save in the local.
        GlobalSkinLibrary.getInstance().downloadSkin(entry.getId(), ((inputStream, exception) -> {
            if (exception != null) {
                Toast.show(exception, this);
                return;
            }
            try {
                SkinFileUtils.copyInputStreamToFile(inputStream, target);
                Toast.show("Download Finished!!", this);
            } catch (Exception exception1) {
                Toast.show(exception1, this);
            }
        }));
    }

    private void updateLikeButtons() {
        buttonStarRating.setHidden(true);
        if (doneRatingCheck) {
            buttonStarRating.setValue(userRating);
            buttonStarRating.setHidden(false);
        }
    }

    private void checkIfLiked() {
        entry.getRate((result, exception) -> {
            if (result != null) {
                userRating = result;
                doneRatingCheck = true;
                reloadUI(entry);
            }
        });
    }

    private String leftZeroPadding(String value, int len) {
        if (value.length() < len) {
            return String.valueOf('0').repeat(len - value.length()) + value;
        }
        return value;
    }

    private void setSkinRating(int rating) {
        boolean isNew = userRating == 0;
        userRating = rating;
        entry.updateRate(rating, (result, exception) -> {
            if (exception == null) {
                if (isNew) {
                    entry.ratingCount += 1;
                }
                reloadUI(entry);
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

    private NSString getMessage() {
        NSMutableString message = new NSMutableString("");

        message.append(getDisplayText("title"));
        message.append("\n\n");

        if (entry == null) {
            return message;
        }

        message.append(getDisplayText("name"));
        message.append(" ");
        message.append(entry.getName());
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

        BakedSkin bakedSkin = BakedSkin.of(entry.getDescriptor());
        if (bakedSkin != null && bakedSkin.getSkin() != null) {
            message.append(getDisplayText("author"));
            message.append(" ");
            message.append(bakedSkin.getSkin().getAuthorName());
            message.append("\n\n");
        }

        if (entry.showsGlobalId) {
            message.append(getDisplayText("global_id"));
            message.append(" ");
            message.append("" + entry.getId());
            message.append("\n\n");
        }

        if (Strings.isNotBlank(entry.getDescription())) {
            message.append(getDisplayText("description"));
            message.append(" ");
            message.append(entry.getDescription());
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
