package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.skin.Skin;
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
import java.util.List;

public class SkinDocumentCollider {

    public static HashMap<Vector3i, Rectangle3i> generateCollisionBox(SkinDocumentNode node) {
        List<OpenTransformedBoundingBox> boxes = generateCollisionBox(node, new OpenPoseStack());
        LinkedHashMap<Vector3i, Rectangle3i> results = new LinkedHashMap<>();
        for (OpenTransformedBoundingBox it : boxes) {
            OpenBoundingBox box = it.getTransformedBoundingBox();

            int minX = MathUtils.floor(box.getMinX() + 8);
            int minY = MathUtils.floor(box.getMinY() + 8);
            int minZ = MathUtils.floor(box.getMinZ() + 8);
            int maxX = MathUtils.ceil(box.getMaxX() + 8);
            int maxY = MathUtils.ceil(box.getMaxY() + 8);
            int maxZ = MathUtils.ceil(box.getMaxZ() + 8);
            Rectangle3i tt = new Rectangle3i(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);

            int blockMinX = MathUtils.floor(minX / 16f);
            int blockMinY = MathUtils.floor(minY / 16f);
            int blockMinZ = MathUtils.floor(minZ / 16f);
            int blockMaxX = MathUtils.ceil(maxX / 16f);
            int blockMaxY = MathUtils.ceil(maxY / 16f);
            int blockMaxZ = MathUtils.ceil(maxZ / 16f);
            for (int z = blockMinZ; z <= blockMaxZ; ++z) {
                for (int y = blockMinY; y <= blockMaxY; ++y) {
                    for (int x = blockMinX; x <= blockMaxX; ++x) {
                        Rectangle3i rr = new Rectangle3i(x * 16, y * 16, z * 16, 16, 16, 16);
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
        ArrayList<OpenTransformedBoundingBox> result = new ArrayList<>();

        if (node.getId().equals("float")) {
            return result;
        }

        poseStack.pushPose();

        node.getTransform().apply(poseStack);
        Skin skin = SkinLoader.getInstance().loadSkin(node.getSkin().getIdentifier());
        if (skin != null) {
            for (SkinPart part : skin.getParts()) {
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
        ArrayList<OpenTransformedBoundingBox> result = new ArrayList<>();
        poseStack.pushPose();
        part.getTransform().apply(poseStack);
        part.getCubeData().forEach(cube -> {
            poseStack.pushPose();
            cube.getTransform().apply(poseStack);
            OpenBoundingBox aabb = new OpenBoundingBox(cube.getShape());
            OpenTransformedBoundingBox tbb = new OpenTransformedBoundingBox(poseStack.last().pose().copy(), aabb);
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
