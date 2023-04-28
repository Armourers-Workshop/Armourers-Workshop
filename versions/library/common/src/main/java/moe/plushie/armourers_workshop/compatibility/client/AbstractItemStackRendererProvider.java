package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface AbstractItemStackRendererProvider {

    @Environment(value = EnvType.CLIENT)
    AbstractItemStackRenderer create();
}
