package com.apple.library.quartzcore;

import com.apple.library.impl.BezierPathImpl;

@SuppressWarnings("unused")
public enum CAMediaTimingFunction {

    LINEAR(null),
    EASE_IN(new BezierPathImpl(0.42f, 0f, 1f, 1f)),
    EASE_OUT(new BezierPathImpl(0f, 0f, 0.58f, 1f)),
    EASE_IN_OUT(new BezierPathImpl(0.42f, 0f, 0.58f, 1f)),
    DEFAULT(new BezierPathImpl(0.25f, 0.1f, 0.25f, 1f)); // ease

    private final BezierPathImpl impl;

    // Create a timing function modelled on a cubic Bezier curve.
    // The end points of the curve are at (0,0) and (1,1),
    // the tow points 'c1' and 'c2' defined by the class instance are the control points.
    // Thus the points defining the Bezier curve are: '[(0,0), c1, c2, (1,1)]'
    CAMediaTimingFunction(BezierPathImpl impl) {
        this.impl = impl;
    }

    public float applying(float t) {
        if (impl != null) {
            return (float) impl.solve(t);
        }
        return t;
    }
}
