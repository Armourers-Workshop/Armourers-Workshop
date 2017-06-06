package riskyken.armourersWorkshop.common.skin.entity;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.entity.SkinnableEntitySkeletonRenderer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinnableEntitySkeleton implements ISkinnableEntity {
    
    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySkeleton.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends ISkinnableEntityRenderer> getRendererClass() {
        return SkinnableEntitySkeletonRenderer.class;
    }

    @Override
    public boolean canUseWandOfStyle() {
        return true;
    }

    @Override
    public boolean canUseSkinsOnEntity() {
        return false;
    }
    
    @Override
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
        skinTypes.add(SkinTypeRegistry.skinHead);
        skinTypes.add(SkinTypeRegistry.skinChest);
        skinTypes.add(SkinTypeRegistry.skinLegs);
        skinTypes.add(SkinTypeRegistry.skinFeet);
        skinTypes.add(SkinTypeRegistry.skinWings);
    }
}
