package moe.plushie.armourers_workshop.api.client.render;

import moe.plushie.armourers_workshop.api.skin.ISkin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBakedSkin {

    <T extends ISkin> T getSkin();

//    <T extends ISkinDye> T getSkinDye();
}
