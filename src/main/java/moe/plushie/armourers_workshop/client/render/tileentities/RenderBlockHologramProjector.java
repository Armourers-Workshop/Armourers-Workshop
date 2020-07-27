package moe.plushie.armourers_workshop.client.render.tileentities;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.skin.IEquipmentModel;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper.ModelType;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector.PowerMode;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockHologramProjector extends TileEntitySpecialRenderer<TileEntityHologramProjector> {

    @Override
    public void render(TileEntityHologramProjector tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (ConfigHandlerClient.showSkinRenderBounds) {
            AxisAlignedBB aabb = new AxisAlignedBB(tileEntity.getPos());
            renderBox(aabb, 1.0F, 1.0F, 0.0F);
        }
        if (tileEntity.getPowerMode().get() != PowerMode.IGNORED) {
            if (tileEntity.getPowerMode().get() == PowerMode.HIGH) {
                if (!tileEntity.getPowered().get()) {
                    return;
                }
            } else {
                if (tileEntity.getPowered().get()) {
                    return;
                }
            }
        }

        ItemStack itemStack = tileEntity.getStackInSlot(0);

        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        if (skinPointer == null) {
            return;
        }

        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return;
        }

        int rot = tileEntity.getBlockMetadata();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);

        GL11.glTranslated(x + 0.5F, y + 0.5D, z + 0.5F);

        GL11.glRotatef(180, 0, 0, 1);

        if (rot == 1) {
            GL11.glRotatef(180, 0, 0, 1);
        }
        if (rot == 2) {
            GL11.glRotatef(90, -1, 0, 0);
        }
        if (rot == 3) {
            GL11.glRotatef(90, 1, 0, 0);
        }
        if (rot == 4) {
            GL11.glRotatef(90, 0, 0, -1);
        }
        if (rot == 5) {
            GL11.glRotatef(90, 0, 0, 1);
        }

        float scale = 0.0625F;

        GL11.glTranslated(tileEntity.getOffsetX().get() * scale, tileEntity.getOffsetY().get() * scale, tileEntity.getOffsetZ().get() * scale);

        GL11.glScalef(-1, -1, 1);

        int speedX = tileEntity.getRotationSpeedX().get();
        int speedY = tileEntity.getRotationSpeedY().get();
        int speedZ = tileEntity.getRotationSpeedZ().get();

        float angleX = 0;
        float angleY = 0;
        float angleZ = 0;

        if (speedX != 0) {
            angleX = (System.currentTimeMillis() % speedX);
            angleX = angleX / speedX * 360F;
        }
        if (speedY != 0) {
            angleY = (System.currentTimeMillis() % speedY);
            angleY = angleY / speedY * 360F;
        }
        if (speedZ != 0) {
            angleZ = (System.currentTimeMillis() % speedZ);
            angleZ = angleZ / speedZ * 360F;
        }
        if (!tileEntity.getGlowing().get()) {
            EnumFacing dir = EnumFacing.byIndex(tileEntity.getBlockMetadata());
            float xLight = tileEntity.getPos().getX();
            float yLight = tileEntity.getPos().getY();
            float zLight = tileEntity.getPos().getZ();

            float offsetX = tileEntity.getOffsetX().get();
            float offsetY = tileEntity.getOffsetY().get();
            float offsetZ = tileEntity.getOffsetZ().get();

            switch (dir) {
            case UP:
                xLight += offsetX * scale;
                yLight += offsetY * scale;
                zLight += offsetZ * scale;
                break;
            case DOWN:
                xLight += -offsetX * scale;
                yLight += -offsetY * scale;
                zLight += offsetZ * scale;
                break;
            case EAST:
                xLight += offsetY * scale;
                yLight += -offsetX * scale;
                zLight += offsetZ * scale;
                break;
            case WEST:
                xLight += -offsetY * scale;
                yLight += offsetX * scale;
                zLight += offsetZ * scale;
                break;
            case NORTH:
                xLight += offsetX * scale;
                yLight += -offsetZ * scale;
                zLight += -offsetY * scale;
                break;
            case SOUTH:
                xLight += -offsetX * scale;
                yLight += offsetY * scale;
                zLight += offsetZ * scale;
                break;
            }
            ModRenderHelper.setLightingForBlock(tileEntity.getWorld(), tileEntity.getPos());
        }

        GL11.glPushMatrix();

        GL11.glTranslated((-tileEntity.getRotationOffsetX().get() + tileEntity.getRotationOffsetX().get()) * scale, (-tileEntity.getRotationOffsetY().get() + tileEntity.getRotationOffsetY().get()) * scale,
                (-tileEntity.getRotationOffsetZ().get() + tileEntity.getRotationOffsetZ().get()) * scale);

        if (tileEntity.getAngleX().get() != 0) {
            GL11.glRotatef(tileEntity.getAngleX().get(), 1F, 0F, 0F);
        }
        if (tileEntity.getAngleY().get() != 0) {
            GL11.glRotatef(tileEntity.getAngleY().get(), 0F, 1F, 0F);
        }
        if (tileEntity.getAngleZ().get() != 0) {
            GL11.glRotatef(tileEntity.getAngleZ().get(), 0F, 0F, 1F);
        }

        if (angleX != 0) {
            GL11.glRotatef(angleX, 1, 0, 0);
        }
        if (angleY != 0) {
            GL11.glRotatef(angleY, 0, 1, 0);
        }
        if (angleZ != 0) {
            GL11.glRotatef(angleZ, 0, 0, 1);
        }

        GL11.glTranslated(tileEntity.getRotationOffsetX().get() * scale, tileEntity.getRotationOffsetY().get() * scale, tileEntity.getRotationOffsetZ().get() * scale);

        if (tileEntity.getGlowing().get()) {
            ModRenderHelper.disableLighting();
        }
        ModRenderHelper.enableAlphaBlend();

        double distance = Minecraft.getMinecraft().player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
        ISkinType skinType = skinPointer.getIdentifier().getSkinType();
        if (skinType == null) {
            skinType = skin.getSkinType();
        }
        skinType = skin.getSkinType();
        
        IEquipmentModel targetModel = SkinModelRenderHelper.INSTANCE.getTypeHelperForModel(ModelType.MODEL_BIPED, skinType);
        targetModel.render(null, skin, null, true, skinPointer.getSkinDye(), null, true, distance, true);

        GL11.glPopMatrix();
        if (tileEntity.isShowRotationPoint()) {
            AxisAlignedBB aabb = new AxisAlignedBB(0, 0, 0, scale, scale, scale);
            renderBox(aabb, 1F, 0F, 1F);
        }

        ModRenderHelper.disableAlphaBlend();
        if (tileEntity.getGlowing().get()) {
            ModRenderHelper.enableLighting();
        }
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
    }

    private void renderBox(AxisAlignedBB aabb, float r, float g, float b) {
        float f1 = 0.002F;
        ModRenderHelper.disableLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(r, g, b, 0.4F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        RenderGlobal.drawSelectionBoundingBox(aabb.contract(f1, f1, f1), r, g, b, 0.4F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        ModRenderHelper.enableLighting();
    }
}
