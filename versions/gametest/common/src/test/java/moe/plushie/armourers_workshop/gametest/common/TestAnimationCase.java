package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationPose;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationCompiler;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.core.utils.ScheduledExpression;
import moe.plushie.armourers_workshop.gametest.utils.TestAnimation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.SAME_THREAD)
public class TestAnimationCase {

    private ExecutionContextImpl context;
    private SkinAnimationCompiler compiler;

    @BeforeEach
    public void setUp() {
        context = new ExecutionContextImpl(new StaticVariableStorage());
        compiler = new SkinAnimationCompiler(new MolangVirtualMachine()) {

            @Override
            protected OptimizedExpression<?> compile(SkinAnimationData data, SkinAnimationData.Point point) {
                if (point instanceof SkinAnimationData.Point.Instruct instruct) {
                    return new AnimationEffect(instruct.script());
                }
                if (point instanceof SkinAnimationData.Point.Sound sound) {
                    return new AnimationEffect(sound.effect());
                }
                if (point instanceof SkinAnimationData.Point.Particle particle) {
                    return new AnimationEffect(particle.effect());
                }
                return super.compile(data, point);
            }
        };
    }


    @Test
    public void testLoopMode() {
        var ani0 = animation("idle", 2.0f, "linear", "none");
        var ani1 = animation("idle", 2.0f, "linear", "last_frame");
        var ani2 = animation("idle", 2.0f, "linear", "loop");

        // start boundaries
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani0.execute(0).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani1.execute(0).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani2.execute(0).translation());

