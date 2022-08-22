package moe.plushie.armourers_workshop;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.*;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.SkinManager;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class ArmourersWorkshop {

    public static final String MOD_ID = "armourers_workshop";
    public static final String MOD_NET_ID = "5";

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
        EnvironmentExecutor.initOn(EnvironmentType.COMMON, () -> () -> {
            // setup network manager.
            NetworkManager.init("play", MOD_NET_ID);
            ModPackets.init();

            ModEntityProfiles.init();
            ModHolidays.init();
            ModPermissions.init();
            SkinningRecipes.init();

            EnvironmentExecutor.run(() -> SkinLibraryManager::startClient, () -> SkinLibraryManager::startServer);
        });
        // setup client in setup.
        EnvironmentExecutor.initOn(EnvironmentType.CLIENT, () -> () -> {
            // setup client objects.
            ModDebugger.init();
            ModKeyBindings.init();
            ClientWardrobeHandler.init();
            CGGraphicsRenderer.init();
        });
        // setup client renderer in finish.
        EnvironmentExecutor.loadOn(EnvironmentType.CLIENT, () -> () -> {
            // setup skin manager.
            SkinManager.init();
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
