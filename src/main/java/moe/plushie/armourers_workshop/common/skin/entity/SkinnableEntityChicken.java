package moe.plushie.armourers_workshop.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererChicken;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinnableEntityChicken extends SkinnableEntity {

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        ArrayList<Class<? extends EntityLivingBase>> classes = new ArrayList<Class<? extends EntityLivingBase>>();
        classes.add(EntityChicken.class);
        return EntityChicken.class;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public LayerRenderer<? extends EntityLivingBase> getLayerRenderer() {
        return new SkinLayerRendererChicken();
    }

    @Override
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
        skinTypes.add(SkinTypeRegistry.skinHead);
    }
    
    @Override
    public int getSlotsForSkinType(ISkinType skinType) {
        return 1;
    }
}
