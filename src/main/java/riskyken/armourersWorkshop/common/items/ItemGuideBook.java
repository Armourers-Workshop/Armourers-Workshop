package riskyken.armourersWorkshop.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;

public class ItemGuideBook extends AbstractModItem {

    public ItemGuideBook() {
        super(LibItemNames.GUIDE_BOOK);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(LibItemResources.GUIDE_BOOK);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (world.isRemote) {
            entityPlayer.openGui(ArmourersWorkshop.instance, LibGuiIds.GUIDE_BOOK, world, 0, 0, 0);
        }
        return itemStack;
    }
}
