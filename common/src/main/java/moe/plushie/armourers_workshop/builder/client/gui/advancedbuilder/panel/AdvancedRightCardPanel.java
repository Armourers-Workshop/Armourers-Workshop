package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIBarItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIMenuController;
import com.apple.library.uikit.UIMenuItem;
import com.apple.library.uikit.UIView;
import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentMinimapView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentTypeListView;
import moe.plushie.armourers_workshop.builder.client.gui.widget.DrawerToolbar;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeNode;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeView;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeViewDelegate;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collection;

// /----------------------\
// | [v] general - outfit |
// |----------------------|
// | v []root             |
// | | > []child          |
// | | v []child          |
// | |     []child        |
// \----------------------/
// /----------------------\
// | [v] [ name ]         |
// |----------------------|
// | properties           |
// |  [x] mirror          |
// |  [x] enabled         |
// |                      |
// | transform            |
// |   location x [ - ]   |
// |            y [ - ]   |
// |            z [ - ]   |
// |   rotation x [ - ]   |
// |            y [ - ]   |
// |            z [ - ]   |
// |      scale x [ 1 ]   |
// |            y [ 1 ]   |
// |            z [ 1 ]   |
// \----------------------/
public class AdvancedRightCardPanel extends UIView implements TreeViewDelegate {


    private final DocumentMinimapView minimapView = new DocumentMinimapView(new CGRect(0, 0, 200, 200));
    private final DocumentTypeListView typeListView = new DocumentTypeListView(new CGRect(0, 0, 200, 16));

    private final DrawerToolbar rightToolbar = new DrawerToolbar(new CGRect(0, 0, 200, 480));

    private final ArrayList<AdvancedPanel> allPanels = new ArrayList<>();

    private SkinDocumentNode selectedNode;
    private final DocumentEditor editor;

    public AdvancedRightCardPanel(DocumentEditor editor, CGRect frame) {
        super(frame);
        this.editor = editor;
        this.init();
        this.setup(frame);
    }

    private void init() {
    }

    private void setup(CGRect rect) {
        float h1 = rect.height * 0.35f;
        float h2 = rect.height * 0.65f;

        UIImage image = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 24).clip(4, 4, 4, 4).build();

        UIView bg1 = new UIView(bounds().insetBy(4, 4, h2, 4));
        bg1.setContents(image);
        bg1.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        addSubview(bg1);

        typeListView.setFrame(new CGRect(0, 0, bg1.bounds().getWidth(), 20));
        typeListView.setMaxRows(16);
        typeListView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        typeListView.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, it) -> {
            SkinDocumentType type = self.typeListView.selectedType();
            if (type != null) {
                self.editor.changeTypeAction(type);
            }
        });
        typeListView.reloadData();
        bg1.insertViewAtIndex(typeListView, 0);

        minimapView.setFrame(bg1.bounds().insetBy(20, 0, 0, 0));
        minimapView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        minimapView.setDelegate(this);
        bg1.insertViewAtIndex(minimapView, 0);

        UIView bg2 = new UIView(bounds().insetBy(h1 + 4, 4, 4, 4));
        bg2.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg2.setContents(image);
        addSubview(bg2);

        rightToolbar.setFrame(bg2.bounds().insetBy(0, 0, 0, 0));
        rightToolbar.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg2.addSubview(rightToolbar);

        addRightPanel(new AdvancedGeneralPanel(editor));
        addRightPanel(new AdvancedSettingPanel(editor));
        addRightPanel(new AdvancedBonePanel(editor));
    }

    @Override
    public void treeViewDidSelect(TreeView treeView, TreeNode nodeView) {
        SkinDocumentNode node = (SkinDocumentNode) nodeView.getContents();
        if (Objects.equal(this.selectedNode, node)) {
            return;
        }
        this.selectedNode = node;
        this.editor.getConnector().update(node);
    }

    @Override
    public Collection<UIMenuItem> treeViewShouldShowMenuForNode(TreeView treeView, TreeNode nodeView) {
        SkinDocumentNode node = (SkinDocumentNode) nodeView.getContents();
        return editor.getNodeMenuItems(node, treeView);
    }

    public void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
        if (Objects.equal(this.selectedNode, node)) {
            this.editor.getConnector().update(node);
        }
    }

    protected void addRightPanel(AdvancedPanel panel) {
        UIBarItem barItem = panel.barItem();
        panel.setFrame(rightToolbar.bounds());
        panel.sizeToFit();
        rightToolbar.addPage(panel, barItem);
        allPanels.add(panel);
    }

    public void setMenuController(UIMenuController menuController) {
        this.minimapView.setMenuController(menuController);
    }

    public UIMenuController getMenuController() {
        return this.minimapView.getMenuController();
    }

    public DocumentMinimapView getMinimapView() {
        return minimapView;
    }

    public DocumentTypeListView getTypeListView() {
        return typeListView;
    }
}
