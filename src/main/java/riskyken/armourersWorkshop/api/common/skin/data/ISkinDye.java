package riskyken.armourersWorkshop.api.common.skin.data;

public interface ISkinDye {
    
    public int getNumberOfDyes();
    
    public byte[] getDyeColour(int index);
    
    public void addDye(int index, byte[] rgb);
    
    public void removeDye(int index);
}
