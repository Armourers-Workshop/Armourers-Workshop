package riskyken.armourersWorkshop.common.items;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.IWorldColourable;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDodgeTool extends AbstractModItem {

    public ItemDodgeTool() {
        super(LibItemNames.DODGE_TOOL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "dodgeTool");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block instanceof IWorldColourable) {
            if (!world.isRemote) {
                Color c = new Color(((IWorldColourable) block).getColour(world,
                        x, y, z));
                ((IWorldColourable) block).setColour(world, x, y, z, UtilColour
                        .makeColourBighter(c, 16).getRGB());
            }
            return true;
        }
        return false;
    }
}
