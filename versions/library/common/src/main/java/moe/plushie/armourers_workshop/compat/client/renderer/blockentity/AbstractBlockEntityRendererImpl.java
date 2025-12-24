package moe.plushie.armourers_workshop.compat.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.annotation.Patch;
import moe.plushie.armourers_workshop.api.client.IBlockEntityRenderer;
import moe.plushie.armourers_workshop.api.client.state.IBlockEntityRenderState;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

@Available("[1.18, 1.26)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractBlockEntityRendererImpl<T extends BlockEntity> implements BlockEntityRenderer<T> {

    public AbstractBlockEntityRendererImpl(IBlockEntityRenderer.Context context) {
    }

    public static IBlockEntityRenderer.Context wrap(BlockEntityRendererProvider.Context context) {
        return null; // not used
    }

    public static BlockEntityRendererProvider.Context unwrap(IBlockEntityRenderer.Context context) {
        return null; // not used
    }

    public static <T extends BlockEntity, S extends IBlockEntityRenderState> IBlockEntityRenderer<T, S> wrap(BlockEntityRenderer<T> renderer) {
        return Objects.unsafeCast(renderer);
    }

    public static <T extends BlockEntity, S extends IBlockEntityRenderState> BlockEntityRenderer<T> unwrap(IBlockEntityRenderer<T, S> renderer) {
        return Objects.unsafeCast(renderer);
    }

    public static <T extends BlockEntity> BlockEntityRendererProvider<T> builder(IBlockEntityRenderer.Provider<T> provider) {
        return context -> AbstractBlockEntityRenderer.unwrap(provider.create(wrap(context)));
    }

    public abstract void render(T entity, float f, PoseStack poseStack, MultiBufferSource bufferSource, int i, int j, Vec3 pos);

    @Patch("implemented in 1.26+")
    public final void render(T entity, float f, PoseStack poseStack, MultiBufferSource bufferSource, int i, int j) {
        render(entity, f, poseStack, bufferSource, i, j, Vec3.ZERO);
    }

    @Patch("implemented in 1.26+")
    public boolean shouldRenderOffScreen() {
        return false;
    }

    public final boolean shouldRenderOffScreen(T entity) {
        return shouldRenderOffScreen();
    }

    public abstract boolean shouldRender(T blockEntity);
}
