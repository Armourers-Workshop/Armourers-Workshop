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
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSkinInfo extends GuiPanel {

    private GuiButtonExt buttonBack;
    private GuiButtonExt buttonDownload;
    private JsonObject skinJson = null;
    
    public GuiGlobalLibraryPanelSkinInfo(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int panelCenter = this.x + this.width / 2;
        buttonBack = new GuiButtonExt(0, panelCenter + 25, this.y + this.height - 25, 80, 20, "Back");
        buttonList.add(buttonBack);
        buttonDownload = new GuiButtonExt(0, panelCenter - 105, this.y + this.height - 25, 80, 20, "Download Skin");
        buttonList.add(buttonDownload);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonBack) {
            ((GuiGlobalLibrary)parent).switchScreen(Screen.SEARCH);
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
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        drawString(fontRenderer, "Skin Info", this.x + 5, this.y + 5, 0xFFEEEEEE);
        
        if (skinJson != null) {
            drawString(fontRenderer, "id: " + skinJson.get("id").getAsInt(), this.x + 5, this.y + 5 + 12 * 1, 0xFFEEEEEE);
            drawString(fontRenderer, "name: " + skinJson.get("name").getAsString(), this.x + 5, this.y + 5 + 12 * 2, 0xFFEEEEEE);
            drawString(fontRenderer, "file id: " + skinJson.get("file_name").getAsString(), this.x + 5, this.y + 5 + 12 * 3, 0xFFEEEEEE);
            
            int iconSize = 200;
            float scale = iconSize / 2;
            
            Skin skin = ClientSkinCache.INSTANCE.getSkinFromServerId(skinJson.get("id").getAsInt());
            if (skin != null) {
                drawString(fontRenderer, "author name: " + skin.getAuthorName(), this.x + 5, this.y + 5 + 12 * 5, 0xFFEEEEEE);
                drawString(fontRenderer, "custom name: " + skin.getCustomName(), this.x + 5, this.y + 5 + 12 * 6, 0xFFEEEEEE);
                
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glTranslatef(this.x + this.width - iconSize, this.y + iconSize / 2, 200.0F);
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
        
        super.draw(mouseX, mouseY, partialTickTime);
    }
    
    public void displaySkinInfo(JsonObject jsonObject) {
        skinJson = jsonObject;
        ((GuiGlobalLibrary)parent).switchScreen(Screen.SKIN_INFO);
    }
    
    private static class DownloadSkin implements Runnable {
        
        private final JsonObject skinJson;
        private final File target;
        
        public DownloadSkin(JsonObject skinJson) {
            this.skinJson = skinJson;
            int skinId = skinJson.get("id").getAsInt();
            String skinName = skinJson.get("name").getAsString();
            File path = new File(SkinIOUtils.getSkinLibraryDirectory(), "downloads/");
            target = new File(path, skinId + " - " + skinName + ".armour");
            if (!path.exists()) {
                path.mkdirs();
            }
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            String name = skinJson.get("file_name").getAsString();
            int serverId = skinJson.get("id").getAsInt();
            Skin skin = SkinDownloader.downloadSkin(name, serverId);
            if (skin != null) {
                if (SkinIOUtils.saveSkinToFile(target, skin)) {
                    ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
                    libraryManager.reloadLibrary();
                }
            }
        }
    }
}
