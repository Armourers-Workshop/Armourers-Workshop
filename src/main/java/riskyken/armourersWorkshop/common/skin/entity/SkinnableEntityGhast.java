package riskyken.armourersWorkshop.common.skin.entity;

import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.entity.SkinnableEntityGhastRenderer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinnableEntityGhast implements ISkinnableEntity {

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityGhast.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends ISkinnableEntityRenderer> getRendererClass() {
        return SkinnableEntityGhastRenderer.class;
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
