package riskyken.armourersWorkshop.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.Session;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader.IDownloadListCallback;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader.IDownloadSkinCallback;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinUtils;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class GuiGlobalSkinLibrary extends GuiScreen implements IDownloadListCallback, IDownloadSkinCallback {

    private final TileEntityGlobalSkinLibrary tileEntity;
    private ArrayList<String> remoteSkins = new ArrayList<String>();
    private ArrayList<String> downloadedSkinsList = new ArrayList<String>();
    
    private GuiButtonExt buttonDownload;
    private GuiButtonExt buttonUpload;
    private GuiButtonExt buttonAuthenticate;
    private int accountColour = 0xFFFFFFFF;
    private String account = "";
    private static ArrayList<SkinPointer> skinPointers = new ArrayList<SkinPointer>();
    
    public static enum BUTTONS {
        DOWNLOAD,
        UPLOAD,
        AUTHENTICATE
    }
    
    public GuiGlobalSkinLibrary(TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        
        buttonDownload = new GuiButtonExt(BUTTONS.DOWNLOAD.ordinal(), 5, 5, 100, 20, "Download Test");
        buttonUpload = new GuiButtonExt(BUTTONS.UPLOAD.ordinal(), 5, 30, 100, 20, "Upload Test");
        buttonUpload.enabled = false;
        buttonAuthenticate = new GuiButtonExt(BUTTONS.AUTHENTICATE.ordinal(), 5, 55, 100, 20, "Authenticate Test");
        buttonAuthenticate.enabled = false;
        
        buttonList.add(buttonDownload);
        buttonList.add(buttonUpload);
        buttonList.add(buttonAuthenticate);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonDownload) {
            buttonDownload.enabled = false;
            //MessageClientGuiButton message = new MessageClientGuiButton((byte) button.id);
            //PacketHandler.networkWrapper.sendToServer(message);
            
            remoteSkins.clear();
            SkinDownloader.downloadSkinList(this);
        }
        if (button == buttonUpload) {
            //SkinUploader.startUpload(Minecraft.getMinecraft().thePlayer);
        }
        if (button == buttonAuthenticate) {
            Session session = Minecraft.getMinecraft().getSession();
            String result = authenticatePlayer(session.getToken());
            if (result != null && result.equals("")) {
                account = "Your account is valid!";
                accountColour = 0xFF00FF00;
            } else {
                account = "Your account is not valid!";
                accountColour = 0xFFFF0000;
            }
        }
    }
    
    private String authenticatePlayer(String token) {
        ModLogger.log("Authenticate Test Started");
        //token = "badtokentest";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"accessToken\": \"" + token  + "\"");
        sb.append("}");
        ModLogger.log(sb.toString());
        String jsonResult = null;
        
        try {
            jsonResult = performPostRequest(new URL("https://authserver.mojang.com/validate"), sb.toString(), "application/json");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        ModLogger.log(jsonResult);
        ModLogger.log("Authenticate Test Finished");
        return jsonResult;
    }
    
    public String performPostRequest(URL url, String post, String contentType) throws IOException {
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        HttpURLConnection connection = createUrlConnection(url);
        byte[] postAsBytes = post.getBytes(Charsets.UTF_8);

        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);
        
        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            return result;
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                return result;
            } else {
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    protected HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
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
        synchronized (skinPointers) {
            skinPointers.clear();
        }
        synchronized (this.downloadedSkinsList) {
            this.downloadedSkinsList.clear();
            this.downloadedSkinsList.addAll(downloadedSkins);
        }
    }
    
    @Override
    public void updateScreen() {
        synchronized (downloadedSkinsList) {
            if (downloadedSkinsList.size() > 0) {
                remoteSkins.clear();
                remoteSkins.addAll(downloadedSkinsList);
                SkinDownloader.downloadSkins(this, new ArrayList<String>(remoteSkins));
                downloadedSkinsList.clear();
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
        drawTextCentered(account, this.width / 2, 42, accountColour);
        if (remoteSkins.size() > 0) {
            fontRendererObj.drawString("Remote Skins:", 5, 80, 0xFFFFFFFF);
        }
        for (int i = 0; i < remoteSkins.size(); i++) {
            fontRendererObj.drawString(remoteSkins.get(i), 5, 90 + i * 9, 0xFFFFFFFF);
        }
        
        drawRect(200, 60, width - 5, height - 5, 0x22FFFFFF);
        int boxW = width - 5 - 200;
        int boxH = height - 5 - 60;
        
        int iconSize = 30;
        
        synchronized (skinPointers) {
            for (int i = 0; i < skinPointers.size(); i++) {
                int x = (int) (i % Math.floor(boxW / iconSize));
                int y = (int) (i / Math.floor(boxW / iconSize));
                SkinPointer skinPonter = skinPointers.get(i);
                Skin skin = SkinUtils.getSkinDetectSide(skinPonter, false, false);
                if (skin != null) {
                    float scale = iconSize - 10;
                    GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                    GL11.glTranslatef(215F + x * iconSize, 80F + y * 40, 200.0F);
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
    
    private void drawTextCentered(String text, int x, int y, int colour) {
        int stringWidth = fontRendererObj.getStringWidth(text);
        fontRendererObj.drawString(text, x - (stringWidth / 2), y, colour);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void skinDownloaded(Skin skin, SkinPointer skinPointer) {
        ModelBakery.INSTANCE.receivedUnbakedModel(skin);
        synchronized (skinPointers) {
            skinPointers.add(skinPointer);
        }
    }
}
