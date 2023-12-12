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

import java.util.List;

public class SkinDocumentImporter {

    private final SkinDocument document;

    public SkinDocumentImporter(SkinDocument document) {
        this.document = document;
    }

    public void execute(String identifier, Skin skin) {
        List<SkinPart> parts = skin.getParts();
        SkinDocumentNode root = document.getRoot();
        for (int i = 0; i < parts.size(); ++i) {
            SkinPart part = parts.get(i);
            if (isEmpty(part)) {
                continue; // ignore empty part
            }
            SkinDocumentNode node = findNodeByType(root, part.getType());
            if (node != null) {
                copyTo(part, node, identifier, String.valueOf(i));
            }
        }
    }

    private void copyTo(SkinPart part, SkinDocumentNode node, String identifier, String indexPath) {
        String ref = SkinCipher.getInstance().encrypt(identifier, indexPath);
        SkinDescriptor descriptor = new SkinDescriptor(DataDomain.SLICE_LOAD.normalize(ref), SkinTypes.ADVANCED);
        if (node.getType() != SkinPartTypes.ADVANCED) {
            node.setSkin(descriptor);
            return;
        }

        String name = part.getName();
        if (name == null || name.isEmpty()) {
            name = "untitled node";
        }

        // this maybe has multiple nodes, so we need to append it.
        SkinDocumentNode newValue = new SkinDocumentNode(name);
        newValue.setSkin(descriptor);
        node.add(newValue);
    }

    private boolean isEmpty(SkinPart part) {
        int cubeTotal = part.getCubeData().getCubeTotal();
        if (cubeTotal != 0) {
            return false;
        }
        for (SkinPart child : part.getParts()) {
            if (!isEmpty(child)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private SkinDocumentNode findNodeById(SkinDocumentNode root, String id) {
        for (SkinDocumentNode node : root.children()) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    @Nullable
    private SkinDocumentNode findNodeByType(SkinDocumentNode root, ISkinPartType partType) {
        // we shouldn't match advanced parts
        if (partType != SkinPartTypes.ADVANCED) {
            for (SkinDocumentNode node : root.children()) {
                if (partType.equals(node.getType())) {
                    return node;
                }
            }
        }
        return findNodeById(root, "static");
    }
}
