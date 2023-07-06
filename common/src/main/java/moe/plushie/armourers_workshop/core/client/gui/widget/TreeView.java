package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.SoundManagerImpl;
import com.apple.library.impl.StateValueImpl;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIScrollView;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.HashMap;

public class TreeView extends UIScrollView {

    private final TreeNode rootNode;
    private final HashMap<String, Entry> entries = new HashMap<>();

    private boolean dirt = true;
    private TreeNode selectedNode = null;

    public TreeView(TreeNode rootNode, CGRect frame) {
        super(frame);
        this.rootNode = rootNode;
        // link node to tree.
        rootNode.link(this);
    }

    public void selectNode(TreeNode node) {
        if (selectedNode != null) {
            deselectNode(selectedNode);
        }
        selectedNode = node;
        for (Entry entry : entries.values()) {
            if (entry.node == node) {
                entry.setSelected(true);
            }
        }
    }

    public void deselectNode(TreeNode node) {
        selectedNode = null;
        for (Entry entry : entries.values()) {
            if (entry.node == node) {
                entry.setSelected(false);
            }
        }
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        if (dirt) {
            buildEntriesIfNeeded();
            dirt = false;
        }
    }

    public void setNeedsDisplay(TreeNode node) {
        dirt = true;
        setNeedsLayout();
    }

    private void buildEntriesIfNeeded() {
        CGRect bounds = bounds();
        CGSize itemSize = new CGSize(bounds.width, 11);
        CGSize contentSize = new CGSize(0, 0);
        HashMap<String, Entry> removedEntries = new HashMap<>(entries);
        buildEntry("", 0, 0, itemSize, contentSize, rootNode, removedEntries);
        setContentSize(contentSize);
        removedEntries.forEach((key, view) -> {
            view.removeFromSuperview();
            entries.remove(key);
        });
    }

    private void buildEntry(String key, float x, float y, CGSize itemSize, CGSize contentSize, TreeNode node, HashMap<String, Entry> removedEntries) {
        // add a entry if needed, but except root.
        if (!key.isEmpty()) {
            CGRect rect = new CGRect(0, y, itemSize.width, itemSize.height);
            Entry entry = removedEntries.remove(key);
            if (entry == null) {
                entry = new Entry(rect);
                entries.put(key, entry);
                addSubview(entry);
            }
            // update the entry content.
            float iconSize = itemSize.height;
            entry.setup(node, rect, x, iconSize);
            entry.setSelected(node == selectedNode);
            // update the all content size.
            contentSize.width = itemSize.width;
            contentSize.height = y + itemSize.height;
            x += iconSize;
        }
        // when the node is folding, ignore all child entries.
        if (node.isFolding()) {
            return;
        }
        // add all child into view.
        int index = 0;
        for (TreeNode child : node.children()) {
            String key1 = key + ":" + index++;
            buildEntry(key1, x, contentSize.height, itemSize, contentSize, child, removedEntries);
        }
    }

    public static class Entry extends UIControl {

        private static final UIImage FOLDING_IMAGE = UIImage.of(ModTextures.LIST).uv(8, 240).build();
        private static final UIImage UNFOLDING_IMAGE = UIImage.of(ModTextures.LIST).uv(8, 248).build();

        private TreeNode node;
        private NSString title;

        private CGRect iconRect = CGRect.ZERO;

        private final StateValueImpl<UIColor> textColor = new StateValueImpl<>();
        private final StateValueImpl<UIColor> backgroundColor = new StateValueImpl<>();

        public Entry(CGRect frame) {
            super(frame);
            this.textColor.setValueForState(AppearanceImpl.TREE_TEXT_COLOR, State.NORMAL);
            this.textColor.setValueForState(AppearanceImpl.TREE_HIGHLIGHTED_TEXT_COLOR, State.HIGHLIGHTED);
            this.textColor.setValueForState(AppearanceImpl.TREE_HIGHLIGHTED_TEXT_COLOR, State.HIGHLIGHTED | State.SELECTED);
            this.textColor.setValueForState(AppearanceImpl.TREE_TEXT_COLOR, State.SELECTED);
            this.backgroundColor.setValueForState(AppearanceImpl.TREE_HIGHLIGHTED_BACKGROUND_COLOR, State.HIGHLIGHTED);
            this.backgroundColor.setValueForState(AppearanceImpl.TREE_SELECTED_BACKGROUND_COLOR, State.SELECTED);
            this.setHighlighted(false);
        }

        public void setup(TreeNode node, CGRect rect, float offset, float size) {
            this.setFrame(rect);
            if (this.node == node) {
                return;
            }
            this.node = node;
            this.title = node.title();
            this.iconRect = new CGRect(offset + 1, 1, 8, 8);
            this.setEnabled(node.isEnabled());
        }

        @Override
        public void render(CGPoint point, CGGraphicsContext context) {
            super.render(point, context);
            // if has some child, we need show the icon.
            if (node != null && !node.children().isEmpty()) {
                context.setBlendColor(textColor.currentValue());
                if (node.isFolding()) {
                    context.drawImage(FOLDING_IMAGE, iconRect);
                } else {
                    context.drawImage(UNFOLDING_IMAGE, iconRect);
                }
                context.setBlendColor(UIColor.WHITE);
            }
            // show the title always.
            if (title != null) {
                context.drawText(title, iconRect.getMaxX(), 1, null, textColor.currentValue(), null);
            }
        }

        @Override
        public void mouseDown(UIEvent event) {
            super.mouseDown(event);
            if (node == null) {
                return;
            }
            // TODO: add double click to fold/unfold?
            // switch folding status if needed.
            if (!node.children().isEmpty() && iconRect.contains(event.locationInView(this))) {
                node.setFolding(!node.isFolding());
                SoundManagerImpl.click();
                return;
            }
            // switch selected status if needed.
            TreeView treeView = ObjectUtils.safeCast(superview(), TreeView.class);
            if (treeView != null) {
                treeView.selectNode(node);
                SoundManagerImpl.click();
            }
        }

        protected void updateStateIfNeeded() {
            super.updateStateIfNeeded();
            int state = State.NORMAL;
            if (isHighlighted()) {
                state |= State.HIGHLIGHTED;
            }
            if (isSelected()) {
                state |= State.SELECTED;
            }
            if (!isEnabled()) {
                state |= State.DISABLED;
            }
            textColor.setCurrentState(state);
            backgroundColor.setCurrentState(state);
            setBackgroundColor(backgroundColor.currentValue());
        }
    }
}
