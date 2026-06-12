package moe.plushie.armourers_workshop.init.platform;

public enum PlatformType {
    FABRIC, FORGE;

    public boolean isFabric() {
        return this == FABRIC;
    }

    public boolean isForge() {
        return this == FABRIC;
    }

}
