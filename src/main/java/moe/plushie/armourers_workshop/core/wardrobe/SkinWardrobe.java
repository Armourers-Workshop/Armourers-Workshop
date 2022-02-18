package moe.plushie.armourers_workshop.core.wardrobe;

import com.google.common.cache.Cache;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.api.*;
import moe.plushie.armourers_workshop.core.api.common.ISkinWardrobe;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinPalette;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.PaintColor;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class SkinWardrobe implements ISkinWardrobe, INBTSerializable<CompoundNBT> {

    private final Inventory inventory = new Inventory(SkinWardrobeSlotType.getTotalSize());
    private final HashSet<EquipmentSlotType> equipmentVisibility = new HashSet<>();

    private final WeakReference<Entity> entityRef;
    private final State state = new State();

    public SkinWardrobe(Entity entity) {
        this.entityRef = new WeakReference<>(entity);
        this.inventory.addListener(inventory -> state.invalidateAll());
    }

    @Nullable
    public static SkinWardrobe of(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        Object key = entity.getId();
        Cache<Object, LazyOptional<SkinWardrobe>> caches = SkinWardrobeStorage.getCaches(entity);
        LazyOptional<SkinWardrobe> wardrobe = caches.getIfPresent(key);
        if (wardrobe != null) {
            return wardrobe.resolve().orElse(null);
        }
        wardrobe = entity.getCapability(SkinWardrobeProvider.WARDROBE_KEY);
        wardrobe.addListener(self -> caches.invalidate(key));
        caches.put(key, wardrobe);
        return wardrobe.resolve().orElse(null);
    }

    public int getFreeSlot(SkinWardrobeSlotType slotType) {
        int unlockedSize = getUnlockedSize(slotType);
        for (int i = 0; i < unlockedSize; ++i) {
            if (inventory.getItem(slotType.getIndex() + i).isEmpty()) {
                return i + 1;
            }
        }
        return Integer.MAX_VALUE;
    }

    public ItemStack getItem(SkinWardrobeSlotType slotType, int slot) {
        if (slot >= getUnlockedSize(slotType)) {
            return ItemStack.EMPTY;
        }
        return inventory.getItem(slotType.getIndex() + slot);
    }

    public void setItem(SkinWardrobeSlotType slotType, int slot, ItemStack itemStack) {
        if (slot >= getUnlockedSize(slotType)) {
            return;
        }
        inventory.setItem(slotType.getIndex() + slot, itemStack);
    }

    public void clear() {
        inventory.clearContent();
    }

    public void sendToAll() {
        NetworkHandler.getInstance().sendToAll(new UpdateWardrobePacket(this));
    }

    public void sendToServer() {
        NetworkHandler.getInstance().sendToServer(new UpdateWardrobePacket(this));
    }

    public void broadcast(ServerPlayerEntity player) {
        NetworkHandler.getInstance().sendTo(new UpdateWardrobePacket(this), player);
    }

    public boolean shouldRenderEquipment(EquipmentSlotType slotType) {
        return !equipmentVisibility.contains(slotType);
    }

    public void setRenderEquipment(boolean enable, EquipmentSlotType slotType) {
        if (enable) {
            equipmentVisibility.remove(slotType);
        } else {
            equipmentVisibility.add(slotType);
        }
    }

    public int getUnlockedSize(SkinWardrobeSlotType slotType) {
        if (slotType == SkinWardrobeSlotType.DYE) {
            return 8;
        }
        return slotType.getSize();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Entity getEntity() {
        return entityRef.get();
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        SkinWardrobeStorage.saveVisibility(equipmentVisibility, nbt);
        SkinWardrobeStorage.saveInventoryItems(inventory, nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        SkinWardrobeStorage.loadVisibility(equipmentVisibility, nbt);
        SkinWardrobeStorage.loadInventoryItems(inventory, nbt);
        state.invalidateAll();
    }

    @OnlyIn(Dist.CLIENT)
    public State snapshot() {
        if (state.tick()) {
            state.reload();
        }
        return state;
    }

    @OnlyIn(Dist.CLIENT)
    public final class State implements SkinBakery.IBakeListener {

        private final ArrayList<SkinDescriptor> missingSkins = new ArrayList<>();

        private final ArrayList<BakedSkin> armorSkins = new ArrayList<>();
        private final ArrayList<BakedSkin> itemSkins = new ArrayList<>();

        private final HashMap<ISkinPaintType, PaintColor> dyeColors = new HashMap<>();

        private final HashSet<ISkinPartType> hasOverriddenParts = new HashSet<>();
        private final HashSet<ISkinPartType> hasParts = new HashSet<>();

        private final HashMap<ISkinPaintType, PaintColor> lastDyeColors = new HashMap<>();
        private final NonNullList<ItemStack> lastEquipmentSlots = NonNullList.withSize(EquipmentSlotType.values().length, ItemStack.EMPTY);

        private SkinPalette palette = SkinPalette.EMPTY;

        private int ticks = 0;

        private boolean isLoaded = false;
        private boolean isListening = false;

        public boolean tick() {
            Entity entity = getEntity();
            int ticks = entity.tickCount;
            if (this.ticks != ticks || !isLoaded) {
                this.ticks = ticks;
                return updateEquipmentSlots(entity) || !isLoaded;
            }
            return false;
        }

        public void reload() {
            Entity entity = getEntity();
            invalidateAll();

            loadDyeSlots(entity, this::updateDye);
            loadHandSlots(entity, this::updateSkin);
            loadArmorSlots(entity, this::updateSkin);

            didLoad();
        }

        public void invalidateAll() {
            ticks = 0;
            isLoaded = false;

            dyeColors.clear();
            missingSkins.clear();
            armorSkins.clear();
            itemSkins.clear();
            hasParts.clear();
            hasOverriddenParts.clear();
        }

        @Override
        public void didBake(SkinDescriptor descriptor, BakedSkin bakedSkin) {
            if (missingSkins.contains(descriptor)) {
                RenderUtils.call(this::invalidateAll);
            }
        }

        public Collection<BakedSkin> getArmorSkins() {
            return armorSkins;
        }

        public Collection<BakedSkin> getItemSkins(ItemStack itemStack) {
            if (itemStack.isEmpty()) {
                return Collections.emptyList();
            }
            for (BakedSkin bakedSkin : itemSkins) {
                if (bakedSkin.isOverride(itemStack)) {
                    return Collections.singletonList(bakedSkin);
                }
            }
            return Collections.emptyList();
        }

        public SkinPalette getPalette() {
            return palette;
        }

        public boolean hasPart(ISkinPartType partType) {
            return hasParts.contains(partType);
        }

        public boolean hasOverriddenPart(ISkinPartType partType) {
            return hasOverriddenParts.contains(partType);
        }

//        public boolean hasOverriddenEquipment(EquipmentSlotType slotType) {
//            switch (slotType) {
//                case HEAD:
//                    return hasOverriddenPart(SkinPartTypes.BIPED_HEAD);
//                case CHEST:
//                    return hasOverriddenPart(SkinPartTypes.BIPED_CHEST) || hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM) || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM);
//                case FEET:
//                    return hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT) || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT);
//                case LEGS:
//                    return hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG);
//                case OFFHAND:
//                case MAINHAND:
//                    break;
//            }
//            return false;
//        }

        private boolean updateEquipmentSlots(Entity entity) {
            int index = 0, changes = 0;
            for (ItemStack itemStack : entity.getAllSlots()) {
                if (lastEquipmentSlots.get(index).equals(itemStack)) {
                    lastEquipmentSlots.set(index, itemStack);
                    changes += 1;
                }
                index += 1;
            }
            return changes != 0;
        }

        private void updateDye(ISkinPaintType paintType, ItemStack itemStack) {
            PaintColor paintColor = BottleItem.getPaintColor(itemStack);
            if (paintColor != null) {
                dyeColors.put(paintType, paintColor);
            }
        }

        private void updateSkin(ItemStack itemStack, boolean allowsArmor) {
            if (itemStack.isEmpty()) {
                return;
            }
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            if (descriptor.isEmpty()) {
                return;
            }
            BakedSkin skin = AWCore.bakery.loadSkin(descriptor);
            if (skin == null) {
                missingSkins.add(descriptor);
                return;
            }
            ISkinType type = skin.getSkin().getType();
            if (type instanceof ISkinArmorType && allowsArmor) {
                armorSkins.add(skin);
                loadSkinPart(skin);
            }
            if (type instanceof ISkinToolType) {
                itemSkins.add(skin);
                loadSkinPart(skin);
            }
        }

        private void loadSkinPart(BakedSkin skin) {
            for (BakedSkinPart part : skin.getSkinParts()) {
                ISkinPartType partType = part.getType();
                hasParts.add(partType);
                if (part.isModelOverridden()) {
                    hasOverriddenParts.add(partType);
                }
            }
        }

        private void loadDyeSlots(Entity entity, BiConsumer<ISkinPaintType, ItemStack> consumer) {
            ISkinPaintType[] dyeSlots = SkinWardrobeSlotType.getDyeSlots();
            for (int i = 0; i < dyeSlots.length; ++i) {
                ItemStack itemStack = inventory.getItem(SkinWardrobeSlotType.DYE.getIndex() + i);
                consumer.accept(dyeSlots[i], itemStack);
            }
        }

        private void loadArmorSlots(Entity entity, BiConsumer<ItemStack, Boolean> consumer) {
            int size = SkinWardrobeSlotType.DYE.getIndex();
            entity.getArmorSlots().forEach(itemStack -> consumer.accept(itemStack, true));
            for (int i = 0; i < size; ++i) {
                consumer.accept(inventory.getItem(i), true);
            }
        }

        private void loadHandSlots(Entity entity, BiConsumer<ItemStack, Boolean> consumer) {
            entity.getHandSlots().forEach(itemStack -> consumer.accept(itemStack, false));
        }

        private void didLoad() {
            if (!lastDyeColors.equals(dyeColors)) {
                palette = new SkinPalette();
                lastDyeColors.clear();
                dyeColors.forEach((paintType, paintColor) -> {
                    lastDyeColors.put(paintType, paintColor);
                    palette.setColor(paintType, paintColor);
                });
            }
            isLoaded = true;
            if (missingSkins.isEmpty()) {
                if (isListening) {
                    AWCore.bakery.removeListener(this);
                    isListening = false;
                }
            } else {
                if (!isListening) {
                    AWCore.bakery.addListener(this);
                    isListening = true;
                }
            }
        }
    }
}

