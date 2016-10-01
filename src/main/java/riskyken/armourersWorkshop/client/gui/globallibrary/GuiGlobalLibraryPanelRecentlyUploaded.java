package riskyken.armourersWorkshop.client.gui.globallibrary;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader.IDownloadListCallback;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader.IDownloadSkinCallback;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinUtils;

public class GuiGlobalLibraryPanelRecentlyUploaded extends GuiPanel implements IDownloadListCallback, IDownloadSkinCallback {
    
    private static final String RECENTLY_UPLOADED_URL = "http://plushie.moe/armourers_workshop/recently-uploaded.php";
    
    private Object syncLock = new Object();
    private ArrayList<SkinPointer> skins = new ArrayList<SkinPointer>();
    
    public GuiGlobalLibraryPanelRecentlyUploaded(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        SkinDownloader.downloadJson(this, RECENTLY_UPLOADED_URL);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        
        fontRenderer.drawString("Recently Uploaded", x + 5, y + 6, 0xFFEEEEEE);
        
        int boxW = width - 5;
        int boxH = height - 5 - 14;
        int iconSize = 50;
        synchronized (skins) {
            for (int i = 0; i < skins.size(); i++) {
                int x = (int) (i % Math.floor(boxW / iconSize));
                int y = (int) (i / Math.floor(boxW / iconSize));
                SkinPointer skinPonter = skins.get(i);
                Skin skin = SkinUtils.getSkinDetectSide(skinPonter, false, false);
                if (skin != null) {
                float scale = iconSize / 2;
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glTranslatef(this.x + iconSize / 2 + x * iconSize, 14 + this.y + iconSize / 2 + y * 40, 200.0F);
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
                ItemStackRenderHelper.renderItemModelFromSkin(skin, skinPonter, true);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
                }
            }
        }
    }

    @Override
    public void listDownloadFinished(JsonArray json) {
        SkinDownloader.downloadSkins(this, json);
    }

    @Override
    public void skinDownloaded(Skin skin, SkinPointer skinPointer) {
        synchronized (skins) {
            skins.add(skinPointer);
        }
        ModelBakery.INSTANCE.receivedUnbakedModel(skin);
    }
}
