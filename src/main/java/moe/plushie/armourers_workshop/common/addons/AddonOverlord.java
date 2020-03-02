package moe.plushie.armourers_workshop.common.addons;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererBibed;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererHeldItem;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntityRegisty;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntity;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AddonOverlord extends ModAddon {

    private static final String ENTITY_SKELETON_WARRIOR_CLASS_NAME = "the_fireplace.overlord.entity.EntitySkeletonWarrior";
    private static final String ENTITY_CONVERTED_SKELETON = "the_fireplace.overlord.entity.EntityConvertedSkeleton";

    public AddonOverlord() {
        super("overlord", "Overlord");
    }

    @Override
    public void init() {
        if (isModLoaded()) {
            SkinnableEntityRegisty.INSTANCE.registerEntity(new SkinnableEntityCustomEntity(ENTITY_SKELETON_WARRIOR_CLASS_NAME));
            SkinnableEntityRegisty.INSTANCE.registerEntity(new SkinnableEntityCustomEntity(ENTITY_CONVERTED_SKELETON));
        }
    }

    public static class SkinnableEntityCustomEntity extends SkinnableEntity {

        private final String className;

        public SkinnableEntityCustomEntity(String className) {
            this.className = className;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addRenderLayer(RenderManager renderManager) {
            Render<Entity> renderer = renderManager.getEntityClassRenderObject(getEntityClass());
            if (renderer != null && renderer instanceof RenderBiped) {
                SkinLayerRendererBibed rendererBibed = new SkinLayerRendererBibed((RenderLivingBase) renderer);
                if (rendererBibed != null) {
                    ((RenderBiped<?>) renderer).addLayer(rendererBibed);
                }

                try {
                    Object object = ReflectionHelper.getPrivateValue(RenderLivingBase.class, (RenderLivingBase) renderer, "field_177097_h", "layerRenderers");
                    if (object != null) {
                        List<LayerRenderer<?>> layerRenderers = (List<LayerRenderer<?>>) object;
                        // Looking for held item layer.
                        for (int i = 0; i < layerRenderers.size(); i++) {
                            LayerRenderer<?> layerRenderer = layerRenderers.get(i);
                            if (layerRenderer.getClass().getName().contains("LayerHeldItem")) {
                                // Replacing held item layer.
                                ModLogger.log("Removing held item layer from " + renderer);
                                layerRenderers.remove(i);
                                ModLogger.log("Adding skinned held item layer to " + renderer);
                                layerRenderers.add(new SkinLayerRendererHeldItem((RenderLivingBase) renderer, layerRenderer));
                                break;
                            }
                        }
                    } else {
                        ModLogger.log(Level.WARN, "Failed to get 'layerRenderers' on " + renderer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ModLogger.log(Level.WARN, "Failed to get renderer for " + className);
            }
        }

        @Override
        public Class<? extends EntityLivingBase> getEntityClass() {
            try {
                return (Class<? extends EntityLivingBase>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
            skinTypes.add(SkinTypeRegistry.skinOutfit);
            skinTypes.add(SkinTypeRegistry.skinHead);
            skinTypes.add(SkinTypeRegistry.skinChest);
            skinTypes.add(SkinTypeRegistry.skinLegs);
            skinTypes.add(SkinTypeRegistry.skinFeet);
            skinTypes.add(SkinTypeRegistry.skinWings);

            skinTypes.add(SkinTypeRegistry.skinSword);
            skinTypes.add(SkinTypeRegistry.skinShield);
            skinTypes.add(SkinTypeRegistry.skinBow);
        }

        @Override
        public int getSlotsForSkinType(ISkinType skinType) {
            if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                return 10;
            }
            if (skinType == SkinTypeRegistry.skinOutfit) {
                return 10;
            }
            return 1;
        }

        @Override
        public boolean canUseWandOfStyle(EntityPlayer user) {
            return true;
        }
    }
}
