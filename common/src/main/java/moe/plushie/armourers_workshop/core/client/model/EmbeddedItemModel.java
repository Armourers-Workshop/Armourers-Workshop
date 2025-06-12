package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.SkinItemProperties;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EmbeddedItemModel {

    private SkinItemProperties properties;

    private final LivingEntity entity;
    private final Level level;

    private final int sourceMode; // 2 force render in box
    private final ItemStack sourceStack;
    private final SkinDescriptor sourceSkin;
    private final EntitySlot sourceSlot;

    private EmbeddedItemModel(@Nullable LivingEntity entity, @Nullable Level level, int sourceMode, EntitySlot sourceSlot, SkinDescriptor sourceSkin, ItemStack sourceStack) {
        this.entity = entity;
        this.level = level;
        this.sourceMode = sourceMode;
        this.sourceStack = sourceStack;
        this.sourceSkin = sourceSkin;
        this.sourceSlot = sourceSlot;
    }


    public static EmbeddedItemModel fromWardrobe(@Nullable LivingEntity entity, @Nullable Level level, EntitySlot slot) {
        return new EmbeddedItemModel(entity, level, 0, slot, slot.descriptor(), slot.itemStack());
    }

    public static EmbeddedItemModel fromComponent(@Nullable LivingEntity entity, @Nullable Level level, SkinDescriptor descriptor, ItemStack itemStack) {
        return new EmbeddedItemModel(entity, level, 1, null, descriptor, itemStack);

    }

    public static EmbeddedItemModel fromSelf(@Nullable LivingEntity entity, @Nullable Level level, SkinDescriptor descriptor, ItemStack itemStack) {
        return new EmbeddedItemModel(entity, level, 2, null, descriptor, itemStack);
    }


    public LivingEntity entity() {
        return entity;
    }


    public Level level() {
        return level;
    }

    public void setProperties(SkinItemProperties properties) {
        this.properties = properties;
    }

    public SkinItemProperties properties() {
        return properties;
    }

    public SkinDescriptor sourceSkin() {
        return sourceSkin;
    }

    public ItemStack sourceStack() {
        return sourceStack;
    }

    public EntitySlot sourceSlot() {
        return sourceSlot;
    }

    public boolean shouldRenderInBox() {
        // for the item required render to box.
        if (sourceMode == 2) {
            return true;
        }
        var skinType = sourceSkin.type();
        if (skinType == SkinTypes.BOAT || skinType == SkinTypes.ITEM_FISHING || skinType == SkinTypes.HORSE) {
            return true;
        }
        // for the tool type skin, don't render in the box.
        if (skinType.isTool()) {
            return false;
        }
        // for the item type skin, don't render in the box.
        if (skinType == SkinTypes.ITEM) {
            return false;
        }
        return true;
    }
}
