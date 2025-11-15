package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.armature.JointShape;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public abstract class AdvancedEntityGuideRenderer extends AdvancedAbstractGuideRenderer {

    protected final BakedArmature armature;
    protected final SkinTextureData texture;

    protected final IRenderType renderType;

    public AdvancedEntityGuideRenderer() {
        this.armature = armature();
        this.texture = texture();
        this.renderType = getRenderType(texture);
    }

    public abstract SkinTextureData texture();

    public abstract BakedArmature armature();

    public IRenderType getRenderType(SkinTextureData texture) {
        return SkinRenderType.entityCutoutNoCull(OpenResourceLocation.parse(texture.name()));
    }

    public void applyOffset(SkinDocument document, IGraphicsContext context) {

    }

    @Override
    public void render(SkinDocument document, int lightmap, int overlay, IGraphicsContext context) {
        context.saveGraphicsState();
        applyOffset(document, context);
        var transforms = armature.transforms();
        var armature1 = armature.armature();
        for (var joint : armature1.allJoints()) {
            var shape = armature1.shapeById(joint.id());
            var transform = transforms[joint.id()];
            if (shape != null && transform != null) {
                context.saveGraphicsState();
                transform.apply(context.ctm());
                renderShape(shape, Colors.getPaletteColor(joint.id()), context);
                context.restoreGraphicsState();
            }
        }
        context.restoreGraphicsState();
    }

    protected void renderShape(JointShape shape, int color, IGraphicsContext context) {
        context.saveGraphicsState();
        var rect = shape.bounds();
        shape.transform().apply(context.ctm());
        renderCube(shape, Colors.WHITE, context);
        renderOutline(rect, color, context);
        context.translateCTM(rect.x(), rect.y(), rect.z());
        for (var shape1 : shape.children()) {
            renderShape(shape1, color, context);
        }
        context.restoreGraphicsState();
    }

    protected void renderOutline(OpenRectangle3f rect, int color, IGraphicsContext context) {
        context.draw(ShapeElement.stroke(rect, color));
    }

    protected void renderCube(JointShape shape, int color, IGraphicsContext context) {
        context.draw(ShapeElement.fill(shape, texture, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, color, renderType));
    }
}
