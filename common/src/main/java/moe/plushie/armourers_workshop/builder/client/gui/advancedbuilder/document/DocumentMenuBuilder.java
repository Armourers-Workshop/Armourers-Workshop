package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIMenuItem;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeView;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DocumentMenuBuilder {

    private final TreeView treeView;
    private final SkinDocumentNode node;
    private final ArrayList<Row> rows = new ArrayList<>();

    public DocumentMenuBuilder(SkinDocumentNode node, TreeView treeView) {
        this.node = node;
        this.treeView = treeView;
    }

    public Row add(int group, String key) {
        NSString title = NSString.localizedString("advanced-skin-builder.menu." + key);
        UIMenuItem.Builder builder = UIMenuItem.of(title).group(group);
        Row row = new Row(builder, node, treeView);
        rows.add(row);
        return row;
    }

    public List<UIMenuItem> build() {
        ArrayList<UIMenuItem> items = new ArrayList<>();
        rows.forEach(it -> items.add(it.builder.build()));
        return items;
    }

    public static class Row {

        private final UIMenuItem.Builder builder;

        private final TreeView treeView;
        private final SkinDocumentNode node;

        public Row(UIMenuItem.Builder builder, SkinDocumentNode node, TreeView treeView) {
            this.treeView = treeView;
            this.node = node;
            this.builder = builder;
        }

        public Row enable(boolean isEnable) {
            builder.enable(isEnable);
            return this;
        }

        public Row execute(Consumer<SkinDocumentNode> consumer) {
            builder.execute(() -> consumer.accept(node));
            return this;
        }

        public Row execute(BiConsumer<SkinDocumentNode, TreeView> consumer) {
            builder.execute(() -> consumer.accept(node, treeView));
            return this;
        }
    }
}
