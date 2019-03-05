package moe.plushie.armourers_workshop.client.model.bake;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintType;
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

    public void renderVertex(IRenderBuffer renderBuffer, SkinPartRenderData renderData, ClientSkinPartData cspd) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        PaintType type = PaintRegistry.getPaintTypeFormByte(t);
        ISkinDye skinDye = renderData.getSkinDye();
        ExtraColours extraColours = renderData.getExtraColours();
        if (type == PaintRegistry.PAINT_TYPE_NONE) {
            return;
        }
        // Dye
        if (type.getId() >= 1 && type.getId() <= 8) {
            // Is a dye paint
            if (skinDye != null && skinDye.haveDyeInSlot(type.getId() - 1)) {
                byte[] dye = skinDye.getDyeColour(type.getId() - 1);
                if (dye.length == 4) {
                    PaintType dyeType = PaintRegistry.getPaintTypeFormByte(dye[3]);
                    if (dyeType == PaintRegistry.PAINT_TYPE_NONE) {
                        return;
                    }
                    int[] averageRGB = cspd.getAverageDyeColour(type.getChannelIndex());
                    byte[] dyedColour = null;
                    if (dyeType.getColourType() != null & extraColours != null) {
                        byte[] extraColour = extraColours.getColourBytes(dyeType.getColourType());
                        if (extraColour.length == 4 && (extraColour[3] & 0xFF) != 0) {
                            dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(dyeType.getColourType()), averageRGB);
                        } else {
                            dyedColour = dyeColour(r, g, b, dye, averageRGB);
                        }
                    } else {
                        dyedColour = dyeColour(r, g, b, dye, averageRGB);
                    }
                    r = dyedColour[0];
                    g = dyedColour[1];
                    b = dyedColour[2];
                }
                
            }
        } else if (type == PaintRegistry.PAINT_TYPE_RAINBOW) {
            int[] averageRGB = cspd.getAverageDyeColour(type.getChannelIndex());
            byte[] dyedColour = dyeColour(r, g, b, new byte[] {(byte)127, (byte)127, (byte)127}, averageRGB);
            r = dyedColour[0];
            g = dyedColour[1];
            b = dyedColour[2];
        } else  if (extraColours != null) {
            if (type.getColourType() != null) {
                int[] averageRGB = cspd.getAverageDyeColour(type.getChannelIndex());
                byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(type.getColourType()), averageRGB);
                r = dyedColour[0];
                g = dyedColour[1];
                b = dyedColour[2];
            }
        }
        
        double paintScale = 1D / 256D;

        if (type == PaintRegistry.PAINT_TYPE_RAINBOW) {
            FaceRenderer.renderFace(x, y, z, r, g, b, a, face, lodLevel, paintScale, 0, paintScale + paintScale, paintScale);
            //FaceRenderer.renderFace(x, y, z, r, g, b, a, face, lodLevel, 0D, 0D, paintScale, paintScale);
        } else {
            FaceRenderer.renderFace(x, y, z, r, g, b, a, face, lodLevel, 0D, 0D, paintScale, paintScale);
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
