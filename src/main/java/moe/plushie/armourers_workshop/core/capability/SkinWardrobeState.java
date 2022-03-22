package moe.plushie.armourers_workshop.core.capability;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.core.api.*;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class SkinWardrobeState implements SkinBakery.IBakeListener {

    private static final ImmutableMap<ISkinPartType, EquipmentSlotType> PART_TO_EQUIPMENT_SLOTS = new ImmutableMap.Builder<ISkinPartType, EquipmentSlotType>()
            .put(SkinPartTypes.BIPED_HEAD, EquipmentSlotType.HEAD)
            .put(SkinPartTypes.BIPED_CHEST, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_LEFT_ARM, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_RIGHT_ARM, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_LEFT_FOOT, EquipmentSlotType.FEET)
            .put(SkinPartTypes.BIPED_RIGHT_FOOT, EquipmentSlotType.FEET)
            .put(SkinPartTypes.BIPED_LEFT_LEG, EquipmentSlotType.LEGS)
            .put(SkinPartTypes.BIPED_RIGHT_LEG, EquipmentSlotType.LEGS)
            .build();

    private final Inventory inventory;

    private final ArrayList<SkinDescriptor> missingSkins = new ArrayList<>();

    private final ArrayList<BakedSkin> armorSkins = new ArrayList<>();
    private final ArrayList<BakedSkin> itemSkins = new ArrayList<>();

    private final HashMap<ISkinPaintType, PaintColor> dyeColors = new HashMap<>();

    private final HashSet<ISkinPartType> hasOverriddenModelParts = new HashSet<>();
    private final HashSet<ISkinPartType> hasOverriddenOverlayParts = new HashSet<>();

    private final HashSet<ISkinPartType> hasParts = new HashSet<>();

    private final HashMap<ISkinPaintType, PaintColor> lastDyeColors = new HashMap<>();
    private final NonNullList<ItemStack> lastEquipmentSlots = NonNullList.withSize(EquipmentSlotType.values().length, ItemStack.EMPTY);

    private ColorScheme colorScheme = ColorScheme.EMPTY;

    private int ticks = 0;

    private boolean isLimitLimbs = false;// MODEL_LEGS_LIMIT_LIMBS
    private boolean isLoaded = false;
    private boolean isListening = false;

    public SkinWardrobeState(Inventory inventory) {
        this.inventory = inventory;
    }

    public boolean tick(Entity entity) {
        int ticks = entity.tickCount;
        if (this.ticks != ticks || !isLoaded) {
            this.ticks = ticks;
            return updateEquipmentSlots(entity) || !isLoaded;
        }
        return false;
    }

    public void reload(Entity entity) {
        invalidateAll();

        loadDyeSlots(entity, this::updateDye);
        loadHandSlots(entity, this::updateSkin);
        loadArmorSlots(entity, this::updateSkin);

        didLoad();
    }

    public void invalidateAll() {
        if (!isLoaded) {
            return;
        }
        ticks = 0;
        isLoaded = false;
        isLimitLimbs = false;

        dyeColors.clear();
        missingSkins.clear();
        armorSkins.clear();
        itemSkins.clear();
        hasParts.clear();
        hasOverriddenModelParts.clear();
        hasOverriddenOverlayParts.clear();
    }

    @Override
    public void didBake(SkinDescriptor descriptor, BakedSkin bakedSkin) {
        if (missingSkins.contains(descriptor)) {
            RenderUtils.call(this::invalidateAll);
        }
    }

    public Iterable<BakedSkin> getArmorSkins() {
        return armorSkins;
    }

    public Iterable<BakedSkin> getItemSkins() {
        return itemSkins;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public boolean isLimitLimbs() {
        return isLimitLimbs;
    }

    public boolean hasPart(ISkinPartType partType) {
        return hasParts.contains(partType);
    }

    public boolean hasOverriddenPart(ISkinPartType partType) {
        return hasOverriddenModelParts.contains(partType);
    }

    public boolean hasOverriddenEquipmentPart(ISkinPartType partType, Function<EquipmentSlotType, Boolean> shouldRenderEquipmentSlot) {
        if (hasOverriddenOverlayParts.contains(partType)) {
            return true;
        }
        EquipmentSlotType slotType = PART_TO_EQUIPMENT_SLOTS.get(partType);
        if (slotType != null) {
            return !shouldRenderEquipmentSlot.apply(slotType);
        }
        return false;
    }


    private boolean updateEquipmentSlots(Entity entity) {
        int index = 0, changes = 0;
        for (ItemStack itemStack : entity.getAllSlots()) {
            if (index >= lastEquipmentSlots.size()) {
                break;
            }
            if (!lastEquipmentSlots.get(index).equals(itemStack)) {
                lastEquipmentSlots.set(index, itemStack);
                changes += 1;
            }
            index += 1;
        }
        return changes != 0;
    }

    private void updateDye(ISkinPaintType paintType, ItemStack itemStack) {
        PaintColor paintColor = ColoredItem.getColor(itemStack);
        if (paintColor != null) {
            dyeColors.put(paintType, paintColor);
        }
    }

    private void updateSkin(ItemStack itemStack, boolean allowsArmor) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return;
        }
        BakedSkin skin = BakedSkin.of(descriptor);
        if (skin == null) {
            missingSkins.add(descriptor);
            return;
        }
        ISkinType type = skin.getSkin().getType();
        if (type instanceof ISkinArmorType && allowsArmor) {
            armorSkins.add(skin);
            loadSkinPart(skin);
        }
        if (type instanceof ISkinToolType || type == SkinTypes.ITEM) {
            itemSkins.add(skin);
            loadSkinPart(skin);
        }
    }

    private void loadSkinPart(BakedSkin skin) {
        // check exists part
        for (BakedSkinPart part : skin.getSkinParts()) {
            hasParts.add(part.getType());
        }
        // check all part status, some skin only one part, but overridden all the models/overlays
        SkinProperties properties = skin.getSkin().getProperties();
        for (ISkinPartType partType : skin.getType().getParts()) {
            if (partType.isModelOverridden(properties)) {
                hasOverriddenModelParts.add(partType);
            }
            if (partType.isOverlayOverridden(properties)) {
                hasOverriddenOverlayParts.add(partType);
            }
            if (partType == SkinPartTypes.BIPED_SKIRT && !isLimitLimbs) {
                isLimitLimbs = properties.get(SkinProperty.MODEL_LEGS_LIMIT_LIMBS);
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
            colorScheme = new ColorScheme();
            lastDyeColors.clear();
            dyeColors.forEach((paintType, paintColor) -> {
                lastDyeColors.put(paintType, paintColor);
                colorScheme.setColor(paintType, paintColor);
            });
        }
        isLoaded = true;
        if (missingSkins.isEmpty()) {
            if (isListening) {
                SkinBakery.getInstance().removeListener(this);
                isListening = false;
            }
        } else {
            if (!isListening) {
                SkinBakery.getInstance().addListener(this);
                isListening = true;
            }
        }
    }
}
