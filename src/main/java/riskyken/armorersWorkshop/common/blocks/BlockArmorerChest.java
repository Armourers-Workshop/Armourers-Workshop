package riskyken.armorersWorkshop.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armorersWorkshop.common.lib.LibBlockNames;
import riskyken.armorersWorkshop.common.lib.LibModInfo;
import riskyken.armorersWorkshop.common.tileentities.TileEntityArmorerChest;

public class BlockArmorerChest extends AbstractModBlock implements ITileEntityProvider {

	public BlockArmorerChest() {
		super(LibBlockNames.ARMORER_CHEST);
	}
	
	private IIcon sideIcon;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armor_block");
		sideIcon = register.registerIcon(LibModInfo.ID.toLowerCase() + ":" + "armor_chestplate");
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
		return new TileEntityArmorerChest();
	}

}
