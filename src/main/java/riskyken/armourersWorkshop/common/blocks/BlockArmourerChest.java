package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerChest;

public class BlockArmourerChest extends AbstractModBlock implements ITileEntityProvider {

	public BlockArmourerChest() {
		super(LibBlockNames.ARMORER_CHEST);
	}
	
	private IIcon sideIcon;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_block");
		sideIcon = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armour_chestplate");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side < 2) {
			return blockIcon;
		}
		return sideIcon;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityArmourerChest();
	}

}
