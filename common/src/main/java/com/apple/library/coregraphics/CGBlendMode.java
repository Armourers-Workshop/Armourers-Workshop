package com.apple.library.coregraphics;

public enum CGBlendMode {
    NORMAL,
    MULTIPLY,
    SCREEN,
    OVERLAY,
    DARKEN,
    LIGHTEN,
    COLOR_DODGE,
    COLOR_BURN,
    SOFT_LIGHT,
    HARD_LIGHT,
    DIFFERENCE,
    EXCLUSION,
    HUE,
    SATURATION,
    COLOR,
    LUMINOSITY,
    CLEAR,              // R = 0
    COPY,               // R = S
    SOURCE_IN,          // R = S * Da
    SOURCE_OUT,         // R = S * (1 - Da)
    SOURCE_A_TOP,       // R = R = S * Da + D * (1 - Sa)
    DESTINATION_OVER,   // R = S * (1 - Da) + D
    DESTINATION_IN,     // R = D * Sa
    DESTINATION_OUT,    // R = S * (1 - Sa)
    DESTINATION_A_TOP,  // R = S * (1 - Da) + D * Sa
    XOR,                // R = S * (1 - Da) + D * (1 - Sa)
    PLUS_DARKER,        // R = MAX(0, (1 - D) + (1 - S))
    PLUS_LIGHTER        // R = MIN(1, S + D)
}
