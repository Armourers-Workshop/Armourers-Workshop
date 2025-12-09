package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.compat.client.item.model.AbstractItemModel;
import moe.plushie.armourers_workshop.core.client.other.DiscoveerableSkinManager;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

@Available("[1.16, 1.22)")
public class AbstractItemModelFinder {

    private static final ResourceLocation MARKER_ID = ResourceLocation.withDefaultNamespace("skin/generated");
    private static final ResourceLocation ITEM_ID = ResourceLocation.withDefaultNamespace("item/generated");

    private static final HashSet<BlockModel> MARK_MODELS = new HashSet<>();

    @Nullable
    public static ResourceLocation resolve(BlockModel model, @Nullable ResourceLocation parent) {
        // we need convert `skin/generated` to `item/generated`.
        if (Objects.equals(parent, MARKER_ID)) {
            MARK_MODELS.add(model);
            return ITEM_ID;
        }
        return parent;
    }

    /// Find a new item model
    public static void accept(BlockModel unbakedModel, BakedModel bakedModel) {
        var name = unbakedModel.name;
        for (var model : MARK_MODELS) {
            if (Objects.equals(model.name, name)) {
                var modelId = OpenResourceLocation.parse(name);
                var itemModel = AbstractItemModel.wrap(bakedModel);
                DiscoveerableSkinManager.getInstance().put(itemModel, modelId);
                break;
            }
        }
    }

    static {
        EventBus.register(DataPackEvent.Reloading.class, event -> {
            if (event.type() == DataPackType.CLIENT_RESOURCES) {
                MARK_MODELS.clear();
            }
        });
    }
}
