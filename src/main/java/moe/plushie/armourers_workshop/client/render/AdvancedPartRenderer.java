package moe.plushie.armourers_workshop.client.render;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedData;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPart;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public final class AdvancedPartRenderer {

    public static void renderAdvancedSkin(Skin skin, SkinRenderData renderData, Entity entity, AdvancedData data, AdvancedPart part) {
        updateParts(skin, renderData, entity, data, part, 0);
        renderParts(skin, renderData, entity, part);
    }

    private static void updateParts(Skin skin, SkinRenderData renderData, Entity entity, AdvancedData data, AdvancedPart part, int depth) {
        double angle = 0;
        double flapTime = 5000F;
        if (entity != null) {
            angle = (((double) System.currentTimeMillis() + entity.getEntityId()) % flapTime);
            angle = Math.sin(angle / flapTime * Math.PI * 2);
        }
        double maxAngle = 5D;
        double minAngle = -5D;
        double fullAngle = maxAngle - minAngle;
        fullAngle *= angle;
        int owo = depth % 2;
        if (owo == 1) {
            // fullAngle = -fullAngle;
        }
        double x = 0;
        if (entity != null) {
            if (entity.getEntityWorld().isRaining()) {
                x = 30;
            }
        }
        part.rotationAngleOffset = new Vec3d(0, 0, 0);
        part.rotationAngleOffset = new Vec3d(x, fullAngle, fullAngle);
        for (int i = 0; i < part.getChildren().size(); i++) {
            AdvancedPart advancedPart = part.getChildren().get(i);
            updateParts(skin, renderData, entity, data, advancedPart, depth + 1);
        }
    }

    private static void renderParts(Skin skin, SkinRenderData renderData, Entity entity, AdvancedPart part) {
        GlStateManager.pushMatrix();
        SkinPart skinPart = skin.getParts().get(0);
        Vec3d pos = part.pos.add(part.posOffset);
        Vec3d rot = part.rotationAngle.add(part.rotationAngleOffset);
        float scale = renderData.getScale() * part.scale;
        GlStateManager.translate(pos.x * scale, pos.y * scale, pos.z * scale);
        GlStateManager.rotate((float) rot.x, 1, 0, 0);
        GlStateManager.rotate((float) rot.y, 0, 1, 0);
        GlStateManager.rotate((float) rot.z, 0, 0, 1);
        renderPart(new SkinPartRenderData(skinPart, renderData));
        for (int i = 0; i < part.getChildren().size(); i++) {
            renderParts(skin, renderData, entity, part.getChildren().get(i));
        }
        GlStateManager.popMatrix();
    }

    private static void renderPart(SkinPartRenderData partRenderData) {
        SkinPartRenderer.INSTANCE.renderPart(partRenderData);
    }
}
