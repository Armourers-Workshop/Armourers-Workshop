package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.item.model.AbstractItemModel;
import moe.plushie.armourers_workshop.core.client.other.DiscoveerableSkinManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

@Available("[1.16, 1.22)")
public class AbstractItemModelFinder {

    private static final ResourceLocation MARKER_ID = ResourceLocation.withDefaultNamespace("skin/generated");
    private static final BlockModel MARKER = BlockModel.fromString("{\"gui_light\": \"front\"}");

    /// Find a new item model
    public static void accept(BlockModel unbakedModel, BakedModel bakedModel) {
        // only support the skin model.
        if (unbakedModel.getRootModel() != MARKER) {
            return;
        }
        var model = OpenResourceLocation.parse(unbakedModel.name);
        var itemModel = AbstractItemModel.wrap(bakedModel);
        DiscoveerableSkinManager.getInstance().put(itemModel, model);
    }

    public static BlockModel resolve(ResourceLocation id) {
        if (id.equals(MARKER_ID)) {
            return MARKER;
        }
        return null;
    }
}
