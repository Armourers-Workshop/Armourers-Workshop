package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.ArrayAccess;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Call;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Literal;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Return;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Statement;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.StructAccess;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Ternary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Unary;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.ObjectBinding;

import java.util.ArrayList;

/**
 * Utility class for parsing and utilising MoLang functions and expressions
 *
 * @see <a href="https://bedrock.dev/docs/1.19.0.0/1.19.30.23/Molang#Math%20Functions">Bedrock Dev - Molang</a>
 */
public class Compiler {

    protected final ObjectBinding bindings;
    protected final Optimizer optimizer;

    public Compiler(ObjectBinding bindings) {
        this.bindings = bindings;
        this.optimizer = new Optimizer();
    }

    /**
     * Compile Molang code to a {@link Expression}.
     *
     * @return A compiled and optimized {@link Expression}.
     */
    public Expression compile(String source) throws SyntaxException {
        var expression = parseAll(source);
        //expression = linker.transform(expression);
        expression = optimizer.transform(expression);
        return expression;
    }

    /**
     * A wrapper around the expression parsing system to optionally support Molang-specific handling for things like compound expressions
     *
     * @param source The math and/or Molang expression to be parsed
     * @return A compiled {@link Expression}, ready for use
     */
    private Expression parseAll(String source) throws SyntaxException {
        var lexer = new Lexer(source);
        var expressions = new ArrayList<Expression>();
        while (true) {
            var token = lexer.next();
            if (token.kind() == Lexer.Kind.EOF) {
                // reached end-of-file!
                break;
            }
            if (token.kind() == Lexer.Kind.ERROR) {
                // tokenization error!
                throw new SyntaxException("Found an invalid token (error): " + token.value(), lexer.cursor());
            }

            var expr = parseCompoundExpression(lexer, 0);

            // check current token, should be a semicolon or an eof
            token = lexer.current();
            if (token.kind() != Lexer.Kind.EOF && token.kind() != Lexer.Kind.SEMICOLON) {
                throw new SyntaxException("Expected a semicolon, but was " + token, lexer.cursor());
            }
            expressions.add(expr);
        }

        // In simple cases, the terminating ; is omitted and the expression result is returned.
        if (expressions.size() == 1) {
            return expressions.get(0);
        }

        // In complex cases, multiple sub-expressions are each terminated with a semicolon ;.
        // Complex expressions evaluate to 0.0 unless there is a return statement,
        // in which case the evaluated value of the return's sub-expression will be returned out of the current scope.
        return new Compound(expressions);
    }

