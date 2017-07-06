package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnableChild;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinningTable;

public class ModBlocks {

    public static Block armourerBrain;
    public static Block miniArmourer;
    public static Block armourLibrary;
    public static Block globalSkinLibrary;
    public static Block boundingBox;
    public static Block colourable;
    public static Block colourableGlowing;
    public static Block colourableGlass;
    public static Block colourableGlassGlowing;
    public static Block colourMixer;
    public static Block mannequin;
    public static Block doll;
    public static Block skinningTable;
    public static Block skinnable;
    public static Block skinnableGlowing;
    public static Block skinnableChild;
    public static Block skinnableChildGlowing;
    public static Block dyeTable;
    public static Block hologramProjector;
    
    public ModBlocks() {
        armourerBrain = new BlockArmourer();
        miniArmourer = new BlockMiniArmourer();
        armourLibrary = new BlockSkinLibrary();
        globalSkinLibrary = new BlockGlobalSkinLibrary();
        boundingBox = new BlockBoundingBox();
        colourable = new BlockColourable(LibBlockNames.COLOURABLE, false);
        colourableGlowing = new BlockColourable(LibBlockNames.COLOURABLE_GLOWING, true);
        colourableGlass = new BlockColourableGlass(LibBlockNames.COLOURABLE_GLASS, false);
        colourableGlassGlowing = new BlockColourableGlass(LibBlockNames.COLOURABLE_GLASS_GLOWING, true);
        colourMixer = new BlockColourMixer();
        mannequin = new BlockMannequin();
        doll = new BlockDoll();
        skinningTable = new BlockSkinningTable();
        skinnable = new BlockSkinnable();
        skinnableGlowing = new BlockSkinnableGlowing();
        skinnableChild = new BlockSkinnableChild();
        skinnableChildGlowing = new BlockSkinnableChildGlowing();
        dyeTable = new BlockDyeTable();
        hologramProjector = new BlockHologramProjector();
    }

    public void registerTileEntities() {
        registerTileEntity(TileEntityArmourer.class, LibBlockNames.ARMOURER_BRAIN);
        registerTileEntity(TileEntityMiniArmourer.class, LibBlockNames.MINI_ARMOURER);
        registerTileEntity(TileEntitySkinLibrary.class, LibBlockNames.ARMOUR_LIBRARY);
        registerTileEntity(TileEntityGlobalSkinLibrary.class, LibBlockNames.GLOBAL_SKIN_LIBRARY);
        registerTileEntity(TileEntityColourable.class, LibBlockNames.COLOURABLE);
        registerTileEntity(TileEntityColourMixer.class, LibBlockNames.COLOUR_MIXER);
        registerTileEntity(TileEntityBoundingBox.class, LibBlockNames.BOUNDING_BOX);
        registerTileEntity(TileEntityMannequin.class, LibBlockNames.MANNEQUIN);
        registerTileEntity(TileEntitySkinningTable.class, LibBlockNames.SKINNING_TABLE);
        registerTileEntity(TileEntitySkinnable.class, LibBlockNames.SKINNABLE);
        registerTileEntity(TileEntityDyeTable.class, LibBlockNames.DYE_TABLE);
        registerTileEntity(TileEntitySkinnableChild.class, LibBlockNames.SKINNABLE_CHILD);
        registerTileEntity(TileEntityHologramProjector.class, LibBlockNames.HOLOGRAM_PROJECTOR);
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
    }
}
