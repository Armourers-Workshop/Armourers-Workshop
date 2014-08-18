package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCustomArmour extends AbstractModItemArmor {

    public ItemCustomArmour(ArmorMaterial armorMaterial, int armorType) {
        super(LibItemNames.CUSTOM_ARMOUR_ITEM + ".type." + armorType, armorMaterial, armorType);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "wings");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        ModelBiped armorModel = null;
        if (itemStack != null) {
            if (itemStack.getItem() instanceof ItemCustomArmour) {
                switch (this.armorType) {
                case 0:
                    return ClientProxy.customHead;
                case 1:
                    return ClientProxy.customChest;
                default:
                    break;
                }
            }
        }
        return null;
    }
}
