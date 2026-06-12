package moe.plushie.armourers_workshop.init.platform.runtime;

public interface CommonPlatform {

    CommonBuilderFactory builder();

    CommonRegistryFactory registry();

    CommonRegistryAccessor registryAccess();
}

