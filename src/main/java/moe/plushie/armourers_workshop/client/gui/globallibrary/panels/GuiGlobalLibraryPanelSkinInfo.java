package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.io.File;

import org.lwjgl.opengl.GL11;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.library.ILibraryManager;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.PlushieUser;
import moe.plushie.armourers_workshop.common.library.global.SkinDownloader;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskUserSkinRate;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskUserSkinRating;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSkinInfo extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibGuiResources.GUI_GLOBAL_LIBRARY);

    private GuiButtonExt buttonBack;
    private GuiButtonExt buttonDownload;
    private GuiButtonExt buttonUserSkins;
    private GuiButtonExt buttonEditSkin;
    private GuiIconButton buttonLikeSkin;
    private GuiIconButton buttonUnlikeSkin;

    private final String guiName;

    private JsonObject skinJson = null;
    private Screen returnScreen;

    private boolean doneLikeCheck = false;
    private boolean haveLiked = false;

    public GuiGlobalLibraryPanelSkinInfo(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".skinInfo";
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int panelCenter = this.x + this.width / 2;
        buttonBack = new GuiButtonExt(0, 2, this.y + this.height - 18, 80, 16, GuiHelper.getLocalizedControlName(guiName, "back"));
        buttonDownload = new GuiButtonExt(0, x + 185 + 6, this.y + this.height - 38, 80, 16, GuiHelper.getLocalizedControlName(guiName, "downloadSkin"));
        buttonUserSkins = new GuiButtonExt(0, x + 3, y + 3, 26, 26, "");
        buttonEditSkin = new GuiButtonExt(0, x + width - 82, this.y + this.height - 18, 80, 16, GuiHelper.getLocalizedControlName(guiName, "editSkin"));

        buttonLikeSkin = new GuiIconButton(parent, 0, x + 191, this.y + 4, 20, 20, GuiHelper.getLocalizedControlName(guiName, "like"), BUTTON_TEXTURES);
        buttonLikeSkin.setIconLocation(96, 0, 16, 16);
        buttonUnlikeSkin = new GuiIconButton(parent, 0, x + 191, this.y + 4, 20, 20, GuiHelper.getLocalizedControlName(guiName, "unlike"), BUTTON_TEXTURES);
        buttonUnlikeSkin.setIconLocation(96, 17, 16, 16);

        updateLikeButtons();

        buttonList.add(buttonBack);
        buttonList.add(buttonDownload);
        buttonList.add(buttonUserSkins);
        buttonList.add(buttonEditSkin);
        buttonList.add(buttonLikeSkin);
        buttonList.add(buttonUnlikeSkin);
    }

    @Override
    public void update() {
        buttonEditSkin.visible = false;
        if (isOwner()) {
            buttonEditSkin.visible = PlushieAuth.PLUSHIE_SESSION.hasPermission(PlushieAction.SKIN_OWNER_EDIT);
        } else {
            buttonEditSkin.visible = PlushieAuth.PLUSHIE_SESSION.hasPermission(PlushieAction.SKIN_MOD_EDIT);
        }
        buttonDownload.visible = PlushieAuth.PLUSHIE_SESSION.hasPermission(PlushieAction.SKIN_DOWNLOAD);
    }

    private boolean isOwner() {
        if (skinJson != null && skinJson.has("user_id")) {
            return PlushieAuth.PLUSHIE_SESSION.isOwner(skinJson.get("user_id").getAsInt());
        }
        return false;
    }

    private void updateLikeButtons() {
        buttonLikeSkin.visible = false;
        buttonUnlikeSkin.visible = false;
        buttonLikeSkin.enabled = true;
        buttonUnlikeSkin.enabled = true;
        if (doneLikeCheck) {
            buttonLikeSkin.visible = !haveLiked;
            buttonUnlikeSkin.visible = haveLiked;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonBack) {
            ((GuiGlobalLibrary) parent).switchScreen(returnScreen);
        }
        if (button == buttonDownload) {
            if (skinJson != null) {
                buttonDownload.enabled = false;
                new DownloadSkin(skinJson);
            }
        }
        if (button == buttonUserSkins) {
            if (skinJson != null && skinJson.has("user_id")) {
                int userId = skinJson.get("user_id").getAsInt();
                PlushieUser plushieUser = GlobalSkinLibraryUtils.getUserInfo(userId);
                if (plushieUser != null) {
                    ((GuiGlobalLibrary) parent).panelUserSkins.clearResults();
                    ((GuiGlobalLibrary) parent).switchScreen(Screen.USER_SKINS);
                    ((GuiGlobalLibrary) parent).panelUserSkins.switchToUser(userId);
                }
            }
        }
        if (button == buttonEditSkin) {
            if (skinJson != null) {
                ((GuiGlobalLibrary) parent).panelSkinEdit.displaySkinInfo(skinJson, returnScreen);
            }
        }
        if (button == buttonLikeSkin) {
            setSkinRating(1);
            buttonLikeSkin.enabled = false;
        }
        if (button == buttonUnlikeSkin) {
            setSkinRating(0);
            buttonUnlikeSkin.enabled = false;
        }
    }

    public void displaySkinInfo(JsonObject jsonObject, Screen returnScreen) {
        skinJson = jsonObject;
        ((GuiGlobalLibrary) parent).switchScreen(Screen.SKIN_INFO);
        ((GuiGlobalLibrary) parent).panelSkinEdit.setModerator(!isOwner());
        this.returnScreen = returnScreen;

        doneLikeCheck = false;
        if (PlushieAuth.isRemoteUser() & skinJson != null) {
            checkIfLiked();
        }
    }

    private void checkIfLiked() {
        int skinId = skinJson.get("id").getAsInt();
        new GlobalTaskUserSkinRating(skinId).createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        if (result.has("isLiked")) {
                            haveLiked = result.get("isLiked").getAsBoolean();
                            doneLikeCheck = true;
                            updateLikeButtons();
                        }
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
        int skinId = skinJson.get("id").getAsInt();
        new GlobalTaskUserSkinRate(skinId, rating).createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {

                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        if (result.has("valid") && result.get("valid").getAsBoolean()) {
                            if (skinJson != null) {
                                if (skinJson.has("likes")) {
                                    if (haveLiked) {
                                        skinJson.addProperty("likes", skinJson.get("likes").getAsInt() - 1);
                                    } else {
                                        skinJson.addProperty("likes", skinJson.get("likes").getAsInt() + 1);
                                    }
                                }
                            }
                            haveLiked = !haveLiked;
                            updateLikeButtons();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }

        PlushieUser user = null;
        if (skinJson != null && skinJson.has("user_id")) {
            int userId = skinJson.get("user_id").getAsInt();
            user = GlobalSkinLibraryUtils.getUserInfo(userId);
        }

        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);

        Skin skin = null;
        SkinIdentifier identifier = new SkinIdentifier(0, null, skinJson.get("id").getAsInt(), null);
        if (skinJson != null && skinJson.has("id")) {
            skin = ClientSkinCache.INSTANCE.getSkin(identifier);
        }

        super.draw(mouseX, mouseY, partialTickTime);
        drawUserbox(x + 2, y + 2, 185, 30, mouseX, mouseY, partialTickTime);
        drawSkinInfo(skin, x + 2, y + 30 + 4, 185, height - 54, mouseX, mouseY, partialTickTime);
        drawPreviewBox(identifier, skin, x + 189, y + 2, width - 189 - 2, height - 22, mouseX, mouseY, partialTickTime);
    }

    public void drawUserbox(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        String fullName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + ".";
        PlushieUser user = null;
        if (skinJson != null && skinJson.has("user_id")) {
            int userId = skinJson.get("user_id").getAsInt();
            user = GlobalSkinLibraryUtils.getUserInfo(userId);
        }
        if (user != null) {
            drawString(fontRenderer, I18n.format(fullName + "uploader", user.getUsername()), boxX + 29, boxY + boxHeight - 12, 0xFFEEEEEE);
            GuiHelper.drawPlayerHead(boxX + 5, boxY + 5, 16, user.getUsername());
        } else {
            GuiHelper.drawPlayerHead(boxX + 5, boxY + 5, 16, null);
        }
    }

    public void drawSkinInfo(Skin skin, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        ModRenderHelper.enableScissorScaled(boxX, boxY, boxWidth, boxHeight);

        String fullName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + ".";

        String info = "";
        
        info += GuiHelper.getLocalizedControlName(guiName, "title") + "\n\n";
        
        if (skinJson != null) {
            info += GuiHelper.getLocalizedControlName(guiName, "name") + " ";
            info += skinJson.get("name").getAsString() + "\n\n";
            
            int yOffset = 100;
            if (skinJson.has("downloads")) {
                info += I18n.format(fullName + "downloads", skinJson.get("downloads").getAsInt()) + "\n\n";
            }
            if (skinJson.has("likes")) {
                info += I18n.format(fullName + "likes", skinJson.get("likes").getAsInt()) + "\n\n";
            }
            if (skin != null) {
                info += GuiHelper.getLocalizedControlName(guiName, "author") + " ";
                info +=  skin.getAuthorName() + "\n\n";
            }
            info += "Global ID: " + skinJson.get("id").getAsInt() + "\n\n";
            
            if (skinJson.has("description")) {
                info += GuiHelper.getLocalizedControlName(guiName, "description") + " ";
                info += skinJson.get("description").getAsString();
            }
        }
        fontRenderer.drawSplitString(info, boxX + 2, boxY + 2, boxWidth - 4, 0xFFEEEEEE);
        ModRenderHelper.disableScissor();
    }

    public void drawPreviewBox(SkinIdentifier identifier, Skin skin, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        if (skin != null) {
            mc.renderEngine.bindTexture(BUTTON_TEXTURES);
            int rating = (int) ((System.currentTimeMillis() / 100D) % 11);
            for (int i = 0; i < 5; i++) {
                drawTexturedModalRect(boxX + 24 + i * 16, boxY + 4, 32, 85, 16, 16);
            }
            int stars = MathHelper.floor(rating / 2F);
            int halfStar = rating % 2;
            for (int i = 0; i < stars; i++) {
                drawTexturedModalRect(boxX + 24 + i * 16, boxY + 4, 0, 85, 16, 16);
            }
            if (halfStar == 1) {
                drawTexturedModalRect(boxX + 24 + stars * 16, boxY + 4, 0, 85, 8, 16);
            }
            
            
            int iconSize = Math.min(boxWidth, boxHeight);
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            float scale = 10 - scaledResolution.getScaleFactor();
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.translate(boxX + boxWidth / 2, boxY + boxHeight / 2, 500.0F);
            GlStateManager.scale((-scale), scale, scale);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
            float rotation = (float) ((double) System.currentTimeMillis() / 10 % 360);
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableNormalize();
            GlStateManager.disableColorMaterial();
            GlStateManager.enableNormalize();
            GlStateManager.enableColorMaterial();
            ModRenderHelper.enableAlphaBlend();
            GlStateManager.enableDepth();

            SkinItemRenderHelper.renderSkinAsItem(skin, new SkinDescriptor(identifier), true, false, boxWidth, boxHeight);

            GlStateManager.disableDepth();
            ModRenderHelper.disableAlphaBlend();
            GlStateManager.disableNormalize();
            GlStateManager.disableColorMaterial();

            GlStateManager.resetColor();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    private static class DownloadSkin implements Runnable {

        private final JsonObject skinJson;
        private final File target;

        public DownloadSkin(JsonObject skinJson) {
            this.skinJson = skinJson;
            int skinId = skinJson.get("id").getAsInt();
            String idString = String.format("%04d", skinId);
            String skinName = skinJson.get("name").getAsString();
            File path = new File(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), "downloads/");
            target = new File(path, SkinIOUtils.makeFileNameValid(idString + " - " + skinName + ".armour"));
            if (!path.exists()) {
                path.mkdirs();
            }
            new Thread(this).start();
        }

        @Override
        public void run() {
            String fileName = skinJson.get("file_name").getAsString();
            int serverId = skinJson.get("id").getAsInt();
            Skin skin = SkinDownloader.downloadSkin(fileName, serverId);
            if (skin != null) {
                if (SkinIOUtils.saveSkinToFile(target, skin)) {
                    ILibraryManager libraryManager = ArmourersWorkshop.getProxy().libraryManager;
                    libraryManager.reloadLibrary();
                }
            }
        }
    }
}
