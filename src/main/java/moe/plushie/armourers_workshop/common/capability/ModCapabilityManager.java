package moe.plushie.armourers_workshop.common.capability;

import java.util.concurrent.Callable;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.entity.EntitySkinHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class ModCapabilityManager {
    
    private ModCapabilityManager() {}
    
    public static void register() {
        CapabilityManager.INSTANCE.register(IEntitySkinCapability.class, new EntitySkinStorage(), new Callable<IEntitySkinCapability>() {
            @Override
            public IEntitySkinCapability call() throws Exception {
                return null;
            }});
    }
    
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        ISkinnableEntity skinnableEntity = EntitySkinHandler.INSTANCE.geSkinnableEntity(event.getObject());
        if (skinnableEntity != null) {
            event.addCapability(new ResourceLocation(LibModInfo.ID, "entity-skin-provider"), new EntitySkinProvider(event.getObject(), skinnableEntity));
        }
    }
}
