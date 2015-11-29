package riskyken.armourersWorkshop.client.model.bake;

import java.awt.Color;

import net.minecraft.util.MathHelper;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.plushieWrapper.client.IRenderBuffer;

public class ColouredVertexWithUV {
    
    private final double x;
    private final double y;
    private final double z;
    
    // TODO Move u v into it's own class.
    private final float u;
    private final float v;
    
    // TODO remove all this junk, we don't need it 4 times for each face!
    private final byte r;
    private final byte g;
    private final byte b;
    private final byte a;
    private final byte t;
    
    private final float norX;
    private final float norY;
    private final float norZ;
    
    public ColouredVertexWithUV(double x, double y, double z, float u, float v, byte r, byte g, byte b, byte a, float norX, float norY, float norZ, byte paintType) {
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.u = u;
        this.v = v;
        
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.t = paintType;
        
        this.norX = norX;
        this.norY = norY;
        this.norZ = norZ;
    }
    
    public void renderVertex(IRenderBuffer renderBuffer, ISkinDye skinDye, ClientSkinPartData cspd, boolean useTexture) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        int type = t & 0xFF;
        if (type != 0) {
            if (type >= 1 && type <=8) {
                //Is a dye paint
                if (skinDye != null && skinDye.haveDyeInSlot(type - 1)) {
                    byte[] dye = skinDye.getDyeColour(type - 1);
                    if (dye.length == 4) {
                        int[] averageRGB = cspd.getAverageDyeColour(type - 1);
                        byte[] dyedColour = dyeVertex(dye, averageRGB);
                        r = dyedColour[0];
                        g = dyedColour[1];
                        b = dyedColour[2];
                    }
                }
            }
            renderBuffer.setNormal(norX, norY, norZ);
            renderBuffer.setColourRGBA_B(r, g, b, a);
            if (useTexture) {
                renderBuffer.addVertexWithUV(x, y, z, (double)u, (double)v);
            } else {
                renderBuffer.addVertex(x, y, z);
            }
            
        }
    }
    
    /**
     * Create a new colour for a dyed vertex.
     * @param dyeColour RGB byte array.
     * @param modelAverageColour RGB int array.
     * @return
     */
    private byte[] dyeVertex(byte[] dyeColour, int[] modelAverageColour) {
        float[] skinHsb = new float[3];
        float[] dyeHsb = new float[3];
        float[] averageHsb = new float[3];
        
        Color.RGBtoHSB(r & 0xFF, g & 0xFF, b & 0xFF, skinHsb);
        Color.RGBtoHSB(dyeColour[0] & 0xFF, dyeColour[1] & 0xFF, dyeColour[2] & 0xFF, dyeHsb);
        Color.RGBtoHSB(modelAverageColour[0], modelAverageColour[1], modelAverageColour[2], averageHsb);
        
        //int average = ((r & 0xFF) + (g  & 0xFF) + (b & 0xFF)) / 3;
        //int dyeAverage = ((dyeColour[0] & 0xFF) + (dyeColour[0]  & 0xFF) + (dyeColour[0] & 0xFF)) / 3;
        
        float saturationOffset = 0.5F - averageHsb[1];
        float brightnessOffset = 0.5F - averageHsb[2];
        float saturation = skinHsb[1] + saturationOffset - 0.5F + dyeHsb[1];
        float brightness = skinHsb[2] + brightnessOffset - 0.5F + dyeHsb[2];
        
        brightness = MathHelper.clamp_float(brightness, 0F, 1F);
        saturation = MathHelper.clamp_float(saturation, 0F, 1F);
        
        Color c = Color.getHSBColor(dyeHsb[0], saturation, brightness);
        
        return new byte [] {(byte)c.getRed(), (byte)c.getGreen(), (byte)c.getBlue()};
    }
}
