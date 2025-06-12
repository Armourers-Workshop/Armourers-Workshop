package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenTransformedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SkinDocumentCollider {

    public static HashMap<OpenVector3i, OpenRectangle3i> generateCollisionBox(SkinDocumentNode node) {
        var boxes = generateCollisionBox(node, new OpenPoseStack());
        var results = new LinkedHashMap<OpenVector3i, OpenRectangle3i>();
        for (var it : boxes) {
            var box = it.transformedBoundingBox();

            var minX = OpenMath.floori(box.minX() + 8);
            var minY = OpenMath.floori(box.minY() + 8);
            var minZ = OpenMath.floori(box.minZ() + 8);
            var maxX = OpenMath.ceili(box.maxX() + 8);
            var maxY = OpenMath.ceili(box.maxY() + 8);
            var maxZ = OpenMath.ceili(box.maxZ() + 8);
            var tt = new OpenRectangle3i(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);

            var blockMinX = OpenMath.floori(minX / 16f);
            var blockMinY = OpenMath.floori(minY / 16f);
            var blockMinZ = OpenMath.floori(minZ / 16f);
            var blockMaxX = OpenMath.ceili(maxX / 16f);
            var blockMaxY = OpenMath.ceili(maxY / 16f);
            var blockMaxZ = OpenMath.ceili(maxZ / 16f);
            for (int z = blockMinZ; z <= blockMaxZ; ++z) {
                for (var y = blockMinY; y <= blockMaxY; ++y) {
                    for (var x = blockMinX; x <= blockMaxX; ++x) {
                        var rr = new OpenRectangle3i(x * 16, y * 16, z * 16, 16, 16, 16);
                        rr.intersection(tt);
                        if (rr.width() <= 0 || rr.height() <= 0 || rr.depth() <= 0) {
                            continue;
                        }
                        results.computeIfAbsent(new OpenVector3i(x, y, z), pos -> rr).union(rr);
                    }
                }
            }
        }
        return results;
    }

    private static ArrayList<OpenTransformedBoundingBox> generateCollisionBox(SkinDocumentNode node, OpenPoseStack poseStack) {
        var result = new ArrayList<OpenTransformedBoundingBox>();

        if (node.id().equals("float")) {
            return result;
        }

        poseStack.pushPose();

        node.transform().apply(poseStack);
        var skin = SkinLoader.getInstance().loadSkin(node.skin().identifier());
        if (skin != null) {
            for (var part : skin.parts()) {
                result.addAll(generateCollisionBox(part, poseStack));
            }
        }

        node.children().forEach(child -> {
            result.addAll(generateCollisionBox(child, poseStack));
        });

        poseStack.popPose();
        return result;
    }

    private static ArrayList<OpenTransformedBoundingBox> generateCollisionBox(SkinPart part, OpenPoseStack poseStack) {
        var result = new ArrayList<OpenTransformedBoundingBox>();
        poseStack.pushPose();
        part.transform().apply(poseStack);
        part.geometries().forEach(geometry -> {
            poseStack.pushPose();
            geometry.transform().apply(poseStack);
            var aabb = geometry.shape().aabb();
            var tbb = new OpenTransformedBoundingBox(poseStack.last().pose().copy(), aabb);
            result.add(tbb);
            poseStack.popPose();
        });
        part.children().forEach(child -> {
            result.addAll(generateCollisionBox(child, poseStack));
        });
        poseStack.popPose();
        return result;
    }
}
