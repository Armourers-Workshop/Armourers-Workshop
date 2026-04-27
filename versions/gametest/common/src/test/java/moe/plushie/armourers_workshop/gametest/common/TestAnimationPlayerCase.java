package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationCompiler;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationLinker;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationPose;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationPlayer;
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
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.SAME_THREAD)
public class TestAnimationPlayerCase {

    private ExecutionContextImpl context;
    private SkinAnimationCompiler compiler;
    private TestAnimationPlayer player;

    @BeforeEach
    public void setUp() {
        context = new ExecutionContextImpl(new StaticVariableStorage());
        compiler = new SkinAnimationCompiler(new MolangVirtualMachine()) {

            @Override
            protected OptimizedExpression<?> compile(SkinAnimationData data, SkinAnimationData.Point point) {
//                if (point instanceof SkinAnimationData.Point.Instruct instruct) {
//                    return new TestAnimationHandler(Name.of("timeline.result"), instruct.script());
//                }
//                if (point instanceof SkinAnimationData.Point.Sound sound) {
//                    return new TestAnimationHandler(Name.of("sound.result"), sound.effect());
//                }
//                if (point instanceof SkinAnimationData.Point.Particle particle) {
//                    return new TestAnimationHandler(Name.of("particle.result"), particle.effect());
//                }
                return super.compile(data, point);
            }
        };
        player = new TestAnimationPlayer(context);
    }


    @Test
    public void testLoadAndActive() {
        TickUtils.setTime(0);

        var animation = createAnimation("parallel1", 2.0f, SkinAnimationData.Loop.LOOP, SkinAnimationData.Interpolation.linear());
        var skin = new TestAnimation(Collections.newList(animation));
        var values = new HashMap<String, TestAnimation>();
        values.put("mainhand", skin);

        player.load(values);
        assertEquals(1, player.loadedCount());
        assertEquals(0, player.activeCount());

        player.process(skin, 1.0, 0.0f);
        assertEquals(vec3(0.000000, 0.000000, 0.000000), captureAnimationResult(animation).translation());

        player.active(values);
        assertEquals(1, player.loadedCount());
        assertEquals(1, player.activeCount());

        player.process(skin, 1.0, 0.0f);
        var result = captureAnimationResult(animation);
        assertEquals(vec3(1.000000, 1.000000, 1.000000), result.translation());
        assertEquals(vec3(1.000000, 1.000000, 1.000000), result.rotation());
        assertEquals(vec3(1.200000, 1.200000, 1.200000), result.scale());
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

    private TestAnimationResult captureAnimationResult(SkinAnimation animation) {
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

    private static class TestAnimationPlayer extends SkinAnimationPlayer<String, TestAnimation> {

        final ExecutionContextImpl context;

        private TestAnimationPlayer(ExecutionContextImpl context) {
            this.context = context;
        }

        @Override
        protected Item createItem(TestAnimation value) {
            return new Item(value.id, value.animations, context);
        }

        @Override
        protected EntityActionSet extractState(Object source) {
            return null;
        }

        public int loadedCount() {
            return allItems.size();
        }

        public int activeCount() {
            return activeItems.size();
        }
    }

    private static class TestAnimation {

        final int id = OpenRandomSource.nextInt(TestAnimation.class);

        final List<SkinAnimation> animations;

        private TestAnimation() {
            this.animations = Collections.emptyList();
        }

        private TestAnimation(List<SkinAnimation> animations) {
            this.animations = animations;
        }
    }
}
