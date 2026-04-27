package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.item.Item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.item.Item;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[16, 26)")
@Extension
public class NameExt {

    @Extension
    public static class Properties {

        public static Item.Properties setId(@This Item.Properties properties, OpenResourceKey id) {
            return properties;
        }

        public static Item.Properties overrideDescription(@This Item.Properties properties, String key) {
            return properties;
        }

        public static Item.Properties useDescriptionPrefix(@This Item.Properties properties, String prefix) {
            return properties;
        }
    }
}
