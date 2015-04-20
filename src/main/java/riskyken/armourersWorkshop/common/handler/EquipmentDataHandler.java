package riskyken.armourersWorkshop.common.handler;

import java.util.BitSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinTypeRegistry;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.equipment.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EquipmentDataHandler implements IEquipmentDataHandler {

    public static final EquipmentDataHandler INSTANCE = new EquipmentDataHandler();
    
    @Override
    public void setCustomEquipmentOnPlayer(EntityPlayer player, ItemStack stack) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.setEquipmentStack(stack);
    }

    @Override
    public ItemStack[] getAllCustomEquipmentForPlayer(EntityPlayer player) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getAllEquipmentStacks();
    }

    @Override
    public ItemStack getCustomEquipmentForPlayer(EntityPlayer player, ISkinType skinType) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getEquipmentStack(skinType);
    }

    @Override
    public void clearAllCustomEquipmentFromPlayer(EntityPlayer player) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearAllEquipmentStacks();
    }

    @Override
    public void clearCustomEquipmentFromPlayer(EntityPlayer player, ISkinType skinType) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearEquipmentStack(skinType);
    }
    
    @SideOnly(Side.CLIENT)
    private IEntityEquipment getLocalPlayerEquipment(Entity entity) {
        return EquipmentModelRenderer.INSTANCE.getPlayerCustomEquipmentData(entity);
    }
    
    @Override
    public ISkinType getSkinTypeFromStack(ItemStack stack) {
        return EquipmentNBTHelper.getSkinTypeFromStack(stack);
    }
    
    @Override
    public boolean hasItemStackGotEquipmentData(ItemStack stack) {
        return EquipmentNBTHelper.stackHasSkinData(stack);
    }
    
    @Override
    public int getEquipmentIdFromItemStack(ItemStack stack) {
        return EquipmentNBTHelper.getSkinIdFromStack(stack);
    }
    
    @Override
    public IInventory getPlayersEquipmentInventory(EntityPlayer player) {
        ExPropsPlayerEquipmentData entityProps = ExPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            return null;
        }
        return entityProps;
    }

    @Override
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId) {
        ExPropsPlayerEquipmentData entityProps = ExPropsPlayerEquipmentData.get(player);
        BitSet armourOverride = entityProps.getArmourOverride();
        if (slotId < 4 & slotId >= 0) {
            return armourOverride.get(slotId);
        }
        return false;
    }
    
    private ExPropsPlayerEquipmentData getExtendedPropsPlayerForPlayer(EntityPlayer player) {
        ExPropsPlayerEquipmentData entityProps = ExPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            ExPropsPlayerEquipmentData.register(player);
        }
        return ExPropsPlayerEquipmentData.get(player);
    }

    @Override
    public ISkinTypeRegistry getISkinTypeRegistry() {
        return SkinTypeRegistry.INSTANCE;
    }
}
