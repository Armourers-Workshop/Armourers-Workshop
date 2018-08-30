package riskyken.armourersWorkshop.common.skin;

import java.awt.Point;

import net.minecraft.util.EnumFacing;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;

/**
 * Helps convert a block in the world into
 * texture coordinates for the players skin.
 * 
 * @author RiskyKen
 *
 */
public class SkinTextureHelper {
    
    public static Point getTextureLocationFromWorldBlock(TileEntityBoundingBox te, int side) {
        ISkinPartTypeTextured skinPart = (ISkinPartTypeTextured) te.getSkinPart();
        Point textureLocation = skinPart.getTextureLocation();
        IPoint3D textureModelSize = skinPart.getTextureModelSize();
        EnumFacing blockFace = EnumFacing.getFront(side);
        
        byte blockX = te.getGuideX();
        byte blockY = te.getGuideY();
        byte blockZ = te.getGuideZ();
        
        int textureX = textureLocation.x;
        int textureY = textureLocation.y;
        
        int shiftX = 0;
        int shiftY = 0;
        
        if (skinPart.isTextureMirrored()) {
            if (blockFace == EnumFacing.EAST | blockFace == EnumFacing.WEST) {
                blockFace = blockFace.getOpposite();
            }
        }
        
        switch (blockFace) {
        case EAST:
            textureY += textureModelSize.getZ();
            shiftX = (byte) (-blockZ + textureModelSize.getZ() - 1);
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case NORTH:
            textureX += textureModelSize.getZ();
            textureY += textureModelSize.getZ();
            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            if (skinPart.isTextureMirrored()) {
                shiftX = (byte) (blockX);
            }
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case WEST:
            textureX += textureModelSize.getZ() + textureModelSize.getX();
            textureY += textureModelSize.getZ();
            shiftX = blockZ;
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case SOUTH:
            textureX += textureModelSize.getZ() + textureModelSize.getX() + textureModelSize.getZ();
            textureY += textureModelSize.getZ();
            shiftX = blockX;
            if (skinPart.isTextureMirrored()) {
                shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            }
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case DOWN:
            textureX += textureModelSize.getZ() + textureModelSize.getX();
            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            if (skinPart.isTextureMirrored()) {
                shiftX = blockX;
            }
            shiftY = (byte) (-blockZ + textureModelSize.getZ() - 1);
            break;
        case UP:
            textureX += textureModelSize.getZ();
            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            if (skinPart.isTextureMirrored()) {
                shiftX = blockX;
            }
            shiftY = (byte) (-blockZ + textureModelSize.getZ() - 1);
            break;
        default:
            break;
        }
        
        textureX += shiftX;
        textureY += shiftY;
        return new Point(textureX, textureY);
    }
}