    /**
     * Parses a single expression.
     * Single expressions don't require a left-hand expression
     * to be parsed, e.g. literals, statements, identifiers,
     * wrapped expressions and execution scopes
     */
    private Expression parseSingle(Lexer lexer) throws SyntaxException {
        var token = lexer.current();
        return switch (token.kind()) {
            case NUMBER -> {
                lexer.next();
                yield new Constant(Double.parseDouble(token.value()));
            }
            case STRING -> {
                lexer.next();
                yield new Literal(token.value());
            }
            case TRUE -> {
                lexer.next();
                yield Constant.ONE;
            }
            case FALSE -> {
                lexer.next();
                yield Constant.ZERO;
            }
            case LPAREN -> {
                lexer.next();
                // wrapped expression: (expression)
                var expression = parseCompoundExpression(lexer, 0);
                token = lexer.current();
                if (token.kind() != Lexer.Kind.RPAREN) {
                    throw new SyntaxException("Non closed expression", lexer.cursor());
                }
                lexer.next();
                yield expression;
            }
            case LBRACE -> {
                token = lexer.next();
                var expressions = new ArrayList<Expression>();
                while (token.kind() != Lexer.Kind.RBRACE) {
                    expressions.add(parseCompoundExpression(lexer, 0));
                    token = lexer.current();
                    if (token.kind() == Lexer.Kind.RBRACE) {
                        break;
                    }
                    if (token.kind() == Lexer.Kind.EOF) {
                        // end reached but not closed yet, huh?
                        throw new SyntaxException("Found the end before the execution scope closing token", lexer.cursor());
                    }
                    if (token.kind() == Lexer.Kind.ERROR) {
                        throw new SyntaxException("Found an invalid token (error): " + token.value(), lexer.cursor());
                    }
                    if (token.kind() != Lexer.Kind.SEMICOLON) {
                        throw new SyntaxException("Missing semicolon", lexer.cursor());
                    }
                    token = lexer.next();
                }
                lexer.next();
                yield new Compound(expressions);
            }
            case BREAK -> {
                lexer.next();
                yield new Statement(Statement.Operator.BREAK);
            }
            case CONTINUE -> {
                lexer.next();
                yield new Statement(Statement.Operator.CONTINUE);
            }
            case IDENTIFIER -> {
                var expr = bindings.propertyByName(token.value());
                if (expr == null) {
                    throw new SyntaxException("Failed to get property: " + token.value(), lexer.cursor());
                }
                token = lexer.next();
                if (token.kind() == Lexer.Kind.DOT) {
                    token = lexer.next();
                    if (token.kind() != Lexer.Kind.IDENTIFIER) {
                        throw new SyntaxException("Unexpected token, expected a valid field token", lexer.cursor());
                    }
                    if (!(expr instanceof ObjectBinding parent)) {
                        throw new SyntaxException("Illegal access to: " + expr + "." + token.value(), lexer.cursor());
                    }
                    expr = parent.propertyByName(token.value());
                    if (expr == null) {
                        throw new SyntaxException("Failed to get property: " + parent + "." + token.value(), lexer.cursor());
                    }
                    lexer.next();
                }
                yield expr;
            }
            case SUB -> {
                lexer.next();
                var expr = parseCompoundExpression(lexer, Unary.Operator.ARITHMETICAL_NEGATION.precedence());
                // this should be a negative value.
                if (expr instanceof Constant constant) {
                    yield new Constant(-constant.value().doubleValue());
                }
                yield new Unary(Unary.Operator.ARITHMETICAL_NEGATION, expr);
            }
            case PLUS -> {
                lexer.next();
                yield parseCompoundExpression(lexer, Unary.Operator.ARITHMETICAL_PLUS.precedence());
            }
            case BANG -> {
                lexer.next();
                var expr = parseCompoundExpression(lexer, Unary.Operator.LOGICAL_NEGATION.precedence());
                yield new Unary(Unary.Operator.LOGICAL_NEGATION, expr);
            }
            case RETURN -> {
                lexer.next();
                var expr = parseCompoundExpression(lexer, 0);
                yield new Return(expr);
            }
            default -> {
                // what's happened?
                yield Constant.ZERO;
            }
        };
    }

    private Expression parseCompoundExpression(Lexer lexer, int lastPrecedence) throws SyntaxException {
        var expr = parseSingle(lexer);
        while (true) {
            var compoundExpr = parseCompound(lexer, expr, lastPrecedence);
            var current = lexer.current();
            if (current.kind() == Lexer.Kind.EOF || current.kind() == Lexer.Kind.SEMICOLON) {
                // found eof, stop parsing, return expr
                return compoundExpr;
            }
            if (compoundExpr == expr) {
                return expr;
            }
            expr = compoundExpr;
        }
    }

