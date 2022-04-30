package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class LinkingToolItem extends FlavouredItem implements IItemModelPropertiesProvider {

    public LinkingToolItem(Properties properties) {
        super(properties);
    }

    public static void setLinkedBlockPos(ItemStack itemStack, BlockPos pos) {
        AWDataSerializers.putBlockPos(itemStack.getOrCreateTag(), AWConstants.NBT.TILE_ENTITY_LINKED_POS, pos, null);
    }

    @Nullable
    public static BlockPos getLinkedBlockPos(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if (tag != null) {
            return AWDataSerializers.getBlockPos(tag, AWConstants.NBT.TILE_ENTITY_LINKED_POS, null);
        }
        return null;
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(AWCore.resource("empty"), (itemStack, world, entity) -> {
            CompoundNBT tag = itemStack.getTag();
            if (tag != null && tag.contains(AWConstants.NBT.TILE_ENTITY_LINKED_POS)) {
                return 0;
            }
            return 1;
        });
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack itemStack, ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        if (world.isClientSide || player == null) {
            return ActionResultType.CONSUME;
        }
        BlockPos linkedBlockPos = getLinkedBlockPos(itemStack);
        SkinnableTileEntity tileEntity = getTitleEntity(world, context.getClickedPos());
        if (tileEntity != null && player.isShiftKeyDown()) {
            tileEntity.setLinkedBlockPos(null);
            player.sendMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.clear"), player.getUUID());
            return ActionResultType.SUCCESS;
        }
        if (linkedBlockPos != null) {
            setLinkedBlockPos(itemStack, null);
            if (tileEntity != null) {
                tileEntity.setLinkedBlockPos(linkedBlockPos);
                player.sendMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.finish"), player.getUUID());
                return ActionResultType.SUCCESS;
            }
            player.sendMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.fail"), player.getUUID());
            return ActionResultType.SUCCESS;
        }
        if (tileEntity != null) {
            player.sendMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.linkedToSkinnable"), player.getUUID());
            return ActionResultType.FAIL;
        }
        setLinkedBlockPos(itemStack, context.getClickedPos());
        player.sendMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.start"), player.getUUID());
        return ActionResultType.SUCCESS;
    }

    private SkinnableTileEntity getTitleEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof SkinnableTileEntity) {
            return (SkinnableTileEntity) tileEntity;
        }
        return null;
    }
}
