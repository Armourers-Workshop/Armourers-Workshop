package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.DelegateImpl;
import com.apple.library.impl.ObjectUtilsImpl;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class UILabel extends UIView {

    private UIColor textColor;
    private UIColor shadowColor;

    private NSTextAlignment.Horizontal textHorizontalAlignment = NSTextAlignment.Horizontal.LEFT;
    private NSTextAlignment.Vertical textVerticalAlignment = NSTextAlignment.Vertical.CENTER;

    protected final DelegateImpl<UILabelDelegate> delegate = DelegateImpl.of(new UILabelDelegate() {
    });

    private int numberOfLines = 1;
    private int lineSpacing = 0;

    private NSString text;
    private List<TextLine> cachedTextLines;

    private float cachedTextWidth;
    private float cachedTextHeight;

    private UIFont font;

    public UILabel(CGRect frame) {
        super(frame);
        this.setUserInteractionEnabled(false);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        Map<String, ?> attributes = attributesAtPoint(event.locationInView(this));
        if (attributes != null) {
            delegate.invoker().labelWillClickAttributes(this, attributes);
        }
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        if (text == null) {
            return;
        }
        UIFont font = this.font != null ? this.font : context.state().font();
        CGRect rect = bounds();
        remakeTextLineIfNeeded(text, font, rect);
        if (cachedTextLines == null || cachedTextLines.isEmpty()) {
            return;
        }
        float dy = sel(rect, cachedTextHeight, textVerticalAlignment);
        for (TextLine line : cachedTextLines) {
            CGPoint offset = line.offset;
            float dx = sel(rect, line.size.width, textHorizontalAlignment);
            context.drawText(line.text, offset.x + dx, offset.y + dy, font, textColor, shadowColor);
        }
    }

    public NSString text() {
        return this.text;
    }

    public void setText(NSString text) {
        this.text = text;
        this.setNeedsRemakeTextLine();
    }

    public UIColor textColor() {
        return this.textColor;
    }

    public void setTextColor(UIColor textColor) {
        this.textColor = textColor;
    }

    public UIColor shadowColor() {
        return shadowColor;
    }

    public void setShadowColor(UIColor shadowColor) {
        this.shadowColor = shadowColor;
    }

    public NSTextAlignment.Horizontal textHorizontalAlignment() {
        return textHorizontalAlignment;
    }

    public void setTextHorizontalAlignment(NSTextAlignment.Horizontal textHorizontalAlignment) {
        this.textHorizontalAlignment = textHorizontalAlignment;
    }

    public NSTextAlignment.Vertical textVerticalAlignment() {
        return textVerticalAlignment;
    }

    public void setTextVerticalAlignment(NSTextAlignment.Vertical textVerticalAlignment) {
        this.textVerticalAlignment = textVerticalAlignment;
    }

    public UIFont font() {
        return this.font;
    }

    public void setFont(UIFont font) {
        this.font = font;
        this.setNeedsRemakeTextLine();
    }

    public int numberOfLines() {
        return this.numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
        this.setNeedsRemakeTextLine();
    }

    public int lineSpacing() {
        return this.lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
        this.setNeedsRemakeTextLine();
    }

    public UILabelDelegate delegate() {
        return delegate.get();
    }

    public void setDelegate(UILabelDelegate delegate) {
        this.delegate.set(delegate);
    }

    @Nullable
    public Map<String, ?> attributesAtPoint(CGPoint point) {
        if (cachedTextLines == null) {
            return null;
        }
        CGRect rect = bounds();
        float dy = sel(rect, cachedTextHeight, textVerticalAlignment);
        for (TextLine line : cachedTextLines) {
            CGPoint offset = line.offset;
            float dx = sel(rect, line.size.width, textHorizontalAlignment);
            if (point.y >= offset.y + dy && point.y <= offset.y + dy + line.size.height) {
                float ptx = point.x - dx;
                return line.text.attributes((int) ptx, line.font);
            }
        }
        return null;
    }

    private float sel(CGRect rect, float value, NSTextAlignment.Vertical alignment) {
        switch (alignment) {
            case BOTTOM:
                return rect.height - value;

            case CENTER:
                return (rect.height - value) / 2;

            default:
                return 0;
        }
    }

    private float sel(CGRect rect, float value, NSTextAlignment.Horizontal alignment) {
        switch (alignment) {
            case RIGHT:
                return rect.width - value;

            case CENTER:
                return (rect.width - value) / 2;

            default:
                return 0;
        }
    }

    private void setNeedsRemakeTextLine() {
        cachedTextLines = null;
    }

    private void remakeTextLineIfNeeded(NSString title, UIFont font, CGRect bounds) {
        // if the cache is still valid, we continue to use it.
        float width = bounds.width;
        if (cachedTextLines != null && cachedTextWidth == width) {
            return;
        }
        // split all line with text.
        LinkedList<NSString> lines = new LinkedList<>();
        if (numberOfLines == 1) {
            lines.add(title);
        } else {
            lines.addAll(title.split((int) width, font));
        }
        // remove all excess lines.
        while (numberOfLines != 0 && lines.size() > numberOfLines) {
            lines.removeLast();
        }
        // transform the char sequence to text line.
        cachedTextLines = ObjectUtilsImpl.map(lines, text -> new TextLine(text, font));
        cachedTextWidth = width;

        int textHeight = 0;
        for (TextLine line : cachedTextLines) {
            line.offset = new CGPoint(0, textHeight);
            textHeight += line.size.height + lineSpacing;
        }
        if (!cachedTextLines.isEmpty()) {
            textHeight -= lineSpacing;
        }

        cachedTextHeight = textHeight;
    }

    protected static class TextLine {
        CGPoint offset;
        CGRect size;
        NSString text;
        UIFont font;

        TextLine(NSString text, UIFont font) {
            this.text = text;
            this.size = text.boundingRectWithFont(font);
            this.font = font;
        }
    }
}
