package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.forge.proxy.ClientProxyImpl;
import moe.plushie.armourers_workshop.init.platform.forge.proxy.CommonProxyImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(ModConstants.MOD_ID)
public class ArmourersWorkshopImpl {

    public ArmourersWorkshopImpl() {
        ArmourersWorkshop.init();
        // start event dispatcher
        CommonProxyImpl.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientProxyImpl::init);
    }
}

