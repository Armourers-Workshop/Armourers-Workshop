package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIBarItem;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIScreen;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.AdvancedBoneSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.AdvancedCameraSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.AdvancedGeneralSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.AdvancedSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.widget.DrawerToolbar;
import moe.plushie.armourers_workshop.builder.client.gui.widget.Shortcut;
import moe.plushie.armourers_workshop.builder.menu.AdvancedSkinBuilderMenu;
import moe.plushie.armourers_workshop.compatibility.client.AbstractMenuWindowProvider;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeNode;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeView;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class AdvancedSkinBuilderWindow extends MenuWindow<AdvancedSkinBuilderMenu> {

    private final AdvancedCameraSkinPanel cameraView;
    private final AdvancedSkinBuilderBlockEntity blockEntity;

    private final UIView rightCard = new UIView(CGRect.ZERO);
    private final TreeView rightTree = new TreeView(new CGRect(0, 0, 200, 200));
    private final DrawerToolbar rightToolbar = new DrawerToolbar(new CGRect(0, 0, 200, 480));

    private final HashMap<Shortcut, Runnable> shortcuts = new HashMap<>();

    private int leftCardOffset = 100;
    private int rightCardOffset = 100;

    public static AbstractMenuWindowProvider<AdvancedSkinBuilderMenu, AdvancedSkinBuilderWindow> PROVIDER;

    public static AdvancedSkinBuilderWindow create(AdvancedSkinBuilderMenu container, Inventory inventory, NSString title) {
        if (PROVIDER != null) {
            return PROVIDER.create(container, inventory, title);
        }
        return new AdvancedSkinBuilderWindow(container, inventory, title);
    }

    public AdvancedSkinBuilderWindow(AdvancedSkinBuilderMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.blockEntity = container.getBlockEntity(AdvancedSkinBuilderBlockEntity.class);
        this.cameraView = new AdvancedCameraSkinPanel(blockEntity);
        this.inventoryView.setHidden(true);
        this.setup();
        this.reloadData();
        this.cameraView.reset();
    }

    private void setup() {
        CGRect bounds = UIScreen.bounds();
        this.setFrame(bounds);
        this.setupCameraView();
        this.setupRightCard(bounds);
        this.setupShortcuts();
    }

    private void setupCameraView() {
        cameraView.setFrame(bounds());
        cameraView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(cameraView);
    }

    private void setupRightCard(CGRect rect) {
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
        float w = 200f;
        float h = rect.height * 2f;

        float h1 = h * 0.35f;
        float h2 = h * 0.65f;

        rightCard.setFrame(new CGRect(0, 0, w, h));
        rightCard.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);
        rightCard.setBackgroundColor(new UIColor(0x1d1d1d));
        addSubview(rightCard);

        UIImage image = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 24).clip(4, 4, 4, 4).build();

        UIView bg1 = new UIView(rightCard.bounds().insetBy(4, 4, h2, 4));
        bg1.setContents(image);
        bg1.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        rightCard.addSubview(bg1);

        UILabel typeView = new UILabel(new CGRect(24, 0, bg1.bounds().getWidth() - 24, 24));
        typeView.setText(new NSString("General - Outfit"));
        typeView.setTextColor(UIColor.WHITE);
        typeView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        bg1.addSubview(typeView);

        rightTree.setFrame(bg1.bounds().insetBy(24, 0, 0, 0));
        rightTree.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg1.addSubview(rightTree);

        UIView bg2 = new UIView(rightCard.bounds().insetBy(h1 + 4, 4, 4, 4));
        bg2.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg2.setContents(image);
        rightCard.addSubview(bg2);

        rightToolbar.setFrame(bg2.bounds().insetBy(0, 0, 0, 0));
        rightToolbar.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        bg2.addSubview(rightToolbar);

        addRightPanel(new AdvancedGeneralSkinPanel(blockEntity));
        addRightPanel(new AdvancedBoneSkinPanel(blockEntity));

        rightCard.setTransform(CGAffineTransform.createScale(0.5f, 0.5f));
        rightCard.setFrame(new CGRect(rect.width - rightCardOffset, 0, rightCardOffset, rect.height));
    }

    private void setupShortcuts() {
        shortcuts.put(Shortcut.of("key.keyboard.control", "key.keyboard.1"), this::toggleLeftCard);
        shortcuts.put(Shortcut.of("key.keyboard.control", "key.keyboard.2"), this::toggleRightCard);
    }

    private void reloadData() {
        TreeNode rootNode = rightTree.rootNode();
        addToNode(rootNode, "Root", 10);
        addToNode(rootNode.nodeAtIndex(0), "First", 10);
        addToNode(rootNode.nodeAtIndex(1), "Second", 10);
        addToNode(rootNode.nodeAtIndex(3), "Thriii", 10);
        addToNode(rootNode.nodeAtIndex(0).nodeAtIndex(0), "Children", 10);
    }

    private void toggleLeftCard() {
        ModLog.debug("{}", "show left card");
    }

    private void toggleRightCard() {
        ModLog.debug("{}", "show right card");

        CGPoint oldValue = rightCard.center();
        CGPoint newValue = new CGPoint(oldValue.x + rightCardOffset, oldValue.y);
        UIView.animationWithDuration(0.35, () -> rightCard.setCenter(newValue));
        rightCardOffset = -rightCardOffset;
    }


    protected void addRightPanel(AdvancedSkinPanel panel) {
        UIBarItem barItem = panel.barItem();
        panel.setFrame(rightToolbar.bounds());
        panel.sizeToFit();
        rightToolbar.addPage(panel, barItem);
    }

    protected void addToNode(TreeNode node, String prefix, int count) {
        for (int i = 0; i < count; ++i) {
            node.add(new TreeNode(new NSString(prefix + " - " + i)));
        }
    }

    @Override
    public void init() {
        super.init();
        cameraView.connect();
    }

    @Override
    public void deinit() {
        super.deinit();
        cameraView.disconnect();
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
    }

    @Override
    public void keyDown(UIEvent event) {
        super.keyDown(event);
        int key1 = event.key();
        int key2 = event.keyModifier();
        shortcuts.forEach((key, handler) -> {
            if (key.matches(key1, key2)) {
                handler.run();
            }
        });
    }

    @Override
    public boolean shouldRenderBackground() {
        return false;
    }
}
