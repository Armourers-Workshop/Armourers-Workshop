package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartModelArmaturePlugin extends ArmaturePlugin {

    private static final DataContainer.Key<Boolean> IS_FLAPPED = DataContainer.key("isFlapped", false);

    public MinecartModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void prepare(EntityRenderState renderState, Entity entity, float partialTicks) {
        var renderData = EntityRenderData.of(entity);
        var isFlapped = isFlapped(entity, partialTicks, renderData);
        renderState.setAssociatedObject(IS_FLAPPED, isFlapped);
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        // fix minecart render direction issue.
        // the vanilla design doesn't care the minecart orientation,
        // but we need a precise orientation.
        if (renderState.getAssociatedObject(IS_FLAPPED)) {
            context.rotateCTM(OpenVector3f.YP.rotationDegrees(180));
        }

        // fix the direction.
        context.scaleCTM(-1, -1, 1);
        context.translateCTM(0, -0.125f, 0);  // 0, -2, 0
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(180));
    }

    private boolean isFlapped(Entity entity, float partialTicks, EntityRenderData renderData) {
        var delta = entity.getDeltaMovement();
        var dx = delta.x();
        var dz = delta.z();
        if (dx == 0 && dz == 0) {
            // no move, keep the last state
            return renderData.getAssociatedObject(IS_FLAPPED);
        }
        var level = entity.level();
        var i = OpenMath.floori(OpenMath.lerp(partialTicks, entity.xOld, entity.getX()));
        var j = OpenMath.floori(OpenMath.lerp(partialTicks, entity.yOld, entity.getY()));
        var k = OpenMath.floori(OpenMath.lerp(partialTicks, entity.zOld, entity.getZ()));
        if (level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            j -= 1;
        }
        var blockState = level.getBlockState(new BlockPos(i, j, k));
        if (!BaseRailBlock.isRail(blockState)) {
            // no rail, keep the last state
            return renderData.getAssociatedObject(IS_FLAPPED);
        }
        var shape = blockState.getValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty());
        var result = isFlapped(shape, dx, dz);
        renderData.setAssociatedObject(IS_FLAPPED, result);
        return result;
    }

    private boolean isFlapped(RailShape shape, double x, double z) {
        double eps = 1e-3;
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
