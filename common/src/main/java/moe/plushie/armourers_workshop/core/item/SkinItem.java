package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class SkinItem extends Item implements IItemPropertiesProvider {

    private final LazyOptional<BlockItem> blockItem;

    public SkinItem(Item.Properties properties) {
        super(properties);
        this.blockItem = LazyOptional.of(() -> new BlockItem(ModBlocks.SKINNABLE.get(), properties));
    }

    public static ItemStack replace(ItemStack targetStack, ItemStack sourceStack) {
        CompoundTag sourceNBT = null;
        if (!sourceStack.isEmpty()) {
            sourceNBT = sourceStack.getTagElement(Constants.Key.SKIN);
        }
        if (sourceNBT != null && sourceNBT.size() != 0) {
            targetStack.addTagElement(Constants.Key.SKIN, sourceNBT.copy());
        } else {
            CompoundTag targetNBT = targetStack.getTag();
            if (targetNBT != null) {
                targetNBT.remove(Constants.Key.SKIN);
            }
        }
        return targetStack;
    }

    public static ItemStack replace(ItemStack targetStack, SkinDescriptor descriptor) {
        if (targetStack.isEmpty()) {
            return descriptor.asItemStack();
        }
        if (targetStack.getItem() == ModItems.SKIN_TEMPLATE.get()) {
            return descriptor.asItemStack();
        }
        if (descriptor.isEmpty()) {
            CompoundTag targetNBT = targetStack.getTag();
            if (targetNBT != null) {
                targetNBT.remove(Constants.Key.SKIN);
            }
        } else {
            targetStack.addTagElement(Constants.Key.SKIN, descriptor.serializeNBT());
        }
        return targetStack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() == SkinTypes.BLOCK) {
            return place(new BlockPlaceContext(context));
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        SkinSlotType slotType = SkinSlotType.of(descriptor.getType());
        if (descriptor.isEmpty() || slotType == null) {
            return InteractionResultHolder.pass(itemStack);
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || !wardrobe.isEditable(player)) {
            return InteractionResultHolder.pass(itemStack);
        }
        int slot = wardrobe.getFreeSlot(slotType);
        if (!wardrobe.getItem(slotType, slot).isEmpty()) {
            return InteractionResultHolder.pass(itemStack);
        }
        wardrobe.setItem(slotType, slot, itemStack.copy());
        itemStack.shrink(1);
        if (!level.isClientSide()) {
            wardrobe.broadcast();
        }
        return InteractionResultHolder.sidedSuccess(itemStack.copy(), level.isClientSide());
    }

    public InteractionResult place(BlockPlaceContext context) {
        return blockItem.resolve().map(item -> item.place(new SkinBlockPlaceContext(context))).orElse(InteractionResult.PASS);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        Skin skin = SkinLoader.getInstance().getSkin(itemStack);
        if (skin != null && !skin.getCustomName().trim().isEmpty()) {
            return new TextComponent(skin.getCustomName());
        }
        return super.getName(itemStack);
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(ArmourersWorkshop.getResource("loading"), (itemStack, world, entity) -> {
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            BakedSkin bakedSkin = BakedSkin.of(descriptor);
            if (bakedSkin != null) {
                return 0;
            }
            return descriptor.getType().getId();
        });
    }

}
