package moe.plushie.armourers_workshop.builder.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.builder.tileentity.SkinCubeTileEntity;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.other.SkinCubeFaceRenderer;
import moe.plushie.armourers_workshop.init.common.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class ArmourerTileEntityRenderer<T extends ArmourerTileEntity> extends TileEntityRenderer<T> {

    public ArmourerTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {


    }

    //        public static void renderBuildingGrid(ISkinType skinType, float scale, boolean showGuides, SkinProperties skinProps, boolean multiblock) {
//            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
//                ISkinPartType skinPartType = skinType.getSkinParts().get(i);
//                IPoint3D partOffset = skinPartType.getOffset();
//                GlStateManager.translate(partOffset.getX() * scale, partOffset.getY() * scale, partOffset.getZ() * scale);
//                if (skinType == SkinTypeRegistry.skinBlock) {
//                    if (skinPartType.getPartName().equals("multiblock") & multiblock) {
//                        // GL11.glColor4f(1F, 1F, 0.0F, 0.2F);
//                        renderBuildingGrid(((SkinBlock) SkinTypeRegistry.skinBlock).partBase, scale, showGuides, skinProps, 1F, 1F, 0.0F, 0.2F);
//                        GL11.glPolygonOffset(6F, 6F);
//                        // GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.25F);
//                        renderBuildingGrid(skinPartType, scale, showGuides, skinProps, 0.5F, 0.5F, 0.5F, 0.25F);
//                    } else if (skinPartType.getPartName().equals("base") & !multiblock) {
//                        // GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.25F);
//                        renderBuildingGrid(skinPartType, scale, showGuides, skinProps, 0.5F, 0.5F, 0.5F, 0.25F);
//                    }
//                } else {
//                    // GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.25F);
//                    renderBuildingGrid(skinPartType, scale, showGuides, skinProps, 0.5F, 0.5F, 0.5F, 0.25F);
//                }
//                GlStateManager.translate(-partOffset.getX() * scale, -partOffset.getY() * scale, -partOffset.getZ() * scale);
//            }
//        }
//
//        public static void renderBuildingGrid(ISkinPartType skinPartType, float scale, boolean showGuides, SkinProperties skinProps, float r, float g, float b, float a) {
//            GlStateManager.translate(0, skinPartType.getBuildingSpace().getY() * scale, 0);
//            GlStateManager.scale(-1, -1, 1);
//            ArmourerRenderHelper.renderGuidePart(skinPartType, scale, showGuides, skinProps, r, g, b, a);
//            GlStateManager.scale(-1, -1, 1);
//            GlStateManager.translate(0, -skinPartType.getBuildingSpace().getY() * scale, 0);
//        }
}
