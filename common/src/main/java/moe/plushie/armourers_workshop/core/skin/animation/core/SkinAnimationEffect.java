package moe.plushie.armourers_workshop.core.skin.animation.core;

import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.utils.ScheduledExpression;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SkinAnimationEffect {

    protected final List<SkinAnimation.Output<ScheduledExpression<?>>> allValues = new ArrayList<>();

    protected final List<SkinAnimation.Output<ScheduledExpression<?>>> particleValues = new ArrayList<>();
    protected final List<SkinAnimation.Output<ScheduledExpression<?>>> soundValues = new ArrayList<>();
    protected final List<SkinAnimation.Output<ScheduledExpression<?>>> instructValues = new ArrayList<>();

    protected int dirty = 0;


    public void add(SkinAnimation.Output<ScheduledExpression<?>> output) {
        var values = identity(output.channel());
        if (values == null) {
            return;
        }
        values.add(output);
        values.sort(Comparator.comparingInt(SkinAnimation.Output::priority));
        allValues.add(output);
    }

    public ScheduledExpression<?> process(SkinAnimation.Channel channel) {
        var values = identity(channel);
        if (values == null) {
            return null; // not supported channel!!!
        }
        for (var output : values) {
            var value = output.get();
            if (value != null) {
                return value;
            }
        }
        return null; // the animation not work.
    }

    public void clear() {
        allValues.forEach(SkinAnimation.Output::reset);
        dirty = 0;
    }

    public void setDirty() {
        dirty = 1;
    }

    public boolean isDirty() {
        return dirty != 0;
    }

    private List<SkinAnimation.Output<ScheduledExpression<?>>> identity(SkinAnimation.Channel channel) {
        return switch (channel) {
            case PARTICLE -> particleValues;
            case SOUND -> soundValues;
            case INSTRUCT -> instructValues;
            default -> null;
        };
    }
}
