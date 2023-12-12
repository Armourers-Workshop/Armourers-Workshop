package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import moe.plushie.armourers_workshop.core.client.gui.widget.TreeNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;

public class DocumentMinimapNode extends TreeNode {

    private SkinDocumentNode contents;

    public DocumentMinimapNode(String name) {
        super(name);
    }

    public DocumentMinimapNode(SkinDocumentNode node) {
        super(node.getName());
        this.setContents(node);
        node.children().forEach(it -> add(new DocumentMinimapNode(it)));
    }

    @Override
    public void setContents(Object contents) {
        super.setContents(contents);
        this.contents = (SkinDocumentNode) contents;
    }

    @Override
    public SkinDocumentNode getContents() {
        return contents;
    }
}
