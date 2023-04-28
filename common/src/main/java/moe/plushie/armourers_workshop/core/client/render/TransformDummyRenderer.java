package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractLivingEntityRenderer;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class TransformDummyRenderer<T extends LivingEntity> extends AbstractLivingEntityRenderer<T, TransformModel<T>> {

    private static TransformDummyRenderer<?> INSTANCE;

    private final ResourceLocation texture = DefaultPlayerSkin.getDefaultSkin();
    private final IPoseStack poseStack;
    private final MultiBufferSource.BufferSource bufferSource;

    public TransformDummyRenderer() {
        super(Context.sharedContext(), null, 0);
        this.poseStack = MatrixUtils.stack();
        this.bufferSource = MultiBufferSource.immediate(new BufferBuilder(4096));
        // yep, we need to hide everything.
        this.poseStack.scale(0, 0, 0);
    }

    public static TransformDummyRenderer<?> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransformDummyRenderer<>();
        }
        return INSTANCE;
    }

    public static boolean isDummy(Object value) {
        return value instanceof TransformDummyRenderer;
    }

    public void render(T entity, TransformModel<T> model, float f, float partialTicks, int light) {
        super.setModel(model);
        super.render(entity, 0, partialTicks, poseStack, bufferSource, light);
        super.setModel(null);
        // we'll still call end, it will empty unless some mod adds new vertices.
        bufferSource.endBatch();
    }

    @Override
    public boolean shouldShowName(T entity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
