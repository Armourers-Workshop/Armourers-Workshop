package moe.plushie.armourers_workshop.core.client.block.tintsource;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface BlockTintSourceType<T extends BlockTintSource> {

    IDataMapCodec<T> codec();
}
