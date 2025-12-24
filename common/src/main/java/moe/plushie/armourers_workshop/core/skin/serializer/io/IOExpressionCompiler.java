package moe.plushie.armourers_workshop.core.skin.serializer.io;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

@FunctionalInterface
public interface IOExpressionCompiler {

    Expression compile(String value) throws Exception;

    default Expression compile(OpenPrimitive value, double defaultValue) {
        try {
            if (value.isNumber()) {
                return new Constant(value.doubleValue());
            }
            if (value.isString()) {
                return compile(value.stringValue());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Constant(defaultValue);
    }
}
