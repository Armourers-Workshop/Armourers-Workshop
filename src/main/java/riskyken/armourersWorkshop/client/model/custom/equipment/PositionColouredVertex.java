package riskyken.armourersWorkshop.client.model.custom.equipment;

import net.minecraft.util.Vec3;

public class PositionColouredVertex {
    
    public Vec3 vector3D;
    public int colour;
    
    public PositionColouredVertex(float x, float y, float z, int colour) {
        this(Vec3.createVectorHelper(x, y, z), colour);
    }
    
    public PositionColouredVertex(Vec3 vector3D, int colour) {
        this.vector3D = vector3D;
        this.colour = colour;
    }
}
