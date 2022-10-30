package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.init.ModLog;

import java.util.function.BiFunction;

public class AbstractProgramPreprocessor {

    private final String prefix;

    public AbstractProgramPreprocessor(String prefix) {
        this.prefix = prefix;
    }

    public String process(String source) {
        if (prefix.isEmpty()) {
            return processVanillaShader(source);
        }
        if (prefix.equals("va")) {
            return processOptifineShader(source);
        }
        if (prefix.equals("iris_")) {
            return processIrisShader(source);
        }
        return source;
    }


    private String processIrisShader(String source) {
        source = replace(source, "ivec2", "UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 0, 1))");
        source = replace(source, "vec2", "UV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 0, 1))");
        //source = replace(source, "vec3", "Normal", "mat3", "normalMatrix", "($1 * $2)");
        ModLog.debug("process iris shader: \n{}", source);
        return source;
    }

    private String processOptifineShader(String source) {
        source = replace(source, "ivec2", "UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 0, 1))");
        ModLog.debug("process optifine shader: \n{}", source);
        return source;
    }

    private String processVanillaShader(String source) {
        source = replace(source, "ivec2", "UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 0, 1))");
        source = replace(source, "vec2", "UV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 0, 1))");
        source = replace(source, "vec3", "Normal", "mat3", "aw_NormalMatrix", "($1 * $2)");
        ModLog.debug("process vanilla shader: \n{}", source);
        return source;
    }

    private String replace(String source, String varType, String var, String matrixType, String matrix, String expr) {
        // compile regular expressions.
        String[] texts = {"in\\s+${varType}\\s+${var};", "$0\nuniform ${matrixType} ${matrix} = ${matrixType}(1);\n#define ${var} ${expr}"};
        for (int i = 0; i < texts.length; ++i) {
            String tmp = texts[i];
            tmp = tmp.replace("${varType}", varType);
            tmp = tmp.replace("${var}", prefix + var);
            tmp = tmp.replace("${matrixType}", matrixType);
            tmp = tmp.replace("${matrix}", matrix);
            tmp = tmp.replace("${expr}", expr.replace("$1", prefix + var).replace("$2", matrix));
            texts[i] = tmp;
        }
        // remove regex warning in the idea.
        BiFunction<String, String, String> func = source::replaceAll;
        return func.apply(texts[0], texts[1]);
    }
}
