package moe.plushie.armourers_workshop.core.render.other;

import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;

public class BakedSkin implements IBakedSkin {

    public Skin skin;
    public SkinDye skinDye;

    public  BakedSkin(Skin skin, SkinDye skinDye) {
        this.skin = skin;
        this.skinDye = skinDye;
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public SkinDye getSkinDye() {
        return skinDye;
    }
}
