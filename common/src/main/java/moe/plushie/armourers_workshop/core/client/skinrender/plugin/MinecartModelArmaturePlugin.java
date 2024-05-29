package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.DataStorageKey;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import manifold.ext.rt.api.auto;

public class MinecartModelArmaturePlugin extends ArmaturePlugin {

    private static final IAssociatedContainerKey<Boolean> IS_FLAPPED = DataStorageKey.of("isFlapped", Boolean.class, () -> false);

    public MinecartModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        auto minecart = (AbstractMinecart) entity;
        auto poseStack = context.pose();

        // fix minecart render direction issue.
        if (isFlapped(minecart, context.getPartialTicks(), context.getRenderData())) {
            // the vanilla design doesn't care the minecart orientation,
            // but we need a precise orientation.
            poseStack.rotate(Vector3f.YP.rotationDegrees(180));
        }

        // fix the direction.
        poseStack.scale(-1, -1, 1);
        poseStack.translate(0, -0.125f, 0);  // 0, -2, 0
        ModDebugger.translate(poseStack);
        poseStack.rotate(Vector3f.YP.rotationDegrees(180));
    }


    private boolean isFlapped(AbstractMinecart entity, float partialTicks, SkinRenderData renderData) {
        auto delta = entity.getDeltaMovement();
        double dx = delta.x();
        double dz = delta.z();
        if (dx == 0 && dz == 0) {
            // no move, keep the last state
            return renderData.getAssociatedObject(IS_FLAPPED);
        }
        auto level = entity.getLevel();
        int i = MathUtils.floor(MathUtils.lerp(partialTicks, entity.xOld, entity.getX()));
        int j = MathUtils.floor(MathUtils.lerp(partialTicks, entity.yOld, entity.getY()));
        int k = MathUtils.floor(MathUtils.lerp(partialTicks, entity.zOld, entity.getZ()));
        if (level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            --j;
        }
        BlockState blockState = level.getBlockState(new BlockPos(i, j, k));
        if (!BaseRailBlock.isRail(blockState)) {
            // no rail, keep the last state
            return renderData.getAssociatedObject(IS_FLAPPED);
        }
        auto shape = blockState.getValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty());
        boolean result = isFlapped(shape, dx, dz);
        renderData.setAssociatedObject(result, IS_FLAPPED);
        return result;
    }

    private boolean isFlapped(RailShape shape, double x, double z) {
        double eps = 1E-3;
        switch (shape) {
            case NORTH_WEST:
            case NORTH_EAST: {
                return z < -eps;
            }
            case SOUTH_EAST:
            case SOUTH_WEST: {
                return z > eps;
            }
            case NORTH_SOUTH: {
                return z < -eps;
            }
            case EAST_WEST: {
                return x < -eps;
            }
            case ASCENDING_EAST:
            case ASCENDING_WEST: {
                return x < -eps;
            }
            case ASCENDING_NORTH:
            case ASCENDING_SOUTH: {
                return z < -eps;
            }
            default: {
                ModLog.debug("{} - ({}, {})", shape, z, x);
                return false;
            }
        }
    }
}
