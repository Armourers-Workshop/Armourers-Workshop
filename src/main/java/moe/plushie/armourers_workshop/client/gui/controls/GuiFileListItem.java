package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.skinlibrary.GuiSkinLibrary;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiFileListItem extends Gui implements IGuiListItem {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.CONTROL_LIST);

    private final LibraryFile file;

    public GuiFileListItem(LibraryFile file) {
        this.file = file;
    }

    public LibraryFile getFile() {
        return file;
    }

    @Override
    public void drawListItem(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, boolean selected, int width) {
        int iconOffset = 0;

        if (GuiSkinLibrary.showModelPreviews() | file.isDirectory()) {
            iconOffset = 10;
        }

        int fontColour = 0xFFAAAAAA;
        if (isHovering(fontRenderer, x, y, mouseX, mouseY, width)) {
            Gui.drawRect(x, y, x + width - 3, y + 12, 0xFFCCCCCC);
            fontColour = 0xFF000000;
        }
        if (selected) {
            Gui.drawRect(x, y, x + width - 3, y + 12, 0xFFFFFF88);
            fontColour = 0xFF000000;
        }
        if (!file.isDirectory()) {
            fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, fontColour);
            if (GuiSkinLibrary.showModelPreviews() | file.isDirectory()) {
                IGuiListItem item = this;
                if (item != null) {
                    SkinIdentifier identifier = new SkinIdentifier(0, new LibraryFile(file.getFullName()), 0, null);
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(identifier, true);
                    if (skin != null) {
                        SkinDescriptor skinPointer = new SkinDescriptor(identifier);
                        float scale = 10F;
                        GlStateManager.pushMatrix();
                        GL11.glTranslatef((float) x + 5, (float) y + 6, 50.0F);
                        GL11.glScalef((-scale), scale, scale);
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                        float rotation = (float) ((double) System.currentTimeMillis() / 10 % 360);
                        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

                        GlStateManager.pushAttrib();
                        RenderHelper.enableGUIStandardItemLighting();
                        GlStateManager.color(1F, 1F, 1F, 1F);
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableNormalize();
                        ModRenderHelper.enableAlphaBlend();
                        SkinItemRenderHelper.renderSkinAsItem(skin, skinPointer, true, false, 16, 16);
                        ModRenderHelper.disableAlphaBlend();
                        GlStateManager.disableNormalize();
                        GlStateManager.disableColorMaterial();
                        GlStateManager.disableRescaleNormal();
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }
                }
            }
        } else {
            GlStateManager.color(1F, 1F, 1F, 1F);
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            if (file.fileName.equals("private")) {
                drawTexturedModalRect(x, y, 32, 0, 12, 12);
                fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, 0xFF8888FF);
            } else {
                drawTexturedModalRect(x, y, 16, 0, 10, 10);
                fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, 0xFF88FF88);
            }
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.resetColor();
    }

    @Override
    public boolean mousePressed(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button, int width) {
        return isHovering(fontRenderer, x, y, mouseX, mouseY, width);
    }

    @Override
    public void mouseReleased(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button, int width) {
    }

    private boolean isHovering(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int width) {
        return mouseX >= x & mouseY >= y & mouseX <= x + width - 3 & mouseY <= y + 11;
    }

    @Override
    public String getDisplayName() {
        return file.fileName;
    }
}
