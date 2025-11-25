package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.state.ItemStackRenderState;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.util.HashMap;
import java.util.IdentityHashMap;

@OnlyIn(Dist.CLIENT)
public class EmbeddedItemModelDiscovery {

    private static final HashMap<String, Entry> ENTRIES = new HashMap<>();
    private static final IdentityHashMap<Object, Entry> BAKED_MODELS = new IdentityHashMap<>();

    public static void start() {
        for (var entry : ENTRIES.values()) {
            entry.preload(SkinBakery.getInstance());
        }
    }

    public static void stop() {
        // nope
    }

    public static void reload(DataPackEvent.Reloading event) {
        // when resource pack did changes, we need to clear invalid resource.
        if (event.type() == DataPackType.CLIENT_RESOURCES) {
            ModLog.debug("Reloading resource pack skins");
            ENTRIES.clear();
            BAKED_MODELS.clear();
        }
    }

    public static SkinDescriptor resolve(ItemStackRenderState itemRenderState) {
        var entry = BAKED_MODELS.get(itemRenderState.itemModel());
        if (entry != null && entry.canUse()) {
            return entry.descriptor;
        }
        return SkinDescriptor.EMPTY;
    }

    public static void bake(Object itemModel, OpenResourceLocation model) {
        try {
            var resourceManager = EnvironmentManager.getClientResourceManager();
            var location = model.withPath("models/" + model.path() + ".json");
            var rootObject = JsonSerializer.readFromResource(resourceManager.readResource(location));
            if (rootObject == null) {
                return;
            }
            var firstObject = rootObject.get("providers").get("layer0");
            var entry = new Entry(firstObject.stringValue());
            ModLog.debug("Registering resource pack skin: '{}' in '{}'", entry.identifier, model);
            BAKED_MODELS.put(itemModel, entry);
            ENTRIES.put(entry.identifier, entry);
            entry.preload(SkinBakery.getInstance());
        } catch (Exception e) {
            ModLog.warn("Unable to bake model: '{}', {}", model, e.getMessage());
        }
    }

    private static class Entry {

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
            bakery.loadSkin(TicketManager.PRELOAD.get(identifier), this::complete);
        }

        public void complete(BakedSkin bakedSkin, Throwable exception) {
            // ignore all load exception.
            if (exception != null) {
                return;
            }
            ModLog.debug("'{}' => did preload skin", identifier);
            descriptor = new SkinDescriptor(identifier, bakedSkin.type());
        }

        public boolean canUse() {
            // because some server disallow user to bind skin to item by self, so we need respect the server options.
            return isLocalFile || ModConfig.Common.enableServerSkinsInResourcePack;
        }
    }
}
