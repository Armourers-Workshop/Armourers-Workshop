package moe.plushie.armourers_workshop.utils;

import com.apple.library.foundation.NSRange;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectUtils {

    public static <S, T> T unsafeCast(S src) {
        // noinspection unchecked
        return (T) src;
    }

    @Nullable
    public static <S, T> T safeCast(S src, Class<T> type) {
        if (type.isInstance(src)) {
            return type.cast(src);
        }
        return null;
    }

    public static String replaceString(String string, NSRange range, String replacementString) {
        return (new StringBuilder(string)).replace(range.startIndex(), range.endIndex(), replacementString).toString();
    }

    @Nullable
    public static <S, T> T compactMap(@Nullable S src, Function<S, T> consumer) {
        if (src != null) {
            return consumer.apply(src);
        }
        return null;
    }

    public static <S> void ifPresent(@Nullable S src, Consumer<S> consumer) {
        if (src != null) {
            consumer.accept(src);
        }
    }


    public static void set(IMatrix3f matrixIn, IMatrix3f matrixOut) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        matrixIn.get(buffer);
        matrixOut.set(buffer);
    }

    public static void set(IMatrix4f matrixIn, IMatrix4f matrixOut) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrixIn.get(buffer);
        matrixOut.set(buffer);
    }

}
