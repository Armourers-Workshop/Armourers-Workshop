package moe.plushie.armourers_workshop.client.gui.controls;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControlSkinPanel extends GuiButtonExt {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_SKIN_PANEL);

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
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.panelPadding = 1;
        this.iconPadding = 1;
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
        rowCount = Math.max(1, (int) Math.floor(boxW / (iconSize + iconPadding)));
        colCount = Math.max(1, (int) Math.floor(boxH / (iconSize + iconPadding)));
        iconCount = rowCount * colCount;
    }

    public int getIconCount() {
        return iconCount;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hover = this.getHoverState(this.hovered);
            drawGradientRect(x, y, x + this.width, y + height, 0xC0222222, 0xD0333333);

            for (int i = 0; i < iconList.size(); i++) {
                int x = i % rowCount;
                int y = i / rowCount;
                int iconX = this.x + x * (iconSize + iconPadding) + panelPadding;
                int iconY = this.y + y * (iconSize + iconPadding) + panelPadding;

                SkinIcon skinIcon = iconList.get(i);
                if (y < colCount) {
                    skinIcon.drawIcon(iconX, iconY, mouseX, mouseY, iconSize, showName & iconSize > 30);
                }
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        for (int i = 0; i < iconList.size(); i++) {
            int x = i % rowCount;
            int y = i / rowCount;
            int iconX = this.x + x * (iconSize + iconPadding) + panelPadding;
            int iconY = this.y + y * (iconSize + iconPadding) + panelPadding;

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
            Rectangle recScissor = ModRenderHelper.getScissor();
            if (recScissor != null) {
                if (!recScissor.intersects(x, y, iconSize, iconSize)) {
                    return;
                }
            }

            int backgroundColour = 0x22AAAAAA;
            int borderColour = 0x22FFFFFF;

            if (mouseOver(x, y, mouseX, mouseY, iconSize)) {
                backgroundColour = 0xC0777711;
                borderColour = 0xCC888811;
            }

            ModRenderHelper.enableScissor(x, y, iconSize, iconSize, true);

            drawRect(x, y, x + iconSize, y + iconSize, backgroundColour);

            drawRect(x, y + 1, x + 1, y + iconSize, borderColour);
            drawRect(x, y, x + iconSize - 1, y + 1, borderColour);
            drawRect(x + 1, y + iconSize - 1, x + iconSize, y + iconSize, borderColour);
            drawRect(x + iconSize - 1, y, x + iconSize, y + iconSize - 1, borderColour);

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

                Minecraft.getMinecraft().renderEngine.bindTexture(skin.getSkinType().getIcon());
                GlStateManager.color(1F, 1F, 1F, 1F);
                Gui.drawScaledCustomSizeModalRect(x, y, 0, 0, 16, 16, (int) (iconSize / 4F), (int) (iconSize / 4F), 16, 16);

                float scale = iconSize / 2;
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                if (showName) {
                    GlStateManager.translate(x + iconSize / 2, y + iconSize / 2 - 4, 200.0F);
                } else {
                    GlStateManager.translate(x + iconSize / 2, y + iconSize / 2, 200.0F);
                }
                GlStateManager.scale((-10), 10, 10);
                if (mouseOver(x, y, mouseX, mouseY, iconSize)) {
                    GlStateManager.scale(1.5F, 1.5F, 1.5F);
                }
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

                SkinItemRenderHelper.renderSkinAsItem(skin, new SkinDescriptor(identifier), true, false, iconSize, iconSize);

                GlStateManager.disableDepth();

                ModRenderHelper.disableAlphaBlend();
                GlStateManager.disableNormalize();
                GlStateManager.disableColorMaterial();

                RenderHelper.disableStandardItemLighting();
                GlStateManager.resetColor();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();

            } else {
                Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
                int speed = 60;
                int frames = 18;

                int frame = (int) ((System.currentTimeMillis() / speed) % frames);
                int u = MathHelper.floor(frame / 9);
                int v = frame - u * 9;

                Gui.drawScaledCustomSizeModalRect(x + 8, y + 8, u * 28, v * 28, 27, 27, iconSize - 16, iconSize - 16, 256, 256);
            }
            ModRenderHelper.disableScissor();
        }

        public boolean mouseOver(int x, int y, int mouseX, int mouseY, int iconSize) {
            return mouseX >= x & mouseY >= y & mouseX < x + iconSize & mouseY < y + iconSize;
        }
    }
}
