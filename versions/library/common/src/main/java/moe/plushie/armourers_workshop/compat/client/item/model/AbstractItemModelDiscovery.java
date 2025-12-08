package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.DiscoveerableSkinManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

@Available("[1.16, 1.22)")
public class AbstractItemModelDiscovery {

    private static final ResourceLocation MARKER_ID = ResourceLocation.withDefaultNamespace("skin/generated");
    private static final BlockModel MARKER = BlockModel.fromString("{\"gui_light\": \"front\"}");

    public static void bake(BlockModel unbakedModel, BakedModel bakedModel) {
        // only support the skin model.
        if (unbakedModel.getRootModel() != MARKER) {
            return;
        }
        var model = OpenResourceLocation.parse(unbakedModel.name);
        DiscoveerableSkinManager.getInstance().put(bakedModel, model);
    }

    public static BlockModel resolve(ResourceLocation id) {
        if (id.equals(MARKER_ID)) {
            return MARKER;
        }
        return null;
    }
}
