package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.client.animation.AnimationTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinPartTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinWingsTransform;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BakedSkinPartCombiner {

    public static List<BakedSkinPart> apply(List<BakedSkinPart> skinParts) {
        var results = new ArrayList<BakedSkinPart>();
        for (var skinPart : skinParts) {
            results.add(clip(skinPart));
        }
        var partId = new AtomicInteger(0);
        ObjectUtils.search(results, BakedSkinPart::getChildren, bakedPart -> bakedPart.setId(partId.getAndIncrement()));
        return results;
    }


    private static BakedSkinPart clip(BakedSkinPart skinPart) {
        // single node, no needs any clip.
        if (skinPart.getChildren().isEmpty()) {
            return skinPart;
        }
        var restNodes = new ArrayList<Node>();
        var motionNodes = new ArrayList<Node>();
        var rootNode = new Node(null, skinPart);
        for (var childNode : rootNode.children) {
            childNode.freeze(motionNodes, restNodes);
        }
        var pendingQuads = new ArrayList<Pair<ISkinTransform, BakedCubeQuads>>();
        for (var childNode : restNodes) {
            var resolvedTransform = childNode.resolveTransform();
            var resolvedQuads = childNode.part.getQuads();
            pendingQuads.add(Pair.of(resolvedTransform, resolvedQuads));
        }
        var resolvedParts = new ArrayList<Pair<SkinPartTransform, BakedSkinPart>>();
        for (var childNode : motionNodes) {
            var resolvedTransform = childNode.resolveTransform();
            var resolvedPart = clip(childNode.part);
            resolvedParts.add(Pair.of(resolvedTransform, resolvedPart));
        }
        var mergedQuads = BakedCubeQuads.merge(skinPart.getQuads(), pendingQuads);
        var resolvedPart = new BakedSkinPart(skinPart.getPart(), skinPart.getTransform(), mergedQuads);
        resolvedParts.forEach(pair -> {
            var transform = pair.getKey();
            var part = pair.getValue();
            if (part.getTransform() == transform) {
                resolvedPart.addPart(part);
            } else {
                resolvedPart.addPart(new BakedSkinPart(part.getPart(), transform, part.getQuads()));
            }
        });
        return resolvedPart;
    }


    public static class Node {

        private final BakedSkinPart part;

        private final Node parent;
        private final ArrayList<Node> children = new ArrayList<>();

        private Node(Node parent, BakedSkinPart part) {
            this.parent = parent;
            this.part = part;
            for (var childPart : part.getChildren()) {
                this.children.add(new Node(this, childPart));
            }
        }

        public SkinPartTransform resolveTransform() {
            if (parent == null) {
                return SkinPartTransform.IDENTITY;
            }
            var childTransform = part.getTransform();
            var parentTransform = parent.resolveTransform();
            if (parentTransform.isIdentity()) {
                return childTransform;
            }
            if (childTransform.isIdentity()) {
                return parentTransform;
            }
            var mergedTransform = new SkinPartTransform();
            for (var transform : parentTransform.getChildren()) {
                mergedTransform.addChild(transform);
            }
            for (var transform : childTransform.getChildren()) {
                mergedTransform.addChild(transform);
            }
            return mergedTransform;
        }

        public void freeze(ArrayList<Node> motionNodes, ArrayList<Node> restNodes) {
            if (!freeze()) {
                motionNodes.add(this);
                return;
            }
            restNodes.add(this);
            for (var child : children) {
                child.freeze(motionNodes, restNodes);
            }
        }

        private boolean freeze() {
            for (var transform : part.getTransform().getChildren()) {
                if (transform instanceof SkinWingsTransform) {
                    return false;
                }
                if (transform instanceof AnimationTransform) {
                    return false;
                }
            }
            return true;
        }
    }
}
