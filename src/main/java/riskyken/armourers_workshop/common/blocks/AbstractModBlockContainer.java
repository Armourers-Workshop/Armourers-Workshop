package riskyken.armourers_workshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.creativetab.ISortOrder;
import riskyken.armourers_workshop.common.lib.LibModInfo;

public abstract class AbstractModBlockContainer extends BlockContainer implements ISortOrder {

    private int sortPriority = 100;
    
    public AbstractModBlockContainer(String name) {
        super(Material.IRON);
        setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        setUnlocalizedName(name);
        ModBlocks.BLOCKS.add(this);
    }
    
    public AbstractModBlockContainer(String name, Material material, SoundType soundType, boolean addCreativeTab) {
        super(material);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        }
        setHardness(3.0F);
        setSoundType(soundType);
        setUnlocalizedName(name);
        ModBlocks.BLOCKS.add(this);
    }
    
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public Block setUnlocalizedName(String name) {
        super.setUnlocalizedName(name);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "tile." + name));
        return this;
    }
    
    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName();
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        return "tile." + LibModInfo.ID.toLowerCase() + ":" + name;
    }
    
    public AbstractModBlockContainer setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }
    
    @Override
    public int getSortPriority() {
        return sortPriority;
    }
}
