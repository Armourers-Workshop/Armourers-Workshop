package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.uikit.UIMenuItem;
import com.apple.library.uikit.UIScrollViewDelegate;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface TreeViewDelegate extends UIScrollViewDelegate {

    @Nullable
    default Collection<UIMenuItem> treeViewShouldShowMenuForNode(TreeView treeView, TreeNode node) {
        return null;
    }
}
