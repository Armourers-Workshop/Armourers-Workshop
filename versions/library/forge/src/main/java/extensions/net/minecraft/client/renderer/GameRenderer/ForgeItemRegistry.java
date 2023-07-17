package extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemStackRendererProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class ForgeItemRegistry {

    public static void registerItemRendererFO(@ThisClass Class<?> clazz, Item item, AbstractItemStackRendererProvider provider) {
        ISkinDataProvider provider1 = ObjectUtils.unsafeCast(item);
        BlockEntityWithoutLevelRenderer renderer = provider.create();
        provider1.setSkinData(new IClientItemExtensions() {

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
