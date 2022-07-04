package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModItems;
import moe.plushie.armourers_workshop.utils.SkinItemUseContext;
import moe.plushie.armourers_workshop.utils.slot.SkinSlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

@SuppressWarnings("NullableProblems")
public class SkinItem extends Item implements IItemModelPropertiesProvider {

    private final BlockItem blockItem;

    public SkinItem(Item.Properties properties) {
        super(properties);
        this.blockItem = new BlockItem(ModBlocks.SKINNABLE, properties);
    }

    public static ItemStack replace(ItemStack targetStack, ItemStack sourceStack) {
        CompoundNBT sourceNBT = null;
        if (!sourceStack.isEmpty()) {
            sourceNBT = sourceStack.getTagElement(AWConstants.NBT.SKIN);
        }
        if (sourceNBT != null && sourceNBT.size() != 0) {
            targetStack.addTagElement(AWConstants.NBT.SKIN, sourceNBT.copy());
        } else {
            CompoundNBT targetNBT = targetStack.getTag();
            if (targetNBT != null) {
                targetNBT.remove(AWConstants.NBT.SKIN);
            }
        }
        return targetStack;
    }

    public static ItemStack replace(ItemStack targetStack, SkinDescriptor descriptor) {
        if (targetStack.isEmpty()) {
            return descriptor.asItemStack();
        }
        if (targetStack.getItem() == ModItems.SKIN_TEMPLATE) {
            return descriptor.asItemStack();
        }
        if (descriptor.isEmpty()) {
            CompoundNBT targetNBT = targetStack.getTag();
            if (targetNBT != null) {
                targetNBT.remove(AWConstants.NBT.SKIN);
            }
        } else {
            targetStack.addTagElement(AWConstants.NBT.SKIN, descriptor.serializeNBT());
        }
        return targetStack;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() == SkinTypes.BLOCK) {
            return place(new BlockItemUseContext(context));
        }
        return ActionResultType.FAIL;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        SkinSlotType slotType = SkinSlotType.of(descriptor.getType());
        if (descriptor.isEmpty() || slotType == null) {
            return ActionResult.pass(itemStack);
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || !wardrobe.isEditable(player)) {
            return ActionResult.pass(itemStack);
        }
        int slot = wardrobe.getFreeSlot(slotType);
        if (!wardrobe.getItem(slotType, slot).isEmpty()) {
            return ActionResult.pass(itemStack);
        }
        wardrobe.setItem(slotType, slot, itemStack.copy());
        itemStack.shrink(1);
        if (!world.isClientSide()) {
            wardrobe.sendToAll();
        }
        return ActionResult.sidedSuccess(itemStack.copy(), world.isClientSide());
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

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(AWCore.resource("loading"), (itemStack, world, entity) -> {
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            BakedSkin bakedSkin = BakedSkin.of(descriptor);
            if (bakedSkin != null) {
                return 0;
            }
            return descriptor.getType().getId();
        });
    }
}
