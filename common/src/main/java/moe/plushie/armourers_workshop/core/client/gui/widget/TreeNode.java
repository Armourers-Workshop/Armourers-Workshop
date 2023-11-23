package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.foundation.NSString;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class TreeNode {

    private TreeView view;
    private NSString title;

    private boolean folding = false;
    private boolean enabled = true;

    private TreeNode parent;
    private final ArrayList<TreeNode> children = new ArrayList<>();

    public TreeNode(NSString title) {
        this.title = title;
    }

    public void setNeedsDisplay() {
        if (view != null) {
            view.setNeedsDisplay(this);
        }
    }

    public void add(TreeNode node) {
        children.add(node);
        node.parent = this;
        node.link(view);
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

    public Collection<TreeNode> children() {
        return children;
    }

    public boolean isFolding() {
        return folding;
    }

    public void setFolding(boolean folding) {
        this.folding = folding;
        setNeedsDisplay();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public NSString title() {
        return title;
    }

    public void setTitle(NSString title) {
        this.title = title;
        setNeedsDisplay();
    }

    protected void link(@Nullable TreeView view) {
        this.view = view;
        for (TreeNode node : children) {
            node.link(view);
        }
    }
}
