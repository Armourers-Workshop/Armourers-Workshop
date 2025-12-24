package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.item.model.AbstractItemModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class DiscoveerableSkinManager {

    private static final DiscoveerableSkinManager INSTANCE = new DiscoveerableSkinManager();

    private final HashMap<String, Entry> entries = new HashMap<>();
    private final HashMap<AbstractItemModel, Entry> bakedModels = new HashMap<>();

    public static DiscoveerableSkinManager getInstance() {
        return INSTANCE;
    }

    public static void start() {
        for (var entry : INSTANCE.entries.values()) {
            entry.load();
        }
    }

    public static void stop() {
        // nope
    }

    public void put(AbstractItemModel itemModel, OpenResourceLocation model) {
        try {
            var resourceManager = EnvironmentManager.getClientResourceManager();
            var location = model.withPath("models/" + model.path() + ".json");
            var rootObject = JsonSerializer.readFromResource(resourceManager.readResource(location));
            if (rootObject == null) {
                return;
            }
            var entry = new Entry(rootObject);
            ModLog.debug("Registering resource pack skin: '{}' in '{}'", entry.identifier, model);
            bakedModels.put(itemModel, entry);
            entries.put(entry.identifier, entry);
        } catch (Exception e) {
            ModLog.warn("Unable to bake model: '{}'", model, e);
        }
    }

    public SkinDescriptor get(AbstractItemModel itemModel) {
        var entry = bakedModels.get(itemModel);
        if (entry != null && entry.canUse()) {
            return entry.descriptor;
        }
        return SkinDescriptor.EMPTY;
    }

    public void reload(DataPackEvent.Reloading event) {
        // when resource pack did changes, we need to clear invalid resource.
        if (event.type() == DataPackType.CLIENT_RESOURCES) {
            ModLog.debug("Reloading resource pack skins");
            entries.clear();
            bakedModels.clear();
        }
    }

    private static class Entry {

        private final String identifier;
        private final boolean isLocalFile;

        private SkinDescriptor descriptor;

        public Entry(IODataObject object) {
            this.identifier = object.get("providers").get("layer0").stringValue();
            this.isLocalFile = DataDomain.isLocal(identifier);
            this.descriptor = new SkinDescriptor(identifier, SkinTypes.UNKNOWN, parseOptions(object.get("options")), SkinPaintScheme.EMPTY);
        }

        public void load() {
            // when we can't use this skin, don't preload it.
            if (!canUse()) {
                return;
            }
            ModLog.debug("'{}' => start preload skin", identifier);
            descriptor = descriptor.withType(SkinTypes.UNKNOWN);
            SkinBakery.getInstance().loadSkin(TicketManager.PRELOAD.get(identifier), this::complete);
        }

        public void complete(BakedSkin bakedSkin, Throwable exception) {
            // ignore all load exception.
            if (exception != null) {
                return;
            }
            ModLog.debug("'{}' => did preload skin", identifier);
            descriptor = descriptor.withType(bakedSkin.type());
        }

        public boolean canUse() {
            // because some server disallow user to bind skin to item by self, so we need respect the server options.
            return isLocalFile || ModConfig.Common.allowsServerSkinsInResourcePack;
        }

        private SkinDescriptor.Options parseOptions(IODataObject object) {
            // default is always enable embedded item renderer.
            var options = new SkinDescriptor.Options(0, 2);
            object.get("embeddedItemRenderer").ifPresent(it -> {
                options.setEmbeddedItemRenderer(it.intValue());
            });
            return options;
        }
    }
}
