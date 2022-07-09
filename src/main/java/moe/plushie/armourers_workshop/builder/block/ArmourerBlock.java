package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings("NullableProblems")
public class ArmourerBlock extends AbstractHorizontalBlock {

    public ArmourerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if (ModContainerTypes.open(ModContainerTypes.ARMOURER, player, world, pos)) {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
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
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, entity, itemStack);
        this.applyTitleEntity(world, pos, te -> te.onPlace(world, pos, state, entity));
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(world, pos, state, player);
        this.applyTitleEntity(world, pos, te -> te.onRemove(world, pos, state));
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, world, pos, explosion);
        this.applyTitleEntity(world, pos, te -> te.onRemove(world, pos, state));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArmourerTileEntity();
    }

    private void applyTitleEntity(World world, BlockPos pos, Consumer<ArmourerTileEntity> consumer) {
        TileEntity entity1 = world.getBlockEntity(pos);
        if (entity1 instanceof ArmourerTileEntity) {
            consumer.accept((ArmourerTileEntity) entity1);
        }
    }
}
