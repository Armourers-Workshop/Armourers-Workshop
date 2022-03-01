package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PlayerNetworkHandler {

    @SubscribeEvent
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.start();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (Objects.equals(event.getPlayer(), Minecraft.getInstance().player)) {
            SkinBakery.stop();
            SkinLoader.getInstance().clear();
        }
    }
}
