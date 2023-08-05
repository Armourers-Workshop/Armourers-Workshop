package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIScreen;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.AdvancedCameraPanel;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.AdvancedRightCardPanel;
import moe.plushie.armourers_workshop.builder.client.gui.widget.Shortcut;
import moe.plushie.armourers_workshop.builder.menu.AdvancedSkinBuilderMenu;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class AdvancedSkinBuilderWindow extends MenuWindow<AdvancedSkinBuilderMenu> {

    private final AdvancedCameraPanel cameraView;
    private final AdvancedRightCardPanel rightCard;

    private final HashMap<Shortcut, Runnable> shortcuts = new HashMap<>();

    private int leftCardOffset = 100;
    private int rightCardOffset = 100;

    public AdvancedSkinBuilderWindow(AdvancedSkinBuilderMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        AdvancedSkinBuilderBlockEntity blockEntity = container.getBlockEntity(AdvancedSkinBuilderBlockEntity.class);
        this.cameraView = new AdvancedCameraPanel(blockEntity);
        this.rightCard = new AdvancedRightCardPanel(blockEntity, new CGRect(0, 0, 200, UIScreen.bounds().getHeight() * 2));
        this.inventoryView.setHidden(true);
        this.setup();
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
        rightCard.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);
        rightCard.setBackgroundColor(new UIColor(0x1d1d1d));
        rightCard.setTransform(CGAffineTransform.createScale(0.5f, 0.5f));
        rightCard.setFrame(new CGRect(rect.width - rightCardOffset, 0, rightCardOffset, rect.height));
        rightCard.setSkinTypeWithIndex(0);
        addSubview(rightCard);
    }

    private void setupShortcuts() {
        shortcuts.put(Shortcut.of("key.keyboard.control", "key.keyboard.1"), this::toggleLeftCard);
        shortcuts.put(Shortcut.of("key.keyboard.control", "key.keyboard.2"), this::toggleRightCard);
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
