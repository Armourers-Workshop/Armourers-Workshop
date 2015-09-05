package riskyken.armourersWorkshop.client.gui.controls;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary.LibraryFile;
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
        int fontColour = UtilColour.getMinecraftColor(8, ColourFamily.MINECRAFT);
        if (isHovering(fontRenderer, x, y, mouseX, mouseY, width)) {
            Gui.drawRect(x, y, x + width - 3, y + 12, 0xFFCCCCCC);
            fontColour = UtilColour.getMinecraftColor(15, ColourFamily.MINECRAFT);
        }
        if (selected) {
            Gui.drawRect(x, y, x + width - 3, y + 12, 0xFFFFFF88);
            fontColour = UtilColour.getMinecraftColor(15, ColourFamily.MINECRAFT);
        }
        

        fontRenderer.drawString(file.fileName, x + 2, y + 2, fontColour);
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
