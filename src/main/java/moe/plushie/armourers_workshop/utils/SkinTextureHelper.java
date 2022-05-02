package moe.plushie.armourers_workshop.utils;//package moe.plushie.armourers_workshop.core.skin;
//
//import java.awt.Point;
//
//import moe.plushie.armourers_workshop.core.skin.Vector3i;
//import moe.plushie.armourers_workshop.core.skin.type.ISkinPartTypeTextured;
//import moe.plushie.armourers_workshop.core.tileentities.TileEntityBoundingBox;
//import net.minecraft.util.EnumFacing;
//
///**
// * Helps convert a block in the world into
// * texture coordinates for the players skin.
// *
// * @author RiskyKen
// *
// */
//public class SkinTextureHelper {
//
//    public static Point getTextureLocationFromWorldBlock(TileEntityBoundingBox te, EnumFacing side) {
//        return getTextureLocationFromBlock(te.getGuideX(), te.getGuideY(), te.getGuideZ(), (ISkinPartTypeTextured) te.getSkinPart(), side);
//    }
//
//    public static Point getTextureLocationFromBlock(byte blockX, byte blockY, byte blockZ, ISkinPartTypeTextured skinPart, EnumFacing side) {
//        Point textureLocation = skinPart.getTextureSkinPos();
//        Vector3i textureModelSize = skinPart.getTextureModelSize();
//        EnumFacing blockFace = side;
//
//        int textureX = textureLocation.x;
//        int textureY = textureLocation.y;
//
//        int shiftX = 0;
//        int shiftY = 0;
//
//        if (skinPart.isTextureMirrored()) {
//            if (blockFace == EnumFacing.EAST | blockFace == EnumFacing.WEST) {
//                blockFace = blockFace.getOpposite();
//            }
//        }
//
//        switch (blockFace) {
//        case EAST:
//            textureY += textureModelSize.getZ();
//            shiftX = (byte) (-blockZ + textureModelSize.getZ() - 1);
//            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
//            break;
//        case NORTH:
//            textureX += textureModelSize.getZ();
//            textureY += textureModelSize.getZ();
//            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
//            if (skinPart.isTextureMirrored()) {
//                shiftX = (byte) (blockX);
//            }
//            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
//            break;
//        case WEST:
//            textureX += textureModelSize.getZ() + textureModelSize.getX();
//            textureY += textureModelSize.getZ();
//            shiftX = blockZ;
//            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
//            break;
//        case SOUTH:
//            textureX += textureModelSize.getZ() + textureModelSize.getX() + textureModelSize.getZ();
//            textureY += textureModelSize.getZ();
//            shiftX = blockX;
//            if (skinPart.isTextureMirrored()) {
//                shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
//            }
//            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
//            break;
//        case DOWN:
//            textureX += textureModelSize.getZ() + textureModelSize.getX();
//            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
//            if (skinPart.isTextureMirrored()) {
//                shiftX = blockX;
//            }
//            shiftY = (byte) (-blockZ + textureModelSize.getZ() - 1);
//            break;
//        case UP:
//            textureX += textureModelSize.getZ();
//            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
//            if (skinPart.isTextureMirrored()) {
//                shiftX = blockX;
//            }
//            shiftY = (byte) (-blockZ + textureModelSize.getZ() - 1);
//            break;
//        default:
//            break;
//        }
//
//        textureX += shiftX;
//        textureY += shiftY;
//        return new Point(textureX, textureY);
//    }
//}