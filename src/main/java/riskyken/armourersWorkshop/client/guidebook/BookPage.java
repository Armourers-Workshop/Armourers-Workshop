package riskyken.armourersWorkshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookPage implements IBookPage {

    private static final int TEXT_COLOUR = 0xFF000000;
    private final ArrayList<String> lines;
    
    public BookPage(ArrayList<String> lines) {
        this.lines = lines;
    }
    
    public List<String> getLines() {
        return lines;
    }

    @Override
    public void renderPage(FontRenderer fontRenderer, int x, int y) {
        for (int i = 0; i < lines.size(); i++) {
            fontRenderer.drawString(lines.get(i), x, y + i * 9, TEXT_COLOUR);
        }
    }
}
