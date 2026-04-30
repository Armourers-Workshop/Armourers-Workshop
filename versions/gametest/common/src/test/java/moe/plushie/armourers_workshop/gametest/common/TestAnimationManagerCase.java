package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationCompiler;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationManager;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.core.utils.ScheduledExpression;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.gametest.utils.TestAnimation;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.SAME_THREAD)
public class TestAnimationManagerCase {

    private ExecutionContextImpl context;
    private SkinAnimationCompiler compiler;

    private AnimationManager animationManager;

    @BeforeEach
    public void setUp() {
        context = new ExecutionContextImpl(new StaticVariableStorage());
        compiler = new SkinAnimationCompiler(new MolangVirtualMachine()) {

            @Override
            protected OptimizedExpression<?> compile(SkinAnimationData data, SkinAnimationData.Point point) {
                if (point instanceof SkinAnimationData.Point.Instruct instruct) {
                    return new AnimationEffect(Name.of("timeline.result"), data.name() + "/" + instruct.script());
                }
                if (point instanceof SkinAnimationData.Point.Sound sound) {
                    return new AnimationEffect(Name.of("sound.result"), data.name() + "/" + sound.effect());
                }
                if (point instanceof SkinAnimationData.Point.Particle particle) {
                    return new AnimationEffect(Name.of("particle.result"), data.name() + "/" + particle.effect());
                }
                return super.compile(data, point);
            }
        };
        animationManager = new AnimationManager(context);
    }


    private void setTime(double t) {
        TickUtils.setTime((long) (t * 1000000L));
    }

    @Test
    public void testLoadAndActive() {
        var skins = new HashMap<String, TestAnimation>();
        skins.put("s1", animation("parallel1", 2.0f, "linear", "loop"));
        skins.put("s2", animation("attack1", 2.0f, "linear", "loop"));
        skins.put("s3", animation("idle", 2.0f, "linear", "loop"));

        setTime(0.0);
        animationManager.load(skins);
        assertEquals(3, animationManager.allItems().size());
        assertEquals(0, animationManager.activeItems().size());
        assertEquals(0, animationManager.triggerableItems().size());

        setTime(0.0);
        animationManager.active(skins);
        assertEquals(3, animationManager.allItems().size());
        assertEquals(3, animationManager.activeItems().size());
        assertEquals(2, animationManager.triggerableItems().size());

        // check result.

        setTime(1.0);
        animationManager.tick(EntityActionSet.IDLE, TickUtils.animationTick());
        animationManager.process(EntityActionSet.IDLE, TickUtils.animationTick());

        setTime(0.0);
        animationManager.load(new HashMap<>());
        assertEquals(0, animationManager.allItems().size());
        assertEquals(3, animationManager.activeItems().size());
        assertEquals(2, animationManager.triggerableItems().size());

        setTime(0.0);
        animationManager.active(new HashMap<>());
        assertEquals(0, animationManager.allItems().size());
        assertEquals(0, animationManager.activeItems().size());
        assertEquals(0, animationManager.triggerableItems().size());
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


    private TestAnimation animation(String name, double duration, Object interpolation, Object loop) {
        return animation(name, duration, loop, it -> {
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

    private TestAnimation animation(String name, double duration, Object loop, Consumer<TestAnimation.Animators> factory) {
        var namedParts = new HashMap<String, SkinPartTransform>();
        namedParts.put("root", new SkinPartTransform(OpenTransform3f.IDENTITY));
        var builder = new TestAnimation.Builder(compiler, namedParts);
        return builder.build(name, 0, duration, loop, factory);
    }


    private static class AnimationEffect implements ScheduledExpression<Runnable> {

        private final Name key;
        private final String name;

        private AnimationEffect(Name channel, String name) {
            this.name = name;
            this.key = channel;
        }

        @Override
        public Runnable submit(ExecutionContext context) {
            context.setVariable(key, Result.valueOf(name));
            return () -> context.setVariable(key, Result.NULL);
        }

        @Override
        public void cancel(Runnable result) {
            result.run();
        }
    }

    private static class AnimationManager extends SkinAnimationManager<String, TestAnimation> {

        final ExecutionContextImpl context;

        private AnimationManager(ExecutionContextImpl context) {
            this.context = context;
        }

        public void process(@Nullable Object value, double animationTick) {
            for (var skin : allItems().keySet()) {
                super.process(skin, animationTick, 0.0f);
            }
        }

        @Override
        protected Item createItem(TestAnimation value) {
            return new Item(value.id(), value.animations(), context);
        }

        @Override
        protected EntityActionSet extractState(Object source) {
            return (EntityActionSet) source;
        }

        public Map<TestAnimation, Item> allItems() {
            return allItems;
        }

        public Map<TestAnimation, Item> activeItems() {
            return activeItems;
        }

        public List<Item> triggerableItems() {
            return triggerableItems;
        }
    }
}
