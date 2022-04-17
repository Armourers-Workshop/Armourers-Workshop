package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UploadSkinPacket;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.library.container.GlobalSkinLibraryContainer;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinUpload;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.ByteArrayOutputStream;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class UploadLibraryPanel extends AbstractLibraryPanel {

    private AWLabel warningLabel;
    private AWTextField textName;
    private AWTextField textTags;
    private AWTextField textDescription;
    private ExtendedButton buttonUpload;

    private String error = null;
    private boolean isUploading = false;

    public UploadLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.upload", Page.SKIN_UPLOAD::equals);
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

        this.buttonUpload = new ExtendedButton(leftPos + 28, topPos + height - 28, 96, 18, getDisplayText("buttonUpload"), this::upload);
        this.buttonUpload.active = false;
        this.addButton(buttonUpload);

        this.addLabel(leftPos + 5, topPos + 25, inputWidth, 10, getDisplayText("skinName"));
        this.addLabel(leftPos + 5, topPos + 55, inputWidth, 10, getDisplayText("skinTags"));
        this.addLabel(leftPos + 5, topPos + 85, inputWidth, 10, getDisplayText("skinDescription"));

        this.warningLabel = addLabel(leftPos + width - 162 - 5, topPos + 5, 162, height - 90, getWarningMessage());

        this.getMenu().ifPresent(container -> container.reload(leftPos, topPos, width, height));
    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        RenderUtils.bind(RenderUtils.TEX_GLOBAL_SKIN_LIBRARY);

        RenderUtils.blit(matrixStack, leftPos + width - 18 * 9 - 5, topPos + height - 82, 0, 180, 162, 76);

        RenderUtils.blit(matrixStack, leftPos + 5, topPos + height - 28, 0, 162, 18, 18);
        RenderUtils.blit(matrixStack, leftPos + 129, topPos + height - 32, 18, 154, 26, 26);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.getMenu().ifPresent(container -> container.setVisible(visible));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.buttonUpload == null) {
            return;
        }
        this.buttonUpload.active = Strings.isNotBlank(textName.getValue()) && !SkinDescriptor.of(getInputStack()).isEmpty() && !isUploading;
    }

    private AWTextField addTextField(int x, int y, int width, int height, String key) {
        AWTextField textField = new AWTextField(font, x, y, width, height, getDisplayText(key));
        textField.setMaxLength(255);
        addWidget(textField);
        return textField;
    }

    private void upload(Button sender) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        BakedSkin bakedSkin = BakedSkin.of(getInputStack());
        if (bakedSkin == null) {
            onUploadFailed("Skin missing.");
            return;
        }

        if (Strings.isBlank(textName.getValue())) {
            onUploadFailed("Skin name missing.");
            return;
        }

        if (isUploading) {
            return;
        }

        // upload now
        this.isUploading = true;
        GameProfile gameProfile = player.getGameProfile();
        Thread thread = new Thread(() -> uploadSkin(gameProfile, bakedSkin.getSkin()));
        thread.start();
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
        IOUtils.closeQuietly(outputStream);
        new GlobalTaskSkinUpload(fileBytes, textName.getValue().trim(), textDescription.getValue().trim()).createTaskAndRun(new FutureCallback<GlobalTaskSkinUpload.Result>() {

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
        NetworkHandler.getInstance().sendToServer(new UploadSkinPacket());
    }

    private void onUploadFailed(String message) {
        error = message;
        isUploading = false;
        if (warningLabel != null) {
            warningLabel.setMessage(getWarningMessage());
        }
    }

    private ITextComponent getWarningMessage() {
        StringTextComponent message = new StringTextComponent("");
        message.append(getDisplayText("label.upload_warning"));
        message.append("\n\n");

        if (Strings.isNotBlank(error)) {
            message.append("§cError: " + error + "§r");
            message.append("\n\n");
        }
        return message;
    }

    private ItemStack getInputStack() {
        return getMenu().map(GlobalSkinLibraryContainer::getInputStack).orElse(ItemStack.EMPTY);
    }
}
