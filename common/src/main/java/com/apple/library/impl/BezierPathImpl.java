package com.apple.library.impl;

public class BezierPathImpl {

    static final int CUBIC_BEZIER_SPLINE_SAMPLES = 11;

    static final int kMaxNewtonIterations = 4;
    static final double kBezierEpsilon = 1e-7;

    double _ax;
    double _bx;
    double _cx;

    double _ay;
    double _by;
    double _cy;

    double _startGradient;
    double _endGradient;

    double _minRange;
    double _maxRange;

    final double[] _splineSamples = new double[CUBIC_BEZIER_SPLINE_SAMPLES];

    public BezierPathImpl(double p1x, double p1y, double p2x, double p2y) {
        _initCoefficients(p1x, p1y, p2x, p2y);
        _initGradients(p1x, p1y, p2x, p2y);
        _initRange(p1y, p2y);
        _initSpline();
    }

    // Evaluates y at the given x with default epsilon.
    public double solve(double x) {
        return solveWithEpsilon(x, kBezierEpsilon);
    }

    // Evaluates y at the given x. The epsilon parameter provides a hint as to the
    // required accuracy and is not guaranteed. Uses gradients if x is
    // out of [0, 1] range.
    public double solveWithEpsilon(double x, double epsilon) {
        if (x < 0.0) {
            return _toFinite(0.0 + _startGradient * x);
        }
        if (x > 1.0) {
            return _toFinite(1.0 + _endGradient * (x - 1.0));
        }
        return _sampleCurveY(_solveCurveX(x, epsilon));
    }

    // Returns an approximation of dy/dx at the given x with default epsilon.
    public double slope(double x) {
        return slopeWithEpsilon(x, kBezierEpsilon);
    }

    // Returns an approximation of dy/dx at the given x.
    // Clamps x to range [0, 1].
    public double slopeWithEpsilon(double x, double epsilon) {
        x = Math.min(Math.max(x, 0), 1);
        double t = _solveCurveX(x, epsilon);
        double dx = _sampleCurveDerivativeX(t);
        double dy = _sampleCurveDerivativeY(t);
        // TODO: We should clamp NaN to a proper value.
        if (dx == 0 && dy == 0) {
            return 0;
        }
        return _toFinite(dy / dx);
    }

    // These getters are used rarely. We reverse compute them from coefficients.
    // See InitCoefficients. The speed has been traded for memory.

    public double getX1() {
        return _cx / 3.0;
    }

    public double getY1() {
        return _cy / 3.0;
    }

    public double getX2() {
        return (_bx + _cx) / 3.0 + getX1();
    }

    public double getY2() {
        return (_by + _cy) / 3.0 + getY1();
    }


    private void _initCoefficients(double p1x, double p1y, double p2x, double p2y) {
        // Calculate the polynomial coefficients, implicit first and last control
        // points are (0,0) and (1,1).
        _cx = 3.0 * p1x;
        _bx = 3.0 * (p2x - p1x) - _cx;
        _ax = 1.0 - _cx - _bx;

        _cy = _toFinite(3.0 * p1y);
        _by = _toFinite(3.0 * (p2y - p1y) - _cy);
        _ay = _toFinite(1.0 - _cy - _by);
    }

    private void _initGradients(double p1x, double p1y, double p2x, double p2y) {
        // End-point gradients are used to calculate timing function results
        // outside the range [0, 1].
        //
        // There are four possibilities for the gradient at each end:
        // (1) the closest control point is not horizontally coincident with regard to
        //     (0, 0) or (1, 1). In this case the line between the end point and
        //     the control point is tangent to the bezier at the end point.
        // (2) the closest control point is coincident with the end point. In
        //     this case the line between the end point and the far control
        //     point is tangent to the bezier at the end point.
        // (3) both internal control points are coincident with an endpoint. There
        //     are two special case that fall into this category:
        //     CubicBezier(0, 0, 0, 0) and CubicBezier(1, 1, 1, 1). Both are
        //     equivalent to linear.
        // (4) the closest control point is horizontally coincident with the end
        //     point, but vertically distinct. In this case the gradient at the
        //     end point is Infinite. However, this causes issues when
        //     interpolating. As a result, we break down to a simple case of
        //     0 gradient under these conditions.

        if (p1x > 0) {
            _startGradient = p1y / p1x;
        } else if (p1y == 0 && p2x > 0) {
            _startGradient = p2y / p2x;
        } else if (p1y == 0 && p2y == 0) {
            _startGradient = 1;
        } else {
            _startGradient = 0;
        }

        if (p2x < 1) {
            _endGradient = (p2y - 1) / (p2x - 1);
        } else if (p2y == 1 && p1x < 1) {
            _endGradient = (p1y - 1) / (p1x - 1);
        } else if (p2y == 1 && p1y == 1) {
            _endGradient = 1;
        } else {
            _endGradient = 0;
        }
    }

