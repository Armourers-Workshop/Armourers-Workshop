package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.gui.FontRenderer;


public class GuiFileListItem implements IGuiListItem {

    private final String displayText;
    
    public GuiFileListItem(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public void drawListItem(FontRenderer fontRenderer, int x, int y, int relativeX, int relativeY) {
        // TODO Auto-generated method stub
        fontRenderer.drawString(displayText, x, y, 4210752);
    }

    @Override
    public boolean mousePressed(int x, int y, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void mouseReleased(int x, int y, int button) {
        // TODO Auto-generated method stub
        
    }
    
}
