package moe.plushie.armourers_workshop.init.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

@Environment(value = EnvType.CLIENT)
public class ClientProxy {

    public static void init() {
        //            SkinResourceManager.init();
        ClientWardrobeHandler.init();
        ModKeyBindings.init();
        ModDebugger.init();

        register(ClientNativeManager.getProvider());
    }

    private static void register(ClientNativeProvider registries) {
        // register custom item color.
        registries.willRegisterItemColor(registry -> Registry.ITEM.getEntries().forEach(object -> {
            Item item = object.get();
            if (item instanceof IItemTintColorProvider) {
                registry.register(((IItemTintColorProvider) item), item);
            }
        }));
        // register custom item property.
        registries.willRegisterItemProperty(registry -> Registry.ITEM.getEntries().forEach(object -> {
            Item item = object.get();
            IItemPropertiesProvider provider = ObjectUtils.safeCast(item, IItemPropertiesProvider.class);
            if (provider != null) {
                provider.createModelProperties((key, property) -> registry.register(key, item, property));
            }
        }));

        // register custom block color.
        registries.willRegisterBlockColor(registry -> Registry.BLOCK.getEntries().forEach(object -> {
            Block block = object.get();
            if (block instanceof IBlockTintColorProvider) {
                registry.register(((IBlockTintColorProvider) block), block);
            }
        }));

        // register custom model.
        registries.willRegisterModel(registry -> SkinPartTypes.registeredTypes().forEach(partType -> {
            ResourceLocation rl = ArmourersWorkshop.getCustomModel(partType.getRegistryName());
            IResourceManager resourceManager = ClientNativeManager.getResourceManager();
            if (resourceManager.hasResource(new ResourceLocation(rl.getNamespace(), "models/item/" + rl.getPath() + ".json"))) {
                registry.register(rl);
            }
        }));
        // register custom sprite
        registries.willRegisterTexture(registry -> Stream.of(SkinSlotType.values()).forEach(slotType -> {
            registry.register(slotType.getIconSprite());
        }));

        registries.willPlayerLogin(player -> {
            SkinBakery.start();
        });
        registries.willPlayerLogout(player -> {
            SkinBakery.stop();
            SkinLoader.getInstance().clear();
            SkinLibraryManager.getClient().getPublicSkinLibrary().reset();
            SkinLibraryManager.getClient().getPrivateSkinLibrary().reset();
            ModContext.reset();
            ModConfigSpec.COMMON.apply(null);
        });

        registries.willTick(TickUtils::tick);

        registries.willGatherTooltip(ItemTooltipManager::appendHoverText);
        registries.willRenderTooltip(ItemTooltipManager::renderHoverText);
    }
}
