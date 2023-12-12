package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import com.apple.library.uikit.UIView;
import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.data.IDataProperty;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinIconView;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;

import java.util.function.Consumer;

/**
 * [ PV | V ][ Name       ]
 */
public class AdvancedHeaderView extends UIView implements UITextFieldDelegate {

    protected SkinDescriptor selectedPart = SkinDescriptor.EMPTY;

    protected final UIButton selectionView = new UIButton(CGRect.ZERO);
    protected final UITextField textView = new UITextField(CGRect.ZERO);

    protected final UIView contentView = new UIView(CGRect.ZERO);
    protected final UIImageView arrowView = new UIImageView(CGRect.ZERO);

    protected final UIImageView partEmptyView = new UIImageView(CGRect.ZERO);
    protected final SkinIconView partPreviewView = new SkinIconView(CGRect.ZERO);

    protected Consumer<UIControl> picker;

    public AdvancedHeaderView(IDataProperty<String> name, IDataProperty<SkinDescriptor> part, CGRect frame) {
        super(frame);
        this.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 24).clip(4, 4, 4, 4).build());
        this.setupSelectionView();
        this.setupPartView(part);
        this.setupInputView(name);
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        textView.resignFirstResponder();
        return true;
    }

    private void setupPartView(IDataProperty<SkinDescriptor> property) {
        partEmptyView.setUserInteractionEnabled(false);
        partEmptyView.setFrame(contentView.bounds());
        partEmptyView.layer().setBorderWidth(1);
        partEmptyView.layer().setBorderColor(0xff800000);
        partEmptyView.setImage(UIImage.of(ModTextures.SKIN_PANEL).uv(224, 0).resizable(32, 32).build());
        partEmptyView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        contentView.addSubview(partEmptyView);

        partPreviewView.setUserInteractionEnabled(false);
        partPreviewView.setBackgroundColor(UIColor.rgba(0x22AAAAAA));
        partPreviewView.setFrame(contentView.bounds());
        partPreviewView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        partPreviewView.setHidden(true);
        contentView.addSubview(partPreviewView);

        property.addObserver(this::setSelectedPart);
    }

    private void setupSelectionView() {
        CGRect rect = bounds().insetBy(1, 1, 1, 1);

        selectionView.setFrame(new CGRect(1, 1, rect.getHeight() + 8 + 4, rect.getHeight()));
        selectionView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleHeight);
        addSubview(selectionView);
        contentView.setFrame(new CGRect(6, 4, rect.getHeight() - 8, rect.getHeight() - 8));
        contentView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        selectionView.addSubview(contentView);
        arrowView.setFrame(new CGRect(rect.getHeight() + 2, (rect.getHeight() - 8) / 2, 8, 8));
        arrowView.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin | AutoresizingMask.flexibleBottomMargin);
        arrowView.setImage(UIImage.of(ModTextures.LIST).uv(0, 248).fixed(8, 8).build());
        selectionView.addSubview(arrowView);
        selectionView.setBackgroundImage(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(120, 24).fixed(20, 24).clip(4, 4, 4, 0).build(), UIControl.State.NORMAL);
        selectionView.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, (self, ctrl) -> {
            if (self.picker != null) {
                self.picker.accept(ctrl);
            }
        });
    }

    private void setupInputView(IDataProperty<String> property) {
        CGRect rect = bounds().insetBy(1, 1, 1, 1);
        textView.setBordered(false);
        textView.setBackgroundColor(UIColor.CLEAR);
        textView.setFont(UIFont.systemFont(11));
        textView.setFrame(new CGRect(selectionView.frame().getMaxX() + 1, 1, rect.getWidth() - selectionView.frame().getMaxX() - 1, rect.getHeight()));
        textView.setEditable(false);
        textView.setDelegate(this);
        textView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        textView.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(124, 24).fixed(20, 24).clip(4, 0, 4, 4).build());
        textView.addTarget(property, UIControl.Event.EDITING_DID_BEGIN, (pro, ctr) -> pro.beginEditing());
        textView.addTarget(property, UIControl.Event.EDITING_DID_END, (pro, ctr) -> pro.endEditing());
        textView.addTarget(property, UIControl.Event.VALUE_CHANGED, (pro, ctr) -> {
            UITextField textView = (UITextField) ctr;
            pro.set(textView.text());

        });
        addSubview(textView);

        property.addObserver(newValue -> {
            String oldValue = textView.text();
            if (!Objects.equal(oldValue, newValue)) {
                textView.setText(newValue);
            }
        });
    }

    public void setName(String name) {
        this.textView.setText(name);
    }

    public String getName() {
        return textView.text();
    }

    public void setEditable(boolean isEditable) {
        textView.setEditable(isEditable);
    }

    public boolean isEditable() {
        return textView.isEditable();
    }

    public void setPicker(Consumer<UIControl> picker) {
        this.picker = picker;
    }

    public Consumer<UIControl> getPicker() {
        return picker;
    }

    public void setSelectedPart(SkinDescriptor selectedPart) {
        this.selectedPart = selectedPart;
        this.partPreviewView.setSkin(selectedPart);
        this.partPreviewView.setHidden(selectedPart.isEmpty());
        this.partEmptyView.setHidden(!selectedPart.isEmpty());
    }

    public SkinDescriptor getSelectedPart() {
        return selectedPart;
    }
}
