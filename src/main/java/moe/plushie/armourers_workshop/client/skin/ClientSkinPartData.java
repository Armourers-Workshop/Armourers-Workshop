package moe.plushie.armourers_workshop.client.skin;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.SkinModel;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData.ModelKey;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSkinPartData implements RemovalListener<ModelKey, SkinModel> {

    /** Blank dye that is used if no dye is applied. */
    public static final SkinDye BLANK_DYE = new SkinDye();
    public ArrayList<ColouredFace>[] vertexLists;
    public final LoadingCache<ModelKey, SkinModel> modelCache;
    public int[] totalCubesInPart;

    private int[] averageR = new int[12];
    private int[] averageG = new int[12];
    private int[] averageB = new int[12];

    public ClientSkinPartData() {
        CacheBuilder builder = CacheBuilder.newBuilder();
        builder.removalListener(this);
        if (ConfigHandlerClient.modelPartCacheExpireTime > 0) {
            builder.expireAfterAccess(ConfigHandlerClient.modelPartCacheExpireTime, TimeUnit.SECONDS);
        }
        if (ConfigHandlerClient.modelPartCacheMaxSize > 0) {
            builder.maximumSize(ConfigHandlerClient.skinCacheMaxSize);
        }
        modelCache = builder.build(new ModelLoader());
    }

    public SkinModel getModelForDye(SkinPartRenderData renderData) {
        ModelKey modelKey = new ModelKey(renderData.getSkinDye(), renderData.getExtraColours(), renderData.getEntityTexture());
        return modelCache.getUnchecked(modelKey);
    }

    public void cleanUpDisplayLists() {
        modelCache.invalidateAll();
    }

    public int getModelCount() {
        return (int) modelCache.size();
    }

    public void setVertexLists(ArrayList<ColouredFace>[] vertexLists) {
        this.vertexLists = vertexLists;
    }

    public void setAverageDyeValues(int[] r, int[] g, int[] b) {
        this.averageR = r;
        this.averageG = g;
        this.averageB = b;
    }

    public int[] getAverageDyeColour(int dyeNumber) {
        return new int[] { averageR[dyeNumber], averageG[dyeNumber], averageB[dyeNumber] };
    }

    @Override
    public void onRemoval(RemovalNotification<ModelKey, SkinModel> notification) {
        notification.getValue().cleanUpDisplayLists();
    }

    private class ModelLoader extends CacheLoader<ModelKey, SkinModel> {

        @Override
        public SkinModel load(ModelKey key) throws Exception {
            return new SkinModel(vertexLists);
        }
    }

    public static class ModelKey {

        private ISkinDye skinDye;
        private IExtraColours extraColours;
        private ResourceLocation entityTexture;

        public ModelKey(ISkinDye skinDye, IExtraColours extraColours, ResourceLocation entityTexture) {
            this.skinDye = skinDye;
            this.extraColours = extraColours;
            this.entityTexture = entityTexture;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((entityTexture == null) ? 0 : entityTexture.hashCode());
            result = prime * result + ((extraColours == null) ? 0 : extraColours.hashCode());
            result = prime * result + ((skinDye == null) ? 0 : skinDye.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ModelKey other = (ModelKey) obj;
            if (entityTexture == null) {
                if (other.entityTexture != null)
                    return false;
            } else if (!entityTexture.equals(other.entityTexture))
                return false;
            if (extraColours == null) {
                if (other.extraColours != null)
                    return false;
            } else if (!extraColours.equals(other.extraColours))
                return false;
            if (skinDye == null) {
                if (other.skinDye != null)
                    return false;
            } else if (!skinDye.equals(other.skinDye))
                return false;
            return true;
        }

    }
}
