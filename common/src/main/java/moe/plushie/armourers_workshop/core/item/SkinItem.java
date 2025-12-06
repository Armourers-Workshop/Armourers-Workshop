package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public class SkinItem extends FlavouredBlockItem {

    public SkinItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected OpenInteractionResult abi$use(Level level, Player player, OpenInteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var descriptor = SkinDescriptor.of(itemStack);
        var slotType = SkinSlotType.byType(descriptor.type());
        if (descriptor.isEmpty() || slotType == null) {
            return OpenInteractionResult.PASS;
        }
        var wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || !wardrobe.isEditable(player) || !wardrobe.isSupported(slotType)) {
            return OpenInteractionResult.PASS;
        }
        var slot = wardrobe.getFreeSlot(slotType);
        if (!wardrobe.getItem(slotType, slot).isEmpty()) {
            return OpenInteractionResult.PASS;
        }
        // we need to wait for the server check.
        if (level.isClientSide()) {
            return OpenInteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
        }
        // we need consume item stack even in creative mode.
        var resultStack = itemStack.copy();
        wardrobe.setItem(slotType, slot, resultStack.split(1));
        wardrobe.broadcast();
        player.setItemInHand(hand, resultStack);
        return OpenInteractionResult.CONSUME.heldItemTransformedTo(itemStack);
    }

    @Override
    protected OpenInteractionResult abi$place(BlockPlaceContext context) {
        var itemStack = context.getItemInHand();
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.type() != SkinTypes.BLOCK) {
            return OpenInteractionResult.PASS;
        }
        // we need expand the context info.
        return super.abi$place(new SkinBlockPlaceContext(context));
    }

    @Override
    protected void abi$appendModelProperties(BiConsumer<OpenResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("loading"), (itemStack, level, entity, id) -> {
            var descriptor = SkinDescriptor.of(itemStack);
            var bakedSkin = SkinBakery.getInstance().loadSkin(TicketManager.INVENTORY.get(descriptor));
            if (bakedSkin != null) {
                return 0;
            }
            return descriptor.type().id() / 1000f;
        });
    }

    @Override
    protected Component abi$getName(ItemStack itemStack) {
        var skin = SkinLoader.getInstance().getSkin(itemStack);
        if (skin != null && !skin.customName().trim().isEmpty()) {
            return TranslateUtils.formatted(skin.customName());
        }
        if (skin != null) {
            return Component.translatable("item.armourers_workshop.skin.name.loading", TranslateUtils.Name.of(skin.type()));
        }
        return Component.translatable("item.armourers_workshop.skin.name");
    }
}
