package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBurnTool extends AbstractModItem {

    public ItemBurnTool() {
        super(LibItemNames.BURN_TOOL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "burnTool");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (!player.isSneaking() & block instanceof IPantableBlock) {
            if (!world.isRemote) {
                int intensity = UtilItems.getIntensityFromStack(stack, 16);
                IPantableBlock worldColourable = (IPantableBlock) block;
                int oldColour = worldColourable.getColour(world, x, y, z);
                int newColour = UtilColour.makeColourDarker(new Color(oldColour), intensity).getRGB();
                UndoManager.playerPaintedBlock(player, world, x, y, z, oldColour);
                ((IPantableBlock) block).setColour(world, x, y, z, newColour);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.BURN, 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        list.add("Intensity: " + intensity);
    }
}
