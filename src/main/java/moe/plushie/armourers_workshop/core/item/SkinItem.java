package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWBlocks;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.SkinItemUseContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class SkinItem extends Item {

    private final BlockItem blockItem;

    public SkinItem(Item.Properties properties) {
        super(properties);
        this.blockItem = new BlockItem(AWBlocks.SKINNABLE, properties);
    }

    public static ItemStack replace(ItemStack targetStack, ItemStack sourceStack) {
        CompoundNBT sourceNBT = null;
        if (!sourceStack.isEmpty()) {
            sourceNBT = sourceStack.getTagElement(AWConstants.NBT.SKIN);
        }
        if (sourceNBT != null && sourceNBT.size() != 0) {
            CompoundNBT targetNBT = targetStack.getOrCreateTag();
            targetNBT.put(AWConstants.NBT.SKIN, sourceNBT.copy());
        } else {
            CompoundNBT targetNBT = targetStack.getTag();
            if (targetNBT != null) {
                targetNBT.remove(AWConstants.NBT.SKIN);
            }
        }
        return targetStack;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getIconIndex(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            return 0;
        }
        return descriptor.getType().getId();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() == SkinTypes.BLOCK) {
            return place(new BlockItemUseContext(context));
        }
        return ActionResultType.PASS;
    }

    public ActionResultType place(BlockItemUseContext context) {
        return blockItem.place(new SkinItemUseContext(context));
    }

    @Override
    public ITextComponent getName(ItemStack itemStack) {
        Skin skin = SkinLoader.getInstance().getSkin(itemStack);
        if (skin != null && !skin.getCustomName().trim().isEmpty()) {
            return new StringTextComponent(skin.getCustomName());
        }
        return super.getName(itemStack);
    }
}
