package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.DataStorageKey;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartModelArmaturePlugin extends ArmaturePlugin {

    private static final IAssociatedContainerKey<Boolean> IS_FLAPPED = DataStorageKey.of("isFlapped", Boolean.class, () -> false);

    public MinecartModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        var minecart = (AbstractMinecart) entity;
        var poseStack = context.pose();

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


    private boolean isFlapped(AbstractMinecart entity, float partialTicks, EntityRenderData renderData) {
        var delta = entity.getDeltaMovement();
        var dx = delta.x();
        var dz = delta.z();
        if (dx == 0 && dz == 0) {
            // no move, keep the last state
            return renderData.getAssociatedObject(IS_FLAPPED);
        }
        var level = entity.getLevel();
        var i = MathUtils.floor(MathUtils.lerp(partialTicks, entity.xOld, entity.getX()));
        var j = MathUtils.floor(MathUtils.lerp(partialTicks, entity.yOld, entity.getY()));
        var k = MathUtils.floor(MathUtils.lerp(partialTicks, entity.zOld, entity.getZ()));
        if (level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            --j;
        }
        var blockState = level.getBlockState(new BlockPos(i, j, k));
        if (!BaseRailBlock.isRail(blockState)) {
            // no rail, keep the last state
            return renderData.getAssociatedObject(IS_FLAPPED);
        }
        var shape = blockState.getValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty());
        var result = isFlapped(shape, dx, dz);
        renderData.setAssociatedObject(result, IS_FLAPPED);
        return result;
    }

    private boolean isFlapped(RailShape shape, double x, double z) {
        double eps = 1E-3;
        return switch (shape) {
            case NORTH_WEST, NORTH_EAST -> z < -eps;
            case SOUTH_EAST, SOUTH_WEST -> z > eps;
            case NORTH_SOUTH -> z < -eps;
            case EAST_WEST -> x < -eps;
            case ASCENDING_EAST, ASCENDING_WEST -> x < -eps;
            case ASCENDING_NORTH, ASCENDING_SOUTH -> z < -eps;
        };
    }
}
