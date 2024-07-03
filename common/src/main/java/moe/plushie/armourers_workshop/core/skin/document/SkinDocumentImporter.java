package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.SkinCipher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SkinDocumentImporter {

    private final SkinDocument document;

    public SkinDocumentImporter(SkinDocument document) {
        this.document = document;
    }

    public void execute(String identifier, Skin skin) {
        var parts = skin.getParts();
        var root = document.getRoot();
        for (var i = 0; i < parts.size(); ++i) {
            var part = parts.get(i);
            if (isEmpty(part)) {
                continue; // ignore empty part
            }
            var node = findNodeByType(root, part.getType());
            if (node != null) {
                copyTo(part, node, identifier, String.valueOf(i));
            }
        }
        copyAnimations(identifier, skin);
    }

    private void copyTo(SkinPart part, SkinDocumentNode node, String identifier, String indexPath) {
        var ref = SkinCipher.getInstance().encrypt(identifier, indexPath);
        var descriptor = new SkinDescriptor(DataDomain.SLICE_LOAD.normalize(ref), SkinTypes.ADVANCED);
        if (node.isBasic()) {
            node.setSkin(descriptor);
            return;
        }

        var name = part.getName();
        if (name == null || name.isEmpty()) {
            name = "untitled node";
        }

        if (name.equals("Float")) {
            var floatNode = findNodeByType(document.getRoot(), SkinPartTypes.ADVANCED_FLOAT);
            if (floatNode != null) {
                node = floatNode;
                name = "untitled float node";
            }
        }

        // this maybe has multiple nodes, so we need to append it.
        var newValue = new SkinDocumentNode(name);
        newValue.setSkin(descriptor);
        node.add(newValue);
    }

    private void copyAnimations(String identifier, Skin skin) {
        var animations = new ArrayList<SkinDocumentAnimation>();
        var descriptor = new SkinDescriptor(identifier, SkinTypes.ADVANCED);
        for (var animation : skin.getAnimations()) {
            animations.add(new SkinDocumentAnimation(animation.getName(), descriptor));
        }
        document.setAnimations(animations);
    }

    private boolean isEmpty(SkinPart part) {
        var cubeTotal = part.getCubeData().getCubeTotal();
        if (cubeTotal != 0) {
            return false;
        }
        for (var child : part.getParts()) {
            if (!isEmpty(child)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private SkinDocumentNode findNodeByType(SkinDocumentNode root, ISkinPartType partType) {
        // we shouldn't match advanced parts
        if (partType != SkinPartTypes.ADVANCED) {
            for (var node : root.children()) {
                if (partType.equals(node.getType())) {
                    return node;
                }
            }
        }
        if (partType != SkinPartTypes.ADVANCED_STATIC) {
            return findNodeByType(root, SkinPartTypes.ADVANCED_STATIC);
        }
        return null;
    }
}
