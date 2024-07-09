package moe.plushie.armourers_workshop.core.skin.molang.functions;

import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;

/**
 * Abstract function class This class provides function capability (i.e. giving it arguments and upon {@link #get()}
 * method you receive output).
 */
public abstract class Function implements IMathValue {

    protected String name;
    protected IMathValue[] args;

    protected Function(IMathValue[] values, String name) throws Exception {
        if (values.length < getRequiredArguments()) {
            var message = String.format(
                    "Function '%s' requires at least %s arguments. %s are given!",
                    this.getName(),
                    this.getRequiredArguments(),
                    values.length
            );

            throw new Exception(message);
        }
        this.args = values;
        this.name = name;
    }

    /**
     * Get the value of nth argument
     */
    public double getArg(int index) {
        if (index < 0 || index >= this.args.length) {
            return 0;
        }
        return this.args[index].get();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString() {
        var argsBuilder = new StringBuilder();
        for (int i = 0; i < this.args.length; i++) {
            argsBuilder.append(this.args[i].toString());
            if (i < this.args.length - 1) {
                argsBuilder.append(", ");
            }
        }
        return this.getName() + "(" + argsBuilder + ")";
    }

    /**
     * Get name of this function
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get minimum count of arguments this function needs
     */
    public int getRequiredArguments() {
        return 0;
    }

    /**
     * A function that has the same parameters and the same results call pure function.
     */
    public abstract static class Pure extends Function {

        protected Pure(IMathValue[] values, String name) throws Exception {
            super(values, name);
        }

        @Override
        public boolean isConstant() {
            for (var arg : args) {
                if (!arg.isConstant()) {
                    return false;
                }
            }
            return true;
        }
    }
}
