package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.client.texture.SmartTexture;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;

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

    public static void clearCache() { }

    @Override
    public void prepare(IGraphicsContext context) {
        // Minecraft owns GPU uploads, native vertex/index buffers and Vulkan submission.
        // Do not install an OpenGL draw callback or allocate GL buffer names.
        if (part.isVisible()) submitWithoutVBO(context);
    }

    private void submitWithoutVBO(IGraphicsContext context) {
        BakedSkinPart part = this.part;
        SkinPaintScheme scheme = this.scheme;
        int lightmap = this.lightmap, overlay = this.overlay, outline = this.outlineColor;
        part.quads().forEach((renderType, quads) -> {
            BiConsumer<IPoseStack.Pose, IVertexConsumer> draw = (pose, builder) -> {
                var poseStack = new OpenPoseStack();
                var smartTexture = Optional.ofNullable(SmartTexture.of(renderType));
                smartTexture.ifPresent(ReferenceCounted::retain);
                try {
                    quads.forEach((transform, faces) -> {
                        poseStack.last().set(pose);
                        transform.apply(poseStack);
                        faces.forEach(face -> face.render(part, scheme, lightmap, overlay, poseStack, builder));
                    });
                } finally {
                    smartTexture.ifPresent(ReferenceCounted::release);
                }
            };
            context.draw(Lazy.create(renderType, draw));
            if ((outline & 0xff000000) != 0) {
                var nativeType = (net.minecraft.client.renderer.rendertype.RenderType) renderType.get();
                nativeType.outline().ifPresent(type -> context.draw(Lazy.create(
                    moe.plushie.armourers_workshop.compat.client.renderer.rendertype.AbstractRenderTypeBuilder.create(type),
                    (pose, builder) -> draw.accept(pose, new OutlineConsumer(builder, outline)))));
            }
        });
    }

    private record OutlineConsumer(IVertexConsumer parent, int tint) implements IVertexConsumer {
        public IVertexConsumer vertex(float x,float y,float z) { parent.vertex(x,y,z); return this; }
        public IVertexConsumer color(int r,int g,int b,int a) { parent.color(tint); return this; }
        public IVertexConsumer uv(float u,float v) { parent.uv(u,v); return this; }
        public IVertexConsumer overlayCoords(int u,int v) { parent.overlayCoords(u,v); return this; }
        public IVertexConsumer uv2(int u,int v) { parent.uv2(u,v); return this; }
        public IVertexConsumer normal(float x,float y,float z) { parent.normal(x,y,z); return this; }
        public void endVertex() { parent.endVertex(); }
    }

    /**
     * Lazy the contents rendering.
     */
    private interface Lazy extends IGraphicsElement, IGraphicsRenderable {

        static Lazy create(IRenderType renderType, BiConsumer<IPoseStack.Pose, IVertexConsumer> consumer) {
            return new Lazy() {

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
