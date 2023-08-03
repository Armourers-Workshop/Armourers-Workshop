package com.apple.library.quartzcore;

@SuppressWarnings("unused")
public class CAAnimation implements CAMediaTiming {

    private Object fromValue;
    private Object toValue;
    private Object byValue;

    private Object animationData;
    private CAMediaTimingFunction timingFunction = CAMediaTimingFunction.DEFAULT;
    private boolean removedOnCompletion = true;

    private double beginTime = 0;
    private double duration = 0;
    private double speed = 1;
    private double timeOffset = 0;
    private int repeatCount = 0;
    private double repeatDuration = 0;
    private boolean autoreverses = false;
    private int filMode = 0;

    public CAAnimation(String keyPath) {
    }

    public static CAAnimation animationWithKeyPath(String keyPath) {
        return new CAAnimation(keyPath);
    }

    public Object fromValue() {
        return fromValue;
    }

    public void setFromValue(Object fromValue) {
        this.fromValue = fromValue;
    }

    public Object toValue() {
        return toValue;
    }

    public void setToValue(Object toValue) {
        this.toValue = toValue;
    }


    public Object byValue() {
        return byValue;
    }

    public void setByValue(Object byValue) {
        this.byValue = byValue;
    }

    public CAMediaTimingFunction timingFunction() {
        return timingFunction;
    }

    public void setTimingFunction(CAMediaTimingFunction timingFunction) {
        this.timingFunction = timingFunction;
    }

    @Override
    public double beginTime() {
        return beginTime;
    }

    @Override
    public void setBeginTime(double time) {
        beginTime = time;
    }

    @Override
    public double duration() {
        return duration;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public double speed() {
        return speed;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public double timeOffset() {
        return timeOffset;
    }

    @Override
    public void setTimeOffset(double timeOffset) {
        this.timeOffset = timeOffset;
    }

    @Override
    public int repeatCount() {
        return repeatCount;
    }

    @Override
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public double repeatDuration() {
        return repeatDuration;
    }

    @Override
    public void setRepeatDuration(double repeatDuration) {
        this.repeatDuration = repeatDuration;
    }

    @Override
    public boolean autoreverses() {
        return autoreverses;
    }

    @Override
    public void setAutoreverses(boolean autoreverses) {
        this.autoreverses = autoreverses;
    }

    @Override
    public int filMode() {
        return filMode;
    }

    @Override
    public void setFillMode(int fillMode) {
        this.filMode = fillMode;
    }

    public Object _animationData() {
        return animationData;
    }

    public void _setAnimationData(Object animationData) {
        this.animationData = animationData;
    }

    public double _currentTime(double tp) {
        double begin = beginTime();
        double speed = speed();
        double offset = timeOffset();
        return (tp - begin) * speed + offset;
    }
}
