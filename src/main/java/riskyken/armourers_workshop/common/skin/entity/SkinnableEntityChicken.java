package riskyken.armourers_workshop.common.skin.entity;

import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import riskyken.armourers_workshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.client.render.entity.SkinnableEntityChickenRenderer;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;

public class SkinnableEntityChicken implements ISkinnableEntity {

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityChicken.class;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends ISkinnableEntityRenderer> getRendererClass() {
        return SkinnableEntityChickenRenderer.class;
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
    }
}
