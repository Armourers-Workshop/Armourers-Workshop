package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.core.layout.StringBuilderEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class ShaderPreprocessor {

    private final String prefix;

    public ShaderPreprocessor(String prefix) {
        this.prefix = prefix;
    }

    public String process(String source) {
        if (prefix.isEmpty()) {
            return processVanillaShader(new Builder(source));
        }
        if (prefix.equals("va")) {
            return processOptifineShader(new Builder(source));
        }
        if (prefix.equals("iris_")) {
            return processIrisShader(new Builder(source));
        }
        return source;
    }

    private String processIrisShader(Builder builder) {
        builder.uniform("mat4", "iris_TextureMat", "mat4", "aw_TextureMatrix", "($1 * $2)");
        builder.uniform("mat4", "iris_LightmapTextureMatrix", "mat4", "aw_LightmapTextureMatrix", "($1 * $2)");
        builder.uniform("mat3", "iris_NormalMat", "mat3", "aw_NormalMatrix", "($1 * $2)");
        builder.uniform("mat4", "iris_ModelViewMat", "mat4", "aw_ModelViewMat", "($1 * $2)");
        ModLog.debug("process iris shader: \n{}", builder);
        return builder.build();
    }

    private String processOptifineShader(Builder builder) {
        builder.attribute("ivec2", "vaUV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 0, 1))");
        builder.uniform("mat4", "textureMatrix", "mat4", "aw_TextureMatrix", "($1 * $2)");
        builder.uniform("mat3", "normalMatrix", "mat3", "aw_NormalMatrix", "($1 * $2)");
        builder.uniform("mat4", "modelViewMatrix", "mat4", "aw_ModelViewMat", "($1 * $2)");
        ModLog.debug("process optifine shader: \n{}", builder);
        return builder.build();
    }

    private String processVanillaShader(Builder builder) {
        builder.attribute("ivec2", "UV2", "mat4", "aw_LightmapTextureMatrix", "ivec2($2 * vec4($1, 0, 1))");
        builder.attribute("vec2", "UV0", "mat4", "aw_TextureMatrix", "vec2($2 * vec4($1, 0, 1))");
        builder.attribute("vec3", "Normal", "mat3", "aw_NormalMatrix", "($1 * $2)");
        builder.uniform("mat4", "ModelViewMat", "mat4", "aw_ModelViewMat", "($1 * $2)");
        ModLog.debug("process vanilla shader: \n{}", builder);
        return builder.build();
    }

    public static class Builder {

        private String source;
        private final ArrayList<String> initializer1 = new ArrayList<>();
        private final ArrayList<String> initializer2 = new ArrayList<>();

        public Builder(String source) {
            this.source = source;
        }

        public Builder uniform(String varType, String var, String matrixType, String matrixVar, String expr) {
            source = register("uniform", source, varType, var, matrixType, matrixVar, expr);
            return this;
        }

        public Builder attribute(String varType, String var, String matrixType, String matrixVar, String expr) {
            source = register("in", source, varType, var, matrixType, matrixVar, expr);
            return this;
        }

        private String register(String category, String source, String varType, String var, String matrixType, String matrix, String expr) {
            // compile regular expressions.
            String[] texts = {
                    "(${category}\\s+${varType}\\s+)${var}(.*?;)", "$1__awrt_${var}_awrt__$2",
                    "\\b${var}\\b", "awrt_${var}",
                    "(${category}\\s+${varType}\\s+)__awrt_${var}_awrt__(.*?;)", "uniform ${matrixType} ${matrix};\n${varType} awrt_${var};\n$1${var}$2",
            };
            String[] regexes = new String[texts.length];
            for (int i = 0; i < texts.length; ++i) {
                String tmp = texts[i];
                tmp = tmp.replace("${category}", category);
                tmp = tmp.replace("${varType}", varType);
                tmp = tmp.replace("${var}", var);
                tmp = tmp.replace("${matrixType}", matrixType);
                tmp = tmp.replace("${matrix}", matrix);
                regexes[i] = tmp;
            }
            // we need to replace all the content correctly.
            for (int i = 0; i < regexes.length / 2; ++i) {
                String newValue = source.replaceAll(regexes[i * 2], regexes[i * 2 + 1]);
                if (i == 0 && newValue.equals(source)) {
                    // sorry, we not found the input var.
                    return source;
                }
                source = newValue;
            }
            // the initializer must be relocation.
            initializer1.add("awrt_" + var + " = " + expr.replace("$1", var).replace("$2", matrix));
            initializer2.add("awrt_" + var + " = " + var);
            return source;
        }

        public String build() {
            // when not any changes, ignore.
            if (initializer1.isEmpty()) {
                return source;
            }
            // NOTE: we can't support "type x = y;" in the global;
            SourceBuilder builder = new SourceBuilder();
            builder.append("uniform int aw_MatrixFlags;\n");
            builder.append("void awrt_main_pre() {\n");
            builder.append("    if (aw_MatrixFlags != 0) {\n");
            builder.append("        ", initializer1, ";\n");
            builder.append("    } else {\n");
            builder.append("        ", initializer2, ";\n");
            builder.append("    }\n");
            builder.append("}\n");
            return source.replaceAll("(void\\s+main\\s*\\(\\)\\s*\\{)", builder.build() + "\n$1\n    awrt_main_pre();\n");
        }

        @Override
        public String toString() {
            return build();
        }
    }

    public static class SourceBuilder {

        private final StringBuffer buffer = new StringBuffer();

        public void append(String value) {
            buffer.append(value);
        }

        public void append(String prefix, Collection<String> children, String suffix) {
            for (String content : children) {
                buffer.append(prefix);
                buffer.append(content);
                buffer.append(suffix);
            }
        }

        public String build() {
            return buffer.toString();
        }
    }
}
