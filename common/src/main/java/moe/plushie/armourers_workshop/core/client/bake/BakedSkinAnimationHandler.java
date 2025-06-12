package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("unused")
public class BakedSkinAnimationHandler {

    private final ArrayList<Callback> tasks = new ArrayList<>();
    private final ArrayList<Pair<Integer, Callback>> pending = new ArrayList<>();

    public BakedSkinAnimationHandler() {
        normal((skin, entity, armature, context) -> AnimationEngine.apply(entity, skin, context));
        normal((skin, entity, armature, context) -> SkinRenderHelper.apply(entity, skin, armature, context.itemSource()));
    }

    public void lowest(Callback handler) {
        pending.add(Pair.of(-100, handler));
        rebuild();
    }

    public void low(Callback handler) {
        pending.add(Pair.of(-10, handler));
        rebuild();
    }

    public void normal(Callback handler) {
        pending.add(Pair.of(0, handler));
        rebuild();
    }

    public void high(Callback handler) {
        pending.add(Pair.of(10, handler));
        rebuild();
    }

    public void highest(Callback handler) {
        pending.add(Pair.of(100, handler));
        rebuild();
    }

    public void apply(BakedSkin skin, Entity entity, BakedArmature armature, SkinRenderContext context) {
        for (var task : tasks) {
            task.apply(skin, entity, armature, context);
        }
    }

    private void rebuild() {
        tasks.clear();
        pending.stream().sorted(Comparator.comparingInt(Pair::getLeft)).forEachOrdered(it -> tasks.add(it.getRight()));
    }

    public interface Callback {

        void apply(BakedSkin skin, Entity entity, BakedArmature bakedArmature, SkinRenderContext context);
    }
}
