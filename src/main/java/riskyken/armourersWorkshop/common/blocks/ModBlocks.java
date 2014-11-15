package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static Block armourerBrain;
    public static Block armourCrafter;
    public static Block armourLibrary;
    public static Block boundingBox;
    public static Block colourable;
    public static Block colourableGlowing;
    public static Block colourMixer;
    public static Block mannequin;
    
    public static void init() {
        armourerBrain = new BlockArmourerBrain();
        armourLibrary = new BlockArmourLibrary();
        boundingBox = new BlockBoundingBox();
        colourable = new BlockColourable(LibBlockNames.COLOURABLE, false);
        colourableGlowing = new BlockColourable(LibBlockNames.COLOURABLE_GLOWING, true);
        colourMixer = new BlockColourMixer();
        mannequin = new BlockMannequin();
    }

    public static void registerTileEntities() {
        registerTileEntity(TileEntityArmourerBrain.class, LibBlockNames.ARMOURER_BRAIN);
        registerTileEntity(TileEntityArmourLibrary.class, LibBlockNames.ARMOUR_LIBRARY);
        registerTileEntity(TileEntityColourable.class, LibBlockNames.COLOURABLE);
        registerTileEntity(TileEntityColourMixer.class, LibBlockNames.COLOUR_MIXER);
        registerTileEntity(TileEntityBoundingBox.class, LibBlockNames.BOUNDING_BOX);
        registerTileEntity(TileEntityMannequin.class, LibBlockNames.MANNEQUIN);
    }

    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
    }
}
