package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.Iterators;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.PoseStackWrapper;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
public class SkinRenderContext {

    private static final Iterator<SkinRenderContext> POOL = Iterators.cycle(ObjectUtils.makeItems(100, i -> new SkinRenderContext(new PoseStack())));

    private int lightmap = 0xf000f0;
    private int overlay = 0;
    private float partialTicks = 0;

    private MultiBufferSource buffers;

    private SkinRenderData renderData;
    private SkinRenderBufferSource bufferProvider;

    private float itemRenderPriority = 0;
    private Vector3f itemRotation;
    private ItemStack itemReference = ItemStack.EMPTY;

    private ColorScheme colorScheme = ColorScheme.EMPTY;
    private AbstractItemTransformType transformType = AbstractItemTransformType.NONE;

    private ITransformf[] transforms;

    private final PoseStack defaultPoseStack;
    private final PoseStackWrapper usingPoseStack;

    public SkinRenderContext(PoseStack poseStack) {
        this.defaultPoseStack = poseStack;
        this.usingPoseStack = new PoseStackWrapper(poseStack);
    }

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, AbstractItemTransformType transformType, PoseStack poseStack, MultiBufferSource buffers) {
        SkinRenderContext context = POOL.next();
        context.setRenderData(renderData);
        context.setLightmap(light);
        context.setPartialTicks(partialTick);
        context.setTransformType(transformType);
        context.setPose(poseStack);
        context.setBuffers(buffers);
        return context;
    }

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, PoseStack poseStack, MultiBufferSource buffers) {
        return alloc(renderData, light, partialTick, AbstractItemTransformType.NONE, poseStack, buffers);
    }

    public void release() {
        this.lightmap = 0xf000f0;
        this.partialTicks = 0;

        this.itemReference = ItemStack.EMPTY;
        this.itemRenderPriority = 0;
        this.itemRotation = null;

        this.colorScheme = ColorScheme.EMPTY;
        this.transformType = AbstractItemTransformType.NONE;

        this.usingPoseStack.set(defaultPoseStack);

        this.bufferProvider = null;
        this.renderData = null;
        this.buffers = null;

        this.transforms = null;
    }

    public boolean shouldRenderPart(ISkinPartType partType) {
        if (renderData != null && renderData.epicFlightContext != null && renderData.epicFlightContext.overrideParts != null) {
            return !renderData.epicFlightContext.overrideParts.contains(partType);
        }
        return true;
    }

    public void pushPose() {
        usingPoseStack.pushPose();
    }

    public void popPose() {
        usingPoseStack.popPose();
    }

    public PoseStackWrapper pose() {
        return usingPoseStack;
    }

    public void setLightmap(int lightmap) {
        this.lightmap = lightmap;
    }

    public int getLightmap() {
        return lightmap;
    }

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    public int getOverlay() {
        return overlay;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setTransformType(AbstractItemTransformType transformType) {
        this.transformType = transformType;
    }

    public AbstractItemTransformType getTransformType() {
        return transformType;
    }

    public void setTransforms(Entity entity, IModel model) {
        setTransforms(null, entity, model);
    }

    public void setTransforms(JointTransformModifier transformModifier, Entity entity, IModel model) {
        if (entity == null || model == null) {
            return;
        }
        if (transformModifier == null) {
            transformModifier = model.getAssociatedObject(JointTransformModifier.DEFAULT);
        }
        transforms = transformModifier.getTransforms(entity.getType(), model);
    }

    public ITransformf[] getTransforms() {
        return transforms;
    }

    public void setReference(float renderPriority, ItemStack reference) {
        setReference(reference, renderPriority, null);
    }

    public void setReference(ItemStack reference, float renderPriority, @Nullable Vector3f rotation) {
        this.itemReference = reference;
        this.itemRotation = rotation;
        this.itemRenderPriority = renderPriority;
    }

    public ItemStack getReference() {
        return itemReference;
    }

    public Vector3f getReferenceRotation() {
        return itemRotation;
    }

    public float getRenderPriority() {
        return itemRenderPriority;
    }

    public void setRenderData(SkinRenderData renderData) {
        this.renderData = renderData;
    }

    public SkinRenderData getRenderData() {
        return renderData;
    }

    public void setPose(PoseStack pose) {
        this.usingPoseStack.set(pose);
    }

    public void setBuffers(MultiBufferSource buffers) {
        this.buffers = buffers;
    }

    public MultiBufferSource getBuffers() {
        return buffers;
    }

    public void setBuffer(SkinRenderBufferSource bufferProvider) {
        this.bufferProvider = bufferProvider;
    }

    public SkinRenderBufferSource.ObjectBuilder getBuffer(@NotNull Skin skin) {
        if (bufferProvider != null) {
            return bufferProvider.getBuffer(skin);
        }
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        return bufferBuilder.getBuffer(skin);
    }
}
