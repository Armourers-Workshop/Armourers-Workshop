package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractClientNativeImpl implements ClientNativeProvider, ClientNativeFactory {
}
