package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.init.ModDebugger;

public class ParticleElement implements IGraphicsElement, IGraphicsRenderable {

    private static final ObjectPool<ParticleElement> POOL = ObjectPool.create(ParticleElement::new);

    private final OpenVector3f p0 = new OpenVector3f(); // left top
    private final OpenVector3f p1 = new OpenVector3f(); // left bottom
    private final OpenVector3f p2 = new OpenVector3f(); // right bottom
    private final OpenVector3f p3 = new OpenVector3f(); // right top

    private final OpenVector2f t0 = new OpenVector2f();
    private final OpenVector2f t1 = new OpenVector2f();
    private final OpenVector2f t2 = new OpenVector2f();
    private final OpenVector2f t3 = new OpenVector2f();

    private final OpenVector3f normal = new OpenVector3f();

    private int color;

    private int overlay;
    private int lightmap;

    private OpenVector3f position;
    private OpenQuaternionf rotation;
    private OpenSize2f size;

    private IRenderType renderType;

    public static ParticleElement newInstance(OpenVector3f position, OpenQuaternionf rotation, OpenSize2f size, OpenRectangle2f textureBox, int color, int lightmap, int overlay, IRenderType renderType) {
        var that = POOL.alloc();
        that.color = color;
        that.lightmap = lightmap;
        that.overlay = overlay;
        that.position = position;
        that.rotation = rotation;
        that.size = size;
        that.renderType = renderType;
        that.setBounds(size, that.rotation);
        that.setTextureBox(textureBox);
        return that;
    }

    @Override
    public void prepare(IGraphicsContext context) {
        if (ModDebugger.skinParticleOrigin) {
            context.saveGraphicsState();
            context.translateCTM(position);
            context.rotateCTM(rotation);
            context.draw(ShapeElement.arrow());
            context.restoreGraphicsState();
        }
        if (ModDebugger.skinParticleBounds) {
            context.saveGraphicsState();
            context.translateCTM(position);
            context.rotateCTM(rotation);
            context.draw(ShapeElement.stroke(-size.width / 2, -size.width / 2, 0, size.width, size.width, 0, Colors.RED));
            context.restoreGraphicsState();
        }
    }

    @Override
    public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
        var r = Colors.getRed(color);
        var g = Colors.getGreen(color);
        var b = Colors.getBlue(color);
        var a = Colors.getAlpha(color);

        var u1 = OverlayTexture.getU(overlay);
        var v1 = OverlayTexture.getV(overlay);

        var u2 = LightmapTexture.getU(lightmap);
        var v2 = LightmapTexture.getV(lightmap);

        builder.vertex(pose, p0.x, p0.y, p0.z).color(r, g, b, a).uv(t0.x, t0.y).overlayCoords(u1, v1).uv2(u2, v2).normal(pose, normal.x, normal.y, normal.z).endVertex();
        builder.vertex(pose, p1.x, p1.y, p1.z).color(r, g, b, a).uv(t1.x, t1.y).overlayCoords(u1, v1).uv2(u2, v2).normal(pose, normal.x, normal.y, normal.z).endVertex();
        builder.vertex(pose, p2.x, p2.y, p2.z).color(r, g, b, a).uv(t2.x, t2.y).overlayCoords(u1, v1).uv2(u2, v2).normal(pose, normal.x, normal.y, normal.z).endVertex();
        builder.vertex(pose, p3.x, p3.y, p3.z).color(r, g, b, a).uv(t3.x, t3.y).overlayCoords(u1, v1).uv2(u2, v2).normal(pose, normal.x, normal.y, normal.z).endVertex();
    }

    @Override
    public IRenderType renderType() {
        return renderType;
    }

    private void setBounds(OpenSize2f size, OpenQuaternionf rotation) {
        var hw = size.width * 0.5f;
        var hh = size.height * 0.5f;

        p0.set(hw, -hh, 0);
        p1.set(hw, hh, 0);
        p2.set(-hw, hh, 0);
        p3.set(-hw, -hh, 0);

        p0.transform(rotation);
        p1.transform(rotation);
        p2.transform(rotation);
        p3.transform(rotation);

        p0.add(position);
        p1.add(position);
        p2.add(position);
        p3.add(position);

        normal.set(0, 0, 1);
        normal.transform(rotation);
    }

    private void setTextureBox(OpenRectangle2f box) {
        t0.set(box.maxX(), box.maxY());
        t1.set(box.maxX(), box.minY());
        t2.set(box.minX(), box.minY());
        t3.set(box.minX(), box.maxY());
    }
}
