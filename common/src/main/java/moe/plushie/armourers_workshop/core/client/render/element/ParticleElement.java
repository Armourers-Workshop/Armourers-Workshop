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


//    @Override
//    public void render(RenderableBedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTick)
//    {
//        this.calculateUVs(particle, partialTick);
//
//        /* Render the particle */
//        double px = Interpolations.lerp(particle.prevPosition.x, particle.position.x, partialTick);
//        double py = Interpolations.lerp(particle.prevPosition.y, particle.position.y, partialTick);
//        double pz = Interpolations.lerp(particle.prevPosition.z, particle.position.z, partialTick);
//        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTick);
//
//        if (particle.relativePosition && particle.relativeRotation)
//        {
//            this.vector.set((float) px, (float) py, (float) pz);
//            emitter.rotation.transform(this.vector);
//
//            px = this.vector.x;
//            py = this.vector.y;
//            pz = this.vector.z;
//
//            px += emitter.lastGlobal.x;
//            py += emitter.lastGlobal.y;
//            pz += emitter.lastGlobal.z;
//        }
//
//        /* Calculate yaw and pitch based on the facing mode */
//        float entityYaw = emitter.cYaw;
//        float entityPitch = emitter.cPitch;
//        double entityX = emitter.cX;
//        double entityY = emitter.cY;
//        double entityZ = emitter.cZ;
//        boolean lookAt = this.facing == CameraFacing.LOOKAT_XYZ || this.facing == CameraFacing.LOOKAT_Y;
//
//        /* Flip width when frontal perspective mode */
//        if (emitter.perspective == 2)
//        {
//            this.w = -this.w;
//        }
//        /* In GUI renderer */
//        else if (emitter.perspective == 100 && !lookAt)
//        {
//            entityYaw = 180 - entityYaw;
//
//            this.w = -this.w;
//            this.h = -this.h;
//        }
//
//        if (lookAt)
//        {
//            double dX = entityX - px;
//            double dY = entityY - py;
//            double dZ = entityZ - pz;
//            double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);
//
//            entityYaw = 180 - (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
//            entityPitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI))) + 180;
//        }
//
//        /* Calculate the geometry for billboards using cool matrix math */
//        int light = emitter.getBrightnessForRender(partialTick, px, py, pz);
//        int lightX = light >> 16 & 65535;
//        int lightY = light & 65535;
//
//        this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
//        this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
//        this.transform.setIdentity();
//
//        if (this.facing == CameraFacing.ROTATE_XYZ || this.facing == CameraFacing.LOOKAT_XYZ)
//        {
//            this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
//            this.transform.mul(this.rotation);
//            this.rotation.rotX(entityPitch / 180 * (float) Math.PI);
//            this.transform.mul(this.rotation);
//        }
//        else if (this.facing == CameraFacing.ROTATE_Y || this.facing == CameraFacing.LOOKAT_Y) {
//            this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
//            this.transform.mul(this.rotation);
//        }
//
//        this.rotation.rotZ(angle / 180 * (float) Math.PI);
//        this.transform.mul(this.rotation);
//        this.transform.setTranslation(new Vector3f((float) px, (float) py, (float) pz));
//
//        for (Vector4f vertex : this.vertices)
//        {
//            this.transform.transform(vertex);
//        }
//
//        float u1 = this.u1 / (float) this.textureWidth;
//        float u2 = this.u2 / (float) this.textureWidth;
//        float v1 = this.v1 / (float) this.textureHeight;
//        float v2 = this.v2 / (float) this.textureHeight;
//
//        builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//    }
//
//    @Override
//    public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTick)
//    {
//        this.calculateUVs(particle, partialTick);
//
//        this.w = this.h = 0.5F;
//        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTick);
//
//        /* Calculate the geometry for billboards using cool matrix math */
//        this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
//        this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
//        this.transform.setIdentity();
//        this.transform.setScale(scale * 2.75F);
//        this.transform.setTranslation(new Vector3f(x, y - scale / 2, 0));
//
//        this.rotation.rotZ(angle / 180 * (float) Math.PI);
//        this.transform.mul(this.rotation);
//
//        for (Vector4f vertex : this.vertices)
//        {
//            this.transform.transform(vertex);
//        }
//
//        float u1 = this.u1 / (float) this.textureWidth;
//        float u2 = this.u2 / (float) this.textureWidth;
//        float v1 = this.v1 / (float) this.textureHeight;
//        float v2 = this.v2 / (float) this.textureHeight;
//
//        BufferBuilder builder = Tessellator.getInstance().getBuffer();
//
//        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//        builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//
//        Tessellator.getInstance().draw();
//    }
