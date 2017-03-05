package riskyken.armourersWorkshop.client.gui.miniarmourer;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;

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
    
    public static ForgeDirection getDirectionForCubeFace(int cubeFace) {
        ForgeDirection dir;
        switch (cubeFace) {
        case 1:
            dir = ForgeDirection.EAST;
            break;
        case 0:
            dir = ForgeDirection.WEST;
            break;
        case 4:
            dir = ForgeDirection.DOWN;
            break;
        case 5:
            dir = ForgeDirection.UP;
            break;
        case 3:
            dir = ForgeDirection.NORTH;
            break;
        case 2:
            dir = ForgeDirection.SOUTH;
            break;
        default:
            dir = ForgeDirection.UNKNOWN;
            break;
        }
        return dir;
    }
}
