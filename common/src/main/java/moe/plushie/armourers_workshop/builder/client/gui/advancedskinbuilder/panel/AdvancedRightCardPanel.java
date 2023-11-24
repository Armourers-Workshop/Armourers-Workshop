package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIBarItem;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIMenuController;
import com.apple.library.uikit.UIMenuItem;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.data.AdvancedSkinTypes;
import moe.plushie.armourers_workshop.builder.client.gui.widget.DrawerToolbar;
import moe.plushie.armourers_workshop.builder.client.gui.widget.NewComboBox;
import moe.plushie.armourers_workshop.builder.client.gui.widget.NewComboItem;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeNode;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeView;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeViewDelegate;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.resources.ResourceLocation;

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

    private final TreeView rightTree = new TreeView(new CGRect(0, 0, 200, 200));
    private final NewComboBox typeView = new NewComboBox(new CGRect(0, 0, 200, 16));
    private final DrawerToolbar rightToolbar = new DrawerToolbar(new CGRect(0, 0, 200, 480));

    private final ArrayList<AdvancedTypeSection> allSections = new ArrayList<>();
    private final ArrayList<AdvancedTypeItem> allItems = new ArrayList<>();

    private UIMenuController menuController;
    private AdvancedTypeItem selectedItem;

    public AdvancedRightCardPanel(AdvancedSkinBuilderBlockEntity blockEntity, CGRect frame) {
        super(frame);
        this.init();
        this.setup(blockEntity, frame);
    }

    private void init() {
        AdvancedSkinTypes.forEach((category, items) -> {
            AdvancedTypeSection section = new AdvancedTypeSection(category);
            items.forEach(it -> allItems.add(section.add(it.getType())));
            allSections.add(section);
        });
    }

    private void setup(AdvancedSkinBuilderBlockEntity blockEntity, CGRect rect) {
        float h1 = rect.height * 0.35f;
        float h2 = rect.height * 0.65f;

        UIImage image = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 24).clip(4, 4, 4, 4).build();

        UIView bg1 = new UIView(bounds().insetBy(4, 4, h2, 4));
        bg1.setContents(image);
        bg1.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        addSubview(bg1);

        typeView.setFrame(new CGRect(0, 0, bg1.bounds().getWidth(), 20));
        typeView.setMaxRows(16);
        typeView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        typeView.addTarget(this, UIControl.Event.VALUE_CHANGED, AdvancedRightCardPanel::setSkinTypeWithCombobox);
        typeView.reloadData(allSections);
        bg1.insertViewAtIndex(typeView, 0);

        rightTree.setContentInsets(new UIEdgeInsets(4, 0, 4, 0));
        rightTree.setFrame(bg1.bounds().insetBy(20, 0, 0, 0));
        rightTree.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        rightTree.setDelegate(this);
        bg1.insertViewAtIndex(rightTree, 0);

        UIView bg2 = new UIView(bounds().insetBy(h1 + 4, 4, 4, 4));
        bg2.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg2.setContents(image);
        addSubview(bg2);

        rightToolbar.setFrame(bg2.bounds().insetBy(0, 0, 0, 0));
        rightToolbar.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg2.addSubview(rightToolbar);

        addRightPanel(new AdvancedGeneralPanel(blockEntity));
        addRightPanel(new AdvancedBonePanel(blockEntity));
    }

    protected void addRightPanel(AdvancedPanel panel) {
        UIBarItem barItem = panel.barItem();
        panel.setFrame(rightToolbar.bounds());
        panel.sizeToFit();
        rightToolbar.addPage(panel, barItem);
    }

    public void setMenuController(UIMenuController menuController) {
        this.menuController = menuController;
        this.rightTree.setMenuController(menuController);
    }

    public void setSkinTypeWithIndex(int index) {
        setSkinTypeWithItem(allItems.get(index));
    }

    private void setSkinTypeWithItem(AdvancedTypeItem item) {
        selectedItem = item;

        TreeNode rootNode = rightTree.rootNode();
        rootNode.clear();

        for (ISkinPartType partType : item.getSkinType().getParts()) {
            NSMutableString name = new NSMutableString("Root - ");
            name.append(TranslateUtils.Name.of(partType));
            rootNode.add(new TreeNode(name));
        }

        rootNode.add(new TreeNode(new NSString("Root - Float")));
        rootNode.add(new TreeNode(new NSString("Root - Static")));
    }

    private void setSkinTypeWithCombobox(UIControl sender) {
        NSIndexPath indexPath = typeView.selectedIndex();
        AdvancedTypeSection section = allSections.get(indexPath.section);
        AdvancedTypeItem item = ObjectUtils.safeCast(section.get(indexPath.row), AdvancedTypeItem.class);
        if (item != null) {
            setSkinTypeWithItem(item);
        }
    }

    @Override
    public Collection<UIMenuItem> treeViewShouldShowMenuForNode(TreeView treeView, TreeNode node) {
        AdvancedMenuAction action = new AdvancedMenuAction(rightTree, node);
        ArrayList<UIMenuItem> items = new ArrayList<>();
        items.add(UIMenuItem.of("Add Node").group(0).execute(action::add).build());
        items.add(UIMenuItem.of("Delete Node").group(0).execute(action::remove).enable(node.parent() != treeView.rootNode()).build());

        items.add(UIMenuItem.of("Bring Forward").group(1).build());
        items.add(UIMenuItem.of("Bring to Front").group(1).build());
        items.add(UIMenuItem.of("Send Backward").group(1).enable(false).build());
        items.add(UIMenuItem.of("Send to Back").group(1).enable(false).build());

        items.add(UIMenuItem.of("Move to Group").group(2).build());

        return items;
    }


    public static class AdvancedTypeSection extends NewComboItem {

        private final String category;

        public AdvancedTypeSection(String category) {
            super(new NSString(TranslateUtils.title("skinCategory.armourers_workshop." + category)));
            this.category = category;
        }

        public AdvancedTypeItem add(ISkinType skinType) {
            ResourceLocation rl = ArmourersWorkshop.getItemIcon(skinType);
            UIImage icon = UIImage.of(rl).resize(12, 12, 16, 16).limit(16, 16).build();
            NSString name = new NSString(TranslateUtils.Name.of(skinType));
            AdvancedTypeItem item = new AdvancedTypeItem(icon, name, skinType);
            add(item);
            return item;
        }
    }

    public static class AdvancedTypeItem extends UIComboItem {

        private final ISkinType skinType;

        public AdvancedTypeItem(UIImage icon, NSString name, ISkinType skinType) {
            super(icon, name);
            this.skinType = skinType;
        }

        public ISkinType getSkinType() {
            return skinType;
        }
    }

    public static class AdvancedMenuAction {

        private final TreeNode node;
        private final TreeView treeView;

        public AdvancedMenuAction(TreeView treeView, TreeNode node) {
            this.treeView = treeView;
            this.node = node;
        }

        public void add() {
            AdvancedPartPickerDialog alert = new AdvancedPartPickerDialog();
            alert.setTitle(new NSString("Pick a skin part"));
            alert.showInView(treeView, () -> {
                if (!alert.isCancelled()) {
                    NSMutableString name = new NSMutableString("New Node");
                    TreeNode newNode = new TreeNode(name);
                    node.add(newNode);
                    treeView.selectNode(newNode);
                }
            });
        }

        public void remove() {
            ConfirmDialog alert = new ConfirmDialog();
            alert.setTitle(new NSString("Delete Node"));
            alert.setMessage(new NSString("Delete XXXXXXX Node"));
            alert.showInView(treeView, () -> {
                if (!alert.isCancelled()) {
                    node.removeFromParent();
                    treeView.deselectNode(node);
                }
            });
        }
    }
}
