package moe.plushie.armourers_workshop.common.init.blocks;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityBoundingBox;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourMixer;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityDyeTable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityOutfitMaker;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnableChild;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinningTable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static final ArrayList<Block> BLOCK_LIST = new ArrayList<Block>();

    public static final Block ARMOURER = new BlockArmourer();
    // public static final Block MINI_ARMOURER = new BlockMiniArmourer();
    public static final Block SKIN_LIBRARY = new BlockSkinLibrary();
    public static final Block GLOBAL_SKIN_LIBRARY = new BlockGlobalSkinLibrary();
    public static final Block BOUNDING_BOX = new BlockBoundingBox();
    public static final Block SKIN_CUBE = new BlockSkinCube(LibBlockNames.SKIN_CUBE, false);
    public static final Block SKIN_CUBE_GLASS = new BlockSkinCubeGlass(LibBlockNames.SKIN_CUBE_GLASS, false);
    public static final Block SKIN_CUBE_GLOWING = new BlockSkinCube(LibBlockNames.SKIN_CUBE_GLOWING, true);
    public static final Block SKIN_CUBE_GLASS_GLOWING = new BlockSkinCubeGlass(LibBlockNames.SKIN_CUBE_GLASS_GLOWING, true);
    public static final Block COLOUR_MIXER = new BlockColourMixer();
    public static final Block MANNEQUIN = new BlockMannequin();
    public static final Block DOLL = new BlockDoll();
    public static final Block SKINNING_TABLE = new BlockSkinningTable();
    public static final Block SKINNABLE = new BlockSkinnable();
    public static final Block SKINNABLE_GLOWING = new BlockSkinnableGlowing();
    public static final Block SKINNABLE_CHILD = new BlockSkinnableChild();
    public static final Block SKINNABLE_CHILD_GLOWING = new BlockSkinnableChildGlowing();
    public static final Block DYE_TABLE = new BlockDyeTable();
    public static final Block HOLOGRAM_PROJECTOR = new BlockHologramProjector();
    public static final Block OUTFIT_MAKER = new BlockOutfitMaker();
    public static final Block ADVANCED_SKIN_BUILDER = new BlockAdvancedSkinBuilder();

    public ModBlocks() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> reg = event.getRegistry();
        for (int i = 0; i < BLOCK_LIST.size(); i++) {
            reg.register(BLOCK_LIST.get(i));
        }
    }

    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (int i = 0; i < BLOCK_LIST.size(); i++) {
            Block block = BLOCK_LIST.get(i);
            if (block instanceof ICustomItemBlock) {
                ((ICustomItemBlock) block).registerItemBlock(registry);
            }
        }
    }

    public void registerTileEntities() {
        registerTileEntity(TileEntityArmourer.class, LibBlockNames.ARMOURER);
        registerTileEntity(TileEntitySkinLibrary.class, LibBlockNames.SKIN_LIBRARY);
        registerTileEntity(TileEntityGlobalSkinLibrary.class, LibBlockNames.GLOBAL_SKIN_LIBRARY);
        registerTileEntity(TileEntityColourable.class, LibBlockNames.SKIN_CUBE);
        registerTileEntity(TileEntityColourMixer.class, LibBlockNames.COLOUR_MIXER);
        registerTileEntity(TileEntityBoundingBox.class, LibBlockNames.BOUNDING_BOX);
        registerTileEntity(TileEntityMannequin.class, LibBlockNames.MANNEQUIN);
        registerTileEntity(TileEntitySkinningTable.class, LibBlockNames.SKINNING_TABLE);
        registerTileEntity(TileEntitySkinnable.class, LibBlockNames.SKINNABLE);
        registerTileEntity(TileEntityDyeTable.class, LibBlockNames.DYE_TABLE);
        registerTileEntity(TileEntitySkinnableChild.class, LibBlockNames.SKINNABLE_CHILD);
        registerTileEntity(TileEntityHologramProjector.class, LibBlockNames.HOLOGRAM_PROJECTOR);
        registerTileEntity(TileEntityOutfitMaker.class, LibBlockNames.OUTFIT_MAKER);
        registerTileEntity(TileEntityAdvancedSkinBuilder.class, LibBlockNames.ADVANCED_SKIN_BUILDER);
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(LibModInfo.ID, "tileentity." + id));
    }
}
