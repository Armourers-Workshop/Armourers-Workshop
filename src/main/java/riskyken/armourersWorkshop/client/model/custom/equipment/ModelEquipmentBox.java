package riskyken.armourersWorkshop.client.model.custom.equipment;



public class ModelEquipmentBox {
    
    private PositionColouredVertex[] vertexPositions;
    public final int xPos;
    public final int yPos;
    public final int zPos;
    private final byte faceFlags;
    
    public ModelEquipmentBox(int xPos, int yPos, int zPos, byte faceFlags) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.faceFlags = faceFlags;
        
        this.vertexPositions = new PositionColouredVertex[8];
        
    }
    
    
}
