package moe.plushie.armourers_workshop.common.blocks;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinPart;
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

    public static ArrayList<Block> BLOCK_LIST = new ArrayList<Block>();
    
    public static Block armourer;
    //public static Block miniArmourer;
    public static Block skinLibrary;
    public static Block globalSkinLibrary;
    public static Block boundingBox;
    public static Block skinCube;
    public static Block skinCubeGlowing;
    public static Block skinCubeGlass;
    public static Block skinCubeGlassGlowing;
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
    public static Block outfitMaker;
    public static Block advancedSkinBuilder;
    public static Block advancedSkinPart ;
    
    public ModBlocks() {
        MinecraftForge.EVENT_BUS.register(this);
        armourer = new BlockArmourer();
        //miniArmourer = new BlockMiniArmourer();
        skinLibrary = new BlockSkinLibrary();
        globalSkinLibrary = new BlockGlobalSkinLibrary();
        boundingBox = new BlockBoundingBox();
        skinCube = new BlockSkinCube(LibBlockNames.SKIN_CUBE, false);
        skinCubeGlowing = new BlockSkinCube(LibBlockNames.SKIN_CUBE_GLOWING, true);
        skinCubeGlass = new BlockSkinCubeGlass(LibBlockNames.SKIN_CUBE_GLASS, false);
        skinCubeGlassGlowing = new BlockSkinCubeGlass(LibBlockNames.SKIN_CUBE_GLASS_GLOWING, true);
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
        outfitMaker = new BlockOutfitMaker();
        advancedSkinBuilder = new BlockAdvancedSkinBuilder();
        advancedSkinPart = new BlockAdvancedSkinPart();
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
                ((ICustomItemBlock)block).registerItemBlock(registry);
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
        registerTileEntity(TileEntityAdvancedSkinPart.class, LibBlockNames.ADVANCED_SKIN_PART);
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(LibModInfo.ID, "tileentity." + id));
    }
}
