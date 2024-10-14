package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;

@Environment(EnvType.CLIENT)
public class FindableSkinManager {

    private final static FindableSkinManager INSTANCE = new FindableSkinManager();

    private static final BlockModel GENERATION_MARKER = Util.make(
            BlockModel.fromString("{\"gui_light\": \"front\"}"), blockModel -> blockModel.name = "skin generation marker"
    );

    private final HashMap<String, Entry> allEntries = new HashMap<>();
    private final IdentityHashMap<BakedModel, Entry> bakedModels = new IdentityHashMap<>();

    private SkinBakery bakery;

    private FindableSkinManager() {
        EventManager.listen(DataPackEvent.Reloading.class, this::didReload);
    }

    public static FindableSkinManager getInstance() {
        return INSTANCE;
    }

    public void start() {
        bakery = SkinBakery.getInstance();
        for (var entry : allEntries.values()) {
            entry.preload(bakery);
        }
    }

    public void stop() {
        bakery = null;
    }

    public SkinDescriptor getSkin(BakedModel bakedModel) {
        var entry = bakedModels.get(bakedModel);
        if (entry != null && entry.canUse()) {
            return entry.descriptor;
        }
        return SkinDescriptor.EMPTY;
    }

    public void bakeSkinModel(BlockModel unbakedModel, BakedModel bakedModel) {
        // only support the skin model.
        if (unbakedModel.getRootModel() != GENERATION_MARKER) {
            return;
        }
        // load a skin from the model file.
        var model = OpenResourceLocation.parse(unbakedModel.name);
        var entry = loadSkinProvider(model);
        if (entry == null) {
            return;
        }
        ModLog.debug("Registering resource pack skin: '{}' in '{}'", entry.identifier, model);
        bakedModels.put(bakedModel, entry);
        allEntries.put(entry.identifier, entry);
        if (bakery != null) {
            entry.preload(bakery);
        }
    }

    @Nullable
    public BlockModel loadSkinModel(ResourceLocation location) {
        var path = location.getPath();
        if (path.startsWith("skin/generated")) {
            return GENERATION_MARKER;
        }
        return null;
    }

    private Entry loadSkinProvider(IResourceLocation id) {
        try {
            var resourceManager = EnvironmentManager.getResourceManager();
            var location = OpenResourceLocation.create(id.getNamespace(), "models/" + id.getPath() + ".json");
            var rootObject = StreamUtils.fromPackObject(resourceManager.readResource(location));
            if (rootObject == null) {
                return null;
            }
            var firstObject = rootObject.get("providers").get("layer0");
            return new Entry(firstObject.stringValue());
        } catch (Exception e) {
            ModLog.warn("Unable to bake model: '{}', {}", id, e.getMessage());
        }
        return null;
    }

    public void didReload(DataPackEvent.Reloading event) {
        // when resource pack did changes, we need to clear invalid resource.
        if (event.getType() == DataPackType.CLIENT_RESOURCES) {
            ModLog.debug("Reloading resource pack skins");
            allEntries.clear();
            bakedModels.clear();
        }
    }

    public static class Entry {

        private final String identifier;
        private final boolean isLocalFile;

        private SkinDescriptor descriptor;

        public Entry(String identifier) {
            this.identifier = identifier;
            this.isLocalFile = DataDomain.isLocal(identifier);
            this.descriptor = new SkinDescriptor(identifier);
        }

        public void preload(SkinBakery bakery) {
            // when we can't use this skin, don't preload it.
            if (!canUse()) {
                return;
            }
            ModLog.debug("'{}' => start preload skin", identifier);
            descriptor = new SkinDescriptor(identifier);
            bakery.loadSkin(identifier, Tickets.PRELOAD, this::complete);
        }

        public void complete(BakedSkin bakedSkin, Exception exception) {
            // ignore all load exception.
            if (exception != null) {
                return;
            }
            ModLog.debug("'{}' => did preload skin", identifier);
            descriptor = new SkinDescriptor(identifier, bakedSkin.getType());
        }

        public boolean canUse() {
            // because some server disallow user to bind skin to item by self, so we need respect the server options.
            return isLocalFile || ModConfig.Common.enableServerSkinsInResourcePack;
        }
    }
}
