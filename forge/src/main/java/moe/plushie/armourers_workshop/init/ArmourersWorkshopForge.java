package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(ArmourersWorkshop.MOD_ID)
public class ArmourersWorkshopForge {

    public ArmourersWorkshopForge() {
        ArmourersWorkshop.init();
        // start event dispatcher
        CommonEventDispatcher.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventDispatcher::init);
    }
}

