package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class BlockSkinnable extends AbstractModBlock implements ITileEntityProvider {

    public BlockSkinnable() {
        super(LibBlockNames.SKINNABLE);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon cubeIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.SKINNABLE);
        cubeIcon = register.registerIcon(LibBlockResources.CUBE);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 4) {
            return cubeIcon;
        }
        return blockIcon;
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                ItemStack returnStack = new ItemStack(ModItems.equipmentSkin, 1);
                EquipmentNBTHelper.addSkinDataToStack(returnStack, skinPointer);
                return returnStack;
            }
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnable();
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean isNormalCube() {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }
}
