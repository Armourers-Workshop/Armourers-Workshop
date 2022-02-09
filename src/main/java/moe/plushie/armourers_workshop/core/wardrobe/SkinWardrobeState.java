package moe.plushie.armourers_workshop.core.wardrobe;

import com.google.common.collect.Iterators;
import moe.plushie.armourers_workshop.core.api.ISkinArmorType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkinWardrobeState {

    public final ArrayList<SkinDescriptor> missingSkins = new ArrayList<>();

    public final ArrayList<BakedSkin> armorSkins = new ArrayList<>();
    public final ArrayList<BakedSkin> itemSkins = new ArrayList<>();

    public final HashSet<ISkinPartType> hasOverriddenParts = new HashSet<>();
    public final HashSet<ISkinPartType> hasParts = new HashSet<>();

    public SkinDye dye = new SkinDye();


    public NonNullList<ItemStack> lastAllSlots = NonNullList.withSize(6, ItemStack.EMPTY);

    public boolean isLoaded = false;

    public void invalidateAll(@Nullable Entity entity) {
        if (entity instanceof LivingEntity) {
            if (updateSlots((LivingEntity) entity)) {
                invalidateAll();
            }
        }
    }

    public void invalidateAll() {
        isLoaded = false;
        missingSkins.clear();
        armorSkins.clear();
        itemSkins.clear();
        hasOverriddenParts.clear();
        hasParts.clear();
    }

    private boolean updateSlots(LivingEntity entity) {
        boolean isChanges = false;
        for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            int index = slotType.getFilterFlag();
            ItemStack itemStack = entity.getItemBySlot(slotType);
            if (!lastAllSlots.get(index).equals(itemStack)) {
                lastAllSlots.set(index, itemStack);
                isChanges = true;
            }
        }
        return isChanges;
    }


    public void loadSkinDye(Entity entity, List<ItemStack> itemStacks) {

//        for (SkinPaintType paintType : SkinPaintTypes.values()) {
//            if (paintType.getDyeType() == null) {
//                continue;
//            }
//            int length = UtilColour.PALETTE_MINECRAFT.length;
//            int color = UtilColour.PALETTE_MINECRAFT[paintType.getIndex() % length];
//            dye.setColor(paintType, 0xff000000 | color);
//        }
//        int i = 0;
//        dye.setColor(SkinPaintTypes.DYE_1, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_2.getId()));
//        dye.setColor(SkinPaintTypes.DYE_2, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_3.getId()));
//        dye.setColor(SkinPaintTypes.DYE_3, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_4.getId()));
//        dye.setColor(SkinPaintTypes.DYE_4, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_5.getId()));
//        dye.setColor(SkinPaintTypes.DYE_5, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_6.getId()));
//        dye.setColor(SkinPaintTypes.DYE_6, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_7.getId()));
//        dye.setColor(SkinPaintTypes.DYE_7, UtilColour.getPaletteColor(++i, SkinPaintTypes.DYE_8.getId()));
//        dye.setColor(SkinPaintTypes.DYE_8, UtilColour.getPaletteColor(++i, SkinPaintTypes.MISC_1.getId()));
//        dye.setColor(SkinPaintTypes.MISC_1, UtilColour.getPaletteColor(++i, SkinPaintTypes.MISC_2.getId()));
//        dye.setColor(SkinPaintTypes.MISC_2, UtilColour.getPaletteColor(++i, SkinPaintTypes.MISC_3.getId()));
//        dye.setColor(SkinPaintTypes.MISC_3, UtilColour.getPaletteColor(++i, SkinPaintTypes.MISC_4.getId()));
//        dye.setColor(SkinPaintTypes.MISC_4, UtilColour.getPaletteColor(++i, SkinPaintTypes.SKIN.getId()));
//        dye.setColor(SkinPaintTypes.SKIN, UtilColour.getPaletteColor(++i, SkinPaintTypes.HAIR.getId()));
//        dye.setColor(SkinPaintTypes.HAIR, UtilColour.getPaletteColor(++i, SkinPaintTypes.EYES.getId()));
//        dye.setColor(SkinPaintTypes.EYES, UtilColour.getPaletteColor(++i, SkinPaintTypes.NORMAL.getId()));
    }

    public void loadSkin(Entity entity, List<ItemStack> itemStacks) {
        Iterator<ItemStack> slots = itemStacks.iterator();
        Iterator<ItemStack> handSlots = Iterators.singletonIterator(ItemStack.EMPTY);

        if (entity instanceof LivingEntity) {
            slots = Iterators.concat(slots, entity.getArmorSlots().iterator());
            handSlots = entity.getHandSlots().iterator();
            updateSlots((LivingEntity) entity);
        }

        Iterators.transform(slots, SkinDescriptor::of).forEachRemaining(descriptor -> loadSkin(descriptor, true));
        Iterators.transform(handSlots, SkinDescriptor::of).forEachRemaining(descriptor -> loadSkin(descriptor, false));

        armorSkins.forEach(this::loadSkinPart);
        itemSkins.forEach(this::loadSkinPart);

        didLoad();
    }

    private void loadSkin(SkinDescriptor descriptor, boolean allowsArmor) {
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
        }
        if (type instanceof ISkinToolType) {
            itemSkins.add(skin);
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

    private void didLoad() {

    }
}
