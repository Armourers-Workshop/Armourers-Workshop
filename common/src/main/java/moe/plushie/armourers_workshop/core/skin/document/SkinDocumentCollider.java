package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenBoundingBox;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenTransformedBoundingBox;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SkinDocumentCollider {

    public static HashMap<Vector3i, Rectangle3i> generateCollisionBox(SkinDocumentNode node) {
        var boxes = generateCollisionBox(node, new OpenPoseStack());
        var results = new LinkedHashMap<Vector3i, Rectangle3i>();
        for (var it : boxes) {
            var box = it.getTransformedBoundingBox();

            var minX = MathUtils.floor(box.getMinX() + 8);
            var minY = MathUtils.floor(box.getMinY() + 8);
            var minZ = MathUtils.floor(box.getMinZ() + 8);
            var maxX = MathUtils.ceil(box.getMaxX() + 8);
            var maxY = MathUtils.ceil(box.getMaxY() + 8);
            var maxZ = MathUtils.ceil(box.getMaxZ() + 8);
            var tt = new Rectangle3i(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);

            var blockMinX = MathUtils.floor(minX / 16f);
            var blockMinY = MathUtils.floor(minY / 16f);
            var blockMinZ = MathUtils.floor(minZ / 16f);
            var blockMaxX = MathUtils.ceil(maxX / 16f);
            var blockMaxY = MathUtils.ceil(maxY / 16f);
            var blockMaxZ = MathUtils.ceil(maxZ / 16f);
            for (int z = blockMinZ; z <= blockMaxZ; ++z) {
                for (var y = blockMinY; y <= blockMaxY; ++y) {
                    for (var x = blockMinX; x <= blockMaxX; ++x) {
                        var rr = new Rectangle3i(x * 16, y * 16, z * 16, 16, 16, 16);
                        rr.intersection(tt);
                        if (rr.getWidth() <= 0 || rr.getHeight() <= 0 || rr.getDepth() <= 0) {
                            continue;
                        }
                        results.computeIfAbsent(new Vector3i(x, y, z), pos -> rr).union(rr);
                    }
                }
            }
        }
        return results;
    }

    private static ArrayList<OpenTransformedBoundingBox> generateCollisionBox(SkinDocumentNode node, OpenPoseStack poseStack) {
        var result = new ArrayList<OpenTransformedBoundingBox>();

        if (node.getId().equals("float")) {
            return result;
        }

        poseStack.pushPose();

        node.getTransform().apply(poseStack);
        var skin = SkinLoader.getInstance().loadSkin(node.getSkin().getIdentifier());
        if (skin != null) {
            for (var part : skin.getParts()) {
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
        part.getTransform().apply(poseStack);
        part.getCubeData().forEach(cube -> {
            poseStack.pushPose();
            cube.getTransform().apply(poseStack);
            var aabb = new OpenBoundingBox(cube.getShape());
            var tbb = new OpenTransformedBoundingBox(poseStack.last().pose().copy(), aabb);
            result.add(tbb);
            poseStack.popPose();
        });
        part.getParts().forEach(child -> {
            result.addAll(generateCollisionBox(child, poseStack));
        });
        poseStack.popPose();
        return result;
    }
}