    private Expression parseCompound(Lexer lexer, Expression left, int lastPrecedence) throws SyntaxException {
        var token = lexer.current();
        switch (token.kind()) {
            case RPAREN:
            case EOF: {
                return left;
            }
            case DOT: { // STRUCT ACCESS EXPRESSION "left.name"
                var expr = left;
                while (token.kind() == Lexer.Kind.DOT) {
                    token = lexer.next();
                    if (token.kind() != Lexer.Kind.IDENTIFIER) {
                        throw new SyntaxException("Unexpected token, expected a valid field token", lexer.cursor());
                    }
                    expr = new StructAccess(expr, token.value());
                    token = lexer.next();
                }
                if (expr == left) {
                    throw new SyntaxException("Unexpected token, expected a valid field token", lexer.cursor());
                }
                return expr;
            }
            case LBRACKET: { // ARRAY ACCESS EXPRESSION: "left[index]"
                token = lexer.next();
                if (token.kind() == Lexer.Kind.RBRACKET) {
                    throw new SyntaxException("Expected a expression, got RBRACKET", lexer.cursor());
                }
                if (token.kind() == Lexer.Kind.EOF) {
                    throw new SyntaxException("Found EOF before closing RBRACKET", lexer.cursor());
                }
                var index = parseCompoundExpression(lexer, 0);
                token = lexer.current();
                if (token.kind() == Lexer.Kind.EOF) {
                    throw new SyntaxException("Found EOF before closing RBRACKET", lexer.cursor());
                }
                if (token.kind() != Lexer.Kind.RBRACKET) {
                    throw new SyntaxException("Expected a closing RBRACKET, found " + token, lexer.cursor());
                }
                lexer.next();
                return new ArrayAccess(left, index);
            }
            case LPAREN: { // CALL EXPRESSION: "left(arguments)"
                token = lexer.next();
                var arguments = new ArrayList<Expression>();

                // start reading the arguments
                if (token.kind() == Lexer.Kind.EOF) {
                    throw new SyntaxException("Found EOF before closing RPAREN", lexer.cursor());
                }

                // find all arguments.
                while (token.kind() != Lexer.Kind.RPAREN) {
                    arguments.add(parseCompoundExpression(lexer, 0));
                    // update token character
                    token = lexer.current();
                    if (token.kind() == Lexer.Kind.EOF) {
                        throw new SyntaxException("Found EOF before closing RPAREN", lexer.cursor());
                    }
                    if (token.kind() == Lexer.Kind.ERROR) {
                        throw new SyntaxException("Found error token: " + token.value(), lexer.cursor());
                    }
                    if (token.kind() == Lexer.Kind.RPAREN) {
                        break;
                    }
                    if (token.kind() != Lexer.Kind.COMMA) {
                        throw new SyntaxException("Expected a comma, got " + token.kind(), lexer.cursor());
                    }
                    lexer.next();
                }
                lexer.next();
                return new Call(left, arguments);
            }
            case QUES: { // QUES EXPRESSION: left ? true [: false]
                // must compute high precedence operations first.
                var conditionalPrecedence = Binary.Operator.CONDITIONAL.precedence();
                if (lastPrecedence > conditionalPrecedence) {
                    return left;
                }
                lexer.next();
                var trueValue = parseCompoundExpression(lexer, conditionalPrecedence);
                if (lexer.current().kind() == Lexer.Kind.COLON) {
                    // then it's a ternary expression, since there is a ':', indicating the next expression
                    lexer.next();
                    var falseValue = parseCompoundExpression(lexer, conditionalPrecedence);
                    return new Ternary(left, trueValue, falseValue);
                }
                return new Binary(Binary.Operator.CONDITIONAL, left, trueValue);
            }
            case ARROW: { // ARROW EXPRESSION: left->right
                lexer.next();
                // the arrow right side expression should ignore last precedence.
                var right = parseCompoundExpression(lexer, 0);
                return new Binary(Binary.Operator.ARROW, left, right);
            }
        }

        // check for binary expressions
        var op = switch (token.kind()) {
            case AMPAMP -> Binary.Operator.AND;
            case BARBAR -> Binary.Operator.OR;
            case LT -> Binary.Operator.LT;
            case LTE -> Binary.Operator.LTE;
            case GT -> Binary.Operator.GT;
            case GTE -> Binary.Operator.GTE;
            case PLUS -> Binary.Operator.ADD;
            case SUB -> Binary.Operator.SUB;
            case STAR -> Binary.Operator.MUL;
            case SLASH -> Binary.Operator.DIV;
            case MOD -> Binary.Operator.MOD;
            case POW -> Binary.Operator.POW;
            case QUESQUES -> Binary.Operator.NULL_COALESCE;
            case EQ -> Binary.Operator.ASSIGN;
            case EQEQ -> Binary.Operator.EQ;
            case BANGEQ -> Binary.Operator.NEQ;
            case PLUSEQ -> Binary.Operator.ADD_ASSIGN;
            case SUBEQ -> Binary.Operator.SUB_ASSIGN;
            case STAREQ -> Binary.Operator.MUL_ASSIGN;
            case SLASHEQ -> Binary.Operator.DIV_ASSIGN;
            case MODEQ -> Binary.Operator.MOD_ASSIGN;
            case POWEQ -> Binary.Operator.POW_ASSIGN;
            case QUESQUESEQ -> Binary.Operator.NULL_COALESCE_ASSIGN;
            default -> null;
        };

        if (op == null || lastPrecedence >= op.precedence()) {
            return left;
        }

        lexer.next();
        var right = parseCompoundExpression(lexer, op.precedence());
        return new Binary(op, left, right);
    }
}