    // This works by taking taking the derivative of the cubic bezier, on the y
    // axis. We can then solve for where the derivative is zero to find the min
    // and max distance along the line. We the have to solve those in terms of time
    // rather than distance on the x-axis
    private void _initRange(double p1y, double p2y) {
        _minRange = 0;
        _maxRange = 1;
        if (0 <= p1y && p1y < 1 && 0 <= p2y && p2y <= 1) {
            return;
        }

        double epsilon = kBezierEpsilon;

        // Represent the function's derivative in the form at^2 + bt + c
        // as in sampleCurveDerivativeY.
        // (Technically this is (dy/dt)*(1/3), which is suitable for finding zeros
        // but does not actually give the slope of the curve.)
        double a = 3.0 * _ay;
        double b = 2.0 * _by;
        double c = _cy;

        // Check if the derivative is constant.
        if (Math.abs(a) < epsilon && Math.abs(b) < epsilon) {
            return;
        }

        // Zeros of the function's derivative.
        double t1 = 0;
        double t2 = 0;

        if (Math.abs(a) < epsilon) {
            // The function's derivative is linear.
            t1 = -c / b;
        } else {
            // The function's derivative is a quadratic. We find the zeros of this
            // quadratic using the quadratic formula.
            double discriminant = b * b - 4 * a * c;
            if (discriminant < 0) {
                return;
            }
            double ds = Math.sqrt(discriminant);
            t1 = (-b + ds) / (2 * a);
            t2 = (-b - ds) / (2 * a);
        }

        double sol1 = 0;
        double sol2 = 0;

        // If the solution is in the range [0,1] then we include it, otherwise we
        // ignore it.

        // An interesting fact about these beziers is that they are only
        // actually evaluated in [0,1]. After that we take the tangent at that point
        // and linearly project it out.
        if (0 < t1 && t1 < 1) {
            sol1 = _sampleCurveY(t1);
        }
        if (0 < t2 && t2 < 1) {
            sol2 = _sampleCurveY(t2);
        }

        _minRange = Math.min(Math.min(_minRange, sol1), sol2);
        _maxRange = Math.max(Math.max(_maxRange, sol1), sol2);
    }

    private void _initSpline() {
        var delta_t = 1.0 / (CUBIC_BEZIER_SPLINE_SAMPLES - 1);
        for (var i = 0; i < CUBIC_BEZIER_SPLINE_SAMPLES; i++) {
            _splineSamples[i] = _sampleCurveX(i * delta_t);
        }
    }

    private double _sampleCurveX(double t) {
        // `ax t^3 + bx t^2 + cx t' expanded using Horner's rule.
        // The x values are in the range [0, 1]. So it isn't needed toFinite
        // clamping.
        // https://drafts.csswg.org/css-easing-1/#funcdef-cubic-bezier-easing-function-cubic-bezier
        return ((_ax * t + _bx) * t + _cx) * t;
    }

    private double _sampleCurveY(double t) {
        return _toFinite(((_ay * t + _by) * t + _cy) * t);
    }

    private double _sampleCurveDerivativeX(double t) {
        return (3.0 * _ax * t + 2.0 * _bx) * t + _cx;
    }

    private double _sampleCurveDerivativeY(double t) {
        return _toFinite(_toFinite(_toFinite(3.0 * _ay) * t + _toFinite(2.0 * _by)) * t + _cy);
    }

    // Given an x value, find a parametric value it came from.
    // x must be in [0, 1] range. Doesn't use gradients.
    private double _solveCurveX(double x, double epsilon) {
        var t0 = 0d;
        var t1 = 0d;
        var t2 = x;
        var x2 = 0d;
        var d2 = 0d;

        // Linear interpolation of spline curve for initial guess.
        var delta_t = 1.0 / (CUBIC_BEZIER_SPLINE_SAMPLES - 1);
        for (var i = 1; i < CUBIC_BEZIER_SPLINE_SAMPLES; i++) {
            if (x <= _splineSamples[i]) {
                t1 = delta_t * i;
                t0 = t1 - delta_t;
                t2 = t0 + (t1 - t0) * (x - _splineSamples[i - 1]) / (_splineSamples[i] - _splineSamples[i - 1]);
                break;
            }
        }

        // Perform a few iterations of Newton's method -- normally very fast.
        // See https://en.wikipedia.org/wiki/Newton%27s_method.
        var newton_epsilon = Math.min(kBezierEpsilon, epsilon);
        for (var i = 0; i < kMaxNewtonIterations; i++) {
            x2 = _sampleCurveX(t2) - x;
            if (Math.abs(x2) < newton_epsilon) {
                return t2;
            }
            d2 = _sampleCurveDerivativeX(t2);
            if (Math.abs(d2) < kBezierEpsilon) {
                break;
            }
            t2 = t2 - x2 / d2;
        }
        if (Math.abs(x2) < epsilon) return t2;

        // Fall back to the bisection method for reliability.
        while (t0 < t1) {
            x2 = _sampleCurveX(t2);
            if (Math.abs(x2 - x) < epsilon) {
                return t2;
            }
            if (x > x2) {
                t0 = t2;
            } else {
                t1 = t2;
            }
            t2 = (t1 + t0) * .5;
        }

        // Failure.
        return t2;
    }

    private double _toFinite(double value) {
//        // TODO: We can clamp this in numeric operation helper
//        // function like ClampedNumeric.
//        if (std::isinf(value)) {
//            if (value > 0)
//                return std::numeric_limits<double>::max();
//            return std::numeric_limits<double>::lowest();
//        }
        return value;
    }
}
