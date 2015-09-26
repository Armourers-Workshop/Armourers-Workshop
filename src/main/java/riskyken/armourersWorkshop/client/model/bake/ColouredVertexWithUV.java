package riskyken.armourersWorkshop.client.model.bake;

import java.awt.Color;

import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.plushieWrapper.client.IRenderBuffer;

public class ColouredVertexWithUV {
    
    private final double x;
    private final double y;
    private final double z;
    
    //TODO Move u v into it's own class.
    private final float u;
    private final float v;
    
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
    
    public void renderVertex(IRenderBuffer renderBuffer, ISkinDye skinDye) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        int type = t & 0xFF;
        if (type != 0) {
            if (type >= 1 && type <=8) {
                //Is a dye paint
                if (skinDye != null && skinDye.getNumberOfDyes() >= type) {
                    byte[] dye = skinDye.getDyeColour(type - 1);
                    if (dye.length == 3) {
                        float[] skinHsb = new float[3];
                        float[] dyeHsb = new float[3];
                        
                        Color.RGBtoHSB(r & 0xFF, g & 0xFF, b & 0xFF, skinHsb);
                        Color.RGBtoHSB(dye[0] & 0xFF, dye[1] & 0xFF, dye[2] & 0xFF, dyeHsb);
                        Color c = Color.getHSBColor(dyeHsb[0], skinHsb[1], dyeHsb[2]);
                        
                        r = (byte)c.getRed();
                        g = (byte)c.getGreen();
                        b = (byte)c.getBlue();
                    }
                }
            }
            renderBuffer.setNormal(norX, norY, norZ);
            renderBuffer.setColourRGBA_B(r, g, b, a);
            renderBuffer.addVertex(x, y, z);
        }
    }
    
    public void renderVertexWithUV(IRenderBuffer renderBuffer, ISkinDye skinDye) {
        renderBuffer.setNormal(norX, norY, norZ);
        renderBuffer.setColourRGBA_B(r, g, b, a);
        renderBuffer.addVertexWithUV(x, y, z, (double)u, (double)v);
    }
}
