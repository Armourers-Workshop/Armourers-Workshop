package com.apple.library.impl;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.init.ModLog;

@SuppressWarnings("unused")
public class SelfTester {

    private static void testConvert() {

        UIWindow w = new UIWindow(new CGRect(5, 5, 1000, 1000));
        UIView p = new UIView(new CGRect(100, 100, 1000, 1000));

        UIView c1 = new UIView(new CGRect(0, 40, 40, 40));
        UIView c2 = new UIView(new CGRect(40, 0, 40, 40));

        p.addSubview(c1);
        p.addSubview(c2);

        assertEqual(c1.convertPointToView(CGPoint.ZERO, p), 0, 40);
        assertEqual(c2.convertPointToView(CGPoint.ZERO, p), 40, 0);

        assertEqual(c1.convertPointFromView(CGPoint.ZERO, p), 0, -40);
        assertEqual(c2.convertPointFromView(CGPoint.ZERO, p), -40, 0);

        assertEqual(c1.convertPointToView(CGPoint.ZERO, null), 0, 0);
        assertEqual(c2.convertPointToView(CGPoint.ZERO, null), 0, 0);

        w.addSubview(p);

        assertEqual(c1.convertPointToView(CGPoint.ZERO, null), 100, 140);
        assertEqual(c2.convertPointToView(CGPoint.ZERO, null), 140, 100);

        UIView c21 = new UIView(new CGRect(0, 40, 40, 40));
        UIView c22 = new UIView(new CGRect(40, 0, 40, 40));

        c2.addSubview(c21);
        c2.addSubview(c22);

        assertEqual(c21.convertPointToView(CGPoint.ZERO, null), 140, 140);
        assertEqual(c22.convertPointToView(CGPoint.ZERO, null), 180, 100);

        assertEqual(c21.convertPointFromView(CGPoint.ZERO, p), -40, -40);
        assertEqual(c22.convertPointFromView(CGPoint.ZERO, p), -80, 0);

        assertEqual(p.convertPointToView(CGPoint.ZERO, c21), -40, -40);
        assertEqual(p.convertPointToView(CGPoint.ZERO, c22), -80, 0);

        UIView c11 = new UIView(new CGRect(0, 40, 40, 40));
        UIView c12 = new UIView(new CGRect(40, 0, 40, 40));

        c1.setTransform(CGAffineTransform.createScale(0.5f, 0.5f));
        c1.addSubview(c11);
        c1.addSubview(c12);

        assertEqual(c11.convertPointToView(CGPoint.ZERO, null), 110, 170);
        assertEqual(c12.convertPointToView(CGPoint.ZERO, null), 130, 150);

        assertEqual(p.convertPointToView(CGPoint.ZERO, c11), -20, -140);
        assertEqual(p.convertPointToView(CGPoint.ZERO, c12), -60, -100);

        assertEqual(c21.convertPointToView(CGPoint.ZERO, c11), 60, -60);
        assertEqual(c22.convertPointToView(CGPoint.ZERO, c12), 100, -100);

        assertEqual(c21.convertPointFromView(CGPoint.ZERO, c11), -30, 30);
        assertEqual(c22.convertPointFromView(CGPoint.ZERO, c12), -50, 50);
    }

    private static void assertEqual(CGPoint p1, float x, float y) {
        CGPoint p2 = new CGPoint(x, y);
        if (p2.equals(p1)) {
            ModLog.debug("{} => {}", p1, p2);
        } else {
            ModLog.error("{} => {}", p1, p2);
        }
    }

    public static void run() {
        testConvert();
    }
}
