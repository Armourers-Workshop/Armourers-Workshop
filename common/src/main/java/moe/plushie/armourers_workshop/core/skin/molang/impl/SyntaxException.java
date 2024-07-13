package moe.plushie.armourers_workshop.core.skin.molang.impl;


import java.io.IOException;

/**
 * Exception that can be thrown during the
 * parsing phase
 */
public class SyntaxException extends IOException {

    private final Cursor cursor;

    public SyntaxException(Cursor cursor) {
        this.cursor = cursor;
    }

    public SyntaxException(String message, Cursor cursor) {
        super(appendCursor(message, cursor));
        this.cursor = cursor;
    }

    public SyntaxException(Throwable cause, Cursor cursor) {
        super(cause);
        this.cursor = cursor;
    }

    public SyntaxException(String message, Throwable cause, Cursor cursor) {
        super(appendCursor(message, cursor), cause);
        this.cursor = cursor;
    }

    public Cursor cursor() {
        return cursor;
    }

    private static String appendCursor(String message, Cursor cursor) {
        if (cursor == null) return message; // todo
        // default format for exception messages, i.e.
        // "unexpected token: '%'"
        // "    at line 2, column 6"
        return message + "\n\tat " + cursor.toString();
    }
}
