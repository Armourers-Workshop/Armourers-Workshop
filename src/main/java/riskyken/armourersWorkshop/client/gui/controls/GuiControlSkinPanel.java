package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

@SideOnly(Side.CLIENT)
public class GuiControlSkinPanel extends GuiButtonExt {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/controls/skin-panel.png");
    
    private final ArrayList<SkinIcon> iconList;
    private int panelPadding;
    private int iconPadding;
    private int iconSize;
    /** Number of icons that can fit into this control. */
    private int iconCount;
    private int rowCount;
    private int colCount;
    private boolean showName;
    private SkinIcon lastPressedSkinIcon;
    
    public GuiControlSkinPanel() {
        this(0, 0, 0, 0);
    }
    
    public GuiControlSkinPanel(int xPos, int yPos, int width, int height) {
        super(0, xPos, yPos, width, height, "");
        iconList = new ArrayList<SkinIcon>();
    }
    
    public void init(int x, int y, int width, int height) {
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
        this.panelPadding = 2;
        this.iconPadding = 5;
        this.iconSize = 50;
        this.showName = false;
        this.lastPressedSkinIcon = null;
        updateIconCount();
    }
    
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        updateIconCount();
    }
    
    public void setPanelPadding(int panelPadding) {
        this.panelPadding = panelPadding;
    }
    
    public void setShowName(boolean showName) {
        this.showName = showName;
    }
    
    public void updateIconCount() {
        int boxW = width + iconPadding - panelPadding * 2;
        int boxH = height + iconPadding - panelPadding * 2;
        rowCount = Math.max(1, (int) Math.floor(boxW / (iconSize + iconPadding))) ;
        colCount = Math.max(1, (int) Math.floor(boxH / (iconSize + iconPadding)));
        iconCount = rowCount * colCount;
    }
    
    public int getIconCount() {
        return iconCount;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hover = this.getHoverState(this.field_146123_n);
            drawGradientRect(xPosition, yPosition, xPosition + this.width, yPosition + height, 0xC0222222, 0xD0333333);
            
            for (int i = 0; i < iconList.size(); i++) {
                int x = i % rowCount;
                int y = (int) (i / rowCount);
                int iconX = xPosition + x * (iconSize + iconPadding) + panelPadding;
                int iconY = yPosition + y * (iconSize + iconPadding) + panelPadding;
                
                SkinIcon skinIcon = iconList.get(i);
                if (y < colCount) {
                    skinIcon.drawIcon(iconX, iconY, mouseX, mouseY, iconSize, showName);
                }
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        for (int i = 0; i < iconList.size(); i++) {
            int x = i % rowCount;
            int y = (int) (i / rowCount);
            int iconX = xPosition + x * (iconSize + iconPadding) + panelPadding;
            int iconY = yPosition + y * (iconSize + iconPadding) + panelPadding;
            
            SkinIcon skinIcon = iconList.get(i);
            if (y < colCount) {
                if (skinIcon.mouseOver(iconX, iconY, mouseX, mouseY, iconSize)) {
                    lastPressedSkinIcon = skinIcon;
                    return true;
                }
            }
        }
        return false;
    }
    
    public SkinIcon getLastPressedSkinIcon() {
        return lastPressedSkinIcon;
    }
    
    public void clearIcons() {
        iconList.clear();
    }

    public void addIcon(JsonObject skinJson) {
        iconList.add(new SkinIcon(skinJson));
    }
    
    public class SkinIcon {
        
        private final JsonObject skinJson;
        private final int id;
        
        public SkinIcon(JsonObject skinJson) {
            this.skinJson = skinJson;
            id = skinJson.get("id").getAsInt();
        }
        
        public JsonObject getSkinJson() {
            return skinJson;
        }
        
        public void drawIcon(int x, int y, int mouseX, int mouseY, int iconSize, boolean showName) {
            if (mouseOver(x, y, mouseX, mouseY, iconSize)) {
                drawRect(x, y, x + iconSize, y + iconSize, 0xC0777711);
            } else {
                drawRect(x, y, x + iconSize, y + iconSize, 0x22FFFFFF);
            }
            SkinIdentifier identifier = new SkinIdentifier(0, null, id, null);

            Skin skin = ClientSkinCache.INSTANCE.getSkin(identifier);
            if (skin != null) {
                if (showName) {
                    String name = skinJson.get("name").getAsString();
                    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                    int size = fontRenderer.getStringWidth(skin.getCustomName());
                    
                    List list = fontRenderer.listFormattedStringToWidth(name, iconSize - 2);
                    
                    int textY = y + iconSize;
                    textY -= fontRenderer.FONT_HEIGHT * list.size();
                    
                    fontRenderer.drawSplitString(name, x + 1, textY, iconSize - 2, 0xFFEEEEEE);
                }
                
                float scale = iconSize / 2;
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glTranslatef(x + iconSize / 2, y + iconSize / 2 - 4, 200.0F);
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
                
            } else {
                Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
                int speed = 60;
                int frames = 18;
                
                int frame = (int) ((System.currentTimeMillis() / (long)speed) % frames);
                int u = MathHelper.floor_double(frame / 9);
                int v = frame - u * 9;
                
                Gui.func_152125_a(
                        x + 8, y + 8,
                        u * 28, v * 28,
                        27, 27,
                        iconSize - 16, iconSize - 16,
                        256, 256
                        );
            }
        }
        
        public boolean mouseOver(int x, int y, int mouseX, int mouseY, int iconSize) {
            return mouseX >= x & mouseY >= y & mouseX < x + iconSize & mouseY < y + iconSize;
        }
    }
}
