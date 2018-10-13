package moe.plushie.armourers_workshop.client.model.bake;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.util.math.MathHelper;

public class ColouredFace {

    public final byte x;
    public final byte y;
    public final byte z;

    public final byte r;
    public final byte g;
    public final byte b;
    private final byte a;

    private final byte t;
    public final byte face;
    private final byte lodLevel;

    public ColouredFace(byte x, byte y, byte z, byte r, byte g, byte b, byte a, byte paintType, byte face, byte lodLevel) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        this.t = paintType;
        this.face = face;
        this.lodLevel = lodLevel;
    }

    public void renderVertex(IRenderBuffer renderBuffer, ISkinDye skinDye, ExtraColours extraColours, ClientSkinPartData cspd, boolean useTexture) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        int type = t & 0xFF;
        if (type != PaintType.NONE.getKey()) {
            // Dye
            if (type >= 1 && type <= 8) {
                // Is a dye paint
                if (skinDye != null && skinDye.haveDyeInSlot(type - 1)) {
                    byte[] dye = skinDye.getDyeColour(type - 1);
                    if (dye.length == 4) {
                        if ((dye[3] & 0xFF) == 0) {
                            return;
                        }
                        int dyeType = dye[3] & 0xFF;
                        int[] averageRGB = cspd.getAverageDyeColour(type - 1);
                        byte[] dyedColour = null;
                        if (dyeType == PaintType.SKIN.getKey() & extraColours != null) {
                            dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.SKIN), averageRGB);
                        } else if (dyeType == PaintType.HAIR.getKey() & extraColours != null) {
                            dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.HAIR), averageRGB);
                        } else if (dyeType == PaintType.EYE.getKey() & extraColours != null) {
                            dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.EYE), averageRGB);
                        } else if (dyeType == PaintType.MISC.getKey() & extraColours != null) {
                            dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.MISC), averageRGB);
                        } else {
                            dyedColour = dyeColour(r, g, b, dye, averageRGB);
                        }
                        r = dyedColour[0];
                        g = dyedColour[1];
                        b = dyedColour[2];
                    }
                }
            }
            if (extraColours != null) {
                // Skin
                if (type == PaintType.SKIN.getKey()) {
                    int[] averageRGB = cspd.getAverageDyeColour(8);
                    byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.SKIN), averageRGB);
                    r = dyedColour[0];
                    g = dyedColour[1];
                    b = dyedColour[2];
                }
                // Hair
                if (type == PaintType.HAIR.getKey()) {
                    int[] averageRGB = cspd.getAverageDyeColour(9);
                    byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.HAIR), averageRGB);
                    r = dyedColour[0];
                    g = dyedColour[1];
                    b = dyedColour[2];
                }

                // Eye
                if (type == PaintType.EYE.getKey()) {
                    int[] averageRGB = cspd.getAverageDyeColour(10);
                    byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.EYE), averageRGB);
                    r = dyedColour[0];
                    g = dyedColour[1];
                    b = dyedColour[2];
                }
                // Misc
                if (type == PaintType.MISC.getKey()) {
                    int[] averageRGB = cspd.getAverageDyeColour(11);
                    byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(ExtraColourType.MISC), averageRGB);
                    r = dyedColour[0];
                    g = dyedColour[1];
                    b = dyedColour[2];
                }
            }

            FaceRenderer.renderFace(x, y, z, r, g, b, a, face, useTexture, lodLevel);
        }
    }

    /**
     * Create a new colour for a dyed vertex.
     * 
     * @param dyeColour          RGB byte array.
     * @param modelAverageColour RGB int array.
     * @return
     */
    public static byte[] dyeColour(byte r, byte g, byte b, byte[] dyeColour, int[] modelAverageColour) {
        if (dyeColour.length == 4) {
            if ((dyeColour[3] & 0xFF) == 0) {
                ModLogger.log("fail?");
                return new byte[] { r, g, b };
            }
        }

        int average = ((r & 0xFF) + (g & 0xFF) + (b & 0xFF)) / 3;
        int modelAverage = (modelAverageColour[0] + modelAverageColour[1] + modelAverageColour[2]) / 3;
        int nR = (int) (average + (dyeColour[0] & 0xFF) - modelAverage);
        int nG = (int) (average + (dyeColour[1] & 0xFF) - modelAverage);
        int nB = (int) (average + (dyeColour[2] & 0xFF) - modelAverage);
        nR = MathHelper.clamp(nR, 0, 255);
        nG = MathHelper.clamp(nG, 0, 255);
        nB = MathHelper.clamp(nB, 0, 255);
        return new byte[] { (byte) nR, (byte) nG, (byte) nB };
    }
}
