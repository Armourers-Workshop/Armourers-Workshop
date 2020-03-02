package moe.plushie.armourers_workshop.common.capability.entityskin;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.inventory.ModInventory.IInventoryCallback;
import moe.plushie.armourers_workshop.common.inventory.SkinInventoryContainer;
import moe.plushie.armourers_workshop.common.inventory.WardrobeInventory;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncSkinCap;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class EntitySkinCapability implements IEntitySkinCapability, IInventoryCallback {
    
    @CapabilityInject(IEntitySkinCapability.class)
    public static final Capability<IEntitySkinCapability> ENTITY_SKIN_CAP = null;

    public static final int MAX_SLOTS_PER_SKIN_TYPE = 10;
    
    private final Entity entity;
    private ISkinnableEntity skinnableEntity;
    private final ISkinType[] validSkinTypes;
    private final SkinInventoryContainer skinInventoryContainer;
    
    public boolean hideHead, hideChest, hideArmRight, hideArmLeft, hideLegLeft, hideLegRight;
    public boolean hideHeadOverlay, hideChestOverlay, hideArmRightOverlay, hideArmLeftOverlay, hideLegLeftOverlay, hideLegRightOverlay;
    
    public EntitySkinCapability(Entity entity, ISkinnableEntity skinnableEntity) {
        this.entity = entity;
        this.skinnableEntity = skinnableEntity;
        ArrayList<ISkinType> skinTypes = new ArrayList<ISkinType>();
        skinnableEntity.getValidSkinTypes(skinTypes);
        validSkinTypes = skinTypes.toArray(new ISkinType[skinTypes.size()]);
        skinInventoryContainer = new SkinInventoryContainer(this, validSkinTypes, skinnableEntity);
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    @Override
    public SkinInventoryContainer getSkinInventoryContainer() {
        return skinInventoryContainer;
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
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getSkinTypeInv(skinType);
        if (wardrobeInventory != null) {
            SkinDescriptor skinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(wardrobeInventory.getStackInSlot(slotIndex));
            return skinDescriptor;
        }
        return null;
    }

    @Override
    public ISkinDescriptor setSkinDescriptor(ISkinType skinType, int slotIndex, ISkinDescriptor skinDescriptor) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getSkinTypeInv(skinType);
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
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getSkinTypeInv(skinType);
        if (wardrobeInventory != null) {
            return wardrobeInventory.getStackInSlot(slotIndex);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack setSkinStack(ISkinType skinType, int slotIndex, ItemStack skinStack) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getSkinTypeInv(skinType);
        if (wardrobeInventory != null) {
            ItemStack oldItemStack = wardrobeInventory.getStackInSlot(slotIndex);
            wardrobeInventory.setInventorySlotContents(slotIndex, skinStack);
            return oldItemStack;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean setStackInNextFreeSlot(ItemStack stack) {
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (descriptor == null) {
            return false;
        }

        ISkinType skinType = descriptor.getIdentifier().getSkinType();
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getSkinTypeInv(skinType);
        if (wardrobeInventory == null) {
            return false;
        }

        int maxSlot = wardrobeInventory.getSizeInventory();
        if (entity instanceof EntityPlayer) {
            IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get((EntityPlayer) entity);
            if (wardrobeCap != null) {
                maxSlot = wardrobeCap.getUnlockedSlotsForSkinType(skinType);
            }
        }
        for (int i = 0; i < maxSlot; i++) {
            if (wardrobeInventory.getStackInSlot(i).isEmpty()) {
                wardrobeInventory.setInventorySlotContents(i, stack);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        skinInventoryContainer.clear();
    }
    
    @Override
    public void clearSkin(ISkinType skinType, int slotIndex) {
        WardrobeInventory wardrobeInventory = skinInventoryContainer.getSkinTypeInv(skinType);
        if (wardrobeInventory != null) {
            wardrobeInventory.removeStackFromSlot(slotIndex);
        }
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack) {
    }
    
    @Override
    public void dirty() {
        if (!entity.getEntityWorld().isRemote) {
            syncToAllTracking();
        }
    }
    
    private MessageServerSyncSkinCap getUpdateMessage() {
        NBTTagCompound compound = (NBTTagCompound)ENTITY_SKIN_CAP.getStorage().writeNBT(ENTITY_SKIN_CAP, this, null);
        return new MessageServerSyncSkinCap(entity.getEntityId(), compound);
    }
    
    @Override
    public void syncToPlayer(EntityPlayerMP entityPlayer) {
        PacketHandler.networkWrapper.sendTo(getUpdateMessage(), entityPlayer);
    }

    @Override
    public void syncToAllTracking() {
        PacketHandler.networkWrapper.sendToAllTracking(getUpdateMessage(), entity);
    }
    
    public static IEntitySkinCapability get(Entity entity) {
        return entity.getCapability(ENTITY_SKIN_CAP, null);
    }
}
