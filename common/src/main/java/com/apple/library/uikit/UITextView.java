package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSRange;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextPosition;
import com.apple.library.foundation.NSTextRange;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.DelegateImpl;
import com.apple.library.impl.InvokerResult;
import com.apple.library.impl.TextInputImpl;
import com.apple.library.impl.TextInputTraits;
import com.apple.library.impl.TextStorageImpl;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public class UITextView extends UIScrollView implements TextInputTraits {

    private final TextStorageImpl storage = new TextStorageImpl();
    private final TextInputImpl input = new TextInputImpl(storage);
    private final DelegateImpl<UITextViewDelegate> delegate = DelegateImpl.of(new UITextViewDelegate() {
        // auto stub
    });

    private UIEdgeInsets contentInsets = new UIEdgeInsets(8, 4, 8, 4);

    private boolean isBordered = true;
    private boolean needSyncCursor = false;

    public UITextView(CGRect frame) {
        super(frame);
        this.storage.setLineSpacing(1);
        this.storage.maxLength = 32;
        this.storage.sizeDidChange = this::sizeDidChange;
        this.storage.selectionDidChange = this::selectionDidChange;
        this.storage.valueDidChange = this::valueDidChange;
        this.storage.valueShouldChange = this::valueShouldChange;
        this.input.returnHandler = this::shouldReturn;
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = bounds().insetBy(contentInsets);
        storage.setBoundingSize(new CGSize(bounds.width, bounds.height));
    }

    @Override
    public void mouseDown(UIEvent event) {
        if (!storage.isFocused()) {
            becomeFirstResponder();
        }
        if (shouldInputText()) {
            input.mouseDown(event.locationInView(this));
        }
    }

    @Override
    public void keyDown(UIEvent event) {
        if (shouldInputText() && input.keyDown(event.key)) {
            return;
        }
        if (isFocused() && event.key == GLFW.GLFW_KEY_ESCAPE) {
            resignFirstResponder();
            return;
        }
        super.keyDown(event);
    }

    @Override
    public void charTyped(UIEvent event) {
        if (shouldInputText() && input.charTyped((char) event.key)) {
            return;
        }
        super.charTyped(event);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        CGRect bounds = bounds();
        CGRect fixedBounds = bounds.insetBy(1, 1, 1, 1);
        if (isBordered) {
            context.fillRect(getBorderColor(), bounds);
            context.fillRect(getFillColor(), fixedBounds);
        }
        context.addClip(UIScreen.convertRectFromView(fixedBounds, this));
        storage.render(point, context);
        context.removeClip();
    }

    public boolean isEditable() {
        return input.isEditable();
    }

    public void setEditable(boolean isEditable) {
        input.setEditable(isEditable);
    }

    public UIFont font() {
        return storage.font();
    }

    public void setFont(UIFont font) {
        storage.setFont(font);
    }

    public NSString placeholder() {
        return storage.placeholder();
    }

    public void setPlaceholder(NSString placeholder) {
        storage.setPlaceholder(placeholder);
    }

    public UIColor placeholderColor() {
        return storage.placeholderColor();
    }

    public void setPlaceholderColor(UIColor placeholderColor) {
        storage.setPlaceholderColor(placeholderColor);
    }

    public String text() {
        return storage.value();
    }

    public void setText(String text) {
        storage.setValue(text);
        storage.checkCursorAndHighlightPos();
    }

    public UIColor textColor() {
        return storage.textColor();
    }

    public void setTextColor(UIColor textColor) {
        storage.setTextColor(textColor);
    }

    public NSTextRange selectedTextRange() {
        return storage.selectedTextRange();
    }

    public void setSelectedTextRange(NSTextRange range) {
        storage.setSelectedTextRange(range);
    }

    public UITextViewDelegate delegate() {
        return delegate.get();
    }

    public void setDelegate(UITextViewDelegate delegate) {
        this.delegate.set(delegate);
    }

    public boolean isEditing() {
        return storage.isFocused();
    }

    public void becomeFirstResponder() {
        if (storage.isFocused() || !isEditable()) {
            return;
        }
        UIWindow window = window();
        if (window != null) {
            if (!delegate.invoker().textViewShouldBeginEditing(this)) {
                return;
            }
            window.setFirstInputResponder(this);
            storage.setFocused(true);
            delegate.invoker().textViewDidBeginEditing(this);
        }
    }

    public void resignFirstResponder() {
        if (!storage.isFocused()) {
            return;
        }
        if (!delegate.invoker().textViewShouldEndEditing(this)) {
            return;
        }
        UIWindow window = window();
        if (window != null) {
            window.setFirstInputResponder(null);
        }
        storage.setFocused(false);
        delegate.invoker().textViewDidEndEditing(this);
    }

    @Override
    public boolean canBecomeFocused() {
        return !_ignoresTouchEvents(this) && input.isEditable();
    }

    public NSTextPosition beginOfDocument() {
        return storage.beginOfDocument();
    }

    public NSTextPosition endOfDocument() {
        return storage.endOfDocument();
    }

    public int maxLength() {
        return storage.maxLength;
    }

    public void setMaxLength(int maxLength) {
        storage.maxLength = maxLength;
    }

    public UIEdgeInsets contentInsets() {
        return contentInsets;
    }

    public void setContentInsets(UIEdgeInsets contentInsets) {
        this.contentInsets = contentInsets;
    }

    private boolean shouldReturn(String value) {
//        return delegate.invoker().textFieldShouldReturn(this);
        return true;
    }

    private boolean valueShouldChange(NSRange range, String newValue) {
        return delegate.invoker().textViewShouldChangeText(this, range, newValue);
    }

    private void valueDidChange(String oldValue, String newValue) {
//        sendEvent(Event.VALUE_CHANGED);
        delegate.invoker().textViewDidChange(this);
    }

    private void selectionDidChange() {
        delegate.invoker().textViewDidChangeSelection(this);
        needSyncCursor = true;
    }

    private void sizeDidChange(CGRect rect, CGSize size) {
        float height = size.height + contentInsets.top + contentInsets.bottom;
        if (contentSize.height != height) {
            storage.offset = new CGPoint(contentInsets.left, contentInsets.top);
            setContentSize(new CGSize(0, height));
        }
        if (needSyncCursor) {
            needSyncCursor = false;
            CGRect box = convertRectToView(rect, superview());
            CGRect frame = frame().insetBy(contentInsets);
            if (box.getMinY() < frame.getMinY() || box.getMaxY() > frame.getMaxY()) {
                setContentOffset(new CGPoint(0, rect.y));
            }
        }
    }

    private UIColor getFillColor() {
        return AppearanceImpl.TEXT_BACKGROUND_COLOR;
    }

    private UIColor getBorderColor() {
        if (storage.isFocused()) {
            return AppearanceImpl.TEXT_FOCUSED_BORDER_COLOR;
        }
        return AppearanceImpl.TEXT_BORDER_COLOR;
    }

    private boolean shouldInputText() {
        return !isHidden() && storage.isFocused() && isUserInteractionEnabled() && isEditable();
    }
}
