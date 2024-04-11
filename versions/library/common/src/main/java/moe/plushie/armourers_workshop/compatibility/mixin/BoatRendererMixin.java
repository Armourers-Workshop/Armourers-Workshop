package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IEntityModelProvider;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Available("[1.20, )")
@Mixin(BoatRenderer.class)
public class BoatRendererMixin<T extends Boat> implements IEntityModelProvider<Boat, ListModel<Boat>> {

    @Shadow
    @Final
    private Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    @Unique
    @Override
    public ListModel<Boat> getModel(Boat entity) {
        return boatResources.get(entity.getVariant()).getSecond();
    }
}
