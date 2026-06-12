package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.client.renderer.block.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractSpecialModelRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractSpecialModelRenderers;
import moe.plushie.armourers_workshop.core.client.block.model.BlockModelWrapper;
import moe.plushie.armourers_workshop.core.client.block.model.BlockModels;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSources;
import moe.plushie.armourers_workshop.core.client.item.model.ItemModelWrapper;
import moe.plushie.armourers_workshop.core.client.item.model.ItemModels;
import moe.plushie.armourers_workshop.core.client.item.model.SpecialModelWrapper;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSources;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public abstract class AbstractClientRegistry extends AbstractClientRegistryImpl {

    @Override
    protected void init() {
        ItemTintSources.init();
        BlockTintSources.init();
        ItemModels.init();
        BlockModels.init();
        AbstractSpecialModelRenderers.init();
    }

    @Override
    protected void load() {
        // scan all item model settings.
        loadAssetModel("items", Registries.ITEMS, ItemModels.CODEC, (item, itemModel) -> {
            // register item tint source.
            if (itemModel instanceof ItemModelWrapper.Unbaked itemModel1 && itemModel1.hasCustomTint()) {
                registerItemTintSource(item, new AbstractItemTintSource(itemModel1.tints()));
            }
            // register item renderer.
            if (itemModel instanceof SpecialModelWrapper.Unbaked itemModel1 && itemModel1.specialModel() instanceof AbstractSpecialModelRenderer<?> modelRenderer) {
                registerItemSpecialRenderer(item, new AbstractItemSpecialRenderer(itemModel1.base(), modelRenderer));
            }
        });
        // scan all block model settings.
        loadAssetModel("blocks", Registries.BLOCKS, BlockModels.CODEC, (block, blockModel) -> {
            // setup block tint source.
            if (blockModel instanceof BlockModelWrapper.Unbaked blockModel1 && blockModel1.hasCustomTint()) {
                registerBlockTintSource(block, new AbstractBlockTintSource(blockModel1.tints()));
            }
            // setup block render type.
            if (blockModel instanceof BlockModelWrapper.Unbaked blockModel1) {
                registerBlockSpecialRenderer(block, new AbstractBlockSpecialRenderer(blockModel1.renderType()));
            }
        });
    }

    protected abstract void registerItemTintSource(TypedHolder<? extends Item> item, AbstractItemTintSource source);

    protected abstract void registerBlockTintSource(TypedHolder<? extends Block> block, AbstractBlockTintSource source);

    protected abstract void registerItemSpecialRenderer(TypedHolder<? extends Item> item, AbstractItemSpecialRenderer renderer);

    protected abstract void registerBlockSpecialRenderer(TypedHolder<? extends Block> block, AbstractBlockSpecialRenderer renderer);

    /// load an asset model by the registry
    private <T, M> void loadAssetModel(String type, TypedRegistry<T> registry, IDataCodec<M> codec, BiConsumer<TypedHolder<? extends T>, M> consumer) {
        var modelKey = IDataSerializerKey.create("model", codec);
        var classLoader = getClass().getClassLoader();
        registry.forEach(holder -> {
            var registryName = holder.registryName();
            var path = String.format("assets/%s/%s/%s.json", registryName.namespace(), type, registryName.path());
            try (var inputStream = classLoader.getResourceAsStream(path)) {
                var serializer = new JsonSerializer(inputStream);
                var itemModel = serializer.read(modelKey);
                if (itemModel != null) {
                    consumer.accept(holder, itemModel);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