        // negative time clamps to first keyframe value
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani0.execute(-1).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani1.execute(-1).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani2.execute(-1).translation());

        // first segment interpolation (0s -> 1s)
        assertEquals(vec3(0.250000, 0.250000, 0.250000), ani0.execute(250).translation());
        assertEquals(vec3(0.250000, 0.250000, 0.250000), ani1.execute(250).translation());
        assertEquals(vec3(0.250000, 0.250000, 0.250000), ani2.execute(250).translation());
        assertEquals(vec3(0.500000, 0.500000, 0.500000), ani0.execute(500).translation());
        assertEquals(vec3(0.500000, 0.500000, 0.500000), ani1.execute(500).translation());
        assertEquals(vec3(0.500000, 0.500000, 0.500000), ani2.execute(500).translation());
        assertEquals(vec3(0.750000, 0.750000, 0.750000), ani0.execute(750).translation());
        assertEquals(vec3(0.750000, 0.750000, 0.750000), ani1.execute(750).translation());
        assertEquals(vec3(0.750000, 0.750000, 0.750000), ani2.execute(750).translation());

        // exact middle keyframe
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani0.execute(1000).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani1.execute(1000).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(1000).translation());

        // second segment interpolation (1s -> 2s)
        assertEquals(vec3(1.250000, 1.250000, 1.250000), ani0.execute(1250).translation());
        assertEquals(vec3(1.250000, 1.250000, 1.250000), ani1.execute(1250).translation());
        assertEquals(vec3(1.250000, 1.250000, 1.250000), ani2.execute(1250).translation());
        assertEquals(vec3(1.500000, 1.500000, 1.500000), ani0.execute(1500).translation());
        assertEquals(vec3(1.500000, 1.500000, 1.500000), ani1.execute(1500).translation());
        assertEquals(vec3(1.500000, 1.500000, 1.500000), ani2.execute(1500).translation());
        assertEquals(vec3(1.750000, 1.750000, 1.750000), ani0.execute(1750).translation());
        assertEquals(vec3(1.750000, 1.750000, 1.750000), ani1.execute(1750).translation());
        assertEquals(vec3(1.750000, 1.750000, 1.750000), ani2.execute(1750).translation());

        // end boundary
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani0.execute(2000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani1.execute(2000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani2.execute(2000).translation());

        // out of range (beyond duration)
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani0.execute(3000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani1.execute(3000).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(3000).translation());

        // two full durations later
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani0.execute(4000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani1.execute(4000).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani2.execute(4000).translation());

        var ani3 = animation("idle", 2.0f, "smooth", "none");
        var ani4 = animation("idle", 2.0f, "smooth", "last_frame");
        var ani5 = animation("idle", 2.0f, "smooth", "loop");

        // start boundaries
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani3.execute(0).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani4.execute(0).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(0).rotation());

        // negative time clamps to first keyframe value
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani3.execute(-1).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani4.execute(-1).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(-1).rotation());

        // first segment interpolation (0s -> 1s)
        assertEquals(vec3(0.179687, 0.179687, 0.179687), ani3.execute(250).rotation());
        assertEquals(vec3(0.179687, 0.179687, 0.179687), ani4.execute(250).rotation());
        assertEquals(vec3(0.109375, 0.109375, 0.109375), ani5.execute(250).rotation());
        assertEquals(vec3(0.437500, 0.437500, 0.437500), ani3.execute(500).rotation());
        assertEquals(vec3(0.437500, 0.437500, 0.437500), ani4.execute(500).rotation());
        assertEquals(vec3(0.375000, 0.375000, 0.375000), ani5.execute(500).rotation());
        assertEquals(vec3(0.726562, 0.726562, 0.726562), ani3.execute(750).rotation());
        assertEquals(vec3(0.726562, 0.726562, 0.726562), ani4.execute(750).rotation());
        assertEquals(vec3(0.703125, 0.703125, 0.703125), ani5.execute(750).rotation());

        // exact middle keyframe
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(1000).rotation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani4.execute(1000).rotation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani5.execute(1000).rotation());

        // second segment interpolation (1s -> 2s)
        assertEquals(vec3(1.273437, 1.273437, 1.273437), ani3.execute(1250).rotation());
        assertEquals(vec3(1.273437, 1.273437, 1.273437), ani4.execute(1250).rotation());
        assertEquals(vec3(1.296875, 1.296875, 1.296875), ani5.execute(1250).rotation());
        assertEquals(vec3(1.562500, 1.562500, 1.562500), ani3.execute(1500).rotation());
        assertEquals(vec3(1.562500, 1.562500, 1.562500), ani4.execute(1500).rotation());
        assertEquals(vec3(1.625000, 1.625000, 1.625000), ani5.execute(1500).rotation());
        assertEquals(vec3(1.820312, 1.820312, 1.820312), ani3.execute(1750).rotation());
        assertEquals(vec3(1.820312, 1.820312, 1.820312), ani4.execute(1750).rotation());
        assertEquals(vec3(1.890625, 1.890625, 1.890625), ani5.execute(1750).rotation());

        // end boundary
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani3.execute(2000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani4.execute(2000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani5.execute(2000).rotation());

        // out of range (beyond duration)
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani3.execute(3000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani4.execute(3000).rotation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani5.execute(3000).rotation());

        // two full durations later
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani3.execute(4000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), ani4.execute(4000).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(4000).rotation());
    }

    @Test
    public void testInterpolationMode() {
        var linear = SkinAnimationData.Interpolation.linear();
        var step = SkinAnimationData.Interpolation.step();
        var bezier1 = SkinAnimationData.Interpolation.bezier(new float[]{-0.1f, -0.1f, -0.1f, -0.25f, -0.25f, -0.25f, 0.1f, 0.1f, 0.1f, 0.25f, 0.25f, 0.25f});
        var bezier2 = SkinAnimationData.Interpolation.bezier(new float[]{-0.1f, -0.1f, -0.1f, 0.25f, 0.25f, 0.25f, 0.1f, 0.1f, 0.1f, -0.25f, -0.25f, -0.25f});
        var smooth = SkinAnimationData.Interpolation.smooth();

        var ani1 = animation("idle", 2.0f, "loop", it -> it.bone("root", 0, ch -> {
            ch.translation(0.00f, linear, pt -> pt.point(1, 1, 1));
            ch.translation(0.25f, bezier1, pt -> pt.point(1, 1, 1));
            ch.translation(0.50f, linear, pt -> pt.point(1, 1, 1));
            ch.translation(0.75f, bezier2, pt -> pt.point(1, 1, 1));
            ch.translation(1.00f, linear, pt -> pt.point(1, 1, 1));
        }));

        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani1.execute(0).translation());
        assertEquals(vec3(0.889581, 0.889581, 0.889581), ani1.execute(170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani1.execute(250).translation());
        assertEquals(vec3(1.110418, 1.110418, 1.110418), ani1.execute(330).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani1.execute(500).translation());
        assertEquals(vec3(1.110418, 1.110418, 1.110418), ani1.execute(670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani1.execute(750).translation());
        assertEquals(vec3(0.889582, 0.889582, 0.889582), ani1.execute(830).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani1.execute(1000).translation());

        var ani2 = animation("idle", 2.0f, "loop", it -> it.bone("root", 0, ch -> {
            ch.translation(0.00f, step, pt -> pt.point(1, 1, 1));
            ch.translation(0.25f, bezier1, pt -> pt.point(1, 1, 1));
            ch.translation(0.50f, step, pt -> pt.point(1, 1, 1));
            ch.translation(0.75f, bezier2, pt -> pt.point(1, 1, 1));
            ch.translation(1.00f, step, pt -> pt.point(1, 1, 1));
        }));

        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(0).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(250).translation());
        assertEquals(vec3(1.110418, 1.110418, 1.110418), ani2.execute(330).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(500).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(750).translation());
        assertEquals(vec3(0.889582, 0.889582, 0.889582), ani2.execute(830).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani2.execute(1000).translation());

        var ani3 = animation("idle", 2.0f, "loop", it -> it.bone("root", 0, ch -> {
            ch.translation(0.00f, smooth, pt -> pt.point(1, 1, 1));
            ch.translation(0.25f, bezier1, pt -> pt.point(1, 1, 1));
            ch.translation(0.50f, smooth, pt -> pt.point(1, 1, 1));
            ch.translation(0.75f, bezier2, pt -> pt.point(1, 1, 1));
            ch.translation(1.00f, smooth, pt -> pt.point(1, 1, 1));
        }));

        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(0).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(250).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(330).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(500).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(750).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(830).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani3.execute(1000).translation());

        var ani4 = animation("idle", 2.0f, "loop", it -> it.bone("root", 0, ch -> {
            ch.translation(0.00f, linear, pt -> pt.point(0, 0, 0));
            ch.translation(0.25f, bezier1, pt -> pt.point(1, 1, 1));
            ch.translation(0.50f, linear, pt -> pt.point(0, 0, 0));
            ch.translation(0.75f, bezier2, pt -> pt.point(1, 1, 1));
            ch.translation(1.00f, linear, pt -> pt.point(0, 0, 0));
        }));

        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani4.execute(0).translation());
        assertEquals(vec3(0.669311, 0.669311, 0.669311), ani4.execute(170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani4.execute(250).translation());
        assertEquals(vec3(0.890147, 0.890147, 0.890147), ani4.execute(330).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani4.execute(500).translation());
        assertEquals(vec3(0.890147, 0.890147, 0.890147), ani4.execute(670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani4.execute(750).translation());
        assertEquals(vec3(0.669311, 0.669311, 0.669311), ani4.execute(830).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani4.execute(1000).translation());

        var ani5 = animation("idle", 2.0f, "loop", it -> it.bone("root", 0, ch -> {
            ch.translation(0.00f, step, pt -> pt.point(0, 0, 0));
            ch.translation(0.25f, bezier1, pt -> pt.point(1, 1, 1));
            ch.translation(0.50f, step, pt -> pt.point(0, 0, 0));
            ch.translation(0.75f, bezier2, pt -> pt.point(1, 1, 1));
            ch.translation(1.00f, step, pt -> pt.point(0, 0, 0));
        }));

        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(0).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani5.execute(250).translation());
        assertEquals(vec3(0.890147, 0.890147, 0.890147), ani5.execute(330).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(500).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani5.execute(750).translation());
        assertEquals(vec3(0.669311, 0.669311, 0.669311), ani5.execute(830).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), ani5.execute(1000).translation());
    }

    @Test
    public void testPaddingMode() {
        var ani0 = animation("idle", 0.5f, 3.0f, "linear", "loop");
        // left padding
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani0.execute(0).scale());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani0.execute(500).scale());
        // right padding
        assertEquals(vec3(0.800000, 0.800000, 0.800000), ani0.execute(3000).scale());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), ani0.execute(3001).scale());
    }

    @Test
    public void testEffectAnimation() {
        var ani1 = animation("idle", 3.0f, "linear", "loop");

        assertEquals("script1", ani1.execute(0).timeline());
        assertEquals("script1", ani1.execute(9999).timeline());
        assertEquals("script2", ani1.execute(1000).timeline());
        assertEquals("script2", ani1.execute(1999).timeline());
        assertEquals("script3", ani1.execute(2000).timeline());
        assertEquals("script3", ani1.execute(2999).timeline());
        assertEquals("script3", ani1.execute(3000).timeline());
        assertEquals("script1", ani1.execute(3001).timeline());

        assertEquals("sound1", ani1.execute(0).sound());
        assertEquals("sound1", ani1.execute(999).sound());
        assertEquals("sound2", ani1.execute(1000).sound());
        assertEquals("sound2", ani1.execute(1999).sound());
        assertEquals("sound3", ani1.execute(2000).sound());
        assertEquals("sound3", ani1.execute(2999).sound());
        assertEquals("sound3", ani1.execute(3000).sound());
        assertEquals("sound1", ani1.execute(3001).sound());

        assertEquals("particle1", ani1.execute(0).particle());
        assertEquals("particle1", ani1.execute(999).particle());
        assertEquals("particle2", ani1.execute(1000).particle());
        assertEquals("particle2", ani1.execute(1999).particle());
        assertEquals("particle3", ani1.execute(2000).particle());
        assertEquals("particle3", ani1.execute(2999).particle());
        assertEquals("particle3", ani1.execute(3000).particle());
        assertEquals("particle1", ani1.execute(3001).particle());
    }

    @Test
    public void testKeyframeAccess() {
        var linear = SkinAnimation.Interpolator.linear();
        var frame1 = new SkinAnimation.Keyframe<>(0, 1000, expr(0, 0, 0), expr(1, 1, 1), linear);
        var frame2 = new SkinAnimation.Keyframe<>(1000, 2000, expr(1, 1, 1), expr(2, 2, 2), linear);
        var frame3 = new SkinAnimation.Keyframe<>(2000, 3000, expr(2, 2, 2), expr(3, 3, 3), linear);
        var animator = new SkinAnimation.Animator<>("root", SkinAnimation.Channel.TRANSLATION, Collections.newList(frame1, frame2, frame3));

        // forward
        assertEquals(frame1, animator.frameAtTime(0));
        assertEquals(frame1, animator.frameAtTime(999));
        assertEquals(frame2, animator.frameAtTime(1000));
        assertEquals(frame2, animator.frameAtTime(1999));
        assertEquals(frame3, animator.frameAtTime(2000));
        assertEquals(frame3, animator.frameAtTime(2999));

        // out of range
        assertEquals(frame1, animator.frameAtTime(-1));
        assertEquals(frame3, animator.frameAtTime(10000));

        // backward seek from cached tail
        assertEquals(frame2, animator.frameAtTime(1500));
        assertEquals(frame1, animator.frameAtTime(500));
    }

    @Test
    public void testDuration() {
        var liner = SkinAnimation.Interpolator.linear();
        var frame1 = new SkinAnimation.Keyframe<>(0, 0, expr(0, 0, 0), expr(1, 1, 1), liner); // zero length
        assertEquals(vec3(0.000000, 0.000000, 0.000000), frame1.evaluate(0, context));
        assertEquals(vec3(1.000000, 1.000000, 1.000000), frame1.evaluate(1, context));
    }

    @Test
    public void testInterpolation() {
        var linear = SkinAnimation.Interpolator.linear();
        var step = SkinAnimation.Interpolator.step();
        var bezier = SkinAnimation.Interpolator.bezier(new OpenVector3f(0.25f, 0.25f, 0.25f), OpenVector3f.ONE, new OpenVector3f(1.0f, 1.0f, 1.0f), OpenVector3f.ONE);
        var smooth = SkinAnimation.Interpolator.smooth();

        var frame11 = new SkinAnimation.Keyframe<>(0, 1000, expr(-1, -1, -1), expr(1, 1, 1), linear); // once
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame11.evaluate(0, context));
        assertEquals(vec3(-0.500000, -0.500000, -0.500000), frame11.evaluate(250, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame11.evaluate(500, context));
        assertEquals(vec3(+0.500000, +0.500000, +0.500000), frame11.evaluate(750, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame11.evaluate(1000, context));

        var frame12 = new SkinAnimation.Keyframe<>(0, 1000, expr(1, 1, 1), expr(-1, -1, -1), linear); // once
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame12.evaluate(0, context));
        assertEquals(vec3(+0.500000, +0.500000, +0.500000), frame12.evaluate(250, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame12.evaluate(500, context));
        assertEquals(vec3(-0.500000, -0.500000, -0.500000), frame12.evaluate(750, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame12.evaluate(1000, context));

        var frame21 = new SkinAnimation.Keyframe<>(0, 1000, expr(-1, -1, -1), expr(1, 1, 1), step); // once
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame21.evaluate(0, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame21.evaluate(250, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame21.evaluate(500, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame21.evaluate(750, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame21.evaluate(1000, context));

        var frame22 = new SkinAnimation.Keyframe<>(0, 1000, expr(1, 1, 1), expr(-1, -1, -1), step); // once
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame22.evaluate(0, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame22.evaluate(250, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame22.evaluate(500, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame22.evaluate(750, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame22.evaluate(1000, context));

        // before:{{-0.1,-0.25,-0.1},{0,-1,0}, {0.1,0.25,0.1},{0,1,0}}
        // after:{{-0.1,0,-0.1},{0,1,0}, {0.1,0,0.1},{0,1,0}}
        // vectors: {0,1},{0.25,2},{1,1},{1,0}
        var frame31 = new SkinAnimation.Keyframe<>(0, 1000, expr(1, 1, 1), expr(0, 0, 0), bezier); // once
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame31.evaluate(0, context));
        assertEquals(vec3(+1.402372, +1.402372, +1.402372), frame31.evaluate(250, context));
        assertEquals(vec3(+1.339977, +1.339977, +1.339977), frame31.evaluate(500, context));
        assertEquals(vec3(+1.018623, +1.018623, +1.018623), frame31.evaluate(750, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame31.evaluate(1000, context));

        var frame32 = new SkinAnimation.Keyframe<>(0, 1000, expr(0, 0, 0), expr(1, 1, 1), bezier); // once
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame32.evaluate(0, context));
        assertEquals(vec3(+0.694213, +0.694213, +0.694213), frame32.evaluate(250, context));
        assertEquals(vec3(+1.130331, +1.130331, +1.130331), frame32.evaluate(500, context));
        assertEquals(vec3(+1.386994, +1.386994, +1.386994), frame32.evaluate(750, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame32.evaluate(1000, context));

        var frame33 = new SkinAnimation.Keyframe<>(0, 250, expr(1, 1, 1), expr(0, 0, 0), SkinAnimation.Interpolator.bezier(new OpenVector3f(0.4f, 0.4f, 0.4f), new OpenVector3f(0.25f, 0.25f, 0.25f), new OpenVector3f(0.6f, 0.6f, 0.6f), OpenVector3f.ZERO));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame33.evaluate(0, context));
        assertEquals(vec3(+0.890147, +0.890147, +0.890147), frame33.evaluate(80, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame33.evaluate(250, context));

        var frame41 = new SkinAnimation.Keyframe<>(0, 1000, null, expr(0, 0, 0), expr(1, 1, 1), null, smooth); // once
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame41.evaluate(0, context));
        assertEquals(vec3(+0.203125, +0.203125, +0.203125), frame41.evaluate(250, context));
        assertEquals(vec3(+0.500000, +0.500000, +0.500000), frame41.evaluate(500, context));
        assertEquals(vec3(+0.796875, +0.796875, +0.796875), frame41.evaluate(750, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame41.evaluate(1000, context));

        var frame42 = new SkinAnimation.Keyframe<>(0, 1000, null, expr(0, 0, 0), expr(-1, -1, -1), null, smooth); // once
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame42.evaluate(0, context));
        assertEquals(vec3(-0.203125, -0.203125, -0.203125), frame42.evaluate(250, context));
        assertEquals(vec3(-0.500000, -0.500000, -0.500000), frame42.evaluate(500, context));
        assertEquals(vec3(-0.796875, -0.796875, -0.796875), frame42.evaluate(750, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame42.evaluate(1000, context));

        var frame43 = new SkinAnimation.Keyframe<>(0, 1000, null, expr(-1, -1, -1), expr(1, 1, 1), null, smooth); // once
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame43.evaluate(0, context));
        assertEquals(vec3(-0.593750, -0.593750, -0.593750), frame43.evaluate(250, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame43.evaluate(500, context));
        assertEquals(vec3(+0.593750, +0.593750, +0.593750), frame43.evaluate(750, context));
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame43.evaluate(1000, context));

        var frame44 = new SkinAnimation.Keyframe<>(0, 1000, null, expr(1, 1, 1), expr(-1, -1, -1), null, smooth); // once
        assertEquals(vec3(+1.000000, +1.000000, +1.000000), frame44.evaluate(0, context));
        assertEquals(vec3(+0.593750, +0.593750, +0.593750), frame44.evaluate(250, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame44.evaluate(500, context));
        assertEquals(vec3(-0.593750, -0.593750, -0.593750), frame44.evaluate(750, context));
        assertEquals(vec3(-1.000000, -1.000000, -1.000000), frame44.evaluate(1000, context));

        var frame45 = new SkinAnimation.Keyframe<>(0, 1000, null, expr(-1, 0, 1), expr(-1, 0, 1), expr(1, 0, -1), smooth); // once
        assertEquals(vec3(-1.000000, +0.000000, +1.000000), frame45.evaluate(0, context));
        assertEquals(vec3(-1.046875, +0.000000, +1.046875), frame45.evaluate(250, context));
        assertEquals(vec3(-1.125000, +0.000000, +1.125000), frame45.evaluate(500, context));
        assertEquals(vec3(-1.140625, +0.000000, +1.140625), frame45.evaluate(750, context));
        assertEquals(vec3(-1.000000, +0.000000, +1.000000), frame45.evaluate(1000, context));

        var frame46 = new SkinAnimation.Keyframe<>(0, 1000, expr(-1, 0, 1), expr(1, 0, -1), expr(1, 0, -1), null, smooth); // once
        assertEquals(vec3(+1.000000, +0.000000, -1.000000), frame46.evaluate(0, context));
        assertEquals(vec3(+1.140625, +0.000000, -1.140625), frame46.evaluate(250, context));
        assertEquals(vec3(+1.125000, +0.000000, -1.125000), frame46.evaluate(500, context));
        assertEquals(vec3(+1.046875, +0.000000, -1.046875), frame46.evaluate(750, context));
        assertEquals(vec3(+1.000000, +0.000000, -1.000000), frame46.evaluate(1000, context));

        var frame47 = new SkinAnimation.Keyframe<>(0, 1000, expr(0, 0, 0), expr(-1, 0, 1), expr(1, 0, -1), expr(0, 0, 0), smooth); // once
        assertEquals(vec3(-1.000000, +0.000000, +1.000000), frame47.evaluate(0, context));
        assertEquals(vec3(-0.640625, +0.000000, +0.640625), frame47.evaluate(250, context));
        assertEquals(vec3(+0.000000, +0.000000, +0.000000), frame47.evaluate(500, context));
        assertEquals(vec3(+0.640625, +0.000000, -0.640625), frame47.evaluate(750, context));
        assertEquals(vec3(+1.000000, +0.000000, -1.000000), frame47.evaluate(1000, context));
    }


    private OpenVector3f vec3(double x, double y, double z) {
        return new OpenVector3f(x, y, z) {

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof OpenVector3f that)) return false;
                return Math.abs(x - that.x()) < 1e-6f
                        && Math.abs(y - that.y()) < 1e-6f
                        && Math.abs(z - that.z()) < 1e-6f;
            }
        };
    }

    private OptimizedExpression<OpenVector3f> expr(double x, double y, double z) {
        return OptimizedExpression.of(new Constant(x), new Constant(y), new Constant(z));
    }


    private Animation animation(String name, double duration, Object interpolation, Object loop) {
        return animation(name, 0, duration, interpolation, loop);
    }

    private Animation animation(String name, double offset, double duration, Object interpolation, Object loop) {
        return animation(name, offset, duration, loop, it -> {
            it.bone("root", 0, ch -> {
                ch.translation(0.0, interpolation, pt -> pt.point(0, 0, 0));
                ch.translation(1.0, interpolation, pt -> pt.point(1, 1, 1));
                ch.translation(2.0, interpolation, pt -> pt.point(2, 2, 2));

                ch.rotation(0.0, interpolation, pt -> pt.point(0, 0, 0));
                ch.rotation(1.0, interpolation, pt -> pt.point(1, 1, 1));
                ch.rotation(2.0, interpolation, pt -> pt.point(2, 2, 2));

                ch.scale(0.0, interpolation, pt -> pt.point(1.0, 1.0, 1.0));
                ch.scale(1.0, interpolation, pt -> pt.point(1.2, 1.2, 1.2));
                ch.scale(2.0, interpolation, pt -> pt.point(0.8, 0.8, 0.8));
            });
            it.effect("root", 0, ch -> {
                ch.instruct(0.0, interpolation, pt -> pt.script("script1"));
                ch.instruct(1.0, interpolation, pt -> pt.script("script2"));
                ch.instruct(2.0, interpolation, pt -> pt.script("script3"));

                ch.sound(0.0, interpolation, pt -> pt.sound("sound1"));
                ch.sound(1.0, interpolation, pt -> pt.sound("sound2"));
                ch.sound(2.0, interpolation, pt -> pt.sound("sound3"));

                ch.particle(0.0, interpolation, pt -> pt.particle("particle1"));
                ch.particle(1.0, interpolation, pt -> pt.particle("particle2"));
                ch.particle(2.0, interpolation, pt -> pt.particle("particle3"));
            });
        });
    }

    private Animation animation(String name, double duration, Object loop, Consumer<TestAnimation.Animators> factory) {
        return animation(name, 0, duration, loop, factory);
    }

    private Animation animation(String name, double offset, double duration, Object loop, Consumer<TestAnimation.Animators> factory) {
        var namedParts = new HashMap<String, SkinPartTransform>();
        namedParts.put("root", new SkinPartTransform(OpenTransform3f.IDENTITY));
        var builder = new TestAnimation.Builder(compiler, namedParts);
        var animation = builder.build(name, offset, duration, loop, factory);
        return new Animation(animation, context);
    }


    private static class Animation {

        final TestAnimation animation;
        final ExecutionContextImpl context;

        private Animation(TestAnimation animation, ExecutionContextImpl context) {
            this.animation = animation;
            this.context = context;
        }

        public Results execute(int time) {
            var animationTick = time / 1000.0;

            // clear context
            context.setVariable(Name.of("timeline.result"), Result.NULL);
            context.setVariable(Name.of("sound.result"), Result.NULL);
            context.setVariable(Name.of("particle.result"), Result.NULL);

            for (var animation : animation.animations()) {
                animation.process(animationTick, context);
            }

            // create result evalutor.
            return new Results();
        }

        public class Results {

            public OpenVector3f translation() {
                return pose().translation();
            }

            public OpenVector3f rotation() {
                return pose().rotation();
            }

            public OpenVector3f scale() {
                return pose().scale();
            }

            public Object timeline() {
                return effect(SkinAnimation.Channel.INSTRUCT);
            }

            public Object sound() {
                return effect(SkinAnimation.Channel.SOUND);
            }

            public Object particle() {
                return effect(SkinAnimation.Channel.PARTICLE);
            }

            private Object effect(SkinAnimation.Channel channel) {
                for (var animation : animation.animations()) {
                    for (var effect : animation.affectedEffects()) {
                        return effect.process(channel).submit(null);
                    }
                }
                return null;
            }

            private SkinAnimationPose pose() {
                var result = new SkinAnimationPose();
                for (var animation : animation.animations()) {
                    for (var transform : animation.affectedTransforms()) {
                        transform.export(result);
                    }
                }
                return result;
            }
        }
    }

    private static class AnimationEffect implements ScheduledExpression<Object> {

        private final String value;

        private AnimationEffect(String value) {
            this.value = value;
        }

        @Override
        public Object submit(ExecutionContext context) {
            return value;
        }
    }
}
