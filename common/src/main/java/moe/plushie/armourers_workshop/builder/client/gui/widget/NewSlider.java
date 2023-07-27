package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.foundation.NSTextRange;
import com.apple.library.impl.InvokerResult;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.MathUtils;

import java.util.Optional;

public class NewSlider extends UIControl implements UITextFieldDelegate {

    private static final UIImage LEFT_ARROW_IMAGE = UIImage.of(ModTextures.LIST).uv(0, 224).fixed(8, 8).build();
    private static final UIImage RIGHT_ARROW_IMAGE = UIImage.of(ModTextures.LIST).uv(8, 224).fixed(8, 8).build();

    private static final UIImage NORMAL_IMAGE = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(28, 28).resizable(16, 16).build();
    private static final UIImage FOCUSED_IMAGE = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(52, 28).resizable(16, 16).build();
    private static final UIImage HIGHLIGHTED_IMAGE = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(76, 28).resizable(16, 16).build();
    private static final UIImage EDITING_IMAGE = UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(100, 28).resizable(16, 16).build();

    protected final UIButton leftView = new UIButton(CGRect.ZERO);
    protected final UIButton rightView = new UIButton(CGRect.ZERO);
    protected final UIButton contentView = new UIButton(CGRect.ZERO);

    protected final UILabel valueView = new UILabel(CGRect.ZERO);
    protected final UITextField valueInputView = new UITextField(CGRect.ZERO);

    protected double value = 1.00;

    protected double minValue = Double.NEGATIVE_INFINITY;
    protected double maxValue = Double.POSITIVE_INFINITY;
    protected double stepValue = 0.01;

    private float multipler = 1;
    private Formatter formatter = Formatter.DEFAULT;

    private boolean isChangedValue = false;
    private CGPoint startDragLocation = null;

    public NewSlider(CGRect frame) {
        super(frame);
        this.setContents(NORMAL_IMAGE);
        this.addSubview(leftView);
        this.addSubview(contentView);
        this.addSubview(rightView);
        this.setup(leftView, LEFT_ARROW_IMAGE);
        this.setup(contentView, null);
        this.setup(rightView, RIGHT_ARROW_IMAGE);

        this.valueInputView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.valueInputView.setContents(EDITING_IMAGE);
        this.valueInputView.setDelegate(this);
        this.valueInputView.setBordered(false);

        this.valueView.setFrame(bounds());
        this.valueView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.valueView.setTextColor(UIColor.WHITE);
        this.valueView.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
        this.addSubview(valueView);

        this.update(1);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        float height = rect.getHeight();
        float width = height * 3 / 4; // 3:4
        leftView.setFrame(new CGRect(0, 0, width, height));
        rightView.setFrame(new CGRect(rect.getWidth() - width, 0, width, height));
        contentView.setFrame(new CGRect(width, 0, rect.getWidth() - width * 2, height));
    }

    @Override
    public void textFieldDidBeginEditing(UITextField textField) {
        leftView.setHidden(true);
        contentView.setHidden(true);
        rightView.setHidden(true);
        valueView.setHidden(true);
    }

    @Override
    public void textFieldDidEndEditing(UITextField textField) {
        valueView.setHidden(false);
        leftView.setHidden(false);
        contentView.setHidden(false);
        rightView.setHidden(false);
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        inputEnd();
        return true;
    }

    public double value() {
        return value;
    }

    public void setValue(double value) {
        this.update(value);
    }

