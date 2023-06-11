package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSTextPosition;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class TextInputImpl {

    public Predicate<String> returnHandler;

    private boolean isEditable = true;
    private CGRect lastUserCursorRect;

    private final TextStorageImpl storage;

    public TextInputImpl(TextStorageImpl storage) {
        this.storage = storage;
    }

    public boolean mouseDown(CGPoint point) {
        int tx = Math.max(point.x - storage.offset.x, 0);
        int ty = Math.max(point.y - storage.offset.y, 0);
        NSTextPosition pos = storage.positionAtPoint(new CGPoint(tx, ty));
        if (pos != null) {
            storage.moveCursorTo(pos, KeyboardManagerImpl.hasShiftDown());
        }
        return false;
    }

    public boolean keyDown(int key) {
        // some methods may rely on this info.
        boolean hasShiftDown = KeyboardManagerImpl.hasShiftDown();
        boolean hasControlDown = KeyboardManagerImpl.hasControlDown();
        // each input causes the user cursor to reset, even if it doesn't.
        CGRect userCursorRect = lastUserCursorRect;
        lastUserCursorRect = null;
        // select all text
        if (KeyboardManagerImpl.isSelectAll(key)) {
            storage.setCursorAndHighlightPos(storage.endOfDocument(), storage.beginOfDocument());
            return true;
        }
        // cut selected text.
        if (KeyboardManagerImpl.isCut(key)) {
            KeyboardManagerImpl.setClipboard(storage.highlightedText());
            if (isEditable) {
                storage.insertText("");
            }
            return true;
        }
        // copy selected text
        if (KeyboardManagerImpl.isCopy(key)) {
            KeyboardManagerImpl.setClipboard(storage.highlightedText());
            return true;
        }
        // paste some text into selected range.
        if (KeyboardManagerImpl.isPaste(key)) {
             if (isEditable) {
                storage.insertText(KeyboardManagerImpl.getClipboard());
            }
            return true;
        }
        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE: {
                if (isEditable) {
                    if (hasControlDown) {
                        storage.deleteText(TextStorageImpl.TextTokenizer.WORLD_BEFORE, 1);
                    } else {
                        storage.deleteText(TextStorageImpl.TextTokenizer.CHAR_BEFORE, 1);
                    }
                }
                return true;
            }
            case GLFW.GLFW_KEY_DELETE: {
                if (isEditable) {
                    if (hasControlDown) {
                        storage.deleteText(TextStorageImpl.TextTokenizer.WORLD_AFTER, 1);
                    } else {
                        storage.deleteText(TextStorageImpl.TextTokenizer.CHAR_AFTER, 1);
                    }
                }
                return true;
            }
            case GLFW.GLFW_KEY_INSERT:
            case GLFW.GLFW_KEY_PAGE_UP:
            case GLFW.GLFW_KEY_PAGE_DOWN: {
                return false;
            }
            case GLFW.GLFW_KEY_DOWN: {
                if (storage.isMultipleLineMode()) {
                    moveToNextLine(userCursorRect, 1, hasShiftDown);
                }
                return false;
            }
            case GLFW.GLFW_KEY_UP: {
                if (storage.isMultipleLineMode()) {
                    moveToNextLine(userCursorRect, -1, hasShiftDown);
                }
                return false;
            }
            case GLFW.GLFW_KEY_RIGHT: {
                if (hasControlDown) {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.WORLD_AFTER, 1, hasShiftDown);
                } else {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.CHAR_AFTER, 1, hasShiftDown);
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT: {
                if (hasControlDown) {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.WORLD_BEFORE, 1, hasShiftDown);
                } else {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.CHAR_BEFORE, 1, hasShiftDown);
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME: {
                storage.moveCursorTo(storage.beginOfDocument(), hasShiftDown);
                return true;
            }
            case GLFW.GLFW_KEY_END: {
                storage.moveCursorTo(storage.endOfDocument(), hasShiftDown);
                return true;
            }
            case GLFW.GLFW_KEY_ENTER: {
                if (storage.isMultipleLineMode()) {
                    storage.insertText("\n");
                    return true;
                }
                if (returnHandler != null && returnHandler.test(storage.value())) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean charTyped(char ch) {
        if (storage.isAllowedChatCharacter(ch)) {
            storage.insertText(Character.toString(ch));
            return true;
        }
        return false;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    private void moveToNextLine(CGRect userCursorRect, int step, boolean selectMode) {
        CGRect rect = storage.cursorRect();
        if (userCursorRect == null) {
            userCursorRect = rect;
        }
        if (rect == null) {
            return;
        }
        lastUserCursorRect = userCursorRect;
        NSTextPosition pos = storage.beginOfDocument();
        int ty = rect.getMidY() + rect.height * step;
        if (ty >= 0) {
            pos = storage.positionAtPoint(new CGPoint(userCursorRect.x, ty));
        }
        if (pos != null) {
            storage.moveCursorTo(pos, selectMode);
        }
    }
}
