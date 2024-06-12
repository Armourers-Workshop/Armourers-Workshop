package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public class SkinItem extends BlockItem implements IItemPropertiesProvider {

    public SkinItem(Block block, Item.Properties properties) {
        super(block, properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        SkinSlotType slotType = SkinSlotType.byType(descriptor.getType());
        if (descriptor.isEmpty() || slotType == null) {
            return InteractionResultHolder.pass(itemStack);
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || !wardrobe.isEditable(player) || !wardrobe.isSupported(slotType)) {
            return InteractionResultHolder.pass(itemStack);
        }
        int slot = wardrobe.getFreeSlot(slotType);
        if (!wardrobe.getItem(slotType, slot).isEmpty()) {
            return InteractionResultHolder.pass(itemStack);
        }
        // we need to wait for the server check.
        if (level.isClientSide()) {
            return InteractionResultHolder.success(itemStack);
        }
        // we need consume item stack even in creative mode.
        ItemStack resultStack = itemStack.copy();
        wardrobe.setItem(slotType, slot, resultStack.split(1));
        wardrobe.broadcast();
        player.setItemInHand(hand, resultStack);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack itemStack = context.getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() != SkinTypes.BLOCK) {
            return InteractionResult.PASS;
        }
        // we need expand the context info.
        return super.place(new SkinBlockPlaceContext(context));
    }

    @Override
    public Component getName(ItemStack itemStack) {
        var skin = SkinLoader.getInstance().getSkin(itemStack);
        if (skin != null && !skin.getCustomName().trim().isEmpty()) {
            return TranslateUtils.formatted(skin.getCustomName());
        }
        if (skin != null) {
            return TranslateUtils.Name.of(skin.getType());
        }
        return super.getName(itemStack);
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public void createModelProperties(BiConsumer<IResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("loading"), (itemStack, level, entity, id) -> {
            var descriptor = SkinDescriptor.of(itemStack);
            var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.INVENTORY);
            if (bakedSkin != null) {
                return 0;
            }
            return descriptor.getType().getId() / 1000f;
        });
    }
}
