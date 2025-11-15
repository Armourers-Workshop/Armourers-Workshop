package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.item.ItemStack;

import moe.plushie.armourers_workshop.core.client.render.model.EmbeddedItemModel;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class EmbeddedModelProvider {

    private static final DataContainer.Key<Storage> KEY = DataContainer.key("EmbeddedItemModels", Storage::new);

    public static void setEmbeddedItemModel(@This ItemStack itemStack, Object key, EmbeddedItemModel itemModel) {
        DataContainer.of(itemStack, KEY).put(key, itemModel);
    }

    @Nullable
    public static EmbeddedItemModel getEmbeddedItemModel(@This ItemStack itemStack, Object key) {
        return DataContainer.of(itemStack, KEY).get(key);
    }

    private static class Storage {

        private final IdentityHashMap<Object, EmbeddedItemModel> models = new IdentityHashMap<>();

        private Storage(ItemStack itemStack) {
        }

        public void put(Object key, @Nullable EmbeddedItemModel model) {
            if (model != null) {
                models.put(key, model);
            } else {
                models.remove(key);
            }
        }

        public EmbeddedItemModel get(Object key) {
            return models.get(key);
        }
    }
}
