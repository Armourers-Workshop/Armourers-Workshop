package moe.plushie.armourers_workshop.core.skin.molang.function.misc;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.List;
import java.util.StringJoiner;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Print the debug log.
 */
public class Print extends Function {

    public Print(String name, List<Expression> arguments) {
        super(name, 1, arguments);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public double getAsDouble() {
        var contents = new StringJoiner(" ");
        for (var argument : arguments()) {
            contents.add(argument.getAsString());
        }
        ModLog.debug("{}", contents);
        return 0;
    }
}
