package com.apple.library.impl;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.foundation.NSRange;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;

@SuppressWarnings("unused")
public class ObjectUtilsImpl extends ObjectUtils {

    private static final OpenMatrix3f SHARED_MATRIX_3x3 = OpenMatrix3f.createScaleMatrix(1, 1, 1);
    private static final OpenMatrix4f SHARED_MATRIX_4x4 = OpenMatrix4f.createScaleMatrix(1, 1, 1);

    public static OpenMatrix3f convertToMatrix3x3(CGAffineTransform transform) {
        var mat = SHARED_MATRIX_3x3;
        mat.m00 = transform.a;
        mat.m01 = transform.b;
        mat.m10 = transform.c;
        mat.m11 = transform.d;
        mat.m20 = transform.tx;
        mat.m21 = transform.ty;
        return mat;
    }

    public static OpenMatrix4f convertToMatrix4x4(CGAffineTransform transform) {
        var mat = SHARED_MATRIX_4x4;
        mat.m00 = transform.a;
        mat.m01 = transform.b;
        mat.m10 = transform.c;
        mat.m11 = transform.d;
        mat.m30 = transform.tx;
        mat.m31 = transform.ty;
        return mat;
    }

    public static String replaceString(String string, NSRange range, String replacementString) {
        return (new StringBuilder(string)).replace(range.startIndex(), range.endIndex(), replacementString).toString();
    }

    public static double currentMediaTime() {
        return TickUtils.ticks() / 1000.0;
    }
}
