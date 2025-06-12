package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIMenuItem;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeView;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentSynchronizer;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentType;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

public class DocumentEditor {

    private final AdvancedBuilderBlockEntity blockEntity;

    private final SkinDocument document;
    private final SkinDocumentSynchronizer synchronizer;

    private final DocumentConnector connector;

    public DocumentEditor(AdvancedBuilderBlockEntity blockEntity) {
        this.document = blockEntity.document();
        this.blockEntity = blockEntity;
        this.connector = new DocumentConnector(document, this);
        this.synchronizer = new SkinDocumentSynchronizer(blockEntity, true);
    }

    public void connect() {
        document.addListener(synchronizer);
    }

    public void disconnect() {
        document.removeListener(synchronizer);
    }

    public void changeTypeAction(SkinDocumentType type) {
        document.setType(type);
    }

    public void nodeAddAction(SkinDocumentNode node) {
        SkinDocumentNode newValue = new SkinDocumentNode("New Node");
        newValue.setName(_resolveName(newValue.name(), node.children()));
        node.insertAtIndex(newValue, 0);
        nodeSelectAction(newValue);
    }

    public void nodeAddLocatorAction(SkinDocumentNode node) {
        SkinDocumentNode newValue = new SkinDocumentNode("New Locator");
        newValue.setType(SkinPartTypes.ADVANCED_LOCATOR);
        newValue.setName(_resolveName(newValue.name(), node.children()));
        node.insertAtIndex(newValue, 0);
        nodeSelectAction(newValue);
    }

    public void nodeMoveUpAction(SkinDocumentNode node) {
        SkinDocumentNode parent = node.parent();
        parent.moveTo(node, parent.children().indexOf(node) - 1);
    }

    public void nodeMoveDownAction(SkinDocumentNode node) {
        SkinDocumentNode parent = node.parent();
        parent.moveTo(node, parent.children().indexOf(node) + 1);
    }

    public void nodeMoveToTopAction(SkinDocumentNode node) {
        SkinDocumentNode parent = node.parent();
        parent.moveTo(node, 0);
    }

    public void nodeMoveToBottomAction(SkinDocumentNode node) {
        SkinDocumentNode parent = node.parent();
        parent.moveTo(node, 10000);
    }

    public void nodeRemoveAction(SkinDocumentNode node, TreeView treeView) {
        ConfirmDialog alert = new ConfirmDialog();
        alert.setTitle(NSString.localizedString("advanced-skin-builder.dialog.delete.title"));
        alert.setMessage(NSString.localizedString("advanced-skin-builder.dialog.delete.message", node.name()));
        alert.showInView(treeView, () -> {
            if (!alert.isCancelled()) {
                node.removeFromParent();
            }
        });
    }

    public void nodeCopyAction(SkinDocumentNode node) {
        DocumentPasteboard.getInstance().setContents(_duplicateNode(node));
    }

    public void nodePasteAction(SkinDocumentNode node) {
        SkinDocumentNode target = DocumentPasteboard.getInstance().contents();
        if (target == null) {
            return;
        }
        SkinDocumentNode newValue = _duplicateNode(target);
        newValue.setName(_resolveName(newValue.name() + " " + "copy", node.children()));
        node.insertAtIndex(newValue, 0);
        nodeSelectAction(newValue);
    }

    public void nodeDuplicateAction(SkinDocumentNode node) {
        SkinDocumentNode parent = node.parent();
        SkinDocumentNode newValue = _duplicateNode(node);
        newValue.setName(_resolveName(node.name() + " " + "copy", parent.children()));
        parent.insertAtIndex(newValue, 0);
        nodeSelectAction(newValue);
    }


    public void nodeSelectAction(SkinDocumentNode node) {
        document.handler().documentDidSelectNode(node);
    }

    public Collection<UIMenuItem> getNodeMenuItems(SkinDocumentNode node, TreeView treeView) {
        var builder = new DocumentMenuBuilder(node, treeView);
        var children = node.parent().children();
        boolean isEnabled = !node.isLocked();
        int order = children.indexOf(node);

        builder.add(0, "add").execute(this::nodeAddAction).enable(!node.isLocator());
        builder.add(0, "copy").execute(this::nodeCopyAction).enable(!node.isLocator());
        builder.add(0, "paste").execute(this::nodePasteAction).enable(DocumentPasteboard.getInstance().contents() != null);
        builder.add(0, "delete").execute(this::nodeRemoveAction).enable(isEnabled);

        if (isEnabled) {
            builder.add(1, "duplicate").execute(this::nodeDuplicateAction).enable(!node.isLocator());
        }

        if (node.type().maximumMarkersNeeded() != 0) {
            builder.add(2, "addLocator").execute(this::nodeAddLocatorAction);
        }

        builder.add(4, "moveUp").execute(this::nodeMoveUpAction).enable(isEnabled && order != 0);
        builder.add(4, "moveToTop").execute(this::nodeMoveToTopAction).enable(isEnabled && order != 0);
        builder.add(4, "moveDown").execute(this::nodeMoveDownAction).enable(isEnabled && order != children.size() - 1);
        builder.add(4, "moveToBottom").execute(this::nodeMoveToBottomAction).enable(isEnabled && order != children.size() - 1);

        //builder.add(8, "moveTo").enable(isEnabled);

        return builder.build();
    }

    public void beginEditing() {
        synchronizer.beginCapture();
    }

    public void endEditing() {
        synchronizer.endCapture();
    }

    public SkinDocument document() {
        return document;
    }

    public Collection<SkinDescriptor> history() {
        LinkedHashSet<SkinDescriptor> allSkins = new LinkedHashSet<>();
        _eachNode(document.root(), it -> {
            if (!it.skin().isEmpty()) {
                allSkins.add(it.skin());
            }
        });
        return allSkins;
    }

    public DocumentConnector connector() {
        return connector;
    }

    public AdvancedBuilderBlockEntity blockEntity() {
        return blockEntity;
    }

    private SkinDocumentNode _duplicateNode(SkinDocumentNode node) {
        SkinDocumentNode newValue = new SkinDocumentNode(node.name());
        newValue.setSkin(node.skin());
        newValue.setLocation(node.location());
        newValue.setRotation(node.rotation());
        newValue.setScale(node.scale());
        newValue.setPivot(node.pivot());
        node.children().forEach(it -> newValue.add(_duplicateNode(it)));
        return newValue;
    }

    private String _resolveName(String name, List<SkinDocumentNode> nodes) {
        HashSet<String> names = new HashSet<>();
        nodes.forEach(it -> names.add(it.name()));
        String resolvedName = name;
        for (int i = 1; names.contains(resolvedName); ++i) {
            resolvedName = name + " " + i;
        }
        return resolvedName;
    }

    private void _eachNode(SkinDocumentNode node, Consumer<SkinDocumentNode> consumer) {
        node.children().forEach(consumer);
        for (SkinDocumentNode child : node.children()) {
            _eachNode(child, consumer);
        }
    }
}
