package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import manifold.ext.rt.api.auto;

@Available("[21, )")
public class BuiltInRegistriesExt {

    public static final auto ITEM = BuiltInRegistries.ITEM;
    public static final auto ITEM_TAG = Registries.ITEM;
    public static final auto CREATIVE_MODE_TAB = Registries.CREATIVE_MODE_TAB;
    public static final auto LOOT_FUNCTION_TYPE = Registries.LOOT_FUNCTION_TYPE;

    public static final auto BLOCK = BuiltInRegistries.BLOCK;
    public static final auto BLOCK_TAG = Registries.BLOCK;
    public static final auto BLOCK_ENTITY_TYPE = BuiltInRegistries.BLOCK_ENTITY_TYPE;

    public static final auto ENTITY_TYPE = BuiltInRegistries.ENTITY_TYPE;
    public static final auto ENTITY_DATA_SERIALIZER = NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS;

    public static final auto MENU = BuiltInRegistries.MENU;
    public static final auto SOUND_EVENT = BuiltInRegistries.SOUND_EVENT;

    public static final auto DATA_COMPONENT_TYPE = BuiltInRegistries.DATA_COMPONENT_TYPE;
}
