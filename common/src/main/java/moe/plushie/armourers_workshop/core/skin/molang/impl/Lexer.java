package moe.plushie.armourers_workshop.core.skin.molang.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class Lexer {

    // the source reader
    private final StringReader reader;

    // the current index
    private final Cursor cursor = new Cursor();

    // the next character to be checked
    private int next;

    // the current token
    private Token token = null;

    Lexer(final String source) {
        this.reader = new StringReader(source);
        this.next = read();
    }

    public Cursor cursor() {
        return cursor;
    }

    public Token current() {
        if (token == null) {
            throw new IllegalStateException("No current token, please call next() at least once");
        }
        return token;
    }

    public Token next() {
        return token = next0();
    }

    public void close() {
        reader.close();
    }

    private Token next0() {
        int c = next;

        // skip whitespace (including tabs and newlines)
        while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            c = read();
        }

        // additional spaces, lines, etc. at the end?
        if (c == -1) {
            // EOF reached
            return new Token(TokenKind.EOF, null, cursor.index(), cursor.index() + 1);
        }

        // read integer or float.
        int start = cursor.index();
        if (Character.isDigit(c)) {
            // first char is a digit, continue reading number
            StringBuilder builder = new StringBuilder(8);
            builder.appendCodePoint(c);
            while (Character.isDigit(c = read())) {
                builder.appendCodePoint(c);
            }

            if (c == '.') {
                builder.append('.');
                while (Character.isDigit(c = read())) {
                    builder.appendCodePoint(c);
                }
            }

            return new Token(TokenKind.NUMBER, builder.toString(), start, cursor.index());
        }

        // read keyword or identifier.
        if (isValidForWordStart(c)) { // [A-z_]
            // may be an identifier or a keyword
            StringBuilder builder = new StringBuilder();
            do {
                builder.appendCodePoint(c);
            } while (isValidForWordContinuation(c = read())); // [A-z_0-9]

            String word = builder.toString();
            TokenKind kind = switch (word.toLowerCase()) {
                case "break" -> TokenKind.BREAK;
                case "continue" -> TokenKind.CONTINUE;
                case "return" -> TokenKind.RETURN;
                case "true" -> TokenKind.TRUE;
                case "false" -> TokenKind.FALSE;
                default -> TokenKind.IDENTIFIER;
            };

            // keywords do not have values
            if (kind != TokenKind.IDENTIFIER) {
                word = null;
            }

            return new Token(kind, word, start, cursor.index());
        }

        // read single quote strings.
        if (c == '\'') {
            StringBuilder value = new StringBuilder(16);
            while (true) {
                c = read();
                if (c == -1) {
                    // the heck? you didn't close the string
                    return new Token(TokenKind.ERROR, "Found end-of-file before closing quote", start, cursor.index());
                }
                if (c == '\'') {
                    // string was closed!
                    break;
                }
                // TODO: should we allow escaping quotes? should we disallow line breaks?
                // not end of file nor quote, this is inside the string literal
                value.appendCodePoint(c);
            }
            // Here, "c" should be a quote, so skip it and give it to the next person
            read();
            return new Token(TokenKind.STRING, value.toString(), start, cursor.index());
        }

        // here we are sure that "c" is NOT:
        // - EOF
        // - Single Quote (')
        // - A-Za-z_
        // - 0-9
        // so it must be some sign like ?, *, +, -
        int c1 = -2; // only set if "c" may have a continuation, for example "==", "!=", "??"
        String value = null; // only set of token kind = ERROR, value is error message
        TokenKind tokenKind = switch (c) {
            case '!' -> {
                c1 = read();
                if (c1 == '=') {
                    read();
                    yield TokenKind.BANGEQ;
                } else {
                    yield TokenKind.BANG;
                }
            }
            case '&' -> {
                c1 = read();
                if (c1 == '&') {
                    read();
                    yield TokenKind.AMPAMP;
                } else {
                    value = "Unexpected token '" + ((char) c1) + "', expected '&' (Molang doesn't support bitwise operators)";
                    yield TokenKind.ERROR;
                }
            }
            case '|' -> {
                c1 = read();
                if (c1 == '|') {
                    read();
                    yield TokenKind.BARBAR;
                } else {
                    value = "Unexpected token '" + ((char) c1) + "', expected '|' (Molang doesn't support bitwise operators)";
                    yield TokenKind.ERROR;
                }
            }
            case '<' -> {
                c1 = read();
                if (c1 == '=') {
                    read();
                    yield TokenKind.LTE;
                } else {
                    yield TokenKind.LT;
                }
            }
            case '>' -> {
                c1 = read();
                if (c1 == '=') {
                    read();
                    yield TokenKind.GTE;
                } else {
                    yield TokenKind.GT;
                }
            }
            case '=' -> {
                c1 = read();
                if (c1 == '=') {
                    read();
                    yield TokenKind.EQEQ;
                } else {
                    yield TokenKind.EQ;
                }
            }
            case '-' -> {
                c1 = read();
                if (c1 == '>') {
                    read();
                    yield TokenKind.ARROW;
                } else {
                    yield TokenKind.SUB;
                }
            }
            case '?' -> {
                c1 = read();
                if (c1 == '?') {
                    read();
                    yield TokenKind.QUESQUES;
                } else {
                    yield TokenKind.QUES;
                }
            }
            case '/' -> TokenKind.SLASH;
            case '*' -> TokenKind.STAR;
            case '%' -> TokenKind.MOD;
            case '^' -> TokenKind.POW;
            case '+' -> TokenKind.PLUS;
            case ',' -> TokenKind.COMMA;
            case '.' -> TokenKind.DOT;
            case '(' -> TokenKind.LPAREN;
            case ')' -> TokenKind.RPAREN;
            case '{' -> TokenKind.LBRACE;
            case '}' -> TokenKind.RBRACE;
            case ':' -> TokenKind.COLON;
            case '[' -> TokenKind.LBRACKET;
            case ']' -> TokenKind.RBRACKET;
            case ';' -> TokenKind.SEMICOLON;
            case '"' -> {
                value = "Unexpected token '\"', expected single quote (') to start a string literal";
                yield TokenKind.ERROR;
            }
            default -> {
                // "c" is something we don't know about!
                value = "Unexpected token '" + ((char) c) + "': invalid token";
                yield TokenKind.ERROR;
            }
        };

        if (c1 == -2) {
            // if token kind was known and the token didn't
            // check for an extra character
            read();
        }

        return new Token(tokenKind, value, start, cursor.index());
    }

    private int read() {
        try {
            int c = reader.read();
            cursor.push(c);
            next = c;
            return c;
        } catch (IOException e) {
            return -1;
        }
    }

    private boolean isValidForWordStart(final int c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_';
    }

    private boolean isValidForWordContinuation(final int c) {
        return isValidForWordStart(c) || Character.isDigit(c);
    }

    /**
     * Class representing a Molang token. Each token has some
     * information set by the lexer (i.e. start/end position,
     * token kind and optional value)
     */
    public static final class Token {
        private final TokenKind kind;
        private final String value;
        private final int start;
        private final int end;

        public Token(TokenKind kind, String value, int start, int end) {
            this.kind = kind;
            this.value = value;
            this.start = start;
            this.end = end;
        }

        /**
         * Gets the token kind.
         *
         * @return The token kind
         */
        public TokenKind kind() {
            return kind;
        }

        /**
         * Gets the token value. Null if this kind
         * of tokens doesn't allow values.
         *
         * @return The token value
         */
        public String value() {
            return value;
        }

        /**
         * Gets the start index of this token.
         *
         * @return The token start
         */
        public int start() {
            return start;
        }

        /**
         * Gets the end index of this token.
         *
         * @return The token end
         */
        public int end() {
            return end;
        }

        @Override
        public String toString() {
            if (kind.hasTag(TokenKind.Tag.HAS_VALUE)) {
                return kind + "(" + value + ")";
            } else {
                return kind.toString();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Token that)) return false;
            return start == that.start && end == that.end && Objects.equals(kind, that.kind) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kind, value, start, end);
        }
    }

    /**
     * An enum of token kinds. Represents a single token kind.
     *
     * <p>Tokens are, commonly, a sequence of one or more continuous
     * characters, like "??", "->", "!", "true", "1.0", "2.0", ...</p>
     *
     * <p>Tokens do not have an specific behavior, they just group
     * certain characters that can be used by the parser.</p>
     */
    public enum TokenKind {
        /**
         * End-of-file token, means that the end was reached
         */
        EOF,

        /**
         * Error token, means that there was an error there
         */
        ERROR(Tag.HAS_VALUE),

        /**
         * Identifier token, has a string value of the identifier name
         */
        IDENTIFIER(Tag.HAS_VALUE),

        /**
         * String literal token, has a string value of its content
         */
        STRING(Tag.HAS_VALUE),

        /**
         * Number literal token, has a string value of its content,
         * which can be parsed to a floating-point number
         */
        NUMBER(Tag.HAS_VALUE),

        /**
         * 'True' literal boolean token
         */
        TRUE,

        /**
         * 'False' literal boolean token
         */
        FALSE,

        /**
         * The "break" keyword
         */
        BREAK,

        /**
         * The "continue" keyword
         */
        CONTINUE,

        /**
         * The "return" keyword
         */
        RETURN,

        /**
         * The dot symbol (.)
         */
        DOT,

        /**
         * The bang or exclamation symbol (!)
         */
        BANG,

        /**
         * Double ampersand token (&&)
         */
        AMPAMP,

        /**
         * Double bar token (||)
         */
        BARBAR,

        /**
         * Less-than token (<)
         */
        LT,

        /**
         * Less-than-or-equal token (<=)
         */
        LTE,

        /**
         * Greater-than token (>)
         */
        GT,

        /**
         * Greater-than-or-equal token (>=)
         */
        GTE,

        /**
         * Equal symbol (=)
         */
        EQ,

        /**
         * Equal-equal token (==)
         */
        EQEQ,

        /**
         * Bang-eq token (!=)
         */
        BANGEQ,

        /**
         * Star symbol (*)
         */
        STAR,

        /**
         * Slash symbol (/)
         */
        SLASH,

        /**
         * Plus symbol (+)
         */
        PLUS,

        /**
         * Hyphen/sub symbol (-)
         */
        SUB,


        /**
         * Mod symbol (%)
         */
        MOD,

        /**
         * Power symbol (^)
         */
        POW,

        /**
         * Left-parenthesis symbol "("
         */
        LPAREN,

        /**
         * Right-parenthesis symbol ")"
         */
        RPAREN,

        /**
         * Left-brace symbol "{"
         */
        LBRACE,

        /**
         * Right-brace symbol "}"
         */
        RBRACE,

        /**
         * Question-question token (??)
         */
        QUESQUES,

        /**
         * Question symbol (?)
         */
        QUES,

        /**
         * Colon symbol (:)
         */
        COLON,

        /**
         * Arrow token (->)
         */
        ARROW,

        /**
         * Left-bracket token "["
         */
        LBRACKET,

        /**
         * Right-bracket "]
         */
        RBRACKET,

        /**
         * Comma symbol (,)
         */
        COMMA,

        /**
         * Semicolon symbol (;)
         */
        SEMICOLON;

        private final Set<Tag> tags;

        TokenKind(final Tag... tags) {
            this.tags = EnumSet.copyOf(Arrays.asList(tags));
        }

        TokenKind() {
            this.tags = Collections.emptySet();
        }

        /**
         * Determines if this token kind has a certain
         * tag.
         *
         * @param tag The tag to check.
         * @return True if this token kind is tagged with
         * the given tag
         */
        public boolean hasTag(final Tag tag) {
            return tags.contains(tag);
        }

        /**
         * An enum of tags for token kinds. Tags specify
         * certain features of token kinds.
         */
        public enum Tag {
            /**
             * A token kind with HAS_VALUE tag will have a variable value,
             * for example, double or string literal tokens have variable
             * values, but they are still parsed with the same token kind.
             */
            HAS_VALUE
        }
    }
}
