package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;

import java.awt.*;

public final class PaintingUtils {

    private static final String TAG_TOOL_PAINT = "toolPaint";

    /**
     * Returns true if the item stack has paint data.
     *
     * @param stack Item stack to check.
     * @return True if the stack has paint data otherwise false.
     */
    public static boolean getToolHasPaint(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains(TAG_TOOL_PAINT)) {
            return true;
        }
        return false;
    }

    /**
     * Sets an RGB colour on an item stack.
     *
     * @param stack Item stack to set the colour on.
     * @param rgb   RGB colour to set.
     */
    public static void setToolPaintColour(ItemStack stack, byte[] rgb) {
        byte[] rgbt = getToolPaintData(stack);
        rgbt[0] = rgb[0];
        rgbt[1] = rgb[1];
        rgbt[2] = rgb[2];
        setToolPaintData(stack, rgbt);
    }

    /**
     * Sets an RGB colour on an item stack.
     *
     * @param stack Item stack to set the colour on.
     * @param rgb   RGB colour to set.
     */
    public static void setToolPaintColour(ItemStack stack, int colour) {
        byte[] rgbt = getToolPaintData(stack);
        Color c = new Color(colour);
        rgbt[0] = (byte) c.getRed();
        rgbt[1] = (byte) c.getGreen();
        rgbt[2] = (byte) c.getBlue();
        setToolPaintData(stack, rgbt);
    }

    /**
     * Gets the RGB colour from an item stack.
     *
     * @param stack Item stack to get the colour from.
     * @return An RGB byte array.
     */
    public static byte[] getToolPaintColourArray(ItemStack stack) {
        byte[] rgbt = getToolPaintData(stack);
        return new byte[]{rgbt[0], rgbt[1], rgbt[2]};
    }

    /**
     * Gets the RGB colour from an item stack.
     *
     * @param stack Item stack to get the colour from.
     * @return An RGB int.
     */
    public static int getToolPaintColourRGB(ItemStack stack) {
        return getToolPaintColour(stack).getRGB();
    }

    /**
     * Gets the RGB colour from an item stack.
     *
     * @param stack Item stack to get the colour from.
     * @return A java.awt.color.
     */
    public static Color getToolPaintColour(ItemStack stack) {
        byte[] rgbt = getToolPaintData(stack);
        return new Color(rgbt[0] & 0xFF, rgbt[1] & 0xFF, rgbt[2] & 0xFF, 255);
    }

//    public static Color getToolDisplayColour(ItemStack stack, int ticks) {
//        byte[] rgbt = getToolPaintData(stack);
//        ISkinPaintType paintType = SkinPaintTypes.byId(rgbt[3]);
//        if (paintType == SkinPaintTypes.RAINBOW) {
//            return getRainbowColour(ticks);
//        } else if (paintType == SkinPaintTypes.PULSE_1) {
//            return getPulse1Colour(rgbt, ticks);
//        } else if (paintType == SkinPaintTypes.PULSE_2) {
//            return getPulse2Colour(rgbt, ticks);
//        }
//        return new Color(rgbt[0] & 0xFF, rgbt[1] & 0xFF, rgbt[2] & 0xFF, 255);
//    }

    /**
     * Set a paint type on an item stack.
     *
     * @param stack     Item stack to set the paint type on.
     * @param paintType Paint type to set.
     */
    public static void setToolPaint(ItemStack stack, ISkinPaintType paintType) {
        byte[] rgbt = getToolPaintData(stack);
        rgbt[3] = (byte) paintType.getId();
        setToolPaintData(stack, rgbt);
    }

    /**
     * Get the paint type from an item stack.
     *
     * @param stack Item stack to get the paint type from.
     * @return Paint type enum.
     */
    public static ISkinPaintType getToolPaintType(ItemStack stack) {
        byte[] rgbt = getToolPaintData(stack);
        return SkinPaintTypes.byId(rgbt[3]);
    }

    public static byte[] getToolPaintData(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains(TAG_TOOL_PAINT, NBT.TAG_BYTE_ARRAY)) {
            return compound.getByteArray(TAG_TOOL_PAINT);
        }
        return getBlankPaintData();
    }

    public static void setToolPaintData(ItemStack stack, byte[] paintData) {
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            compound = new CompoundNBT();
        }
        compound.putByteArray(TAG_TOOL_PAINT, paintData);
        stack.setTag(compound);
    }

    public static void setPaintData(CompoundNBT compound, byte[] paintData) {
        compound.putByteArray(TAG_TOOL_PAINT, paintData);
    }

    public static byte[] getBlankRGBColour() {
        return new byte[]{(byte) 255, (byte) 255, (byte) 255};
    }

    public static byte[] getBlankPaintData() {
        return new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255};
    }
//
//    public static byte[] intToBytes(int trgb) {
//        int t = 0xFF & (trgb >> 24);
//        int r = 0xFF & (trgb >> 16);
//        int g = 0xFF & (trgb >> 8);
//        int b = 0xFF & (trgb >> 0);
//
//        return new byte[]{(byte) r, (byte) g, (byte) b, (byte) t};
//    }
//
//    public static int bytesToInt(byte[] rgbt) {
//        return ((rgbt[3] & 0xFF) << 24) | ((rgbt[0] & 0xFF) << 16) | ((rgbt[1] & 0xFF) << 8) | (rgbt[2] & 0xFF);
//    }

    public static float getPaintTextureOffset(int ticks) {
        double f = ticks % (255L * 25) / 25f;
        return Math.round(f);
    }

//    private static Color getRainbowColour(int ticks) {
//        float f = ticks % (255L * 25) / 25F;
//        return new Color(Color.HSBtoRGB(f / 255F, 1F, 1F));
//    }
//
//    private static Color getPulse1Colour(byte[] rgbt, int ticks) {
//        float f = (float) (ticks % (255L * 25D)) / 25F;
//        f = f * 2F;
//        if (f > 255) {
//            f = 255F - (f - 255);
//        }
//        f = MathHelper.clamp(f, 0, 255);
//        float[] hsb = Color.RGBtoHSB(rgbt[0] & 0xFF, rgbt[1] & 0xFF, rgbt[2] & 0xFF, null);
//        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], f / 255F));
//    }
//
//    private static Color getPulse2Colour(byte[] rgbt, int ticks) {
//        float f = (float) (ticks % (255L * 12.5D)) / 12.5F;
//        f = f * 2F;
//        if (f > 255) {
//            f = 255F - (f - 255);
//        }
//        f = MathHelper.clamp(f, 0, 255);
//        float[] hsb = Color.RGBtoHSB(rgbt[0] & 0xFF, rgbt[1] & 0xFF, rgbt[2] & 0xFF, null);
//        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], f / 255F));
//    }

//    @SideOnly(Side.CLIENT)
//    public static IExtraColours getLocalPlayerExtraColours() {
//        IWardrobeCap wardrobeCapability = WardrobeCap.get(Minecraft.getMinecraft().player);
//        if (wardrobeCapability != null) {
//            return wardrobeCapability.getExtraColours();
//        }
//        return null;
//    }
}
