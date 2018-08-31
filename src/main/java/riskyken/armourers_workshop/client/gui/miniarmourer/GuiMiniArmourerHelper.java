package riskyken.armourers_workshop.client.gui.miniarmourer;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    
    public static Color getColourFromId(int id) {
        int r = 0;
        int g = 0;
        int b = 0;
        while (id > 255 * 256) {
            b += 1;
            id -= 256 * 256;
        }
        while (id > 255) {
            g += 1;
            id -= 256;
        }
        while (id > 0) {
            r += 1;
            id -= 1;
        }
        return new Color(r, g, b);
    }
    
    public static EnumFacing getDirectionForCubeFace(int cubeFace) {
        EnumFacing dir = null;
        switch (cubeFace) {
        case 1:
            dir = EnumFacing.EAST;
            break;
        case 0:
            dir = EnumFacing.WEST;
            break;
        case 4:
            dir = EnumFacing.DOWN;
            break;
        case 5:
            dir = EnumFacing.UP;
            break;
        case 3:
            dir = EnumFacing.NORTH;
            break;
        case 2:
            dir = EnumFacing.SOUTH;
            break;
        }
        return dir;
    }
}
