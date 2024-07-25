package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.StringImpl;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIMenuController;
import com.apple.library.uikit.UIMenuItem;
import com.apple.library.uikit.UIScreen;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentImporter;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentMinimapNode;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentMinimapView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentTypeListView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel.AdvancedCameraPanel;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel.AdvancedLeftCardPanel;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel.AdvancedRightCardPanel;
import moe.plushie.armourers_workshop.builder.menu.AdvancedBuilderMenu;
import moe.plushie.armourers_workshop.builder.network.AdvancedExportPacket;
import moe.plushie.armourers_workshop.builder.network.AdvancedImportPacket;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.FileProviderDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeIndexPath;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentListener;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.init.ModMenuOptions;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class AdvancedBuilderWindow extends MenuWindow<AdvancedBuilderMenu> implements SkinDocumentListener {

    private static final int CARD_WIDTH = 100;

    private final ArrayList<NSString> helps = new ArrayList<>();
    private final UIButton helpView = new UIButton(new CGRect(0, 0, 7, 8));

    private final AdvancedCameraPanel cameraView;
    private final AdvancedLeftCardPanel leftCard;
    private final AdvancedRightCardPanel rightCard;

    private final DocumentMinimapView minimapView;
    private final DocumentTypeListView typeListView;

    private final SkinDocument doc;
    private final DocumentEditor editor;

    private CGSize cachedTitleSize;

    public AdvancedBuilderWindow(AdvancedBuilderMenu container, Inventory inventory, NSString title) {
        this(container, container.getBlockEntity(), inventory, title);
    }

    public AdvancedBuilderWindow(AdvancedBuilderMenu container, AdvancedBuilderBlockEntity blockEntity, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.editor = new DocumentEditor(blockEntity);
        this.doc = editor.getDocument();
        this.cameraView = new AdvancedCameraPanel(editor);
        this.leftCard = new AdvancedLeftCardPanel(editor, new CGRect(0, 0, 200, UIScreen.bounds().getHeight() * 2));
        this.rightCard = new AdvancedRightCardPanel(editor, new CGRect(0, 0, 200, UIScreen.bounds().getHeight() * 2));
        this.inventoryView.setHidden(true);
        this.minimapView = rightCard.getMinimapView();
        this.typeListView = rightCard.getTypeListView();
        this.setup();
        this.cameraView.reset();
        this.documentDidReload();
    }

    private void setup() {
        CGRect bounds = UIScreen.bounds();
        this.setFrame(bounds);
        this.setupCameraView();
        this.setupLeftCard(bounds);
        this.setupRightCard(bounds);
        this.setupShortcuts();
        this.setupHelp();
    }

    private void setupCameraView() {
        cameraView.setFrame(bounds());
        cameraView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(cameraView);
    }

    private void setupLeftCard(CGRect rect) {
        float offset = -CARD_WIDTH;
        if (Options.SHOWS_LEFT_CARD.get()) {
            offset = 0;
        }
        leftCard.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleHeight);
        leftCard.setBackgroundColor(new UIColor(0x1d1d1d));
        leftCard.setTransform(CGAffineTransform.createScale(0.5f, 0.5f));
        leftCard.setFrame(new CGRect(offset, 0, CARD_WIDTH, rect.height));
        addSubview(leftCard);
    }

    private void setupRightCard(CGRect rect) {
        float offset = 0;
        if (Options.SHOWS_RIGHT_CARD.get()) {
            offset = CARD_WIDTH;
        }
        rightCard.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);
        rightCard.setBackgroundColor(new UIColor(0x1d1d1d));
        rightCard.setTransform(CGAffineTransform.createScale(0.5f, 0.5f));
        rightCard.setFrame(new CGRect(rect.width - offset, 0, CARD_WIDTH, rect.height));
        addSubview(rightCard);
        // provide a scaled menu.
        UIMenuController menuController = new UIMenuController();
        menuController.setTransform(CGAffineTransform.createScale(0.5f, 0.5f));
        rightCard.setMenuController(menuController);
    }

    private void setupHelp() {
        helpView.setBackgroundImage(ModTextures.helpButtonImage(), UIControl.State.ALL);
        helpView.setTooltip(StringImpl.join(helps, "\n"));
        helpView.setCanBecomeFocused(false);
        addSubview(helpView);
    }

    private void setupHelpLayout(CGRect rect) {
        if (cachedTitleSize == null) {
            cachedTitleSize = titleView.sizeThatFits(CGSize.ZERO);
        }
        CGRect target = titleView.frame();
        CGRect frame = helpView.frame().copy();
        frame.setX(target.x + ((target.width - cachedTitleSize.width) / 2) + cachedTitleSize.width + 2);
        frame.setY(target.y);
        helpView.setFrame(frame);
    }

    private void setupShortcuts() {
        addShortcut("show1", this::toggleLeftCard, "key.keyboard.control", "key.keyboard.1");
        addShortcut("show2", this::toggleRightCard, "key.keyboard.control", "key.keyboard.2");
        addShortcut("import", this::importAction, "key.keyboard.control", "key.keyboard.i");
        addShortcut("export", this::exportAction, "key.keyboard.control", "key.keyboard.e");
    }

    private void toggleLeftCard() {
        float value = 0;
        if (Options.SHOWS_LEFT_CARD.get()) {
            Options.SHOWS_LEFT_CARD.set(false);
            value = -CARD_WIDTH;
        } else {
            Options.SHOWS_LEFT_CARD.set(true);
            value = CARD_WIDTH;
        }
        CGPoint oldValue = leftCard.center();
        CGPoint newValue = new CGPoint(oldValue.x + value, oldValue.y);
        UIView.animationWithDuration(0.35, () -> leftCard.setCenter(newValue));
    }

    private void toggleRightCard() {
        float value = 0;
        if (Options.SHOWS_RIGHT_CARD.get()) {
            Options.SHOWS_RIGHT_CARD.set(false);
            value = CARD_WIDTH;
        } else {
            Options.SHOWS_RIGHT_CARD.set(true);
            value = -CARD_WIDTH;
        }
        CGPoint oldValue = rightCard.center();
        CGPoint newValue = new CGPoint(oldValue.x + value, oldValue.y);
        UIView.animationWithDuration(0.35, () -> rightCard.setCenter(newValue));
    }

    private void importAction() {
        SkinDocumentType documentType = rightCard.getTypeListView().selectedType();
        if (documentType == null) {
            return;
        }
        importNewSkin(documentType.getSkinType(), documentType.usesItemTransforms(), skin -> {
            AdvancedBuilderBlockEntity blockEntity = editor.getBlockEntity();
            AdvancedImportPacket packet = new AdvancedImportPacket(blockEntity, skin, "");
            NetworkManager.sendToServer(packet);
        });
    }

    private void exportAction() {
        SkinDocumentType documentType = rightCard.getTypeListView().selectedType();
        if (documentType == null) {
            return;
        }
        ConfirmDialog alert = new ConfirmDialog();
        alert.setTitle(NSString.localizedString("advanced-skin-builder.dialog.exporter.title"));
        alert.setMessage(NSString.localizedString("advanced-skin-builder.dialog.exporter.message"));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                var origin = Minecraft.getInstance().getUser().getGameProfile();
                var nbt = DataSerializers.writeGameProfile(new CompoundTag(), origin);
                AdvancedExportPacket packet = new AdvancedExportPacket(editor.getBlockEntity(), nbt);
                NetworkManager.sendToServer(packet);
            }
        });
    }

    private void addShortcut(String name, Runnable action, String... keys) {
        UIMenuItem.Builder builder = UIMenuItem.of(name);
        builder.keyDown(keys);
        builder.execute(action);
        UIMenuItem item = builder.build();
        helps.add(NSString.localizedString("advanced-skin-builder.shortcut." + name, item.key()));
        addMenuItem(item);
    }

    public void importNewSkin(ISkinType skinType, boolean keepItemTransforms, Consumer<Skin> consumer) {
        NSString title = NSString.localizedString("advanced-skin-builder.dialog.importer.title");
        SkinLibraryManager libraryManager = SkinLibraryManager.getClient();
        if (!libraryManager.shouldUploadFile(inventory.player)) {
            NSString message = NSString.localizedString("skin-library.error.illegalOperation");
            UserNotificationCenter.showToast(message, UIColor.RED, title, null);
            return;
        }
        File rootPath = new File(EnvironmentManager.getRootDirectory(), "model-imports");
        if (!rootPath.exists() && !rootPath.mkdirs()) {
            NSString message = new NSString("Can't create directory");
            UserNotificationCenter.showToast(message, UIColor.RED, title, null);
            return;
        }
        FileProviderDialog alert = new FileProviderDialog(rootPath, "bbmodel");
        alert.setTitle(title);
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                DocumentImporter importer = new DocumentImporter(alert.getSelectedFile(), skinType);
                importer.setKeepItemTransforms(keepItemTransforms);
                importer.execute(consumer);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        doc.addListener(this);
        cameraView.connect();
        editor.connect();
    }

    @Override
    public void deinit() {
        editor.disconnect();
        cameraView.disconnect();
        doc.removeListener(this);
        super.deinit();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        setupHelpLayout(bounds());
    }

    @Override
    public void documentDidSelectNode(SkinDocumentNode node) {
        TreeIndexPath indexPath = minimapView.findNodePath(node);
        minimapView.setSelectedIndex(indexPath);
    }

    @Override
    public void documentDidReload() {
        documentDidChangeType(editor.getDocument().getType());
        documentDidChangeSettings(new CompoundTag());
        documentDidChangeProperties(new CompoundTag());
    }

    @Override
    public void documentDidChangeType(SkinDocumentType type) {
        typeListView.setSelectedType(type);
        TreeIndexPath indexPath = minimapView.getSelectedIndex();
        minimapView.reloadData(editor.getDocument().getRoot());
        minimapView.setSelectedIndex(indexPath);
    }

    @Override
    public void documentDidChangeSettings(CompoundTag tag) {
        editor.getConnector().update(editor.getDocument().getSettings());
    }

    @Override
    public void documentDidChangeProperties(CompoundTag tag) {
        editor.getConnector().update(editor.getDocument().getProperties());
    }

    @Override
    public void documentDidInsertNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
        DocumentMinimapNode nodeView = minimapView.findNode(node);
        if (nodeView != null) {
            DocumentMinimapNode targetView = new DocumentMinimapNode(target);
            nodeView.insertAtIndex(targetView, index);
        }
    }

    @Override
    public void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
        rightCard.documentDidUpdateNode(node, tag);
        if (tag.contains(SkinDocumentNode.Keys.NAME)) {
            DocumentMinimapNode nodeView = minimapView.findNode(node);
            if (nodeView != null) {
                nodeView.setTitle(node.getName());
            }
        }
    }

    @Override
    public void documentDidRemoveNode(SkinDocumentNode node) {
        DocumentMinimapNode nodeView = minimapView.findNode(node);
        if (nodeView != null) {
            TreeIndexPath indexPath = minimapView.getSelectedIndex();
            nodeView.removeFromParent();
            minimapView.setSelectedIndex(indexPath);
        }
    }

    @Override
    public void documentDidMoveNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
        DocumentMinimapNode nodeView = minimapView.findNode(node);
        DocumentMinimapNode targetView = minimapView.findNode(target);
        if (nodeView != null && targetView != null) {
            nodeView.moveTo(targetView, index);
        }
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
    }

    @Override
    public boolean shouldRenderBackground() {
        return false;
    }


    public static class Options {
        public static final Options SHOWS_LEFT_CARD = new Options("advanced.showLeftCard");
        public static final Options SHOWS_RIGHT_CARD = new Options("advanced.showRightCard");

        private final String key;
        private boolean value;

        public Options(String key) {
            this.key = key;
            this.value = ModMenuOptions.getInstance().getBoolean(key, true);
        }

        public void set(boolean value) {
            this.value = value;
            ModMenuOptions.getInstance().putBoolean(key, value);
        }

        public boolean get() {
            return value;
        }
    }
}
