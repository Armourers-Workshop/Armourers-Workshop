package moe.plushie.armourers_workshop.init.platform.runtime;

public interface ClientPlatform {

    ClientBuilderFactory builder();

    ClientRegistryFactory registry();

    ClientEventAccessor eventAccessor();
}
