package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class SkinRenderObjectBuilder implements ConcurrentBufferBuilder {

    protected final BakedSkin skin;
    protected final ConcurrentBufferCompiler compiler = new ConcurrentBufferCompiler();
    protected final ConcurrentRenderingPipeline pipeline = new ConcurrentRenderingPipeline();

    public SkinRenderObjectBuilder(BakedSkin skin) {
        this.skin = skin;
    }

    @Override
    public void addPart(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, ConcurrentRenderingContext context) {
        // debug the vbo render.
        if (ModDebugger.vbo) {
            drawWithoutVBO(part, skin, scheme, context);
            return;
        }
        draw(part, skin, scheme, false, context);
        if (context.shouldRenderOutline()) {
            draw(part, skin, scheme, true, context);
        }
    }

    @Override
    public void addShape(OpenVector3f origin, ConcurrentRenderingContext context) {
        ShapeTesselator.vector(origin, 16, context.poseStack(), context.bufferSource());
    }

    @Override
    public void addShape(OpenVoxelShape shape, int color, ConcurrentRenderingContext context) {
        ShapeTesselator.stroke(shape.bounds(), color, context.poseStack(), context.bufferSource());
    }

    @Override
    public void addShape(BakedArmature armature, ConcurrentRenderingContext context) {
        var bufferSource = context.bufferSource();
        var poseStack = context.poseStack();
        var transforms = armature.transforms();
        var armature1 = armature.armature();
        for (var joint : armature1.allJoints()) {
            var shape = armature1.shapeById(joint.id());
            var transform = transforms[joint.id()];
            if (ModDebugger.defaultArmature) {
                transform = armature1.globalTransformById(joint.id());
            }
            if (shape != null && transform != null) {
                poseStack.pushPose();
                transform.apply(poseStack);
//                ModDebugger.translate(context.pose().pose());
//			poseStack.translate(box.o.getX(), box.o.getY(), box.o.getZ());
                ShapeTesselator.stroke(shape, ColorUtils.getPaletteColor(joint.id()), poseStack, bufferSource);
                ShapeTesselator.vector(0, 0, 0, 4, 4, 4, poseStack, bufferSource);
                poseStack.popPose();
            }
        }
    }

    public void endBatch(Consumer<ShaderVertexObject> consumer) {
        pipeline.commit(consumer);
    }

    private void draw(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, boolean isOutline, ConcurrentRenderingContext context) {
        // we need compile the skin part, but not render when part invisible.
        var group = compiler.compile(part, skin, scheme, isOutline);
        if (group != null && !group.isEmpty() && part.isVisible()) {
            pipeline.add(group, context);
        }
    }

    private void drawWithoutVBO(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, ConcurrentRenderingContext context) {
        var poseStack = context.poseStack();
        var bufferSource = context.bufferSource();
        part.quads().forEach((renderType, quads) -> {
            var builder = bufferSource.getBuffer(renderType);
            quads.forEach((transform, faces) -> {
                poseStack.pushPose();
                transform.apply(poseStack);
                faces.forEach(face -> face.render(part, scheme, context.lightmap(), context.overlay(), poseStack, builder));
                poseStack.popPose();
            });
        });
    }

    private void drawWithVBO(ConcurrentBufferCompiler.Group group, ConcurrentRenderingContext context) {
        var pipeline1 = new ConcurrentRenderingPipeline();
        var pipeline2 = new SkinVertexBufferBuilder.Pipeline();
        pipeline1.add(group, context);
        pipeline1.commit(pipeline2::add);
        pipeline2.end();
    }
}
