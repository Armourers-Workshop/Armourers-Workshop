package riskyken.armourersWorkshop.common.handler;

import java.util.BitSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.ISkinDataHandler;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EquipmentDataHandler implements ISkinDataHandler {

    public static final EquipmentDataHandler INSTANCE = new EquipmentDataHandler();
    
    @Override
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.setEquipmentStack(stack);
        return false;
    }

    @Override
    public ItemStack getSkinForPlayer(EntityPlayer player, ISkinType skinType) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getEquipmentStack(skinType);
    }

    @Override
    public void removeSkinTypeFromPlayer(EntityPlayer player, ISkinType skinType) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearEquipmentStack(skinType);
    }

    @Override
    public boolean stackHasSkinPointer(ItemStack stack) {
        return EquipmentNBTHelper.stackHasSkinData(stack);
    }

    @Override
    public ISkinPointer getSkinPointerFromStack(ItemStack stack) {
        return EquipmentNBTHelper.getSkinPointerFromStack(stack);
    }
    
    @Override
    public ISkinType getSkinTypeFromStack(ItemStack stack) {
        return EquipmentNBTHelper.getSkinTypeFromStack(stack);
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
    
    @SideOnly(Side.CLIENT)
    private IEntityEquipment getLocalPlayerEquipment(Entity entity) {
        return EquipmentModelRenderer.INSTANCE.getPlayerCustomEquipmentData(entity);
    }
    
    private ExPropsPlayerEquipmentData getExtendedPropsPlayerForPlayer(EntityPlayer player) {
        ExPropsPlayerEquipmentData entityProps = ExPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            ExPropsPlayerEquipmentData.register(player);
        }
        return ExPropsPlayerEquipmentData.get(player);
    }
}
