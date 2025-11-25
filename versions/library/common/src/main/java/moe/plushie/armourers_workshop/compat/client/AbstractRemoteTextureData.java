package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//public class AbstractRemoteTextureData {
//
//    private static final Map<String, AbstractRemoteTextureData> DOWNLOADED_TEXTURES = new HashMap<>();
//
//    private final String url;
//
//    private OpenNativeImage data;
//    private boolean isSlimModel = false;
//
//    public AbstractRemoteTextureData(String url) {
//        this.url = url;
//    }
//
//    @Nullable
//    public static AbstractRemoteTextureData ofNullable(String url) {
//        if (url != null && !url.isEmpty()) {
//            return of(url);
//        }
//        return null;
//    }
//
//    public static AbstractRemoteTextureData of(String url) {
//        return DOWNLOADED_TEXTURES.computeIfAbsent(url, AbstractRemoteTextureData::new);
//    }
//
//    public void setData(OpenNativeImage data) {
//        this.data = data;
//    }
//
//    public OpenNativeImage data() {
//        return data;
//    }
//
//    public void setSlimModel(boolean isSlimModel) {
//        this.isSlimModel = isSlimModel;
//    }
//
//    public boolean isSlimModel() {
//        return isSlimModel;
//    }
//
//    public String url() {
//        return url;
//    }
//}
