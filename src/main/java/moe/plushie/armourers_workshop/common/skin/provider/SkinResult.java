package moe.plushie.armourers_workshop.common.skin.provider;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.provider.SkinProvider.ISkinResult;

public class SkinResult implements ISkinResult {

    private final LoadState loadState;
    private final Skin skin;

    public SkinResult(LoadState loadState, Skin skin) {
        this.loadState = loadState;
        this.skin = skin;
    }

    @Override
    public LoadState getLoadState() {
        return loadState;
    }

    @Override
    public ISkin getSkin() {
        return skin;
    }
}
