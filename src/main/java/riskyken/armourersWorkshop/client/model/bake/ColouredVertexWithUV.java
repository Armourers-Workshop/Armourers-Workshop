package riskyken.armourersWorkshop.client.model.bake;

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
        
        this.norX = norX;
        this.norY = norY;
        this.norZ = norZ;
    }
    
    public void renderVertex(IRenderBuffer renderBuffer) {
        renderBuffer.setNormal(norX, norY, norZ);
        renderBuffer.setColourRGBA_B(r, g, b, a);
        renderBuffer.addVertex(x, y, z);
    }
    
    public void renderVertexWithUV(IRenderBuffer renderBuffer) {
        renderBuffer.setNormal(norX, norY, norZ);
        renderBuffer.setColourRGBA_B(r, g, b, a);
        renderBuffer.addVertexWithUV(x, y, z, (double)u, (double)v);
    }
}
