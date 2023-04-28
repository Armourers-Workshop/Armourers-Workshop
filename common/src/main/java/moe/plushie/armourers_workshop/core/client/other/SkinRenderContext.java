package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.Iterators;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.common.IItemTransformType;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;
import moe.plushie.armourers_workshop.core.skin.Skin;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

;

public class SkinRenderContext {

    private static final Iterator<SkinRenderContext> POOL = Iterators.cycle(make(32));

    public int light;
    public float partialTicks;
    public IPoseStack poseStack;
    public SkinRenderData renderData;
    public MultiBufferSource buffers;
    public IItemTransformType transformType;

    private ITransformf[] transforms;

    public int slotIndex;
    public ItemStack itemStack;

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, IItemTransformType transformType, IPoseStack poseStack, MultiBufferSource buffers) {
        SkinRenderContext context = POOL.next();
        context.init(renderData, light, partialTick, transformType, poseStack, buffers);
        return context;
    }

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, IPoseStack poseStack, MultiBufferSource buffers) {
        return alloc(renderData, light, partialTick, IItemTransformType.NONE, poseStack, buffers);
    }

    private static Collection<SkinRenderContext> make(int size) {
        ArrayList<SkinRenderContext> contexts = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            contexts.add(new SkinRenderContext());
        }
        return contexts;
    }

    public void init(SkinRenderData renderData, int light, float partialTick, IItemTransformType transformType, IPoseStack poseStack, MultiBufferSource buffers) {
        this.renderData = renderData;
        this.light = light;
        this.partialTicks = partialTick;
        this.transformType = transformType;
        this.poseStack = poseStack;
        this.buffers = buffers;
        // ig
        this.itemStack = ItemStack.EMPTY;
        this.slotIndex = 0;
        this.transforms = null;
    }

    public void release() {
    }

    public void pushPose() {
        poseStack.pushPose();
    }

    public void popPose() {
        poseStack.popPose();
    }

    public boolean shouldRenderPart(ISkinPartType partType) {
        if (renderData != null && renderData.epicFlightContext != null && renderData.epicFlightContext.overrideParts != null) {
            return !renderData.epicFlightContext.overrideParts.contains(partType);
        }
        return true;
    }

    public void setItem(ItemStack itemStack, int slotIndex) {
        this.itemStack = itemStack;
        this.slotIndex = slotIndex;
    }

    public void setTransforms(Entity entity, IModelHolder<?> model) {
        setTransforms(null, entity, model);
    }

    public void setTransforms(JointTransformModifier transformModifier, Entity entity, IModelHolder<?> model) {
        if (entity == null || model == null) {
            return;
        }
        if (transformModifier == null) {
            transformModifier = model.getExtraData(JointTransformModifier.DEFAULT);
        }
        transforms = transformModifier.getTransforms(entity.getType(), model);
    }

    public ITransformf[] getTransforms() {
        return transforms;
    }

    public SkinRenderObjectBuilder getBuffer(@NotNull Skin skin) {
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        return bufferBuilder.getBuffer(skin);
    }

}
