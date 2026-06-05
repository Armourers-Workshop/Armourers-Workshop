package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.SkinCubeBlockEntity;
import moe.plushie.armourers_workshop.builder.client.render.state.AdvancedBuilderRenderState;
import moe.plushie.armourers_workshop.builder.client.render.state.ArmourerRenderState;
import moe.plushie.armourers_workshop.builder.client.render.state.SkinCubeRenderState;
import moe.plushie.armourers_workshop.compat.api.blockentity.BlockEntityAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.ArrowAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.BoatAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.EntityAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.FishingHookAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.LivingEntityAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.MinecartAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.PlayerAccessor;
import moe.plushie.armourers_workshop.compat.api.entity.ThrownTridentAccessor;
import moe.plushie.armourers_workshop.compat.client.AbstractClientNamedClass;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractModelHolder;
import moe.plushie.armourers_workshop.compat.client.renderer.entity.AbstractEntityRenderDispatcher;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.armature.ArmatureSerializers;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.core.DefaultLayerArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.render.state.ArrowRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.BoatRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.FishingHookRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.HologramProjectorRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.MinecartRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.PlayerRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.RenderStateManager;
import moe.plushie.armourers_workshop.core.client.render.state.SkinnableRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.ThrownTridentRenderState;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.AllayBodyJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.AllayHeadJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.AllayWingJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.DefaultBabyJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.DefaultSkirtJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.FlatWingJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.HorseBodyJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.ArrowModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.BoatModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.FishingModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.MinecartModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.TridentModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.VillagerModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.data.DataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.NamedClass;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import moe.plushie.armourers_workshop.library.blockentity.GlobalSkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.client.render.GlobalSkinLibraryRenderState;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class SkinRendererManager {

    public static final ArmatureTransformerManager DEFAULT = new DefaultArmatureTransformerManager();
    public static final ArmatureTransformerManager EPIC_FIGHT = new EpicFlightArmatureTransformerManager();

    public static void init() {
        ProfileLoader.init();
        TransformerLoader.init();
    }

    public static void reload() {
        // nop
    }

    private static class ProfileLoader {

        private static final ProfileLoader INSTANCE = new ProfileLoader();

        private final Map<IRegistryHolder<?>, EntityProfile> registeredProfiles = new LinkedHashMap<>();
        private final Map<EntityType<?>, Map<String, IEntityRenderer<?, ?>>> registeredEntityRenderers = new LinkedHashMap<>();

        private final List<Pair<EntityType<?>, IEntityRenderer<?, ?>>> forceEntityRenderers = new ArrayList<>();

        private boolean isReady = false;

        public static void init() {
            ModEntityProfiles.addListener(INSTANCE::profileDidChange);
            AbstractEntityRenderDispatcher.addListener(INSTANCE::rendererDidChange);
        }

        private void update(IRegistryHolder<?> entityType, EntityProfile entityProfile) {
            // we can get the entity type?
            if (!(entityType.get() instanceof EntityType<?> resolvedEntityType)) {
                return;
            }
            // add our own custom armor layer to target entity renderer.
            var entityRenderers = registeredEntityRenderers.get(resolvedEntityType);
            if (entityRenderers != null) {
                entityRenderers.forEach((key, entityRenderer) -> update(resolvedEntityType, entityProfile, entityRenderer));
            }
        }

        private void update(EntityType<?> entityType, EntityProfile entityProfile, IEntityRenderer<?, ?> entityRenderer) {
            var rendererContext = EntityRendererContext.of(entityRenderer);
            rendererContext.setEntityType(entityType);
            rendererContext.setEntityProfile(entityProfile);
        }

        private void profileDidChange(IRegistryHolder<?> entityType, EntityProfile entityProfile) {
            if (entityProfile != null) {
                if (registeredProfiles.containsKey(entityType)) {
                    ModLog.debug("Update Entity Renderer '{}'", entityType.registryName());
                } else {
                    ModLog.debug("Attach Entity Renderer '{}'", entityType.registryName());
                }
                registeredProfiles.put(entityType, entityProfile);
            } else {
                ModLog.debug("Detach Entity Renderer '{}'", entityType.registryName());
                registeredProfiles.remove(entityType);
            }
            if (isReady) {
                RenderSystem.safeCall(() -> update(entityType, entityProfile));
            }
        }

        private void rendererDidChange(Map<EntityType<?>, Map<String, IEntityRenderer<?, ?>>> newValue) {
            //var horseArmorType = NamedClass.forName("minecraft:layer/horse_armor");
            var armourType = NamedClass.forName("minecraft:layer/humanoid_armor");
            // reset all
            forceEntityRenderers.clear();
            registeredEntityRenderers.clear();
            // put the new entity renderers.
            registeredEntityRenderers.putAll(newValue);
            registeredEntityRenderers.forEach((entityType, renderers) -> renderers.forEach((model, renderer) -> {
                // ..
                if (armourType != null && findLayer(renderer, armourType)) {
                    ModLog.debug("Detect Entity Renderer '{}'", AbstractRegistryManager.getEntityTypeKey(entityType));
                    forceEntityRenderers.add(Pair.of(entityType, renderer));
                }
            }));
            // reload all entity renderers.
            RenderSystem.recordRenderCall(() -> {
                // execute the pending tasks.
                isReady = false;
                registeredProfiles.forEach(this::update);
                forceEntityRenderers.stream().filter(it -> !findProfile(it.getLeft())).forEach(it -> {
                    ModLog.debug("Attach Entity Renderer (Auto) '{}'", AbstractRegistryManager.getEntityTypeKey(it.getKey()));
                    update(it.getLeft(), ModEntityProfiles.getEmptyProfile(), it.getRight());
                });
                isReady = true;
            });
        }

        private boolean findProfile(EntityType<?> entityType) {
            return registeredProfiles.keySet().stream().anyMatch(it -> it.get() == entityType);
        }

        private boolean findLayer(IEntityRenderer<?, ?> renderer, Class<?> layerClass) {
            // layer only in the living entity renderers.
            if (!(renderer instanceof ILivingEntityRenderer<?, ?, ?> entityRenderer)) {
                return false;
            }
            return entityRenderer.abi$getLayers().stream().anyMatch(layerClass::isInstance);
        }
    }

    private static class TransformerLoader extends ArmatureSerializers {

        public static void init() {
            registerClasses();
            registerModifiers();
            registerPlugins();
            registerModels();
            registerEntityStates();
            registerBlockEntityStates();
            DataPackManager.register(DataPackType.BUNDLED_DATA, "skin/transformers", TransformerLoaderImpl::new, TransformerLoaderImpl::clean, TransformerLoaderImpl::freeze, 0);
        }

        private static void registerClasses() {
            AbstractClientNamedClass.registerLayerClasses();
            AbstractClientNamedClass.registerModelClasses();
        }

        private static void registerModifiers() {
            registerModifier("armourers_workshop:modifier/baby_head_apt", DefaultBabyJointModifier::new);
            registerModifier("armourers_workshop:modifier/body_to_skirt", DefaultSkirtJointModifier::new);
            registerModifier("armourers_workshop:modifier/body_to_flat_wing", FlatWingJointModifier::new);
            registerModifier("armourers_workshop:modifier/apply_ally_head", AllayHeadJointModifier::new);
            registerModifier("armourers_workshop:modifier/apply_ally_body", AllayBodyJointModifier::new);
            registerModifier("armourers_workshop:modifier/apply_ally_wing", AllayWingJointModifier::new);
            registerModifier("armourers_workshop:modifier/apply_horse_body", HorseBodyJointModifier::new);
        }

        private static void registerPlugins() {

            registerPlugin("armourers_workshop:plugin/hidden_any_layer", DefaultLayerArmaturePlugin::any);
            registerPlugin("armourers_workshop:plugin/hidden_villager_layer", DefaultLayerArmaturePlugin::villager);
            registerPlugin("armourers_workshop:plugin/hidden_mob_layer", DefaultLayerArmaturePlugin::mob);

            registerPlugin("armourers_workshop:plugin/fix_villager_model", VillagerModelArmaturePlugin::new);
            registerPlugin("armourers_workshop:plugin/fix_trident_model", TridentModelArmaturePlugin::new);
            registerPlugin("armourers_workshop:plugin/fix_arrow_model", ArrowModelArmaturePlugin::new);
            registerPlugin("armourers_workshop:plugin/fix_fishing_model", FishingModelArmaturePlugin::new);
            registerPlugin("armourers_workshop:plugin/fix_boat_model", BoatModelArmaturePlugin::new);
            registerPlugin("armourers_workshop:plugin/fix_minecart_model", MinecartModelArmaturePlugin::new);
        }

        private static void registerModels() {

            registerModel("minecraft:model/slime");
            registerModel("minecraft:model/ghast");

            registerModel("minecraft:model/enderman");
            registerModel("minecraft:model/zombie_villager");

            registerModel("minecraft:model/illager", it -> {
                it.put("head", "root.head");
                it.put("body", "root.body");
                it.put("left_leg", "root.left_leg");
                it.put("right_leg", "root.right_leg");
                it.put("arms", "root.arms");
                it.put("right_arm", "root.right_arm");
                it.put("left_arm", "root.left_arm");
                it.put("hat", "root.head.hat");
                it.put("nose", "root.head.nose");
            });

            registerModel("minecraft:model/quadruped", it -> {
                it.put("head", "root.head");
                it.put("body", "root.body");
                it.put("right_front_leg", "root.right_front_leg");
                it.put("right_hind_leg", "root.right_hind_leg");
                it.put("left_hind_leg", "root.left_hind_leg");
                it.put("left_front_leg", "root.left_front_leg");
            });

            registerModel("minecraft:model/wolf", it -> {
                it.put("head", "root.head");
                it.put("body", "root.body");
                it.put("right_front_leg", "root.right_front_leg");
                it.put("right_hind_leg", "root.right_hind_leg");
                it.put("left_hind_leg", "root.left_hind_leg");
                it.put("left_front_leg", "root.left_front_leg");
                it.put("tail", "root.tail");
                // root.upper_body
                // root.head.real_head
                // root.tail.real_tail
            });

            registerModel("minecraft:model/villager", it -> {
                it.put("hat", "root.head.hat");
                it.put("hat_rim", "root.head.hat.hat_rim");
                it.put("head", "root.head");
                it.put("nose", "root.head.nose");
                it.put("body", "root.body");
                it.put("right_leg", "root.right_leg");
                it.put("left_leg", "root.left_leg");
                it.put("left_arm", "root.arms");
                it.put("right_arm", "root.arms");
                it.put("jacket", "root.body.jacket");
            });

            registerModel("minecraft:model/iron_golem", it -> {
                it.put("head", "root.head");
                it.put("body", "root.body");
                it.put("right_leg", "root.right_leg");
                it.put("left_leg", "root.left_leg");
                it.put("right_arm", "root.right_arm");
                it.put("left_arm", "root.left_arm");
            });

            registerModel("minecraft:model/humanoid", it -> {
                it.put("head", "root.head");
                it.put("body", "root.body");
                it.put("right_arm", "root.right_arm");
                it.put("left_arm", "root.left_arm");
                it.put("right_leg", "root.right_leg");
                it.put("left_leg", "root.left_leg");
                it.put("hat", "root.head.hat");
            });
            registerModel("minecraft:model/player", it -> {
                it.put("left_pants", "root.left_leg.left_pants");
                it.put("right_pants", "root.right_leg.right_pants");
                it.put("left_sleeve", "root.left_arm.left_sleeve");
                it.put("right_sleeve", "root.right_arm.right_sleeve");
                it.put("jacket", "root.body.jacket");
            });

            registerModel("minecraft:model/chicken", it -> {
                it.put("head", "root.head");
                it.put("beak", "root.head.beak");
                it.put("red_thing", "root.head.red_thing");
                it.put("body", "root.body");
                it.put("right_leg", "root.right_leg");
                it.put("left_leg", "root.left_leg");
                it.put("right_wing", "root.right_wing");
                it.put("left_wing", "root.left_wing");
            });

            registerModel("minecraft:model/creeper", it -> {
                it.put("head", "root.head");
                //it.put("body", "root.body");
                //it.put("left_front_leg", "root.left_front_leg");
                //it.put("right_front_leg", "root.right_front_leg");
                //it.put("left_hind_leg", "root.left_hind_leg");
                //it.put("right_hind_leg", "root.right_hind_leg");
            });

            registerModel("minecraft:model/horse", it -> {
                it.put("head", "root.head_parts");
                it.put("body", "root.body");
                it.put("right_hind_leg", "root.right_hind_leg");
                it.put("left_hind_leg", "root.left_hind_leg");
                it.put("right_front_leg", "root.right_front_leg");
                it.put("left_front_leg", "root.left_front_leg");
                //it.put("right_front_baby_leg", "bodyParts[5]");
                //it.put("left_front_baby_leg", "bodyParts[6]");
                //it.put("right_hind_baby_leg", "bodyParts[7]");
                //it.put("left_hind_baby_leg", "bodyParts[8]");
                it.put("tail", "root.body.tail");
            });

            registerModel("minecraft:model/boat", it -> {
                it.put("bottom", "root.bottom");
                it.put("back", "root.back");
                it.put("front", "root.front");
                it.put("right", "root.right");
                it.put("left", "root.left");
                it.put("left_paddle", "root.left_paddle");
                it.put("right_paddle", "root.right_paddle");
            });

            registerModel("minecraft:model/raft", it -> {
                it.put("bottom", "root.bottom");
                it.put("left_paddle", "root.left_paddle");
                it.put("right_paddle", "root.right_paddle");
            });

            registerModel("minecraft:model/allay", it -> {
                it.put("root", "root");
                it.put("head", "root.head");
                it.put("body", "root.body");
                it.put("right_arm", "root.body.right_arm");
                it.put("left_arm", "root.body.left_arm");
                it.put("right_leg", "root.body.right_wing");
                it.put("left_leg", "root.body.left_wing");
                it.put("right_wing", "root.body.right_wing");
                it.put("left_wing", "root.body.left_wing");
            });
        }

        private static void registerEntityStates() {
            registerEntityState(EntityAccessor.class, EntityRenderState::new, EntityRenderState::extract);
            registerEntityState(LivingEntityAccessor.class, LivingEntityRenderState::new, LivingEntityRenderState::extract);

            registerEntityState(BoatAccessor.class, BoatRenderState::new, BoatRenderState::extract);
            registerEntityState(MinecartAccessor.class, MinecartRenderState::new, MinecartRenderState::extract);

            registerEntityState(ArrowAccessor.class, ArrowRenderState::new, ArrowRenderState::extract);
            registerEntityState(FishingHookAccessor.class, FishingHookRenderState::new, FishingHookRenderState::extract);
            registerEntityState(ThrownTridentAccessor.class, ThrownTridentRenderState::new, ThrownTridentRenderState::extract);

            registerEntityState(PlayerAccessor.class, PlayerRenderState::new, PlayerRenderState::extract);

            registerEntityState(MannequinEntity.class, MannequinRenderState::new, MannequinRenderState::extract);
        }

        private static void registerBlockEntityStates() {
            registerBlockEntityState(BlockEntityAccessor.class, BlockEntityRenderState::new, BlockEntityRenderState::extract);

            registerBlockEntityState(SkinnableBlockEntity.class, SkinnableRenderState::new, SkinnableRenderState::extract);
            registerBlockEntityState(GlobalSkinLibraryBlockEntity.class, GlobalSkinLibraryRenderState::new, GlobalSkinLibraryRenderState::extract);
            registerBlockEntityState(HologramProjectorBlockEntity.class, HologramProjectorRenderState::new, HologramProjectorRenderState::extract);

            registerBlockEntityState(ArmourerBlockEntity.class, ArmourerRenderState::new, ArmourerRenderState::extract);
            registerBlockEntityState(AdvancedBuilderBlockEntity.class, AdvancedBuilderRenderState::new, AdvancedBuilderRenderState::extract);

            registerBlockEntityState(SkinCubeBlockEntity.class, SkinCubeRenderState::new, SkinCubeRenderState::extract);
            registerBlockEntityState(BoundingBoxBlockEntity.class, SkinCubeRenderState::new, SkinCubeRenderState::extract);
        }

        public static void registerModel(String registryName) {
            registerModel(registryName, it -> {
                // nope.
            });
        }

        public static void registerModel(String registryName, Consumer<Map<String, String>> provider) {
            var clazz = NamedClass.forName(registryName);
            if (clazz != null) {
                var mapper = new LinkedHashMap<String, String>();
                provider.accept(mapper);
                registerClass(registryName, clazz);
                AbstractModelHolder.register(clazz, mapper);
            }
        }

        public static <T extends EntityAccessor, S extends EntityRenderState> void registerEntityState(Class<T> entityClass, Supplier<S> stateFactory, BiConsumer<T, S> extractHandler) {
            RenderStateManager.register(entityClass, stateFactory, extractHandler);
        }

        public static <T extends BlockEntityAccessor, S extends BlockEntityRenderState> void registerBlockEntityState(Class<T> entityClass, Supplier<S> stateFactory, BiConsumer<T, S> extractHandler) {
            RenderStateManager.register(entityClass, stateFactory, extractHandler);
        }
    }

    private static class TransformerLoaderImpl implements DataPackBuilder {

        private static final Map<String, ArmatureTransformerManager> MANAGERS = Collections.immutableMap(it -> {
            it.put("armourers_workshop:armature", DEFAULT);
            it.put("epicfight:armature", EPIC_FIGHT);
        });

        private final OpenResourceKey registryName;

        public TransformerLoaderImpl(OpenResourceKey key) {
            var path = FileUtils.getRegistryName(key.path(), "skin/transformers/");
            this.registryName = key.withPath(path);
        }

        @Override
        public void append(IODataObject object, OpenResourceKey file) {
            var type = object.get("type").stringValue();
            var manager = MANAGERS.get(type);
            if (manager != null) {
                manager.append(registryName, object);
            }
        }

        @Override
        public void build() {
        }

        public static void clean() {
            MANAGERS.values().forEach(ArmatureTransformerManager::clear);
        }

        public static void freeze() {
            MANAGERS.values().forEach(ArmatureTransformerManager::freeze);
        }
    }
}
