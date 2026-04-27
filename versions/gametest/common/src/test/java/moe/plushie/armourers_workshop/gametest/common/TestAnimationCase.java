package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationCompiler;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationLinker;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationPose;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

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
                    return new TestAnimationHandler(Name.of("timeline.result"), instruct.script());
                }
                if (point instanceof SkinAnimationData.Point.Sound sound) {
                    return new TestAnimationHandler(Name.of("sound.result"), sound.effect());
                }
                if (point instanceof SkinAnimationData.Point.Particle particle) {
                    return new TestAnimationHandler(Name.of("particle.result"), particle.effect());
                }
                return super.compile(data, point);
            }
        };
    }


    @Test
    public void testLoopMode() {
        var ani0 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.NONE, SkinAnimationData.Interpolation.linear());
        var ani1 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LAST_FRAME, SkinAnimationData.Interpolation.linear());
        var ani2 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, SkinAnimationData.Interpolation.linear());

        // start boundaries
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani0, 0).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani1, 0).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani2, 0).translation());

        // negative time clamps to first keyframe value
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani0, -1).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani1, -1).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani2, -1).translation());

        // first segment interpolation (0s -> 1s)
        assertEquals(vec3(0.250000, 0.250000, 0.250000), executeAnimation(ani0, 250).translation());
        assertEquals(vec3(0.250000, 0.250000, 0.250000), executeAnimation(ani1, 250).translation());
        assertEquals(vec3(0.250000, 0.250000, 0.250000), executeAnimation(ani2, 250).translation());
        assertEquals(vec3(0.500000, 0.500000, 0.500000), executeAnimation(ani0, 500).translation());
        assertEquals(vec3(0.500000, 0.500000, 0.500000), executeAnimation(ani1, 500).translation());
        assertEquals(vec3(0.500000, 0.500000, 0.500000), executeAnimation(ani2, 500).translation());
        assertEquals(vec3(0.750000, 0.750000, 0.750000), executeAnimation(ani0, 750).translation());
        assertEquals(vec3(0.750000, 0.750000, 0.750000), executeAnimation(ani1, 750).translation());
        assertEquals(vec3(0.750000, 0.750000, 0.750000), executeAnimation(ani2, 750).translation());

        // exact middle keyframe
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani0, 1000).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani1, 1000).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 1000).translation());

        // second segment interpolation (1s -> 2s)
        assertEquals(vec3(1.250000, 1.250000, 1.250000), executeAnimation(ani0, 1250).translation());
        assertEquals(vec3(1.250000, 1.250000, 1.250000), executeAnimation(ani1, 1250).translation());
        assertEquals(vec3(1.250000, 1.250000, 1.250000), executeAnimation(ani2, 1250).translation());
        assertEquals(vec3(1.500000, 1.500000, 1.500000), executeAnimation(ani0, 1500).translation());
        assertEquals(vec3(1.500000, 1.500000, 1.500000), executeAnimation(ani1, 1500).translation());
        assertEquals(vec3(1.500000, 1.500000, 1.500000), executeAnimation(ani2, 1500).translation());
        assertEquals(vec3(1.750000, 1.750000, 1.750000), executeAnimation(ani0, 1750).translation());
        assertEquals(vec3(1.750000, 1.750000, 1.750000), executeAnimation(ani1, 1750).translation());
        assertEquals(vec3(1.750000, 1.750000, 1.750000), executeAnimation(ani2, 1750).translation());

        // end boundary
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani0, 2000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani1, 2000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani2, 2000).translation());

        // out of range (beyond duration)
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani0, 3000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani1, 3000).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 3000).translation());

        // two full durations later
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani0, 4000).translation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani1, 4000).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani2, 4000).translation());

        var ani3 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.NONE, SkinAnimationData.Interpolation.smooth());
        var ani4 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LAST_FRAME, SkinAnimationData.Interpolation.smooth());
        var ani5 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, SkinAnimationData.Interpolation.smooth());

        // start boundaries
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani3, 0).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani4, 0).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 0).rotation());

        // negative time clamps to first keyframe value
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani3, -1).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani4, -1).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, -1).rotation());

        // first segment interpolation (0s -> 1s)
        assertEquals(vec3(0.179687, 0.179687, 0.179687), executeAnimation(ani3, 250).rotation());
        assertEquals(vec3(0.179687, 0.179687, 0.179687), executeAnimation(ani4, 250).rotation());
        assertEquals(vec3(0.109375, 0.109375, 0.109375), executeAnimation(ani5, 250).rotation());
        assertEquals(vec3(0.437500, 0.437500, 0.437500), executeAnimation(ani3, 500).rotation());
        assertEquals(vec3(0.437500, 0.437500, 0.437500), executeAnimation(ani4, 500).rotation());
        assertEquals(vec3(0.375000, 0.375000, 0.375000), executeAnimation(ani5, 500).rotation());
        assertEquals(vec3(0.726562, 0.726562, 0.726562), executeAnimation(ani3, 750).rotation());
        assertEquals(vec3(0.726562, 0.726562, 0.726562), executeAnimation(ani4, 750).rotation());
        assertEquals(vec3(0.703125, 0.703125, 0.703125), executeAnimation(ani5, 750).rotation());

        // exact middle keyframe
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 1000).rotation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani4, 1000).rotation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani5, 1000).rotation());

        // second segment interpolation (1s -> 2s)
        assertEquals(vec3(1.273437, 1.273437, 1.273437), executeAnimation(ani3, 1250).rotation());
        assertEquals(vec3(1.273437, 1.273437, 1.273437), executeAnimation(ani4, 1250).rotation());
        assertEquals(vec3(1.296875, 1.296875, 1.296875), executeAnimation(ani5, 1250).rotation());
        assertEquals(vec3(1.562500, 1.562500, 1.562500), executeAnimation(ani3, 1500).rotation());
        assertEquals(vec3(1.562500, 1.562500, 1.562500), executeAnimation(ani4, 1500).rotation());
        assertEquals(vec3(1.625000, 1.625000, 1.625000), executeAnimation(ani5, 1500).rotation());
        assertEquals(vec3(1.820312, 1.820312, 1.820312), executeAnimation(ani3, 1750).rotation());
        assertEquals(vec3(1.820312, 1.820312, 1.820312), executeAnimation(ani4, 1750).rotation());
        assertEquals(vec3(1.890625, 1.890625, 1.890625), executeAnimation(ani5, 1750).rotation());

        // end boundary
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani3, 2000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani4, 2000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani5, 2000).rotation());

        // out of range (beyond duration)
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani3, 3000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani4, 3000).rotation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani5, 3000).rotation());

        // two full durations later
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani3, 4000).rotation());
        assertEquals(vec3(2.000000, 2.000000, 2.000000), executeAnimation(ani4, 4000).rotation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 4000).rotation());
    }

    @Test
    public void testInterpolationMode() {
        var linear = SkinAnimationData.Interpolation.linear();
        var step = SkinAnimationData.Interpolation.step();
        var bezier1 = SkinAnimationData.Interpolation.bezier(new float[]{-0.1f, -0.1f, -0.1f, -0.25f, -0.25f, -0.25f, 0.1f, 0.1f, 0.1f, 0.25f, 0.25f, 0.25f});
        var bezier2 = SkinAnimationData.Interpolation.bezier(new float[]{-0.1f, -0.1f, -0.1f, 0.25f, 0.25f, 0.25f, 0.1f, 0.1f, 0.1f, -0.25f, -0.25f, -0.25f});
        var smooth = SkinAnimationData.Interpolation.smooth();

        var ani1 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, Collections.newList(
                new SkinAnimationData.Keyframe(0.00f, "position", linear, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.25f, "position", bezier1, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.50f, "position", linear, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.75f, "position", bezier2, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(1.00f, "position", linear, Collections.newList(point(1, 1, 1)))
        ));

        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani1, 0).translation());
        assertEquals(vec3(0.889581, 0.889581, 0.889581), executeAnimation(ani1, 170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani1, 250).translation());
        assertEquals(vec3(1.110418, 1.110418, 1.110418), executeAnimation(ani1, 330).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani1, 500).translation());
        assertEquals(vec3(1.110418, 1.110418, 1.110418), executeAnimation(ani1, 670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani1, 750).translation());
        assertEquals(vec3(0.889582, 0.889582, 0.889582), executeAnimation(ani1, 830).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani1, 1000).translation());

        var ani2 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, Collections.newList(
                new SkinAnimationData.Keyframe(0.00f, "position", step, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.25f, "position", bezier1, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.50f, "position", step, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.75f, "position", bezier2, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(1.00f, "position", step, Collections.newList(point(1, 1, 1)))
        ));

        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 0).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 250).translation());
        assertEquals(vec3(1.110418, 1.110418, 1.110418), executeAnimation(ani2, 330).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 500).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 750).translation());
        assertEquals(vec3(0.889582, 0.889582, 0.889582), executeAnimation(ani2, 830).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani2, 1000).translation());

        var ani3 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, Collections.newList(
                new SkinAnimationData.Keyframe(0.00f, "position", smooth, Collections.newList(point(1, 1, 1))), // linear
                new SkinAnimationData.Keyframe(0.25f, "position", bezier1, Collections.newList(point(1, 1, 1))), // linear
                new SkinAnimationData.Keyframe(0.50f, "position", smooth, Collections.newList(point(1, 1, 1))), // linear
                new SkinAnimationData.Keyframe(0.75f, "position", bezier2, Collections.newList(point(1, 1, 1))), // linear
                new SkinAnimationData.Keyframe(1.00f, "position", smooth, Collections.newList(point(1, 1, 1)))  // linear
        ));

        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 0).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 250).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 330).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 500).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 750).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 830).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani3, 1000).translation());

        var ani4 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, Collections.newList(
                new SkinAnimationData.Keyframe(0.00f, "position", linear, Collections.newList(point(0, 0, 0))),
                new SkinAnimationData.Keyframe(0.25f, "position", bezier1, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.50f, "position", linear, Collections.newList(point(0, 0, 0))),
                new SkinAnimationData.Keyframe(0.75f, "position", bezier2, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(1.00f, "position", linear, Collections.newList(point(0, 0, 0)))
        ));

        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani4, 0).translation());
        assertEquals(vec3(0.669311, 0.669311, 0.669311), executeAnimation(ani4, 170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani4, 250).translation());
        assertEquals(vec3(0.890147, 0.890147, 0.890147), executeAnimation(ani4, 330).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani4, 500).translation());
        assertEquals(vec3(0.890147, 0.890147, 0.890147), executeAnimation(ani4, 670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani4, 750).translation());
        assertEquals(vec3(0.669311, 0.669311, 0.669311), executeAnimation(ani4, 830).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani4, 1000).translation());

        var ani5 = createAnimation("idle", 2.0f, SkinAnimationData.Loop.LOOP, Collections.newList(
                new SkinAnimationData.Keyframe(0.00f, "position", step, Collections.newList(point(0, 0, 0))),
                new SkinAnimationData.Keyframe(0.25f, "position", bezier1, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(0.50f, "position", step, Collections.newList(point(0, 0, 0))),
                new SkinAnimationData.Keyframe(0.75f, "position", bezier2, Collections.newList(point(1, 1, 1))),
                new SkinAnimationData.Keyframe(1.00f, "position", step, Collections.newList(point(0, 0, 0)))
        ));

        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 0).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 170).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani5, 250).translation());
        assertEquals(vec3(0.890147, 0.890147, 0.890147), executeAnimation(ani5, 330).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 500).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 670).translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani5, 750).translation());
        assertEquals(vec3(0.669311, 0.669311, 0.669311), executeAnimation(ani5, 830).translation());
        assertEquals(vec3(0.000000, 0.000000, 0.000000), executeAnimation(ani5, 1000).translation());
    }

    @Test
    public void testPaddingMode() {
        var ani0 = createAnimation("idle", 0.5f, 3.0f, SkinAnimationData.Loop.LOOP, SkinAnimationData.Interpolation.linear());
        // left padding
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani0, 0).scale());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani0, 500).scale());
        // right padding
        assertEquals(vec3(0.800000, 0.800000, 0.800000), executeAnimation(ani0, 3000).scale());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), executeAnimation(ani0, 3001).scale());
    }

    @Test
    public void testEffectAnimation() {
        var ani1 = createAnimation("idle", 3.0f, SkinAnimationData.Loop.LOOP, SkinAnimationData.Interpolation.linear());

        assertEquals("script1", executeAnimation(ani1, 0).timeline());
        assertEquals("script1", executeAnimation(ani1, 9999).timeline());
        assertEquals("script2", executeAnimation(ani1, 1000).timeline());
        assertEquals("script2", executeAnimation(ani1, 1999).timeline());
        assertEquals("script3", executeAnimation(ani1, 2000).timeline());
        assertEquals("script3", executeAnimation(ani1, 2999).timeline());
        assertEquals("script3", executeAnimation(ani1, 3000).timeline());
        assertEquals("script1", executeAnimation(ani1, 3001).timeline());

        assertEquals("sound1", executeAnimation(ani1, 0).sound());
        assertEquals("sound1", executeAnimation(ani1, 999).sound());
        assertEquals("sound2", executeAnimation(ani1, 1000).sound());
        assertEquals("sound2", executeAnimation(ani1, 1999).sound());
        assertEquals("sound3", executeAnimation(ani1, 2000).sound());
        assertEquals("sound3", executeAnimation(ani1, 2999).sound());
        assertEquals("sound3", executeAnimation(ani1, 3000).sound());
        assertEquals("sound1", executeAnimation(ani1, 3001).sound());

        assertEquals("particle1", executeAnimation(ani1, 0).particle());
        assertEquals("particle1", executeAnimation(ani1, 999).particle());
        assertEquals("particle2", executeAnimation(ani1, 1000).particle());
        assertEquals("particle2", executeAnimation(ani1, 1999).particle());
        assertEquals("particle3", executeAnimation(ani1, 2000).particle());
        assertEquals("particle3", executeAnimation(ani1, 2999).particle());
        assertEquals("particle3", executeAnimation(ani1, 3000).particle());
        assertEquals("particle1", executeAnimation(ani1, 3001).particle());
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

    private SkinAnimationData.Point point(double x, double y, double z) {
        return new SkinAnimationData.Point.Bone(OpenPrimitive.of(x), OpenPrimitive.of(y), OpenPrimitive.of(z));
    }

    private SkinAnimationData.Point effect(String name, String value) {
        return switch (name) {
            case "timeline" -> new SkinAnimationData.Point.Instruct(value);
            case "sound" -> new SkinAnimationData.Point.Sound(value, null);
            case "particle" -> new SkinAnimationData.Point.Particle(value, "", "", null);
            default -> throw new IllegalArgumentException();
        };
    }

    private SkinAnimation createAnimation(String name, float duration, SkinAnimationData.Loop loop, SkinAnimationData.Interpolation interpolation) {
        return createAnimation(name, 0, duration, loop, interpolation);
    }

    private SkinAnimation createAnimation(String name, float offset, float duration, SkinAnimationData.Loop loop, SkinAnimationData.Interpolation interpolation) {
        var data = new SkinAnimationData(name, duration, loop, Collections.newList(
                new SkinAnimationData.Animator("root", 0, Collections.newList(
                        new SkinAnimationData.Keyframe(offset + 0.0f, "position", interpolation, Collections.newList(point(0, 0, 0))),
                        new SkinAnimationData.Keyframe(offset + 1.0f, "position", interpolation, Collections.newList(point(1, 1, 1))),
                        new SkinAnimationData.Keyframe(offset + 2.0f, "position", interpolation, Collections.newList(point(2, 2, 2))),

                        new SkinAnimationData.Keyframe(offset + 0.0f, "rotation", interpolation, Collections.newList(point(0, 0, 0))),
                        new SkinAnimationData.Keyframe(offset + 1.0f, "rotation", interpolation, Collections.newList(point(1, 1, 1))),
                        new SkinAnimationData.Keyframe(offset + 2.0f, "rotation", interpolation, Collections.newList(point(2, 2, 2))),

                        new SkinAnimationData.Keyframe(offset + 0.0f, "scale", interpolation, Collections.newList(point(1.0, 1.0, 1.0))),
                        new SkinAnimationData.Keyframe(offset + 1.0f, "scale", interpolation, Collections.newList(point(1.2, 1.2, 1.2))),
                        new SkinAnimationData.Keyframe(offset + 2.0f, "scale", interpolation, Collections.newList(point(0.8, 0.8, 0.8)))
                )),
                new SkinAnimationData.Animator("armourers:effects", 0, Collections.newList(
                        new SkinAnimationData.Keyframe(offset + 0.0f, "timeline", interpolation, Collections.newList(effect("timeline", "script1"))),
                        new SkinAnimationData.Keyframe(offset + 1.0f, "timeline", interpolation, Collections.newList(effect("timeline", "script2"))),
                        new SkinAnimationData.Keyframe(offset + 2.0f, "timeline", interpolation, Collections.newList(effect("timeline", "script3"))),

                        new SkinAnimationData.Keyframe(offset + 0.0f, "sound", interpolation, Collections.newList(effect("sound", "sound1"))),
                        new SkinAnimationData.Keyframe(offset + 1.0f, "sound", interpolation, Collections.newList(effect("sound", "sound2"))),
                        new SkinAnimationData.Keyframe(offset + 2.0f, "sound", interpolation, Collections.newList(effect("sound", "sound3"))),

                        new SkinAnimationData.Keyframe(offset + 0.0f, "particle", interpolation, Collections.newList(effect("particle", "particle1"))),
                        new SkinAnimationData.Keyframe(offset + 1.0f, "particle", interpolation, Collections.newList(effect("particle", "particle2"))),
                        new SkinAnimationData.Keyframe(offset + 2.0f, "particle", interpolation, Collections.newList(effect("particle", "particle3")))
                ))
        ));
        return linkAnimation(compiler.compile(data));
    }

    private SkinAnimation createAnimation(String name, float duration, SkinAnimationData.Loop loop, List<SkinAnimationData.Keyframe> keyframes) {
        var data = new SkinAnimationData(name, duration, loop, Collections.newList(new SkinAnimationData.Animator("root", 0, keyframes)));
        return linkAnimation(compiler.compile(data));
    }

    private SkinAnimation linkAnimation(SkinAnimation animation) {
        var namedParts = new HashMap<String, SkinPartTransform>();
        namedParts.put("root", new SkinPartTransform(OpenTransform3f.IDENTITY));
        var linker = new SkinAnimationLinker(namedParts);
        linker.link(animation);
        return animation;
    }

    private TestAnimationResult executeAnimation(SkinAnimation animation, int time) {
        // clear context
        context.setVariable(Name.of("timeline.result"), Result.NULL);
        context.setVariable(Name.of("sound.result"), Result.NULL);
        context.setVariable(Name.of("particle.result"), Result.NULL);

        animation.process(time / 1000.0, context);

        // create result evalutor.
        return new TestAnimationResult(context, () -> {
            var result = new SkinAnimationPose();
            animation.affectedTransforms().forEach(it -> it.export(result));
            return result;
        });
    }

    private static class TestAnimationResult {

        private final ExecutionContext context;
        private final Supplier<SkinAnimationPose> provider;

        private TestAnimationResult(ExecutionContext context, Supplier<SkinAnimationPose> provider) {
            this.context = context;
            this.provider = provider;
        }

        public OpenVector3f translation() {
            return provider.get().translation();
        }

        public OpenVector3f rotation() {
            return provider.get().rotation();
        }

        public OpenVector3f scale() {
            return provider.get().scale();
        }

        public Object timeline() {
            return context.getVariable(Name.of("timeline.result")).getAsString();
        }

        public Object sound() {
            return context.getVariable(Name.of("sound.result")).getAsString();
        }

        public Object particle() {
            return context.getVariable(Name.of("particle.result")).getAsString();
        }
    }

    private static class TestAnimationHandler implements OptimizedExpression<Object> {

        private final Name key;
        private final String name;

        private TestAnimationHandler(Name channel, String name) {
            this.name = name;
            this.key = channel;
        }

        @Override
        public Object evaluate(ExecutionContext context) {
            context.setVariable(key, Result.valueOf(name));
            return null;
        }
    }
}
