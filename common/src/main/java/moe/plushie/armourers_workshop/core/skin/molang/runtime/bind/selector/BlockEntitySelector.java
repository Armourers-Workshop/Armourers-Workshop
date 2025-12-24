package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface BlockEntitySelector {

    float partialTick();

    default double x() {
        return getX(partialTick());
    }

    default double y() {
        return getY(partialTick());
    }

    default double z() {
        return getZ(partialTick());
    }

    double getX(double partialTick);

    double getY(double partialTick);

    double getZ(double partialTick);
}
