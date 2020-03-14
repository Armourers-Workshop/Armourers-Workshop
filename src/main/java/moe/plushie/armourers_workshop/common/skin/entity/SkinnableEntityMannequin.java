package moe.plushie.armourers_workshop.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public class SkinnableEntityMannequin extends SkinnableEntity {

    @Override
    public Class<? extends Entity> getEntityClass() {
        return EntityMannequin.class;
    }
    
    @Override
    public void addRenderLayer(RenderManager renderManager) {
        // NO-OP
    }

    @Override
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
        skinTypes.add(SkinTypeRegistry.skinOutfit);
        
        skinTypes.add(SkinTypeRegistry.skinHead);
        skinTypes.add(SkinTypeRegistry.skinChest);
        skinTypes.add(SkinTypeRegistry.skinLegs);
        skinTypes.add(SkinTypeRegistry.skinFeet);
        skinTypes.add(SkinTypeRegistry.skinWings);
    }
    
    @Override
    public int getSlotsForSkinType(ISkinType skinType) {
        if (skinType.getVanillaArmourSlotId() != -1) {
            return 10;
        }
        if (skinType == SkinTypeRegistry.skinWings) {
            return 10;
        }
        if (skinType == SkinTypeRegistry.skinOutfit) {
            return 10;
        }
        return 1;
    }
}
