package moe.plushie.armourers_workshop.core.model.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IEquipmentModel {

//    void render(Entity entity, Skin skin, float limb1, float limb2, float limb3, float headY, float headX);
//
    void render(Entity entity, IBakedSkin skin, Model model, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer);
//
//    void render(Entity entity, Skin skin, BipedModel modelBiped, SkinRenderData renderData);
//
//    void render(Entity entity, ISkin skin, MatrixStack matrix, IRenderTypeBuffer renderer);
}
