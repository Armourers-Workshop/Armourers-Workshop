package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMultiBlock;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static Block armourerBrain;
    public static Block armourerMultiBlock;
    public static Block boundingBox;
    public static Block colourable;
    public static Block colourableGlowing;
    
    public static void init() {
        armourerBrain = new BlockArmourerBrain();
        armourerMultiBlock = new BlockArmourerMultiBlock();
        boundingBox = new BlockBoundingBox();
        colourable = new BlockColourable(LibBlockNames.COLOURABLE, false);
        colourableGlowing = new BlockColourable(LibBlockNames.COLOURABLE_GLOWING, true);
    }

    public static void registerTileEntities() {
        registerTileEntity(TileEntityArmourerBrain.class, LibBlockNames.ARMOURER_BRAIN);
        registerTileEntity(TileEntityMultiBlock.class, LibBlockNames.ARMOURER_MULTI_BLOCK);
        registerTileEntity(TileEntityColourable.class, LibBlockNames.COLOURABLE);
    }

    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
    }
}
