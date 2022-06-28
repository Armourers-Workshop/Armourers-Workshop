package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.builder.particle.PaintSplashParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public class ModParticleTypes {

    private static final ArrayList<ParticleType<?>> REGISTERED_ITEMS = new ArrayList<>();

    public static final ParticleType<PaintSplashParticleData> PAINT_SPLASH = register("paint_splash", false, PaintSplashParticleData.Type::new);

    private static <T extends IParticleData> ParticleType<T> register(String name, boolean overrideLimiter, Function<Boolean, ParticleType<T>> builder) {
        ResourceLocation registryName = AWCore.resource(name);
        ParticleType<T> particleType = builder.apply(overrideLimiter);
        particleType.setRegistryName(registryName);
        REGISTERED_ITEMS.add(particleType);
        return particleType;
    }

    public static void forEach(Consumer<ParticleType<?>> action) {
        REGISTERED_ITEMS.forEach(action);
    }
}
