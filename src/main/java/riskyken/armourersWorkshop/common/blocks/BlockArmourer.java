package riskyken.armourersWorkshop.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.ArmourerType;
import riskyken.armourersWorkshop.common.items.block.ModItemBlockWithMetadata;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockArmourer extends AbstractModBlock implements ITileEntityProvider {

	public BlockArmourer() {
		super(LibBlockNames.ARMORER_CHEST);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
		for (int i = 0; i < 4; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
		super.onBlockPlacedBy(world, x, y, z, entityLivingBase, itemStack);
	}
	
	@Override
	public Block setBlockName(String name) {
		GameRegistry.registerBlock(this, ModItemBlockWithMetadata.class, "block." + name);
		return super.setBlockName(name);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
		if (!world.isRemote) { return true; }
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null & te instanceof TileEntityArmourer) {
			((TileEntityArmourer)te).buildArmourItem(player);
		}
		return true; 
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon[] sideIcons;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		sideIcons = new IIcon[4];
		blockIcon = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_block");
		sideIcons[0] = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_head");
		sideIcons[1] = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_chest");
		sideIcons[2] = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_legs");
		sideIcons[3] = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_feet");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side < 2) {
			return blockIcon;
		}
		return sideIcons[meta];
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityArmourer(ArmourerType.getOrdinal(metadata + 1));
	}

}