    public double maxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        this.update(value);
    }

    public double minValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        this.update(value);
    }

    public double stepValue() {
        return stepValue;
    }

    public void setStepValue(double stepValue) {
        this.stepValue = stepValue;
    }

    public float multipler() {
        return multipler;
    }

    public void setMultipler(float multipler) {
        this.multipler = multipler;
    }

    public Formatter formatter() {
        return formatter;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    private void setup(UIButton view, UIImage arrow) {
        view.setImage(arrow, State.HIGHLIGHTED);
        view.setImage(arrow, State.SELECTED);
        view.setImage(arrow, State.DISABLED);

        view.setBackgroundImage(NORMAL_IMAGE, State.NORMAL);
        view.setBackgroundImage(HIGHLIGHTED_IMAGE, State.HIGHLIGHTED);
        view.setBackgroundImage(FOCUSED_IMAGE, State.SELECTED);
        view.setBackgroundImage(HIGHLIGHTED_IMAGE, State.SELECTED | State.HIGHLIGHTED);
        view.setBackgroundImage(EDITING_IMAGE, State.DISABLED);

        view.addTarget(this, Event.MOUSE_ENTERED, NewSlider::buttonEnter);
        view.addTarget(this, Event.MOUSE_EXITED, NewSlider::buttonExit);
        view.addTarget(this, Event.MOUSE_LEFT_DOWN, NewSlider::buttonDown);
    }

    private void buttonEnter(UIControl sender) {
        leftView.setSelected(true);
        contentView.setSelected(true);
        rightView.setSelected(true);
    }

    private void buttonExit(UIControl sender) {
        leftView.setSelected(false);
        contentView.setSelected(false);
        rightView.setSelected(false);
    }

    private void buttonDown(UIControl sender) {
        UIWindow window = window();
        if (window == null) {
            return;
        }
        // hook all mouse move and up events.
        window.addGlobalTarget(this, Event.MOUSE_MOVED, NewSlider::buttonMove);
        window.addGlobalTarget(this, Event.MOUSE_LEFT_UP, NewSlider::buttonUp);

        leftView.setEnabled(false);
        contentView.setEnabled(false);
        rightView.setEnabled(false);

        isChangedValue = false;
        startDragLocation = null;
    }

    private void buttonMove(UIEvent event) {
        // we required an exclusive event.
        event.cancel(InvokerResult.SUCCESS);

        // when first move call, we need save mouse start location.
        CGPoint location = event.locationInWindow();
        if (startDragLocation == null) {
            startDragLocation = location.copy();
            return;
        }

        float dx = Math.round((location.x - startDragLocation.x) * multipler);
        if (dx == 0) {
            return;
        }
        update(value + dx * stepValue);
        startDragLocation.x += dx / multipler;
        isChangedValue = true;
    }

    private void buttonUp(UIEvent event) {
        UIWindow window = window();
        if (window == null) {
            return;
        }
        // remove hook of the mouse move and up events.
        window.removeGlobalTarget(this, Event.MOUSE_MOVED);
        window.removeGlobalTarget(this, Event.MOUSE_LEFT_UP);

        leftView.setEnabled(true);
        contentView.setEnabled(true);
        rightView.setEnabled(true);

        // when no value changes occurs, we need to check the clicked button.
        if (isChangedValue) {
            return;
        }

        // the click on the left side?
        UIView view = hitTest(event.locationInView(this), event);
        if (view == leftView) {
            update(value - stepValue);
            return;
        }

        // the click on the right side?
        if (view == rightView) {
            update(value + stepValue);
            return;
        }

        // enter text input mode.
        valueInputView.setFrame(bounds());
        valueInputView.setSelectedTextRange(new NSTextRange(valueInputView.beginOfDocument(), valueInputView.endOfDocument()));
        addSubview(valueInputView);
        valueInputView.becomeFirstResponder();

        window.addGlobalTarget(this, Event.MOUSE_MOVED, NewSlider::inputMove);
        window.addGlobalTarget(this, Event.MOUSE_LEFT_DOWN, NewSlider::inputDown);
    }

    private void inputDown(UIEvent event) {
        inputMove(event);
        if (event.isCancelled()) {
            inputEnd();
        }
    }

    private void inputMove(UIEvent event) {
        // we required an exclusive event when location not in input view.
        if (!pointInside(event.locationInView(valueInputView), event)) {
            event.cancel(InvokerResult.SUCCESS);
        }
    }

    private void inputEnd() {
        UIWindow window = window();
        if (window == null) {
            return;
        }
        window.removeGlobalTarget(this, Event.MOUSE_LEFT_DOWN);
        window.removeGlobalTarget(this, Event.MOUSE_MOVED);

        Optional<Double> newValue = formatter.parse(valueInputView.text());
        update(newValue.orElse(value));
        valueInputView.resignFirstResponder();
        valueInputView.removeFromSuperview();
    }

    private void update(double newValue) {
        // to avoid -0
        if (Math.abs(newValue) < 0.000001) {
            newValue = 0;
        }
        value = MathUtils.clamp(newValue, minValue, maxValue);
        valueView.setText(new NSString(formatter.display(value)));
        valueInputView.setText(formatter.input(value));
    }

    public interface Formatter {

        Formatter DEFAULT = new Formatter() {

            @Override
            public String display(double input) {
                return String.format("%.3f", input);
            }

            @Override
            public Optional<Double> parse(String input) {
                try {
                    return Optional.of(Double.parseDouble(input));
                } catch (Exception e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }
        };

        String display(double value);

        Optional<Double> parse(String value);

        default String input(double value) {
            return display(value);
        }
    }
}
