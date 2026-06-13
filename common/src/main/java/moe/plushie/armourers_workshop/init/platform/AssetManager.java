package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.client.renderer.block.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.core.client.block.model.BlockModelWrapper;
import moe.plushie.armourers_workshop.core.client.block.model.BlockModels;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSources;
import moe.plushie.armourers_workshop.core.client.item.model.ItemModelWrapper;
import moe.plushie.armourers_workshop.core.client.item.model.ItemModels;
import moe.plushie.armourers_workshop.core.client.item.model.SpecialModelWrapper;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSources;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderers;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientEventAccessor;
import moe.plushie.armourers_workshop.init.registry.Registries;

import java.util.function.BiConsumer;

public class AssetManager {

    public static void init() {
        // registry client model attachments.
        ItemTintSources.init();
        BlockTintSources.init();
        ItemModels.init();
        BlockModels.init();
        SpecialModelRenderers.init();
        // start load client assets.
        loadAssets(Platform.get().client().eventAccessor());
    }

    private static void loadAssets(ClientEventAccessor api) {
        // scan all item model settings.
        loadAssetModel("items", Registries.ITEMS, ItemModels.CODEC, (item, itemModel) -> {
            // register item tint source.
            if (itemModel instanceof ItemModelWrapper.Unbaked itemModel1 && itemModel1.hasCustomTint()) {
                api.registerItemTintSource(item, new AbstractItemTintSource(itemModel1.tints()));
            }
            // register item renderer.
            if (itemModel instanceof SpecialModelWrapper.Unbaked itemModel1) {
                api.registerItemSpecialRenderer(item, new AbstractItemSpecialRenderer(itemModel1.base(), itemModel1.specialModel()));
            }
        });
        // scan all block model settings.
        loadAssetModel("blocks", Registries.BLOCKS, BlockModels.CODEC, (block, blockModel) -> {
            // setup block tint source.
            if (blockModel instanceof BlockModelWrapper.Unbaked blockModel1 && blockModel1.hasCustomTint()) {
                api.registerBlockTintSource(block, new AbstractBlockTintSource(blockModel1.tints()));
            }
            // setup block render type.
            if (blockModel instanceof BlockModelWrapper.Unbaked blockModel1) {
                api.registerBlockSpecialRenderer(block, new AbstractBlockSpecialRenderer(blockModel1.renderType()));
            }
        });
    }

    /// load an asset model by the registry
    private static <T, M> void loadAssetModel(String type, TypedRegistry<T> registry, IDataCodec<M> codec, BiConsumer<TypedHolder<? extends T>, M> consumer) {
        var modelKey = IDataSerializerKey.create("model", codec);
        var classLoader = AssetManager.class.getClassLoader();
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
