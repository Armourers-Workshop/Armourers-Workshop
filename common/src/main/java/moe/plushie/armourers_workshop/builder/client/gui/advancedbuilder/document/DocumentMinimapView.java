package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIMenuController;
import com.apple.library.uikit.UIView;
import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeIndexPath;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeNode;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeView;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeViewDelegate;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DocumentMinimapView extends UIView {

    private final TreeView treeView;

    public DocumentMinimapView(CGRect frame) {
        super(frame);
        this.treeView = new TreeView(new DocumentMinimapNode("Root"), bounds());
        this.treeView.setContentInsets(new UIEdgeInsets(4, 0, 4, 0));
        this.treeView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.addSubview(treeView);
    }

    public void reloadData(SkinDocumentNode rootNode) {
        _applyToSubviews(rootNode, treeView.rootNode(), (node, nodeView) -> {
            nodeView.setTitle(node.getName());
            nodeView.setLocked(node.isLocked());
            nodeView.setContents(node);
        });
    }

    public DocumentMinimapNode findNode(SkinDocumentNode node) {
        TreeNode nodeView = _findNode(node);
        return (DocumentMinimapNode) nodeView;
    }

    public TreeIndexPath findNodePath(SkinDocumentNode node) {
        ArrayList<Integer> indexes = new ArrayList<>();
        while (node != null) {
            SkinDocumentNode parent = node.parent();
            if (parent != null) {
                indexes.add(0, parent.children().indexOf(node));
            }
            node = parent;
        }
        return new TreeIndexPath(indexes);
    }

    public void setSelectedIndex(TreeIndexPath indexPath) {
        treeView.selectNode(_findNode(indexPath));
    }

    public TreeIndexPath getSelectedIndex() {
        return _findNodePath(treeView.selectedNode());
    }

    public void setDelegate(TreeViewDelegate delegate) {
        treeView.setDelegate(delegate);
    }

    public TreeViewDelegate getDelegate() {
        return treeView.delegate();
    }

    public void setMenuController(UIMenuController menuController) {
        treeView.setMenuController(menuController);
    }

    public UIMenuController getMenuController() {
        return treeView.menuController();
    }

    private TreeNode _findNode(SkinDocumentNode node) {
        SkinDocumentNode parent = node.parent();
        if (parent == null) {
            return treeView.rootNode();
        }
        TreeNode nodeView = _findNode(parent);
        if (nodeView == null) {
            return null;
        }
        for (TreeNode childView : nodeView.children()) {
            if (childView.getContents() == node) {
                return childView;
            }
        }
        return null;
    }

    private TreeNode _findNode(TreeIndexPath indexPath) {
        List<Integer> indexes = indexPath.getIndexes();
        if (indexes.isEmpty()) {
            indexes = Lists.newArrayList(0);
        }
        TreeNode node = treeView.rootNode();
        for (int index : indexes) {
            int size = node.children().size();
            if (size == 0) {
                return node;
            }
            if (index >= size) {
                return node.children().get(size - 1);
            }
            node = node.children().get(index);
        }
        return node;
    }

    private TreeIndexPath _findNodePath(TreeNode nodeView) {
        ArrayList<Integer> indexes = new ArrayList<>();
        while (nodeView != null) {
            TreeNode parentView = nodeView.parent();
            if (parentView != null) {
                indexes.add(0, parentView.children().indexOf(nodeView));
            }
            nodeView = parentView;
        }
        return new TreeIndexPath(indexes);
    }


    private void _applyToSubviews(SkinDocumentNode rootNode, TreeNode rootNodeView, BiConsumer<SkinDocumentNode, TreeNode> applier) {
        List<SkinDocumentNode> children = rootNode.children();
        ArrayList<TreeNode> subviews = new ArrayList<>(rootNodeView.children());
        int inputSize = children.size();
        int viewSize = subviews.size();
        for (int i = 0; i < inputSize; ++i) {
            SkinDocumentNode node = children.get(i);
            TreeNode nodeView;
            if (i < viewSize) {
                // reuse node
                nodeView = subviews.get(i);
                applier.accept(node, nodeView);
            } else {
                // too little, create a new node.
                nodeView = new DocumentMinimapNode("");
                applier.accept(node, nodeView);
                rootNodeView.add(nodeView);
            }
            _applyToSubviews(node, nodeView, applier);
            if (nodeView.children().isEmpty()) {
                nodeView.setFolding(false);
            }
        }
        for (int i = inputSize; i < viewSize; ++i) {
            // too much, remove it.
            TreeNode nodeView = subviews.get(i);
            nodeView.removeFromParent();
        }
    }
}
