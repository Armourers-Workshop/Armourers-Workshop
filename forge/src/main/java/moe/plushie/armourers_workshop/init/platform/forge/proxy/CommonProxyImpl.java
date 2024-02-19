package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.CommonNativeManagerImpl;
import moe.plushie.armourers_workshop.init.platform.forge.EnvironmentManagerImpl;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ConfigBuilderImpl;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class CommonProxyImpl {

    public static void init() {
        ArmourersWorkshop.init();

        CommonNativeManagerImpl.INSTANCE.willServerStart(EnvironmentManagerImpl::attach);
        CommonNativeManagerImpl.INSTANCE.didServerStop(EnvironmentManagerImpl::detach);

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        // listen the fml events.
        Registry.willCommonSetupFO(event -> EnvironmentExecutor.didInit(EnvironmentType.COMMON));
        Registry.willLoadCompleteFO(event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.COMMON)));

        // listen the config changes events.
        Registry.willConfigReloadFO(spec -> {
            ConfigBuilderImpl.reloadSpec(ModConfigSpec.CLIENT, spec);
            ConfigBuilderImpl.reloadSpec(ModConfigSpec.COMMON, spec);
        });
        Registry.shouldEntityAttackFO((entity, player) -> {
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }
            ItemStack itemStack = player.getMainHandItem();
            IItemHandler handler = ObjectUtils.safeCast(itemStack.getItem(), IItemHandler.class);
            if (handler != null) {
                InteractionResult result = handler.attackLivingEntity(itemStack, player, entity);
                if (result.consumesAction()) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }
}
