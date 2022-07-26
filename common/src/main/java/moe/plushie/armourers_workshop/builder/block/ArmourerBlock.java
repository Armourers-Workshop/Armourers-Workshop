package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModMenus;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;

public class ArmourerBlock extends AbstractHorizontalBlock implements EntityBlock {

    public ArmourerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (MenuManager.openMenu(ModMenus.ARMOURER, player, world, pos)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation p_185499_2_) {
        return state; // can't rotate
    }

    @Override
    public BlockState mirror(BlockState state, Mirror p_185471_2_) {
        return state; // can't mirror
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @org.jetbrains.annotations.Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        this.applyTitleEntity(level, blockPos, te -> te.onPlace(level, blockPos, blockState, livingEntity));
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.applyTitleEntity(level, blockPos, te -> te.onRemove(level, blockPos, te.getBlockState()));
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new ArmourerBlockEntity();
    }

    private void applyTitleEntity(Level world, BlockPos pos, Consumer<ArmourerBlockEntity> consumer) {
        BlockEntity entity1 = world.getBlockEntity(pos);
        if (entity1 instanceof ArmourerBlockEntity) {
            consumer.accept((ArmourerBlockEntity) entity1);
        }
    }
}
