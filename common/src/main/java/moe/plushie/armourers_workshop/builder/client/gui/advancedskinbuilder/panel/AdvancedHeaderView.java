package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UIView;

/**
 * [ PV | V ][ Cube       ]
 */
public class AdvancedHeaderView extends UIView {

    protected final UIButton selectionView = new UIButton(CGRect.ZERO);
    protected final UITextField textView = new UITextField(CGRect.ZERO);

    protected final UIView previewView = new UIView(CGRect.ZERO);
    protected final UIImageView arrowView = new UIImageView(CGRect.ZERO);

    public AdvancedHeaderView(CGRect frame) {
        super(frame);
        setupA();
        setupB();
    }

    private void setupA() {
        CGRect rect = bounds();
        selectionView.setFrame(new CGRect(0, 0, rect.getHeight() + 8, rect.getHeight()));
        selectionView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleHeight);
        addSubview(selectionView);
        previewView.setBackgroundColor(UIColor.ORANGE);
        previewView.setFrame(new CGRect(0, 0, rect.getHeight(), rect.getHeight()));
        previewView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        selectionView.addSubview(previewView);
        arrowView.setFrame(new CGRect(rect.getHeight(), (rect.getHeight() - 8) / 2, 8, 8));
        arrowView.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin | AutoresizingMask.flexibleBottomMargin);
        arrowView.setBackgroundColor(UIColor.GREEN);
        selectionView.addSubview(arrowView);
    }

    private void setupB() {
        CGRect rect = bounds();
        textView.setBordered(false);
        textView.setBackgroundColor(UIColor.CLEAR);
        textView.setFont(UIFont.systemFont(14));
        textView.setFrame(new CGRect(selectionView.bounds().getWidth(), 0, rect.getWidth() - selectionView.bounds().getWidth(), rect.getHeight()));
        textView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(textView);
    }
}
