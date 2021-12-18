package moe.plushie.armourers_workshop.core.api.client.render;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBakedSkin {

    <T extends ISkin> T getSkin();

    <T extends ISkinDye> T getSkinDye();
}
