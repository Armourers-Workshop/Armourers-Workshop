package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.io.File;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.global.GlobalSkinLibraryUtils;
import riskyken.armourersWorkshop.common.library.global.PlushieUser;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSkinInfo extends GuiPanel {

    private GuiButtonExt buttonBack;
    private GuiButtonExt buttonDownload;
    private JsonObject skinJson = null;
    private Screen returnScreen;
    
    public GuiGlobalLibraryPanelSkinInfo(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        buttonList.clear();
        int panelCenter = this.x + this.width / 2;
        buttonBack = new GuiButtonExt(0, panelCenter + 25, this.y + this.height - 25, 80, 20, GuiHelper.getLocalizedControlName(guiName, "skinInfo.back"));
        buttonList.add(buttonBack);
        buttonDownload = new GuiButtonExt(0, panelCenter - 105, this.y + this.height - 25, 80, 20, GuiHelper.getLocalizedControlName(guiName, "skinInfo.downloadSkin"));
        buttonList.add(buttonDownload);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonBack) {
            ((GuiGlobalLibrary)parent).switchScreen(returnScreen);
        }
        if (button == buttonDownload) {
            if (skinJson != null) {
                buttonDownload.enabled = false;
                new DownloadSkin(skinJson);
            }
        }
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
        if (skinJson != null && skinJson.has("id")) {
            skin = ClientSkinCache.INSTANCE.getSkinFromServerId(skinJson.get("id").getAsInt());
        }
        
        drawUserbox(x + 5, y + 5, 160, 30, mouseX, mouseY, partialTickTime);
        drawSkinInfo(skin, x + 5, y + 20 + 20, 160, height - 70, mouseX, mouseY, partialTickTime);
        drawPreviewBox(skin, x + 170, y + 5, width - 175, height - 35, mouseX, mouseY, partialTickTime);
        
        
        super.draw(mouseX, mouseY, partialTickTime);
    }
    
    public void drawUserbox(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        PlushieUser user = null;
        if (skinJson != null && skinJson.has("user_id")) {
            int userId = skinJson.get("user_id").getAsInt();
            user = GlobalSkinLibraryUtils.getUserInfo(userId);
        }
        if (user != null) {
            drawString(fontRenderer, "Uploader: " + user.getUsername(), boxX + 28, boxY + 5, 0xFFEEEEEE);
            GuiHelper.drawPlayerHead(boxX + 5, boxY + 5, 16, user.getUsername());
        } else {
            GuiHelper.drawPlayerHead(boxX + 5, boxY + 5, 16, null);
        }
    }
    
    public void drawSkinInfo(Skin skin, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        drawString(fontRenderer, "Skin Info", boxX + 5, boxY + 5, 0xFFEEEEEE);
        if (skinJson != null) {
            int yOffset = 12;
            //drawString(fontRenderer, "id: " + skinJson.get("id").getAsInt(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
            //yOffset += 12;
            drawString(fontRenderer, "name: " + skinJson.get("name").getAsString(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
            yOffset += 12;
            //drawString(fontRenderer, "file id: " + skinJson.get("file_name").getAsString(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
            //yOffset += 12;
            if (skinJson.has("downloads")) {
                drawString(fontRenderer, "downloads: " + skinJson.get("downloads").getAsString(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
                yOffset += 12;
            }
            if (skinJson.has("description")) {
                drawString(fontRenderer, "description: " + skinJson.get("description").getAsString(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
                yOffset += 12;
            }
            /*
            if (skinJson.has("user_id")) {
                drawString(fontRenderer, "user_id: " + skinJson.get("user_id").getAsString(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
                yOffset += 12;
            }
            */
            if (skin != null) {
                drawString(fontRenderer, "author name: " + skin.getAuthorName(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
                yOffset += 12;
                if (!StringUtils.isNullOrEmpty(skin.getCustomName())) {
                    drawString(fontRenderer, "custom name: " + skin.getCustomName(), boxX + 5, boxY + 5 + yOffset, 0xFFEEEEEE);
                    yOffset += 12;
                }
            }
        }
    }
    
    public void drawPreviewBox(Skin skin, int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x22888888, 0x22CCCCCC);
        if (skin != null) {
            int iconSize = Math.min(boxWidth, boxHeight);
            float scale = iconSize / 2;
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glTranslatef(boxX + boxWidth / 2, boxY + boxHeight / 2, 200.0F);
            GL11.glScalef((float)(-scale), (float)scale, (float)scale);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            float rotation = (float)((double)System.currentTimeMillis() / 10 % 360);
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            ModRenderHelper.enableAlphaBlend();
            ItemStackRenderHelper.renderItemModelFromSkin(skin, new SkinPointer(skin), true, false);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
    
    public void displaySkinInfo(JsonObject jsonObject, Screen returnScreen) {
        skinJson = jsonObject;
        ((GuiGlobalLibrary)parent).switchScreen(Screen.SKIN_INFO);
        this.returnScreen = returnScreen;
    }
    
    private static class DownloadSkin implements Runnable {
        
        private final JsonObject skinJson;
        private final File target;
        
        public DownloadSkin(JsonObject skinJson) {
            this.skinJson = skinJson;
            int skinId = skinJson.get("id").getAsInt();
            String idString = String.format ("%04d", skinId);
            String skinName = skinJson.get("name").getAsString();
            File path = new File(SkinIOUtils.getSkinLibraryDirectory(), "downloads/");
            target = new File(path, idString + " - " + skinName + ".armour");
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
                    ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
                    libraryManager.reloadLibrary();
                }
            }
        }
    }
}
