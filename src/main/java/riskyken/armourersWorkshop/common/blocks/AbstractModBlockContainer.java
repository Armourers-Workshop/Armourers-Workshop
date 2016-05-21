package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public abstract class AbstractModBlockContainer extends BlockContainer {

    public AbstractModBlockContainer(String name) {
        super(Material.iron);
        setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        setHardness(3.0F);
        setStepSound(soundTypeMetal);
        setBlockName(name);
    }
    
    public AbstractModBlockContainer(String name, Material material, SoundType soundType, boolean addCreativeTab) {
        super(material);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        }
        setHardness(3.0F);
        setStepSound(soundType);
        setBlockName(name);
    }
    
    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        return "tile." + LibModInfo.ID.toLowerCase() + ":" + name;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return ArmourersWorkshop.proxy.getTileEntityForSide(this, world, metadata);
    }
    
    public abstract TileEntity getTileEntityCommon(World world, int metadata);
    
    @SideOnly(Side.CLIENT)
    public TileEntity getTileEntityClient(World world, int metadata) {
        return getTileEntityCommon(world, metadata);
    }
}
