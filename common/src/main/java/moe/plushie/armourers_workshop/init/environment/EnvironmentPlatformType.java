package moe.plushie.armourers_workshop.init.environment;

public enum EnvironmentPlatformType {
    FABRIC, FORGE;

    public boolean isFabric() {
        return this == FABRIC;
    }

    public boolean isForge() {
        return this == FABRIC;
    }

}
