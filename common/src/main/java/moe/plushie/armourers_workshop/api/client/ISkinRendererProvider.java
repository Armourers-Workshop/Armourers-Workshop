package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

@Environment(value = EnvType.CLIENT)
public interface ISkinRendererProvider<T> {

    T create(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel, EntityProfile entityProfile);
}
