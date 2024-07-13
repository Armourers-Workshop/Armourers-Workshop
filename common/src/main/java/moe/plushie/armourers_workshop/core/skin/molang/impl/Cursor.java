package moe.plushie.armourers_workshop.core.skin.molang.impl;

import java.util.Objects;

/**
 * Mutable class that tracks the position of characters
 * when performing lexical analysis
 *
 * <p>Can be used to show the position of lexical errors
 * in a human-readable way</p>
 */
public final class Cursor {

    private int index = 0;
    private int line = 1;
    private int column = 0;

    public Cursor() {
    }

    public Cursor(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    public int index() {
        return index;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public void push(final int character) {
        index++;
        if (character == '\n') {
            // if it's a line break,
            // reset the column
            line++;
            column = 1;
        } else {
            column++;
        }
    }

    public Cursor copy() {
        return new Cursor(line, column);
    }

    @Override
    public String toString() {
        return "line " + line + ", column " + column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cursor that)) return false;
        return line == that.line && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }
}
