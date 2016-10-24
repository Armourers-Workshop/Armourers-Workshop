package riskyken.armourersWorkshop.client.gui.miniarmourer;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiMiniArmourerHelper {
    
    private GuiMiniArmourerHelper() {}
    
    public static Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r,g,b);
    }
    
    public static int getIdFromColour(Color colour) {
        Color c = new Color(colour.getRGB());
        int id = c.getRed();
        id += c.getGreen() * 256;
        id += c.getBlue() * 256 * 256;
        return id;
    }
}
