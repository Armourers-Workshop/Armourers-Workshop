package riskyken.armourersWorkshop.client.gui.globallibrary;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonCallable;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

public class GuiGlobalLibraryPanelRecentlyUploaded extends GuiPanel {
    
    private static final String RECENTLY_UPLOADED_URL = "http://plushie.moe/armourers_workshop/recently-uploaded.php";
    
    private Object syncLock = new Object();
    private ArrayList<SkinPointer> skins = new ArrayList<SkinPointer>();
    private int displayLimit = 1;
    
    private FutureTask<JsonArray> downloadListTask;
    private CompletionService<Skin> skinCompletion;
    
    public GuiGlobalLibraryPanelRecentlyUploaded(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinCompletion = new ExecutorCompletionService<Skin>(((GuiGlobalLibrary)parent).skinDownloadExecutor);
    }
    
    public void updateRecentlyUploadedSkins() {
        skins.clear();
        downloadListTask = new FutureTask<JsonArray>(new DownloadJsonCallable(RECENTLY_UPLOADED_URL + "?limit=" + displayLimit));
        ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(downloadListTask);
    }
    
    @Override
    public void updatePanel() {
        if (downloadListTask != null && downloadListTask.isDone()) {
            try {
                JsonArray json = downloadListTask.get();
                SkinDownloader.downloadSkins(skinCompletion, json);
                //SkinDownloader.downloadSkins(this, json);
                downloadListTask = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Future<Skin> futureSkin = skinCompletion.poll();
        if (futureSkin != null) {
            try {
                Skin skin = futureSkin.get();
                if (skin != null) {
                    SkinPointer skinPointer = new SkinPointer(skin);
                    skins.add(skinPointer);
                    if (skin != null && !ClientSkinCache.INSTANCE.isSkinInCache(skinPointer)) {
                        ModelBakery.INSTANCE.receivedUnbakedModel(skin);
                    } else {
                        if (skin != null) {
                            ClientSkinCache.INSTANCE.addServerIdMap(skin);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void initGui() {
        int boxW = width - 5;
        int boxH = height - 5 - 12;
        int iconSize = 50;
        int rowSize = (int) Math.floor(boxW / iconSize);
        int colSize = (int) Math.floor(boxH / iconSize);
        displayLimit = colSize * rowSize;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        
        fontRenderer.drawString("Recently Uploaded", x + 5, y + 6, 0xFFEEEEEE);
        
        int boxW = width - 5;
        int boxH = height - 5 - 12;
        int iconSize = 50;
        synchronized (skins) {
            for (int i = 0; i < skins.size(); i++) {
                int rowSize = (int) Math.floor(boxW / iconSize);
                int colSize = (int) Math.floor(boxH / iconSize);
                int x = i % rowSize;
                int y = (int) (i / rowSize);
                SkinPointer skinPointer = skins.get(i);
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer, false);
                if (skin != null) {
                    float scale = iconSize / 2;
                    if (y < colSize) {
                        //fontRenderer.drawString(skin.getCustomName(), this.x + x * iconSize, this.y + y * iconSize + iconSize, 0xFFEEEEEE);
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                        GL11.glTranslatef(this.x + iconSize / 2 + x * iconSize, 12 + this.y + iconSize / 2 + y * iconSize, 200.0F);
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
                        ItemStackRenderHelper.renderItemModelFromSkin(skin, skinPointer, true, false);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }
}
