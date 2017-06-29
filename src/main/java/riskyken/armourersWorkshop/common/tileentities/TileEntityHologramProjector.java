package riskyken.armourersWorkshop.common.tileentities;

import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityHologramProjector extends AbstractTileEntityInventory {

    private static final int INVENTORY_SIZE = 1;
    
    public TileEntityHologramProjector() {
        super(INVENTORY_SIZE);
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.HOLOGRAM_PROJECTOR;
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
}
