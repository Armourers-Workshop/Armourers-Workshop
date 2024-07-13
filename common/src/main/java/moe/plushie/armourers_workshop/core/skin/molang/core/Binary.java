package moe.plushie.armourers_workshop.core.skin.molang.core;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * A computed value of argA and argB defined by the contract of the {@link Op}
 */
public final class Binary implements Expression {

    private final Op op;
    private final Expression lhs;
    private final Expression rhs;

    public Binary(Op op, Expression lhs, Expression rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public boolean isMutable() {
        return lhs.isMutable() || rhs.isMutable();
    }

    @Override
    public double getAsDouble() {
        return op.compute(lhs, rhs);
    }

    @Override
    public String toString() {
        return lhs + " " + op.symbol() + " " + rhs;
    }

    public enum Op {

        AND("&&", 300) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs != 0 && rhs != 0 ? 1 : 0;
            }
        },
        OR("||", 200) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs != 0 || rhs != 0 ? 1 : 0;
            }
        },
        LT("<", 700) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs < rhs ? 1 : 0;
            }
        },
        LTE("<=", 700) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs <= rhs ? 1 : 0;
            }
        },
        GT(">", 700) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs > rhs ? 1 : 0;
            }
        },
        GTE(">=", 700) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs >= rhs ? 1 : 0;
            }
        },
        ADD("+", 900) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs + rhs;
            }
        },
        SUB("-", 900) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs - rhs;
            }
        },
        MUL("*", 1000) {
            @Override
            public double compute(double lhs, double rhs) {
                return lhs * rhs;
            }
        },
        DIV("/", 1000) {
            @Override
            public double compute(double lhs, double rhs) {
                return rhs == 0 ? lhs : lhs / rhs;
            }
        },
        MOD("%", 1000) {
            @Override
            public double compute(double lhs, double rhs) {
                return rhs == 0 ? lhs : lhs % rhs;
            }
        },
        POW("^", 1500) {
            @Override
            public double compute(double lhs, double rhs) {
                return Math.pow(lhs, rhs);
            }
        },
        ARROW("->", 2000) {
            @Override
            public double compute(Expression lhs, Expression rhs) {
                // TODO: ?
                return 0;
            }
        },
        NULL_COALESCE("??", 2) {
            @Override
            public double compute(Expression lhs, Expression rhs) {
                // TODO: ?
                return 0;
            }
        },
        ASSIGN("=", 1) {
            @Override
            public double compute(Expression lhs, Expression rhs) {
                if (lhs instanceof Variable variable) {
                    variable.set(rhs);
                }
                return 0;
            }
        },
        CONDITIONAL("?", 1) {
            @Override
            public double compute(Expression lhs, Expression rhs) {
                return lhs.getAsDouble() != 0 ? rhs.getAsDouble() : 0;
            }
        },
        EQ("==", 500) {
            @Override
            public double compute(double lhs, double rhs) {
                return Math.abs(lhs - rhs) < 0.00001 ? 1 : 0;
            }
        },
        NEQ("!=", 500) {
            @Override
            public double compute(double lhs, double rhs) {
                return Math.abs(lhs - rhs) >= 0.00001 ? 1 : 0;
            }
        };

        private final String symbol;
        private final int precedence;

        Op(final String symbol, final int precedence) {
            this.symbol = symbol;
            this.precedence = precedence;
        }

        public String symbol() {
            return symbol;
        }

        public int precedence() {
            return precedence;
        }

        /**
         * Computing the mathematical result of two input arguments
         *
         * @param lhs The first input argument
         * @param rhs The second input argument
         * @return The computed value of the two inputs
         */
        public double compute(double lhs, double rhs) {
            return 0;
        }

        /**
         * Computing the mathematical result of two input arguments
         *
         * @param lhs The first input argument
         * @param rhs The second input argument
         * @return The computed value of the two inputs
         */
        public double compute(Expression lhs, Expression rhs) {
            return compute(lhs.getAsDouble(), rhs.getAsDouble());
        }
    }
}
