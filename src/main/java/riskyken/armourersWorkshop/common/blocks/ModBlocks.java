package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static Block armorerChest;
    public static Block boundingBox;

    public static void init() {
        armorerChest = new BlockArmourer();
        boundingBox = new BlockBoundingBox();
    }

    public static void registerTileEntities() {
        registerTileEntity(TileEntityArmourer.class,
                LibBlockNames.ARMORER_CHEST);
    }

    public static void registerTileEntity(
            Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, "te." + id);
    }
}
