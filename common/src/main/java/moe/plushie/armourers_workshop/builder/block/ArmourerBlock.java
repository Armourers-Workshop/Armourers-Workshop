package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.common.IBlockEntityProvider;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModBlockEntities;
import moe.plushie.armourers_workshop.init.ModMenus;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ArmourerBlock extends AbstractHorizontalBlock implements IBlockEntityProvider {

    public ArmourerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntities.ARMOURER.create(level, blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult traceResult) {
        return MenuManager.openMenu(ModMenus.ARMOURER, level.getBlockEntity(blockPos), player);
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
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        this.applyTitleEntity(level, blockPos, te -> te.onPlace(level, blockPos, blockState, livingEntity));
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.applyTitleEntity(level, blockPos, te -> te.onRemove(level, blockPos, te.getBlockState()));
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    private void applyTitleEntity(Level level, BlockPos pos, Consumer<ArmourerBlockEntity> consumer) {
        ArmourerBlockEntity blockEntity = ObjectUtils.safeCast(level.getBlockEntity(pos), ArmourerBlockEntity.class);
        if (blockEntity != null) {
            consumer.accept(blockEntity);
        }
    }
}
