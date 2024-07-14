package moe.plushie.armourers_workshop.compatibility.client.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.BoatModel;

@Available("[1.19, )")
@Environment(EnvType.CLIENT)
public class AbstractSkinnableModelImpl {

    public static final Class<BoatModel> RAFT = null;
    public static final Class<AllayModel> ALLAY = AllayModel.class;
}
