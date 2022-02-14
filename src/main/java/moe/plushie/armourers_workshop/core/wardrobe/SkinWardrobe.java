package moe.plushie.armourers_workshop.core.wardrobe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.common.item.BottleItem;
import moe.plushie.armourers_workshop.core.api.*;
import moe.plushie.armourers_workshop.core.api.common.ISkinWardrobe;
import moe.plushie.armourers_workshop.core.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.skin.data.Palette;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
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
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class SkinWardrobe implements ISkinWardrobe, INBTSerializable<CompoundNBT> {

    private final static Cache<Object, LazyOptional<SkinWardrobe>> CACHES = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.SECONDS).build();

    private final Inventory inventory = new Inventory(SkinSlotType.getTotalSize());

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
        Object key = SkinCache.borrowKey(entity.getId(), entity.getClass());
        LazyOptional<SkinWardrobe> wardrobe = CACHES.getIfPresent(key);
        if (wardrobe != null) {
            SkinCache.returnKey(key);
            return wardrobe.resolve().orElse(null);
        }
        wardrobe = entity.getCapability(SkinWardrobeProvider.WARDROBE_KEY);
        if (!wardrobe.isPresent()) {
            SkinCache.returnKey(key);
            return null;
        }
        wardrobe.addListener(self -> CACHES.invalidate(key));
        CACHES.put(key, wardrobe);
        return wardrobe.resolve().orElse(null);
    }

    public int getFreeSlot(SkinSlotType slotType) {
        int unlockedSize = getUnlockedSize(slotType);
        for (int i = 0; i < unlockedSize; ++i) {
            if (inventory.getItem(slotType.getIndex() + i).isEmpty()) {
                return i + 1;
            }
        }
        return Integer.MAX_VALUE;
    }

    public ItemStack getItem(SkinSlotType slotType, int slot) {
        if (slot >= getUnlockedSize(slotType)) {
            return ItemStack.EMPTY;
        }
        return inventory.getItem(slotType.getIndex() + slot);
    }

    public void setItem(SkinSlotType slotType, int slot, ItemStack itemStack) {
        if (slot >= getUnlockedSize(slotType)) {
            return;
        }
        inventory.setItem(slotType.getIndex() + slot, itemStack);
        sendToAll();
    }

    public void clear() {
        inventory.clearContent();
        sendToAll();
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

    public int getUnlockedSize(SkinSlotType slotType) {
        if (slotType == SkinSlotType.DYE) {
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
        saveAllItems(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        loadAllItems(nbt);
        state.invalidateAll();
    }

    private void saveAllItems(CompoundNBT nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < itemStacks.size(); ++i) {
            itemStacks.set(i, inventory.getItem(i));
        }
        ItemStackHelper.saveAllItems(nbt, itemStacks);
    }

    private void loadAllItems(CompoundNBT nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, itemStacks);
        for (int i = 0; i < itemStacks.size(); ++i) {
            ItemStack newItemStack = itemStacks.get(i);
            ItemStack oldItemStack = inventory.getItem(i);
            if (!Objects.equals(newItemStack, oldItemStack)) {
                inventory.setItem(i, newItemStack);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public State snapshot() {
        if (state.tick(getEntity())) {
            state.refresh(getEntity());
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

        private Palette palette = Palette.EMPTY;

        private int tickCount = 0;
        private boolean isLoaded = false;
        private boolean isListening = false;

        public boolean tick(Entity entity) {
            int tickCount = entity.tickCount;
            if (tickCount == this.tickCount && isLoaded) {
                return false;
            }
            this.tickCount = tickCount;
            return updateEquipmentSlots(entity) || !isLoaded;
        }

        public void refresh(Entity entity) {
            invalidateAll();

            loadDyeSlots(entity, this::updateDye);
            loadHandSlots(entity, this::updateSkin);
            loadArmorSlots(entity, this::updateSkin);

            didLoad();
        }

        public void invalidateAll() {
            tickCount = 0;
            isLoaded = false;

            dyeColors.clear();
            ;
            missingSkins.clear();
            armorSkins.clear();
            itemSkins.clear();
            hasParts.clear();
            hasOverriddenParts.clear();
//            palette = new Palette();
        }

        @Override
        public void didBake(SkinDescriptor descriptor, BakedSkin bakedSkin) {
            if (missingSkins.contains(descriptor)) {
                RenderSystem.recordRenderCall(this::invalidateAll);
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

        public Palette getPalette() {
            return palette;
        }

        public boolean hasPart(ISkinPartType partType) {
            return hasParts.contains(partType);
        }

        public boolean hasOverriddenPart(ISkinPartType partType) {
            return hasOverriddenParts.contains(partType);
        }

        public boolean hasOverriddenEquipment(EquipmentSlotType slotType) {
            switch (slotType) {
                case HEAD:
                    return hasOverriddenPart(SkinPartTypes.BIPED_HEAD);
                case CHEST:
                    return hasOverriddenPart(SkinPartTypes.BIPED_CHEST) || hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM) || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM);
                case FEET:
                    return hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT) || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT);
                case LEGS:
                    return hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG);
                case OFFHAND:
                case MAINHAND:
                    break;
            }
            return false;
        }

        private boolean updateEquipmentSlots(Entity entity) {
            if (!(entity instanceof LivingEntity)) {
                return false;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            boolean isChanges = false;
            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
                int index = slotType.getFilterFlag();
                ItemStack itemStack = livingEntity.getItemBySlot(slotType);
                if (!lastEquipmentSlots.get(index).equals(itemStack)) {
                    lastEquipmentSlots.set(index, itemStack);
                    isChanges = true;
                }
            }
            return isChanges;
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
            BakedSkin skin = SkinCore.bakery.loadSkin(descriptor);
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
            ISkinPaintType[] dyeSlots = SkinSlotType.getDyeSlots();
            for (int i = 0; i < dyeSlots.length; ++i) {
                ItemStack itemStack = inventory.getItem(SkinSlotType.DYE.getIndex() + i);
                consumer.accept(dyeSlots[i], itemStack);
            }
        }

        private void loadArmorSlots(Entity entity, BiConsumer<ItemStack, Boolean> consumer) {
            int size = SkinSlotType.DYE.getIndex();
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
                palette = new Palette();
                lastDyeColors.clear();
                dyeColors.forEach((paintType, paintColor) -> {
                    lastDyeColors.put(paintType, paintColor);
                    palette.setColor(paintType, paintColor);
                });
            }
            isLoaded = true;
            if (missingSkins.isEmpty()) {
                if (isListening) {
                    SkinCore.bakery.removeListener(this);
                    isListening = false;
                }
            } else {
                if (!isListening) {
                    SkinCore.bakery.addListener(this);
                    isListening = true;
                }
            }
        }
    }
}

