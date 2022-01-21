package moe.plushie.armourers_workshop.core.wardrobe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.ISkinWardrobe;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.SkinPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SkinWardrobe implements ISkinWardrobe, INBTSerializable<CompoundNBT> {

    private final static Cache<Integer, LazyOptional<SkinWardrobe>> CACHES = CacheBuilder.newBuilder()
            .maximumSize(32)
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build();

    public final NonNullList<ItemStack> skinItemStacks = NonNullList.withSize(64, ItemStack.EMPTY);
    public final NonNullList<ItemStack> dyeItemStacks = NonNullList.withSize(16, ItemStack.EMPTY);

    private final WeakReference<Entity> entityRef;
    private SkinWardrobeState state;

    public SkinWardrobe(Entity entity) {
        this.entityRef = new WeakReference<>(entity);
    }

    @Nullable
    public static SkinWardrobe of(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        Integer key = entity.getId();
        LazyOptional<SkinWardrobe> wardrobe = CACHES.getIfPresent(key);
        if (wardrobe != null) {
            return wardrobe.resolve().orElse(null);
        }
        wardrobe = entity.getCapability(SkinWardrobeProvider.WARDROBE_KEY);
        wardrobe.addListener(self -> CACHES.invalidate(key));
        CACHES.put(key, wardrobe);
        return wardrobe.resolve().orElse(null);
    }


    public ItemStack getItemBySlot(int slot) {
        if (slot >= skinItemStacks.size()) {
            return ItemStack.EMPTY;
        }
        return skinItemStacks.get(slot);
    }

    public void setItemSlot(int slot, ItemStack itemStack) {
        if (slot >= skinItemStacks.size()) {
            return;
        }
        skinItemStacks.set(slot, itemStack);
        refresh();
        sync();
    }

    public void clear() {
        skinItemStacks.clear();
        refresh();
        sync();
    }

    public void refresh() {
        if (state != null) {
            state.invalidateAll();
            state = null;
        }
    }

    public void sync() {
        SkinPacketHandler.sendTo(this, null);
    }

    public void sync(ServerPlayerEntity player) {
        SkinPacketHandler.sendTo(this, player);
    }

    public Entity getEntity() {
        return entityRef.get();
    }

    public SkinWardrobeState getState() {
        if (state == null) {
            state = new SkinWardrobeState();
            state.loadSkin(getEntity(), skinItemStacks);
            state.loadSkinTheme(getEntity(), dyeItemStacks);
        }
        return state;
    }


    @OnlyIn(Dist.CLIENT)
    public Collection<BakedSkin> getArmorSkins() {
        return getState().armorSkins;
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<BakedSkin> getItemSkins(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        }
        for (BakedSkin bakedSkin : getState().itemSkins) {
            if (bakedSkin.isOverride(itemStack)) {
                return Collections.singletonList(bakedSkin);
            }
        }
        return Collections.emptyList();
    }

    @OnlyIn(Dist.CLIENT)
    public SkinDye getDye() {
        return getState().dye;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasPart(ISkinPartType partType) {
        return getState().hasParts.contains(partType);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasOverriddenPart(ISkinPartType partType) {
        return getState().hasOverriddenParts.contains(partType);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasOverriddenEquipment(EquipmentSlotType slotType) {
        switch (slotType) {
            case HEAD:
                return hasOverriddenPart(SkinPartTypes.BIPED_HEAD);
            case CHEST:
                return hasOverriddenPart(SkinPartTypes.BIPED_CHEST)
                        || hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)
                        || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM);
            case FEET:
                return hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)
                        || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT);
            case LEGS:
                return hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG)
                        || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG);
            case OFFHAND:
            case MAINHAND:
                break;
        }
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        // save slot into ListNBT
        ListNBT slots = new ListNBT();
        for (int i = 0; i < skinItemStacks.size(); ++i) {
            ItemStack itemStack = skinItemStacks.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            CompoundNBT itemData = new CompoundNBT();
            itemData.putByte("slot", (byte) i);
            itemStack.save(itemData);
            slots.add(itemData);
        }
        CompoundNBT nbt = new CompoundNBT();
        if (slots.size() != 0) {
            nbt.put("slots", slots);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        skinItemStacks.clear();
        // restore slots from ListNBT
        ListNBT slots = nbt.getList("slots", nbt.getId());
        for (int i = 0; i < slots.size(); ++i) {
            CompoundNBT itemData = slots.getCompound(i);
            int slot = itemData.getByte("slot") & 255;
            ItemStack itemStack = ItemStack.of(itemData);
            skinItemStacks.set(slot, itemStack);
        }
        refresh();
    }
}
