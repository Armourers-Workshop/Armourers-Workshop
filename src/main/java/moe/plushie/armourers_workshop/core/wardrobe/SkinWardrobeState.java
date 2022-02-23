package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.api.*;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.data.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.PaintColor;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
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

@OnlyIn(Dist.CLIENT)
public final class SkinWardrobeState implements SkinBakery.IBakeListener {

    private final Inventory inventory;

    private final ArrayList<SkinDescriptor> missingSkins = new ArrayList<>();

    private final ArrayList<BakedSkin> armorSkins = new ArrayList<>();
    private final ArrayList<BakedSkin> itemSkins = new ArrayList<>();

    private final HashMap<ISkinPaintType, PaintColor> dyeColors = new HashMap<>();

    private final HashSet<ISkinPartType> hasOverriddenParts = new HashSet<>();
    private final HashSet<ISkinPartType> hasParts = new HashSet<>();

    private final HashMap<ISkinPaintType, PaintColor> lastDyeColors = new HashMap<>();
    private final NonNullList<ItemStack> lastEquipmentSlots = NonNullList.withSize(EquipmentSlotType.values().length, ItemStack.EMPTY);

    private ColorScheme colorScheme = ColorScheme.EMPTY;

    private int ticks = 0;

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

    public Iterable<BakedSkin> getArmorSkins() {
        return armorSkins;
    }

    public Iterable<BakedSkin> getItemSkins() {
        return itemSkins;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public boolean hasPart(ISkinPartType partType) {
        return hasParts.contains(partType);
    }

    public boolean hasOverriddenPart(ISkinPartType partType) {
        return hasOverriddenParts.contains(partType);
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
        if (itemStack.isEmpty()) {
            return;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return;
        }
        BakedSkin skin = SkinBakery.getInstance().loadSkin(descriptor);
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
