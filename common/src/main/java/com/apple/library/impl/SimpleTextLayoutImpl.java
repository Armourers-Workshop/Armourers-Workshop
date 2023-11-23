package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;

import java.util.LinkedList;
import java.util.List;

public class SimpleTextLayoutImpl {

    private final float contentWidth;
    private final float contentHeight;

    private final List<Line> textLines;

    public SimpleTextLayoutImpl(NSString title, UIFont font, int numberOfLines, float lineSpacing, float preferredMaxLayoutWidth) {
        // when width not provided, we will use the largest single row size.
        if (preferredMaxLayoutWidth <= 0) {
            preferredMaxLayoutWidth = 10000;
        }
        // split all line with text.
        LinkedList<NSString> lines = new LinkedList<>();
        if (numberOfLines == 1) {
            lines.add(title);
        } else {
            lines.addAll(title.split(preferredMaxLayoutWidth, font));
        }
        // remove all excess lines.
        while (numberOfLines != 0 && lines.size() > numberOfLines) {
            lines.removeLast();
        }
        // transform the char sequence to text line.
        this.textLines = ObjectUtilsImpl.map(lines, text -> new Line(text, font));

        float textWidth = 0;
        float textHeight = 0;
        for (Line line : textLines) {
            line.offset = new CGPoint(0, textHeight);
            textHeight += line.size.height + lineSpacing;
            textWidth = Math.max(textWidth, line.size.width);
        }
        if (!textLines.isEmpty()) {
            textHeight -= lineSpacing;
        }

        this.contentWidth = textWidth;
        this.contentHeight = textHeight;
    }

    public List<Line> contents() {
        return textLines;
    }

    public CGSize contentSize() {
        return new CGSize(contentWidth, contentHeight);
    }

    public boolean isEmpty() {
        return textLines.isEmpty();
    }

    public static class Line {

        public CGPoint offset;
        public CGRect size;
        public NSString text;
        public UIFont font;

        public Line(NSString text, UIFont font) {
            this.text = text;
            this.size = text.boundingRectWithFont(font);
            this.font = font;
        }
    }
}
