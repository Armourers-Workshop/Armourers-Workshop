package com.apple.library.impl;

import moe.plushie.armourers_workshop.utils.ObjectUtils;

public class ObjectImpl {

    // "<%s: 0x%x; arg1 = arg2; ...; argN-1 = argN>"
    public static String makeDescription(Object obj, Object... arguments) {
        return ObjectUtils.makeDescription(obj, arguments);
    }
}
