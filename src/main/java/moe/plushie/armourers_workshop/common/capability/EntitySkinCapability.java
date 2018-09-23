package moe.plushie.armourers_workshop.common.capability;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.IInventorySlotUpdate;
import moe.plushie.armourers_workshop.common.inventory.SkinInventoryContainer;
import moe.plushie.armourers_workshop.common.inventory.WardrobeInventory;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class EntitySkinCapability implements IEntitySkinCapability, IInventorySlotUpdate {
    
    @CapabilityInject(IEntitySkinCapability.class)
    public static final Capability<IEntitySkinCapability> SKIN_CAP = null;
    
    private final Entity entity;
    private ISkinnableEntity skinnableEntity;
    private final ISkinType[] validSkinTypes;
    private final SkinInventoryContainer skinInventoryContainer;
    private boolean autoSync;
    
    public EntitySkinCapability(Entity entity, ISkinnableEntity skinnableEntity) {
        this.entity = entity;
        this.skinnableEntity = skinnableEntity;
        ArrayList<ISkinType> skinTypes = new ArrayList<ISkinType>();
        skinnableEntity.getValidSkinTypes(skinTypes);
        validSkinTypes = skinTypes.toArray(new ISkinType[0]);
        skinInventoryContainer = new SkinInventoryContainer(this, validSkinTypes);
        autoSync = true;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    @Override
    public SkinInventoryContainer getSkinInventoryContainer() {
        return skinInventoryContainer;
    }

    @Override
    public void disableAutoSync() {
        autoSync = false;
        
    }

    @Override
    public void enableAutoSync(boolean sync) {
        autoSync = true;
        if (sync) {
            syncToAllAround();
        }
    }

    @Override
    public void syncToPlayer(EntityPlayerMP entityPlayer) {
        // TODO Auto-generated method stub
    }

    @Override
    public void syncToAllAround() {
        // TODO Auto-generated method stub
    }
    
    @Override
    public ISkinType[] getValidSkinTypes() {
        return validSkinTypes;
    }

    @Override
    public boolean canHoldSkinType(ISkinType skinType) {
        for (int i  = 0; i < validSkinTypes.length; i++) {
            if (skinType == validSkinTypes[i]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSlotCountForSkinType(ISkinType skinType) {
        if (canHoldSkinType(skinType)) {
            return skinnableEntity.getSlotsForSkinType(skinType);
        } else {
            return 0;
        }
    }

    @Override
    public ISkinDescriptor getSkinDescriptor(ISkinType skinType, int slotIndex) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getInventoryForSkinType(skinType);
        if (wardrobeInventory != null) {
            SkinDescriptor skinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(wardrobeInventory.getStackInSlot(slotIndex));
            return skinDescriptor;
        }
        return null;
    }

    @Override
    public ISkinDescriptor setSkinDescriptor(ISkinType skinType, int slotIndex, ISkinDescriptor skinDescriptor) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getInventoryForSkinType(skinType);
        if (wardrobeInventory != null) {
            SkinDescriptor oldSkinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(wardrobeInventory.getStackInSlot(slotIndex));
            ItemStack itemStack = SkinNBTHelper.makeEquipmentSkinStack((SkinDescriptor)skinDescriptor);
            wardrobeInventory.setInventorySlotContents(slotIndex, itemStack);
            return oldSkinDescriptor;
        }
        return null;
    }

    @Override
    public ItemStack getSkinStack(ISkinType skinType, int slotIndex) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getInventoryForSkinType(skinType);
        if (wardrobeInventory != null) {
            return wardrobeInventory.getStackInSlot(slotIndex);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack setSkinStack(ISkinType skinType, int slotIndex, ItemStack skinStack) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getInventoryForSkinType(skinType);
        if (wardrobeInventory != null) {
            ItemStack oldItemStack = wardrobeInventory.getStackInSlot(slotIndex);
            wardrobeInventory.setInventorySlotContents(slotIndex, skinStack);
            return oldItemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack) {
        // TODO Auto-generated method stub
    }
}
