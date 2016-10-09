package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchResults extends GuiPanel {
    
    private JsonArray json = null;
    private int displayCount = 1;
    private int page = 0;
    private int mouseDownIndex = -1;
    
    private FutureTask<JsonArray> downloadSearchResultsTask;
    private CompletionService<Skin> skinCompletion;
    
    public GuiGlobalLibraryPanelSearchResults(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinCompletion = new ExecutorCompletionService<Skin>(((GuiGlobalLibrary)parent).skinDownloadExecutor);
    }
    
    public void setDownloadSearchResultsTask(FutureTask<JsonArray> downloadSearchResultsTask) {
        this.downloadSearchResultsTask = downloadSearchResultsTask;
    }
    
    @Override
    public void update() {
        if (downloadSearchResultsTask != null && downloadSearchResultsTask.isDone()) {
            try {
                json = null;
                page = 0;
                json = downloadSearchResultsTask.get();
                SkinDownloader.downloadSkins(skinCompletion, json);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            downloadSearchResultsTask = null;
        }
        
        Future<Skin> futureSkin = skinCompletion.poll();
        if (futureSkin != null) {
            try {
                Skin skin = futureSkin.get();
                if (skin != null) {
                    SkinPointer skinPointer = new SkinPointer(skin);
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
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButtonExt(0, x + 5, y + height - 25, 80, 20, "<<"));
        buttonList.add(new GuiButtonExt(1, x + width - 85, y + height - 25, 80, 20, ">>"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        int skinCount = 0;
        if (json != null) {
            skinCount = json.size();
        }
        int maxPages = (int) Math.ceil((float)skinCount / (float)displayCount);
        
        if (button.id == 0) {
            if (page > 0) {
                page--;
            }
        }
        if (button.id == 1) {
            if (page < maxPages - 1) {
                page++;
            }
        }
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled | haveOpenDialog()) {
            return;
        }
        mouseDownIndex = getSkinIndexAtLocation(mouseX, mouseY);
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (!visible | !enabled | haveOpenDialog()) {
            return;
        }
        int index = getSkinIndexAtLocation(mouseX, mouseY);
        if (index == mouseDownIndex & index != -1) {
            ((GuiGlobalLibrary)parent).panelSkinInfo.displaySkinInfo(json.get(index).getAsJsonObject());
            index = -1;
        }
        super.mouseMovedOrUp(mouseX, mouseY, button);
    }
    
    private int getSkinIndexAtLocation(int locX, int locY) {
        if (json == null) {
            return -1;
        }
        
        int boxW = width - 5;
        int boxH = height - 5 - 12;
        int iconSize = 110;

        for (int i = page * displayCount; i < json.size(); i++) {
            int rowSize = (int) Math.floor(boxW / iconSize);
            int colSize = (int) Math.floor(boxH / iconSize);
            displayCount = rowSize * colSize;
            int x = (i - page * displayCount) % rowSize;
            int y = (i - page * displayCount) / rowSize;
            
            int iconX = this.x + x * iconSize + 5;
            int iconY = this.y + y * iconSize + 5 + 22;
            int iconW = iconX + iconSize - 10;
            int iconH = iconY + iconSize - 10;
            if (y < colSize) {
                if (locX >= iconX & locX < iconW) {
                    if (locY >= iconY & locY < iconH) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
        
        int boxW = width - 5;
        int boxH = height - 5 - 12;
        int iconSize = 110;
        
        int maxPages = 0;
        int totalSkins = 0;
        if (json != null) {
            maxPages = (int) Math.ceil((float)json.size() / (float)displayCount);
            totalSkins = json.size();
        }
        
        fontRenderer.drawString("Search Results:  Page " + (page + 1) + " of " + (maxPages) + " - Total of " + totalSkins + " Results", x + 5, y + 6, 0xFFEEEEEE);
        
        if (json != null) {
            for (int i = page * displayCount; i < json.size(); i++) {
                int rowSize = (int) Math.floor(boxW / iconSize);
                int colSize = (int) Math.floor(boxH / iconSize);
                displayCount = rowSize * colSize;
                int x = (i - page * displayCount) % rowSize;
                int y = (i - page * displayCount) / rowSize;
                JsonObject skinJson = json.get(i).getAsJsonObject();
                Skin skin = ClientSkinCache.INSTANCE.getSkinFromServerId(skinJson.get("id").getAsInt());
                if (skin != null) {
                    float scale = iconSize / 3;
                    if (y < colSize) {
                        int iconX = this.x + x * iconSize + 5;
                        int iconY = this.y + y * iconSize + 5 + 22;
                        int iconW = iconX + iconSize - 10;
                        int iconH = iconY + iconSize - 10;
                        int hoverColour = 0xC0101010;
                        if (mouseX >= iconX & mouseX < iconW) {
                            if (mouseY >= iconY & mouseY < iconH) {
                                hoverColour = 0xC0444410;
                            }
                        }
                        drawGradientRect(iconX, iconY, iconW, iconH, hoverColour, 0xD0101010);
                        
                        int size = fontRenderer.getStringWidth(skin.getCustomName());
                        fontRenderer.drawString(skin.getCustomName(), (int) (this.x + x * iconSize + iconSize / 2 - size / 2), this.y + y * iconSize + iconSize, 0xFFEEEEEE);
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                        GL11.glTranslatef(iconX + iconSize / 2, iconY + iconSize / 2 - 12, 200.0F);
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
            }
        }
    }
}
