package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractModelViewStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.client.other.SkinGraphicsContext;
import moe.plushie.armourers_workshop.core.client.texture.SmartTexture;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModDebugger;

import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings("unsed")
public class SkinPartElement implements IGraphicsElement {

    private static final ObjectPool<SkinPartElement> POOL = ObjectPool.create(SkinPartElement::new);

    private BakedSkinPart part;
    private BakedSkin skin;
    private SkinPaintScheme scheme;

    private int lightmap;
    private int overlay;

    private int outlineColor;
    private float renderPriority;

    public static SkinPartElement newInstance(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, ConcurrentRenderingContext context) {
        var that = POOL.alloc();
        that.part = part;
        that.skin = skin;
        that.scheme = scheme;
        that.lightmap = context.lightmap();
        that.overlay = context.overlay();
        that.outlineColor = context.outlineColor();
        that.renderPriority = context.itemSource().renderPriority();
        return that;
    }

    @Override
    public void prepare(IGraphicsContext context) {
        // debug render without vbo.
        if (ModDebugger.withoutVBO) {
            submitWithoutVBO(context);
            return;
        }
        // save ctm.
        var ms = AbstractModelViewStack.getInstance();
        var src = context.ctm().last();
        var dest = new OpenPoseStack.Pose();
        // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
        dest.setProperties(src.properties());
        dest.pose().set(ms.last());
        dest.pose().multiply(src.pose());
        dest.normal().set(src.normal());
        // submit skin vertex into graphics.
        var context1 = SkinGraphicsContext.getInstance();
        submitWithVBO(dest, context1);
        context1.submit(context);
    }

    private void submitWithVBO(OpenPoseStack.Pose pose, SkinGraphicsContext context) {
        context.draw(part, skin, scheme, lightmap, overlay, pose, false, outlineColor, renderPriority);
        if (shouldRenderOutline()) {
            context.draw(part, skin, scheme, lightmap, overlay, pose, true, outlineColor, renderPriority);
        }
    }

    private void submitWithoutVBO(IGraphicsContext context) {
        var poseStack = new OpenPoseStack();
        part.quads().forEach((renderType, quads) -> {
            context.draw(LazyRenderImpl.create(renderType, (pose, builder) -> {
                var smartTexture = Optional.ofNullable(SmartTexture.of(renderType));
                smartTexture.ifPresent(ReferenceCounted::retain);
                quads.forEach((transform, faces) -> {
                    poseStack.last().set(pose);
                    transform.apply(poseStack);
                    faces.forEach(face -> face.render(part, scheme, lightmap, overlay, poseStack, builder));
                });
                smartTexture.ifPresent(ReferenceCounted::release);
            }));
        });
    }

    private boolean shouldRenderOutline() {
        return (outlineColor & 0xff000000) != 0;
    }

    /**
     * Lazy the contents rendering.
     */
    protected interface LazyRenderImpl extends IGraphicsElement, IGraphicsRenderable {

        static LazyRenderImpl create(IRenderType renderType, BiConsumer<IPoseStack.Pose, IVertexConsumer> consumer) {
            return new LazyRenderImpl() {

                @Override
                public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
                    consumer.accept(pose, builder);
                }

                @Override
                public IRenderType renderType() {
                    return renderType;
                }
            };
        }
    }
}
