package riskyken.armourersWorkshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.skinlibrary.GuiSkinLibrary;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinItemRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

@SideOnly(Side.CLIENT)
public class GuiFileListItem extends Gui implements IGuiListItem {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/controls/list.png");
    
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
        
        int fontColour = UtilColour.getMinecraftColor(8, ColourFamily.MINECRAFT);
        if (isHovering(fontRenderer, x, y, mouseX, mouseY, width)) {
            Gui.drawRect(x, y, x + width - 3, y + 12, 0xFFCCCCCC);
            fontColour = UtilColour.getMinecraftColor(15, ColourFamily.MINECRAFT);
        }
        if (selected) {
            Gui.drawRect(x, y, x + width - 3, y + 12, 0xFFFFFF88);
            fontColour = UtilColour.getMinecraftColor(15, ColourFamily.MINECRAFT);
        }
        if (!file.isDirectory()) {
            fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, fontColour);
            if (GuiSkinLibrary.showModelPreviews() | file.isDirectory()) {
                IGuiListItem item = this;
                if (item != null) {
                    SkinIdentifier identifier = new SkinIdentifier(0, new LibraryFile(file.getFullName()), 0, null);
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(identifier, true);
                    if (skin != null) {
                        SkinPointer skinPointer = new SkinPointer(identifier);
                        float scale = 10F;
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        GL11.glTranslatef((float)x + 5, (float)y + 6, 50.0F);
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
                        //ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true, false);
                        SkinItemRenderHelper.renderSkinAsItem(skin, skinPointer, true, false,
                                16,
                                16
                                );
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    }
                }
            }
        } else {
            GL11.glColor4f(1, 1, 1, 1);
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            if (file.fileName.equals("private")) {
                drawTexturedModalRect(x, y, 32, 0, 12, 12);
                fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, 0xFF8888FF);
            } else {
                drawTexturedModalRect(x, y, 16, 0, 10, 10);
                fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, 0xFF88FF88);
            }
            
            GL11.glColor4f(1, 1, 1, 1);
        }
        

        
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
