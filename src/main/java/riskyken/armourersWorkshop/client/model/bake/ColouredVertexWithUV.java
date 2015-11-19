package riskyken.armourersWorkshop.client.model.bake;

import java.awt.Color;

import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
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
    
    public void renderVertex(IRenderBuffer renderBuffer, ISkinDye skinDye, ClientSkinPartData cspd) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        int type = t & 0xFF;
        if (type != 0) {
            if (type >= 1 && type <=8) {
                //Is a dye paint
                int[] averageRGB = cspd.getAverageDyeColour(type - 1);
                Color average = new Color(averageRGB[0], averageRGB[1], averageRGB[2]);
                
                if (skinDye != null && skinDye.haveDyeInSlot(type - 1)) {
                    byte[] dye = skinDye.getDyeColour(type - 1);
                    if (dye.length == 4) {
                        float[] skinHsb = new float[3];
                        float[] dyeHsb = new float[3];
                        float[] averageHsb = new float[3];
                        
                        Color.RGBtoHSB(r & 0xFF, g & 0xFF, b & 0xFF, skinHsb);
                        Color.RGBtoHSB(dye[0] & 0xFF, dye[1] & 0xFF, dye[2] & 0xFF, dyeHsb);
                        Color.RGBtoHSB(averageRGB[0], averageRGB[1], averageRGB[2], averageHsb);
                        
                        float saturationOffset = 0.5F - averageHsb[1];
                        float brightnessOffset = 0.5F - averageHsb[2];
                        
                        float saturation = skinHsb[1] + saturationOffset - 0.5F + dyeHsb[1];
                        float brightness = skinHsb[2] + brightnessOffset - 0.5F + dyeHsb[2];
                        if (brightness < 0) {
                            brightness = 0;
                        }
                        if (brightness > 1) {
                            brightness = 1;
                        }
                        if (saturation < 0) {
                            saturation = 0;
                        }
                        if (saturation > 1) {
                            saturation = 1;
                        }
                        
                        //Color c = Color.getHSBColor(dyeHsb[0], (skinHsb[1] * 0.50F) + (dyeHsb[1] * 0.50F), (skinHsb[2] * 0.25F) + (dyeHsb[2] * 0.75F));
                        Color c = Color.getHSBColor(dyeHsb[0], saturation, brightness);
                        
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
    
    public void renderVertexWithUV(IRenderBuffer renderBuffer, ISkinDye skinDye, ClientSkinPartData cspd) {
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
                        float[] skinHsb = new float[3];
                        float[] dyeHsb = new float[3];
                        
                        Color.RGBtoHSB(r & 0xFF, g & 0xFF, b & 0xFF, skinHsb);
                        Color.RGBtoHSB(dye[0] & 0xFF, dye[1] & 0xFF, dye[2] & 0xFF, dyeHsb);
                        Color c = Color.getHSBColor(dyeHsb[0], (skinHsb[1] * 0.50F) + (dyeHsb[1] * 0.50F), (skinHsb[2] * 0.25F) + (dyeHsb[2] * 0.75F));
                        
                        r = (byte)c.getRed();
                        g = (byte)c.getGreen();
                        b = (byte)c.getBlue();
                    }
                }
            }
            renderBuffer.setNormal(norX, norY, norZ);
            renderBuffer.setColourRGBA_B(r, g, b, a);
            renderBuffer.addVertexWithUV(x, y, z, (double)u, (double)v);
        }
    }
}
