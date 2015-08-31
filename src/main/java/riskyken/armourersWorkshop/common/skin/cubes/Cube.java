package riskyken.armourersWorkshop.common.skin.cubes;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.utils.ModLogger;

public class Cube implements ICube {
    
    protected byte id = -1;
    
    @Override
    public boolean isGlowing() {
        return false;
    }
    
    @Override
    public boolean needsPostRender() {
        return false;
    }
    
    @Override
    public void setId(byte id) {
        if (this.id != -1) {
            ModLogger.log(Level.WARN, "Resetting cube id.");
        }
        this.id = id;
    }
    
    @Override
    public byte getId() {
        return id;
    }
}
