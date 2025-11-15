package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.client.animation.AnimatedTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.part.wings.WingPartTransform;
import moe.plushie.armourers_workshop.init.ModDebugger;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BakedSkinPartCombiner {

    public static List<BakedSkinPart> apply(List<BakedSkinPart> skinParts) {
        if (ModDebugger.skinPartCombiner) {
            return skinParts;
        }
        var results = new ArrayList<BakedSkinPart>();
        for (var skinPart : skinParts) {
            results.add(clip(skinPart));
        }
        return results;
    }

    private static BakedSkinPart clip(BakedSkinPart rootPart) {
        // single node, no needs any clipLayer.
        if (rootPart.children().isEmpty()) {
            return rootPart;
        }
        var restNodes = new ArrayList<Node>();
        var motionNodes = new ArrayList<Node>();
        var rootNode = new Node(null, rootPart);
        for (var childNode : rootNode.children) {
            childNode.freeze(motionNodes, restNodes);
        }
        var pendingQuads = new ArrayList<Pair<ITransform, BakedGeometryQuads>>();
        for (var childNode : restNodes) {
            var resolvedTransform = childNode.resolveTransform();
            var resolvedQuads = childNode.part.quads();
            pendingQuads.add(Pair.of(resolvedTransform, resolvedQuads));
        }
        var childrenParts = new ArrayList<Pair<SkinPartTransform, BakedSkinPart>>();
        for (var childNode : motionNodes) {
            var transform = childNode.resolveTransform();
            var part = clip(childNode.part);
            childrenParts.add(Pair.of(transform, part));
        }
        var mergedQuads = BakedGeometryQuads.merge(rootPart.quads(), pendingQuads);
        var resolvedPart = new BakedSkinPart(rootPart.part(), rootPart.transform(), mergedQuads);
        for (var pair : childrenParts) {
            var transform = pair.getKey();
            var childPart = pair.getValue();
            if (childPart.transform() == transform) {
                resolvedPart.addPart(childPart);
            } else {
                var childPart1 = new BakedSkinPart(childPart.part(), transform, childPart.quads());
                childPart.children().forEach(childPart1::addPart);
                resolvedPart.addPart(childPart1);
            }
        }
        return resolvedPart;
    }


    protected static class Node {

        private final BakedSkinPart part;

        private final Node parent;
        private final ArrayList<Node> children = new ArrayList<>();

        private Node(Node parent, BakedSkinPart part) {
            this.parent = parent;
            this.part = part;
            for (var childPart : part.children()) {
                this.children.add(new Node(this, childPart));
            }
        }

        public SkinPartTransform resolveTransform() {
            if (parent == null) {
                return SkinPartTransform.IDENTITY;
            }
            var childTransform = part.transform();
            var parentTransform = parent.resolveTransform();
            if (parentTransform.isIdentity()) {
                return childTransform;
            }
            if (childTransform.isIdentity()) {
                return parentTransform;
            }
            var mergedTransform = new SkinPartTransform();
            for (var transform : parentTransform.children()) {
                mergedTransform.addChild(transform);
            }
            for (var transform : childTransform.children()) {
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
            // we can't freeze the non-part type, because the part will be read/write transform for real time.
            if (part.type() != SkinPartTypes.ADVANCED) {
                return false;
            }
            // determine node freeze by transform.
            for (var transform : part.transform().children()) {
                if (transform instanceof WingPartTransform) {
                    return false;
                }
                if (transform instanceof AnimatedTransform) {
                    return false;
                }
            }
            return true;
        }
    }
}
