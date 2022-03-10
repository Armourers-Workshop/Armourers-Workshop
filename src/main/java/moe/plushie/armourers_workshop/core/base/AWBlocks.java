package moe.plushie.armourers_workshop.core.base;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class AWBlocks {

    private static final HashMap<ResourceLocation, Block> REGISTERED_BLOCKS = new HashMap<>();

    public static final Block HOLOGRAM_PROJECTOR = register("hologram-projector", HologramProjectorBlock::new, p -> p.requiresCorrectToolForDrops().lightLevel(litBlockEmission(13)).strength(5.0F, 1200.0F));

    private static ToIntFunction<BlockState> litBlockEmission(int level) {
        return state -> state.getValue(BlockStateProperties.LIT) ? level : 0;
    }

    private static <T extends Block> T register(String name, Function<Block.Properties, T> factory) {
        return register(name, factory, null);
    }

    private static <T extends Block> T register(String name, Function<Block.Properties, T> factory, Consumer<Block.Properties> customizer) {
        Block.Properties properties = AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_RED);
        if (customizer != null) {
            customizer.accept(properties);
        }
        ResourceLocation registryName = AWCore.resource(name);
        T block = factory.apply(properties);
        block.setRegistryName(registryName);
        REGISTERED_BLOCKS.put(registryName, block);
        return block;
    }

    public static void forEach(Consumer<Block> action) {
        REGISTERED_BLOCKS.values().forEach(action);
    }
}
