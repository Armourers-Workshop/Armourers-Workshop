package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import moe.plushie.armourers_workshop.api.common.IEntityHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientStartupEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class ClientProxyImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);

        ClientPickBlockGatherCallback.EVENT.register(this::onPickItem);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.CLIENT, FabricLoader.getInstance().getConfigDir());

        ClientStartupEvents.CLIENT_WILL_START.register(minecraft -> {
            // we will call `ResourceManager.registerReloadListener` in willSetup phase.
            EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);
        });

        ClientStartupEvents.CLIENT_STARTED.register(minecraft -> {
            EnvironmentExecutor.didInit(EnvironmentType.CLIENT);
            EnvironmentExecutor.didSetup(EnvironmentType.CLIENT);
        });
    }

    public ItemStack onPickItem(Player player, HitResult result) {
        EntityHitResult result1 = ObjectUtils.safeCast(result, EntityHitResult.class);
        if (result1 != null) {
            IEntityHandler handler = ObjectUtils.safeCast(result1.getEntity(), IEntityHandler.class);
            if (handler != null) {
                return handler.getCustomPickResult(result);
            }
        }
        return ItemStack.EMPTY;
    }
}
