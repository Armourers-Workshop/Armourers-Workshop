package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.init.platform.VersionResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * iris
 * iris >= 1.8.12
 * !optifine
 * iris >= 1.8.0 && sodium
 * iris || optifine
 * iris >= 1.6 && (sodium || embeddium)
 * (!optifine && iris >= 1.8.12) || (sodium >= 0.5.0 && !indium)
 */
public class Evaluator {

    private final HashMap<String, Boolean> caches = new HashMap<>();
    private final Function<String, Optional<Version>> resolver;

    public Evaluator() {
        this(VersionResolver::getVersion);
    }

    public Evaluator(Function<String, Optional<Version>> resolver) {
        this.resolver = resolver;
    }

    public boolean eval(String condition) {
        // check the cache result first.
        var result = caches.get(condition);
        if (result != null) {
            return result;
        }
        var tokenizer = new Tokenizer(condition);
        var parser = new Parser(tokenizer.tokenize(), resolver);
        var expression = parser.parse();
        // execute and cache it.
        result = expression.getAsBoolean();
        caches.put(condition, result);
        return result;
    }

    private static class Parser {

        private final List<Token> tokens;
        private final Function<String, Optional<Version>> resolver;

        private int index = 0;

        public Parser(List<Token> tokens, Function<String, Optional<Version>> resolver) {
            this.tokens = tokens;
            this.resolver = resolver;
        }

        public BooleanSupplier parse() {
            var cur = (BooleanSupplier) null;
            while (current().kind() != Kind.EOF) {
                cur = parseSingle(cur);
            }
            if (cur == null) {
                return () -> true;
            }
            return cur;
        }

        public Token current() {
            return tokens.get(index);
        }

        public Token next() {
            return tokens.get(index++);
        }

        private BooleanSupplier parseSingle(BooleanSupplier left) {
            var cur = current();
            switch (cur.kind()) {
                case IDENTIFIER -> {
                    next();
                    var modId = cur.value();
                    var comparator = current().toComparator();
                    if (comparator == null) {
                        return () -> resolver.apply(modId).isPresent();
                    }
                    next();
                    var expected = Version.parse(current().value());
                    next();
                    return () -> {
                        var actual = resolver.apply(modId).orElse(null);
                        if (actual == null) {
                            return false;
                        }
                        return comparator.apply(actual.compareTo(expected));
                    };
                }
                case NOT -> {
                    next();
                    var value = parseSingle(null);
                    assertNonnull(value, "missing unary operator !!");
                    return () -> !value.getAsBoolean();
                }
                case LPAREN -> {
                    next();
                    return parse();
                }
                case AND -> {
                    next();
                    var right = parseSingle(null);
                    assertNonnull(left, "missing left operator !!");
                    assertNonnull(right, "missing right operator !!");
                    return () -> left.getAsBoolean() && right.getAsBoolean();
                }
                case OR -> {
                    next();
                    var right = parseSingle(null);
                    assertNonnull(left, "missing left operator !!");
                    assertNonnull(right, "missing right operator !!");
                    return () -> left.getAsBoolean() || right.getAsBoolean();
                }
                case RPAREN -> {
                    next();
                    return left;
                }
                default -> throw new RuntimeException("Unexpected token: " + cur);
            }
        }

        private void assertNonnull(Object object, String message) {
            if (object == null) {
                throw new RuntimeException(message);
            }
        }
    }

    private static class Tokenizer {

        private static final LinkedHashMap<Object, Kind> TEMPLATE = new LinkedHashMap<>();

        private final String source;
        private final int len;
        private int pos;

        protected Tokenizer(String source) {
            this.source = source;
            this.len = source.length();
        }

        public List<Token> tokenize() {
            var tokens = new ArrayList<Token>();
            while (pos < len) {
                var token = readToken();
                if (token != null) {
                    tokens.add(token);
                }
            }
            tokens.add(Token.END);
            return tokens;
        }

        private Token readToken() {
            for (var entry : TEMPLATE.entrySet()) {
                // match a pattern?
                if (entry.getKey() instanceof Pattern pattern) {
                    var matcher = pattern.matcher(source).region(pos, len);
                    if (!matcher.lookingAt()) {
                        continue;
                    }
                    pos = matcher.end();
                    if (entry.getValue() == null) {
                        return null;
                    }
                    var value = source.substring(matcher.start(), matcher.end());
                    return new Token(entry.getValue(), value);
                }
                // match a fixed string?
                if (entry.getKey() instanceof String pattern) {
                    if (source.startsWith(pattern, pos)) {
                        pos += pattern.length();
                        return new Token(entry.getValue(), null);
                    }
                }
            }
            return null;
        }

        static {
            TEMPLATE.put(Pattern.compile("\\s+"), null);
            TEMPLATE.put("&&", Kind.AND);
            TEMPLATE.put("||", Kind.OR);
            TEMPLATE.put(">=", Kind.GTE);
            TEMPLATE.put("<=", Kind.LTE);
            TEMPLATE.put("==", Kind.EQ);
            TEMPLATE.put("!=", Kind.NEQ);
            TEMPLATE.put(">", Kind.GT);
            TEMPLATE.put("<", Kind.LT);
            TEMPLATE.put("!", Kind.NOT);
            TEMPLATE.put("(", Kind.LPAREN);
            TEMPLATE.put(")", Kind.RPAREN);
            TEMPLATE.put(Pattern.compile("[0-9A-Za-z+\\-_.]+"), Kind.IDENTIFIER);
        }
    }

    private static class Token {

        public static final Token END = new Token(Kind.EOF, null);

        private final Kind kind;
        private final String value;

        public Token(Kind kind, String value) {
            this.kind = kind;
            this.value = value;
        }

        public Kind kind() {
            return kind;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            if (value != null) {
                return String.format("%s(%s)", kind, value);
            }
            return kind.toString();
        }

        ///  convert the kind into a comparator
        public Function<Integer, Boolean> toComparator() {
            return switch (kind) {
                case EQ -> it -> it == 0;
                case NEQ -> it -> it != 0;
                case GT -> it -> it > 0;
                case LT -> it -> it < 0;
                case GTE -> it -> it >= 0;
                case LTE -> it -> it <= 0;
                default -> null;
            };
        }
    }

    private enum Kind {
        IDENTIFIER, // mod id, mod version

        EQ,         // ==
        NEQ,        // !=
        GT,         // >
        LT,         // <
        GTE,        // >=
        LTE,        // <=

        NOT,        // !
        AND,        // &&
        OR,         // ||

        LPAREN,     // (
        RPAREN,     // )
        EOF
    }
}
