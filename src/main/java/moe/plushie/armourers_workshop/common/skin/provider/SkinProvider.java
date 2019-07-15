package moe.plushie.armourers_workshop.common.skin.provider;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class SkinProvider {

    public static ISkinResult getSkin(ISkinDescriptor skinDescriptor) {
        return getSkin(skinDescriptor, FMLCommonHandler.instance().getSide());
    }

    public static ISkinResult getSkin(ISkinDescriptor skinDescriptor, Side side) {
        switch (side) {
        case CLIENT:
            //return getSkinClient(skinDescriptor);
        case SERVER:
            //return getSkinServer(skinDescriptor);
        }
        return null;
    }
    /*
    private static ISkinResult getSkinClient(ISkinIdentifier identifier, boolean request) {
        return ClientSkinCache.INSTANCE.getSkin(identifier, request);
    }

    private static ISkinResult getSkinServer(ISkinIdentifier identifier, boolean softLoad) {
        return CommonSkinCache.INSTANCE.getSkin(identifier, softLoad);
    }
     */
    public static interface ISkinRequest {

        public ISkinIdentifier getIdentifier();

    }

    public static interface ISkinResult {
        
        public LoadState getLoadState();
        
        public ISkin getSkin();
        
        public static enum LoadState {
            REQUESTED_QUEUED,
            REQUESTED,
            REQUESTED_FAILED,
            BAKING_QUEUED,
            BAKING,
            BAKING_FAILED,
            READY
        }
    }
}
