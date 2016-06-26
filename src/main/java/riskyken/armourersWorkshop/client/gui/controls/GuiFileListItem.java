package riskyken.armourersWorkshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

@SideOnly(Side.CLIENT)
public class GuiFileListItem extends Gui implements IGuiListItem {

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
        
        if (ConfigHandler.libraryShowsModelPreviews) {
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
        
        fontRenderer.drawString(file.fileName, x + 2 + iconOffset, y + 2, fontColour);
        
        if (ConfigHandler.libraryShowsModelPreviews) {
            IGuiListItem item = this;
            if (item != null) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(item.getDisplayName(), true);
                if (skin != null) {
                    SkinPointer skinPointer = new SkinPointer(skin.getSkinType(), skin.lightHash());
                    float scale = 8F;
                    GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
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
                    ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            }
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
