package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ContextSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LevelSelector;
import net.minecraft.world.entity.Entity;

public class ContextSelectorImpl implements ContextSelector {

    private int id = 0;

    private double beginTime = 0;
    private double animTime = 0;

    private double animationTicks = 0;
    private float partialTicks = 0;

    public void upload(int id, double beginTime, double animTime, double animationTicks, float partialTicks) {
        this.id = id;
        this.beginTime = beginTime;
        this.animTime = animTime;
        this.animationTicks = animationTicks;
        this.partialTicks = partialTicks;
    }

    public int id() {
        return id;
    }

    @Override
    public float partialTick() {
        return partialTicks;
    }

    @Override
    public double animationTicks() {
        return animationTicks;
    }

    @Override
    public double animTime() {
        return animTime;
    }

    @Override
    public double lifeTime() {
        return animationTicks - beginTime;
    }

    @Override
    public double fps() {
        return -1;
    }


    @Override
    public LevelSelector level() {
        return null;
    }

    public double getCameraDistanceFormEntity(Entity entity) {
        return 0;
    }

    @Override
    public int entityCount() {
        return 0;
    }

    @Override
    public boolean isRenderingInInventory() {
        return false;
    }

    @Override
    public boolean isRenderingInFirstPersonMod() {
        return false;
    }

    @Override
    public boolean isFirstPerson() {
        return false;
    }
}
