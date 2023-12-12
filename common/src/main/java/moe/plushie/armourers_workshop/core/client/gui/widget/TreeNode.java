package moe.plushie.armourers_workshop.core.client.gui.widget;

import moe.plushie.armourers_workshop.utils.MathUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private TreeView view;
    private String title;
    private Object contents;

    private boolean folding = false;
    private boolean enabled = true;
    private boolean locked = false;

    private TreeNode parent;
    private final ArrayList<TreeNode> children = new ArrayList<>();

    public TreeNode(String title) {
        this.title = title;
    }

    public void setNeedsDisplay() {
        if (view != null) {
            view.setNeedsDisplay(this);
        }
    }

    public void add(TreeNode node) {
        insertAtIndex(node, children.size());
    }

    public void insertAtIndex(TreeNode node, int index) {
        children.add(index, node);
        node.parent = this;
        node.link(view);
        setNeedsDisplay();
    }

    public void moveTo(TreeNode node, int toIndex) {
        int index = children.indexOf(node);
        if (index < 0 || index == toIndex) {
            return;
        }
        children.remove(index);
        children.add(MathUtils.clamp(toIndex, 0, children.size()), node);
        setNeedsDisplay();
    }

    public void removeFromParent() {
        if (parent == null) {
            return;
        }
        parent.children.remove(this);
        parent.setNeedsDisplay();
        parent = null;
        link(null);
    }

    public void clear() {
        children.forEach(node -> node.link(null));
        children.clear();
        setNeedsDisplay();
    }

    public TreeNode nodeAtIndex(int index) {
        return children.get(index);
    }

    public TreeNode parent() {
        return parent;
    }

    public List<TreeNode> children() {
        return children;
    }

    public boolean isFolding() {
        return folding;
    }

    public void setFolding(boolean folding) {
        this.folding = folding;
        setNeedsDisplay();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

    public Object getContents() {
        return contents;
    }

    public TreeView getTreeView() {
        return view;
    }

    protected void link(@Nullable TreeView view) {
        this.view = view;
        for (TreeNode node : children) {
            node.link(view);
        }
    }
}
