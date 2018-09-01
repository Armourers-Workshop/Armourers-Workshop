package moe.plushie.armourers_workshop.client.model;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomModel {

    @SideOnly(Side.CLIENT)
    public void registerModels();

}
