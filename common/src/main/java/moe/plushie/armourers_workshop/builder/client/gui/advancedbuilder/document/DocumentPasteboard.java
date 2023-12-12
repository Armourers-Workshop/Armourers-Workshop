package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;

public class DocumentPasteboard {

    private static final DocumentPasteboard INSTANCE = new DocumentPasteboard();

    private SkinDocumentNode contents;

    public static DocumentPasteboard getInstance() {
        return INSTANCE;
    }

    public void setContents(SkinDocumentNode contents) {
        this.contents = contents;
    }

    public SkinDocumentNode getContents() {
        return contents;
    }
}
