package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader.IDownloadListCallback;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class GuiGlobalSkinLibrary extends GuiScreen implements IDownloadListCallback {

    private final TileEntityGlobalSkinLibrary tileEntity;
    private ArrayList<String> remoteSkins = new ArrayList<String>();
    private ArrayList<String> downloadedSkins = new ArrayList<String>();
    private GuiButtonExt buttonDownload;
    private GuiButtonExt buttonUpload;
    
    public GuiGlobalSkinLibrary(TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        
        buttonDownload = new GuiButtonExt(0, 5, 5, 100, 20, "Download Test");
        buttonUpload = new GuiButtonExt(1, 5, 35, 100, 20, "Upload Test");
        buttonUpload.enabled = false;
        
        buttonList.add(buttonDownload);
        buttonList.add(buttonUpload);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonDownload) {
            remoteSkins.clear();
            SkinDownloader.downloadSkinList(this);
        }
        if (button == buttonUpload) {
            //SkinUploader.startUpload(Minecraft.getMinecraft().thePlayer);
        }
    }
    
    @Override
    public void listDownloadFinished(JsonObject json) {
        ArrayList<String> downloadedSkins = new ArrayList<String>();
        JsonArray array =  json.getAsJsonArray("skins");
        for (int i = 0; i < array.size(); i++) {
            JsonElement item = array.get(i);
            if (!item.isJsonNull()) {
                downloadedSkins.add(item.getAsString());
            }
        }
        synchronized (this.downloadedSkins) {
            this.downloadedSkins.clear();
            this.downloadedSkins.addAll(downloadedSkins);
        }
    }
    
    @Override
    public void updateScreen() {
        synchronized (downloadedSkins) {
            if (downloadedSkins.size() > 0) {
                remoteSkins.clear();
                remoteSkins.addAll(downloadedSkins);
                downloadedSkins.clear();
            }
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        this.drawRect(0, 0, this.width, this.height, 0xCC000000);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        drawTextCentered(tileEntity.getBlockType().getLocalizedName(), this.width / 2, 2, UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT));
        drawTextCentered("WARNING - This block is unfinished.", this.width / 2, 22, 0xFF0000);
        drawTextCentered("!!! - DO NOT USE - !!!", this.width / 2, 32, 0xFF0000);
        if (remoteSkins.size() > 0) {
            fontRendererObj.drawString("Remote Skins:", 5, 60, 0xFFFFFFFF);
        }
        for (int i = 0; i < remoteSkins.size(); i++) {
            fontRendererObj.drawString(remoteSkins.get(i), 5, 70 + i * 10, 0xFFFFFFFF);
        }
    }
    
    private void drawTextCentered(String text, int x, int y, int colour) {
        int stringWidth = fontRendererObj.getStringWidth(text);
        fontRendererObj.drawString(text, x - (stringWidth / 2), y, colour);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
