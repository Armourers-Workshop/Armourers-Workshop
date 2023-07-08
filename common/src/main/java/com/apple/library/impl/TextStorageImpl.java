package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSRange;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextPosition;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import com.mojang.blaze3d.vertex.Tesselator;
import moe.plushie.armourers_workshop.compatibility.AbstractShaderTesselator;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class TextStorageImpl {

    public CGPoint offset = CGPoint.ZERO;
    public int maxLength = 1000;

    public BiConsumer<CGRect, CGSize> sizeDidChange = (c, s) -> {};
    public BiConsumer<String, String> valueDidChange = (o, n) -> {};
    public BiPredicate<NSRange, String> valueShouldChange = (o, n) -> true;
    public Runnable selectionDidChange = () -> {};

    private String value = "";

    private NSString placeholder;
    private UIColor placeholderColor;

    private UIFont font;
    private UIColor textColor;

    private CGSize boundingSize = CGSize.ZERO;

    private NSTextPosition highlightPos = NSTextPosition.ZERO;
    private NSTextPosition cursorPos = NSTextPosition.ZERO;

    private int lineSpacing = 1;
    private boolean isFocused = false;
    private long cursorTimestamp = 0;

    private CGRect cursorRect = CGRect.ZERO;
    private Collection<CGRect> highlightedRects;
    private Font cachedFont;

    private Collection<TextLine> cachedTextLines;

    public void insertText(String inputText) {
        String oldValue = value;
        String replacementText = formattedString(inputText);
        NSRange range = selectionRange();
        if (!valueShouldChange.test(range, replacementText)) {
            return;
        }
        int length = replacementText.length();
        if (maxLength > 0) {
            int remaining = Math.max((maxLength - (oldValue.length() - range.length)), 0);
            if (remaining < length) {
                replacementText = replacementText.substring(0, remaining);
                length = remaining;
            }
        }
        int startIndex = range.startIndex();
        int endIndex = range.endIndex();
        setValue((new StringBuilder(oldValue)).replace(startIndex, endIndex, replacementText).toString());
        setCursorAndHighlightPos(NSTextPosition.forward(startIndex + length));
        valueDidChange.accept(oldValue, value);
    }

    public void deleteText(TextTokenizer tokenizer, int count) {
        if (value.isEmpty()) {
            return;
        }
        if (!cursorPos.equals(highlightPos)) {
            insertText("");
            return;
        }
        int advancedIndex = tokenizer.advance(value, cursorPos.value, count);
        int startIndex = Math.min(advancedIndex, cursorPos.value);
        int endIndex = Math.max(advancedIndex, cursorPos.value);
        if (startIndex == endIndex) {
            return;
        }
        String oldValue = value;
        if (!valueShouldChange.test(NSRange.of(startIndex, endIndex), "")) {
            return;
        }
        setValue((new StringBuilder(oldValue)).delete(startIndex, endIndex).toString());
        setCursorAndHighlightPos(NSTextPosition.forward(startIndex));
        valueDidChange.accept(oldValue, value);
    }

    private void setCursorPos(NSTextPosition pos) {
        this.cursorPos = clamp(pos, beginOfDocument(), endOfDocument());
        this.cursorTimestamp = System.currentTimeMillis();
        this.setNeedsRemakeTextLine();
    }

    private void setHighlightPos(NSTextPosition pos) {
        this.highlightPos = clamp(pos, beginOfDocument(), endOfDocument());
        this.setNeedsRemakeTextLine();
    }

    public void sizeToFit() {
        Font font = defaultFont();
        remakeTextLineIfNeeded(boundingSize, font);
    }

    public void render(CGPoint point, CGGraphicsContext context) {
        // auto resize before the render.
        if (cachedTextLines == null) {
            sizeToFit();
        }
        Font font = cachedFont;
        int textColor = defaultTextColor();
        if (cachedFont == null || cachedTextLines == null) {
            return;
        }
        context.saveGraphicsState();
        context.translateCTM(offset.x, offset.y, 0);

        auto pose = context.state().ctm().last().pose();
        auto buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        if (placeholder != null && cachedTextLines.isEmpty()) {
            int placeholderColor = defaultPlaceholderColor();
            font.drawInBatch(placeholder.chars(), 1, 0, placeholderColor, true, pose, buffers, false, 0, 15728880);
        }
        for (TextLine line : cachedTextLines) {
            font.drawInBatch(line.chars, line.rect.x, line.rect.y, textColor, true, pose, buffers, false, 0, 15728880);
            context.strokeDebugRect(line.index, line.rect);
        }
        buffers.endBatch();

        renderHighlightedRectIfNeeded(context);
        renderCursorIfNeeded(context);

        context.restoreGraphicsState();
    }

    public void renderCursorIfNeeded(CGGraphicsContext context) {
        if (!isFocused || cursorRect == null) {
            return;
        }
        long diff = (System.currentTimeMillis() - cursorTimestamp) % 1200;
        if (diff > 600) {
            return;
        }
        context.fillRect(AppearanceImpl.TEXT_CURSOR_COLOR, cursorRect);
    }

    public void renderHighlightedRectIfNeeded(CGGraphicsContext context) {
        if (!isFocused || highlightedRects == null || highlightedRects.isEmpty()) {
            return;
        }
        auto pose = context.state().ctm().last().pose();
        auto tesselator = AbstractShaderTesselator.getInstance();
        auto builder = tesselator.begin(SkinRenderType.GUI_HIGHLIGHTED_TEXT);
        for (CGRect rect : highlightedRects) {
            builder.vertex(pose, rect.getMinX(), rect.getMaxY(), 0).endVertex();
            builder.vertex(pose, rect.getMaxX(), rect.getMaxY(), 0).endVertex();
            builder.vertex(pose, rect.getMaxX(), rect.getMinY(), 0).endVertex();
            builder.vertex(pose, rect.getMinX(), rect.getMinY(), 0).endVertex();
        }
        context.setBlendColor(AppearanceImpl.TEXT_HIGHLIGHTED_COLOR);
        tesselator.end();
        context.setBlendColor(UIColor.WHITE);
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.setNeedsRemakeTextLine();
    }

    public UIColor textColor() {
        return textColor;
    }

    public void setTextColor(UIColor textColor) {
        this.textColor = textColor;
    }

    public NSString placeholder() {
        return placeholder;
    }

    public void setPlaceholder(NSString placeholder) {
        this.placeholder = placeholder;
    }

    public UIColor placeholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(UIColor placeholderColor) {
        this.placeholderColor = placeholderColor;
    }

    public UIFont font() {
        return font;
    }

    public void setFont(UIFont font) {
        this.font = font;
        this.setNeedsRemakeTextLine();
    }

    public void setBoundingSize(CGSize size) {
        this.boundingSize = size;
        this.setNeedsRemakeTextLine();
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public NSTextPosition beginOfDocument() {
        return NSTextPosition.ZERO;
    }

    public NSTextPosition endOfDocument() {
        return NSTextPosition.forward(value.length());
    }

    public NSTextPosition positionAtPoint(CGPoint point) {
        // not layout or renderer.
        if (cachedFont == null || cachedTextLines == null) {
            return null;
        }
        ArrayList<TextLine> selectedLines = new ArrayList<>();
        if (isMultipleLineMode()) {
            for (TextLine line : cachedTextLines) {
                if (line.insideAtY(point.y)) {
                    selectedLines.add(line);
                }
            }
        } else {
            selectedLines.addAll(cachedTextLines);
        }
        for (TextLine line : selectedLines) {
            if (line.insideAtX(point.x)) {
                String value = cachedFont.plainSubstrByWidth(line.text, (int) (point.x - line.rect.x));
                int index = line.range.startIndex() + value.length();
                return NSTextPosition.forward(index);
            }
        }
        if (!selectedLines.isEmpty()) {
            TextLine line = selectedLines.get(selectedLines.size() - 1);
            return NSTextPosition.backward(line.range.endIndex());
        }
        return endOfDocument();
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
        // when focus is lose, we must automatically cancel the highlighted.
        if (!isFocused) {
            setHighlightPos(cursorPos);
        }
    }

    public boolean isMultipleLineMode() {
        return boundingSize.width != 0;
    }

    public String highlightedText() {
        NSTextPosition startPos = min(cursorPos, highlightPos);
        NSTextPosition endPos = max(cursorPos, highlightPos);
        return value.substring(startPos.value, endPos.value);
    }

    public CGRect cursorRect() {
        return cursorRect;
    }

    public void setCursorAndHighlightPos(NSTextPosition pos) {
        setCursorAndHighlightPos(pos, pos);
    }

    public void setCursorAndHighlightPos(NSTextPosition cursorPos, NSTextPosition highlightPos) {
        setCursorPos(cursorPos);
        setHighlightPos(highlightPos);
        selectionDidChange.run();
    }

    public void checkCursorAndHighlightPos() {
        NSTextPosition cursorPos1 = clamp(cursorPos, beginOfDocument(), endOfDocument());
        NSTextPosition highlightPos1 = clamp(highlightPos, beginOfDocument(), endOfDocument());
        if (cursorPos1.equals(cursorPos) && highlightPos1.equals(highlightPos)) {
            return;
        }
        setCursorAndHighlightPos(cursorPos1, highlightPos1);
    }

    private void setNeedsRemakeTextLine() {
        cachedTextLines = null;
    }

    private void remakeTextLineIfNeeded(CGSize boundingSize, Font font) {
        if (cachedTextLines != null) {
            return;
        }
        int x = 0;
        int y = 0;
        int lineIndex = 0;
        int lineHeight = font.lineHeight + lineSpacing;
        int maxHeight = 0;

        NSRange selection = NSRange.of(cursorPos.value, highlightPos.value);

        List<TextLine> lines = split(value, selection, font, boundingSize.width);
        for (TextLine line : lines) {
            int width = font.width(line.chars);
            if (lineIndex != line.index) {
                lineIndex = line.index;
                y += maxHeight;
                x = 0;
            }
            line.rect = new CGRect(x, y, width, lineHeight);
            maxHeight = lineHeight;
            x += width;
        }

        CGRect lastLineRect = new CGRect(0, y + maxHeight, 0, lineHeight);
        remakeHighlightedLines(lines, boundingSize, lastLineRect);
        cursorRect = cursorRectAtIndex(cursorPos, lines, lastLineRect);

        cachedTextLines = lines;
        cachedFont = font;

        sizeDidChange.accept(cursorRect, new CGSize(x, Math.max(lastLineRect.y, lineHeight)));
    }

    private void remakeHighlightedLines(List<TextLine> lines, CGSize boundingSize, CGRect lastLineRect) {
        if (highlightPos.equals(cursorPos)) {
            highlightedRects = null;
            return;
        }
        CGPoint startPoint = pointAtIndex(min(cursorPos, highlightPos), lines, lastLineRect, false);
        CGPoint endPoint = pointAtIndex(max(cursorPos, highlightPos), lines, lastLineRect, false);
        if (startPoint.y == endPoint.y) {
            CGRect rect = new CGRect(startPoint.x, startPoint.y, endPoint.x - startPoint.x, lastLineRect.height);
            highlightedRects = Collections.singletonList(rect);
            return;
        }
        ArrayList<CGRect> rects = new ArrayList<>();
        rects.add(new CGRect(startPoint.x, startPoint.y, boundingSize.width - startPoint.x, lastLineRect.height));
        float my = startPoint.y + lastLineRect.height;
        if (my != endPoint.y) {
            rects.add(new CGRect(0, my, boundingSize.width, endPoint.y - my));
        }
        rects.add(new CGRect(0, endPoint.y, endPoint.x, lastLineRect.height));
        highlightedRects = rects;
    }

    private NSRange selectionRange() {
        return NSRange.of(cursorPos.value, highlightPos.value);
    }

    private String formattedString(String value) {
        if (isMultipleLineMode()) {
            return value;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (!isAllowedChatCharacter(c)) continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private CGPoint pointAtIndex(NSTextPosition pos, Iterable<TextLine> lines, CGRect lastLineRect, boolean enabledBackward) {
        // If you are backward mode, we preferentially check endIndex.
        if (pos.isBackward && enabledBackward) {
            for (TextLine line : lines) {
                if (line.range.endIndex() == pos.value) {
                    return line.endPoint();
                }
            }
        }
        for (TextLine line : lines) {
            if (line.range.startIndex() == pos.value) {
                return line.startPoint();
            }
        }
        for (TextLine line : lines) {
            if (line.range.endIndex() == pos.value) {
                return line.endPoint();
            }
        }
        return new CGPoint(lastLineRect.x, lastLineRect.y);
    }

    private CGRect cursorRectAtIndex(NSTextPosition pos, Iterable<TextLine> lines, CGRect lastLineRect) {
        CGPoint point = pointAtIndex(pos, lines, lastLineRect, true);
        return new CGRect(point.x, point.y - 2, 1, lastLineRect.height + 2);
    }

    private int defaultPlaceholderColor() {
        if (placeholderColor != null) {
            return placeholderColor.getRGB();
        }
        return 0xff333333;
    }

    private int defaultTextColor() {
        if (textColor != null) {
            return textColor.getRGB();
        }
        return 0xffffffff;
    }

    private Font defaultFont() {
        if (font != null) {
            return font.impl();
        }
        return Minecraft.getInstance().font;
    }

    private List<TextLine> split(String value, Font font, float maxWidth) {
        if (value.isEmpty()) {
            return Collections.emptyList();
        }
        if (maxWidth == 0) {
            return Collections.singletonList(new TextLine(0, 0, value.length(), value));
        }
        ArrayList<TextLine> lines = new ArrayList<>();
        font.getSplitter().splitLines(value, (int) maxWidth, Style.EMPTY, false, (style, startIndex, endIndex) -> {
            lines.add(new TextLine(lines.size(), startIndex, endIndex, value.substring(startIndex, endIndex)));
        });
        return lines;
    }

    private ArrayList<TextLine> split(String value, NSRange selection, Font font, float maxWidth) {
        List<TextLine> wrappedTextLines = split(value, font, maxWidth);
        if (wrappedTextLines.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<TextLine> resolvedTextLines = new ArrayList<>(wrappedTextLines.size() + 2);
        for (TextLine line : wrappedTextLines) {
            // a special case when has a blank line we can't to split it.
            if (line.range.intersects(selection) && !line.range.isEmpty()) {
                // split
                int lineIndex = line.index;
                int leftStartIndex = line.range.startIndex();
                int leftEndIndex = Math.max(leftStartIndex, selection.startIndex());
                int rightEndIndex = line.range.endIndex();
                int rightStartIndex = Math.min(selection.endIndex(), rightEndIndex);
                if (leftStartIndex != leftEndIndex) {
                    // left part
                    resolvedTextLines.add(new TextLine(lineIndex, leftStartIndex, leftEndIndex, value.substring(leftStartIndex, leftEndIndex)));
                }
                if (leftEndIndex != rightStartIndex) {
                    // highlight part
                    resolvedTextLines.add(new TextLine(lineIndex, leftEndIndex, rightStartIndex, value.substring(leftEndIndex, rightStartIndex)));
                }
                if (rightStartIndex != rightEndIndex) {
                    // right part
                    resolvedTextLines.add(new TextLine(lineIndex, rightStartIndex, rightEndIndex, value.substring(rightStartIndex, rightEndIndex)));
                }
                continue;
            }
            resolvedTextLines.add(line);
        }
        return resolvedTextLines;
    }

    NSTextPosition clamp(NSTextPosition value, NSTextPosition minValue, NSTextPosition maxValue) {
        if (value.value < minValue.value) {
            return minValue;
        }
        if (value.value > maxValue.value) {
            return maxValue;
        }
        return value;
    }

    NSTextPosition min(NSTextPosition value, NSTextPosition value1) {
        if (value.value < value1.value) {
            return value;
        }
        return value1;
    }

    NSTextPosition max(NSTextPosition value, NSTextPosition value1) {
        if (value.value > value1.value) {
            return value;
        }
        return value1;
    }

    void moveCursorTo(TextTokenizer tokenizer, int count, boolean selectMode) {
        int index = tokenizer.advance(value, cursorPos.value, count);
        moveCursorTo(NSTextPosition.forward(index), selectMode);
    }

    void moveCursorTo(NSTextPosition pos, boolean selectMode) {
        if (selectMode) {
            setCursorAndHighlightPos(pos, highlightPos);
        } else {
            setCursorAndHighlightPos(pos, pos);
        }
    }

    boolean isAllowedChatCharacter(char c) {
        return c != '\u00a7' && c >= ' ' && c != '\u007f';
    }

    interface TextTokenizer {

        TextTokenizer WORLD_AFTER = (value, index, step) -> {
            for (int k = 0; k < step; ++k) {
                int l = value.length();
                index = value.indexOf(' ', index);
                if (index == -1) {
                    index = l;
                } else {
                    while (index < l && value.charAt(index) == ' ') {
                        ++index;
                    }
                }
            }
            return index;
        };

        TextTokenizer WORLD_BEFORE = (value, index, step) -> {
            for (int k = 0; k < step; ++k) {
                while (index > 0 && value.charAt(index - 1) == ' ') {
                    --index;
                }
                while (index > 0 && value.charAt(index - 1) != ' ') {
                    --index;
                }
            }
            return index;
        };

        TextTokenizer CHAR_AFTER = (value, index, step) -> {
            int length = value.length();
            for (int k = 0; index < length && k < step; ++k) {
                if (Character.isHighSurrogate(value.charAt(index++)) && index < length && Character.isLowSurrogate(value.charAt(index))) {
                    ++index;
                }
            }
            return index;
        };

        TextTokenizer CHAR_BEFORE = (value, index, step) -> {
            for (int k = 0; index > 0 && k < step; ++k) {
                --index;
                if (Character.isLowSurrogate(value.charAt(index)) && index > 0 && Character.isHighSurrogate(value.charAt(index - 1))) {
                    --index;
                }
            }
            return index;
        };

        int advance(String value, int index, int step);
    }

    static class TextLine {

        final int index;

        final String text;
        final FormattedCharSequence chars;
        final NSRange range;

        CGRect rect = CGRect.ZERO;

        TextLine(int index, int startIndex, int endIndex, String text) {
            this.text = text;
            this.chars = FormattedCharSequence.forward(text, Style.EMPTY);
            this.index = index;
            this.range = new NSRange(startIndex, endIndex - startIndex);
        }

        boolean insideAtX(float x) {
            return rect.getMinX() <= x && x < rect.getMaxX();
        }

        boolean insideAtY(float y) {
            return rect.getMinY() <= y && y < rect.getMaxY();
        }

        CGPoint startPoint() {
            return new CGPoint(rect.x, rect.y);
        }

        CGPoint endPoint() {
            return new CGPoint(rect.x + rect.width, rect.y);
        }
    }
}
