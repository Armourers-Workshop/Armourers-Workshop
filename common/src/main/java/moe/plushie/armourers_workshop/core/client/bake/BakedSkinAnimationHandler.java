package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class BakedSkinAnimationHandler {

    private final ArrayList<Callback> tasks = new ArrayList<>();
    private final ArrayList<Pair<Integer, Callback>> pending = new ArrayList<>();

    public BakedSkinAnimationHandler() {
        normal((renderState, skin, armature, context) -> AnimationEngine.apply(renderState, skin, context));
        normal((renderState, skin, armature, context) -> SkinRenderHelper.apply(renderState, skin, armature, context.itemSource()));
    }

    public void lowest(Callback callback) {
        pending.add(Pair.of(-100, callback));
        rebuild();
    }

    public void low(Callback callback) {
        pending.add(Pair.of(-10, callback));
        rebuild();
    }

    public void normal(Callback callback) {
        pending.add(Pair.of(0, callback));
        rebuild();
    }

    public void high(Callback callback) {
        pending.add(Pair.of(10, callback));
        rebuild();
    }

    public void highest(Callback callback) {
        pending.add(Pair.of(100, callback));
        rebuild();
    }

    public void apply(EntityRenderState renderState, BakedSkin skin, BakedArmature armature, ConcurrentRenderingContext context) {
        for (var task : tasks) {
            task.apply(renderState, skin, armature, context);
        }
    }

    private void rebuild() {
        tasks.clear();
        pending.stream().sorted(Comparator.comparingInt(Pair::getLeft)).forEachOrdered(it -> tasks.add(it.getRight()));
    }

    @FunctionalInterface
    public interface Callback {

        void apply(EntityRenderState renderState, BakedSkin skin, BakedArmature armature, ConcurrentRenderingContext context);
    }
}
