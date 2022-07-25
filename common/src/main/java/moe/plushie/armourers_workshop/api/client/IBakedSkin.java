package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.skin.ISkin;

public interface IBakedSkin {

    <T extends ISkin> T getSkin();

//    <T extends ISkinDye> T getSkinDye();
}
