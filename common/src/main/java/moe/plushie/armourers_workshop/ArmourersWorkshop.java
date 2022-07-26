package moe.plushie.armourers_workshop;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.*;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class ArmourersWorkshop {

    public static final String MOD_ID = "armourers_workshop";

    public static void init() {
        ModItemGroups.init();
        ModItems.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModEntities.init();
        ModCapabilities.init();
        ModMenus.init();
        ModSounds.init();
        ModConfig.init();
        // setup common objects.
        EnvironmentExecutor.setupOn(EnvironmentType.COMMON, () -> () -> {
            // setup network manager.
            NetworkManager.init("aw2", "3");
            ModPackets.init();

            ModHolidays.init();
            ModPermissions.init();
            ModEntityProfiles.init();
            SkinningRecipes.init();

            EnvironmentExecutor.run(() -> SkinLibraryManager::startClient, () -> SkinLibraryManager::startServer);
        });
        // setup client only objects.
        EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, () -> () -> {
            // setup client objects.
            ModDebugger.init();
            ModKeyBindings.init();
            ClientWardrobeHandler.init();

            // setup skin renderer manager.
            SkinRendererManager.init();
        });
    }

    public static ResourceLocation getResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ResourceLocation getItemIcon(ISkinType skinType) {
        if (skinType == SkinTypes.UNKNOWN || skinType.getRegistryName() == null) {
            return null;
        }
        return getResource("textures/item/template/" + skinType.getRegistryName().getPath() + ".png");
    }

    public static ModelResourceLocation getCustomModel(ResourceLocation resourceLocation) {
        String name = resourceLocation.getPath().toLowerCase();
        name = name.replaceAll("\\.base", "");
        name = name.replaceAll("\\.", "_");
        return new ModelResourceLocation(getResource("skin/" + name), "inventory");
    }

}