package moe.plushie.armourers_workshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.ApiRegistrar;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings.MovementType;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public class ModelSkinOutfit extends AbstractModelSkin {

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        if (skin == null) {
            return;
        }
        ArrayList<SkinPart> parts = skin.getParts();

        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
        }

        if (ClientProxy.isJrbaClientLoaded()) {
            this.isChild = false;
        }

        RenderHelper.enableGUIStandardItemLighting();

        if (skin.hasPaintData() & showSkinPaint) {
            if (extraColours == null) {
                extraColours = ExtraColours.EMPTY_COLOUR;
            }
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, skinDye, extraColours);
            st.bindTexture();
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (!itemRender) {
                GL11.glTranslated(0, -12 * SCALE, 0);
            }
            bipedLeftLeg.render(SCALE);
            bipedRightLeg.render(SCALE);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
        
        boolean overrideChest = SkinProperties.PROP_OVERRIDE_MODEL_CHEST.getValue(skin.getProperties());
        MovementType movmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skin.getProperties()));
        double angle = 45D;
        
        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);

            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }
            if (isSneak) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
                GL11.glTranslated(0, -3 * SCALE, 4 * SCALE);
            }

            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getPartType());

            if (part.getPartType().getRegistryName().equals("armourers:head.base")) {
                renderHead(part, SCALE, skinDye, extraColours, distance, doLodLoading);
            }
            
            if (part.getPartType().getRegistryName().equals("armourers:chest.base")) {
                renderChest(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:chest.leftArm")) {
                renderLeftArm(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading, overrideChest);
            } else if (part.getPartType().getRegistryName().equals("armourers:chest.rightArm")) {
                renderRightArm(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading, overrideChest);
            }
            
            if (part.getPartType().getRegistryName().equals("armourers:legs.leftLeg")) {
                renderLeftLeg(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:legs.rightLeg")) {
                renderRightLeg(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:legs.skirt")) {
                renderSkirt(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            }
            
            if (part.getPartType().getRegistryName().equals("armourers:feet.leftFoot")) {
                renderLeftFoot(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:feet.rightFoot")) {
                renderRightFoot(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            }
            
            if (part.getPartType().getRegistryName().equals("armourers:wings.leftWing")) {
                angle = SkinUtils.getFlapAngleForWings(entity, skin);
                renderLeftWing(part, SCALE, skinDye, extraColours, distance, angle, doLodLoading, movmentType);
            }
            if (part.getPartType().getRegistryName().equals("armourers:wings.rightWing")) {
                angle = SkinUtils.getFlapAngleForWings(entity, skin);
                renderRightWing(part, SCALE, skinDye, extraColours, distance, -angle, doLodLoading, movmentType);
            }

            GL11.glPopMatrix();
        }

        GL11.glColor3f(1F, 1F, 1F);
    }

    private void renderHead(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleY), 0, 1, 0);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleX), 1, 0, 0);
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderChest(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
        }
        GL11.glColor3f(1F, 1F, 1F);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftArm(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, boolean override) {
        GL11.glPushMatrix();

        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glTranslatef(5.0F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * scale, 0F);

        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);

        if (slim & !override) {
            GL11.glTranslatef(-0.25F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 0.5F * scale, 0F);

            GL11.glScalef(0.75F, 1F, 1F);
        }

        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);

        GL11.glPopMatrix();
    }

    private void renderRightArm(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, boolean override) {
        GL11.glPushMatrix();

        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glTranslatef(-5.0F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * scale, 0F);

        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);

        if (slim & !override) {
            GL11.glTranslatef(0.25F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 0.5F * scale, 0F);

            GL11.glScalef(0.75F, 1F, 1F);
        }

        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftLeg(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        //if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        //}
        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderRightLeg(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        //if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        //}
        GL11.glTranslated(-2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }
    
    private void renderLeftFoot(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        //if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        //}
        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
        
        
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }
    
    private void renderRightFoot(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        //if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        //}
        GL11.glTranslated(-2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
        
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderSkirt(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        //if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        //}
        if (isRiding) {
            GL11.glRotated(-70, 1F, 0F, 0F);
        }

        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftWing(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, double distance, double angle, boolean doLodLoading, MovementType movmentType) {
        GL11.glPushMatrix();

        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (part.getMarkerCount() > 0) {
            point = part.getMarker(0);
            axis = part.getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(SCALE * 0.5F, SCALE * 0.5F, SCALE * 0.5F);
        GL11.glTranslated(SCALE * point.getX(), SCALE * point.getY(), SCALE * point.getZ());

        switch (axis) {
        case UP:
            GL11.glRotated(angle, 0, 1, 0);
            break;
        case DOWN:
            GL11.glRotated(angle, 0, -1, 0);
            break;
        case SOUTH:
            GL11.glRotated(angle, 0, 0, -1);
            break;
        case NORTH:
            GL11.glRotated(angle, 0, 0, 1);
            break;
        case EAST:
            GL11.glRotated(angle, 1, 0, 0);
            break;
        case WEST:
            GL11.glRotated(angle, -1, 0, 0);
            break;
        }

        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);

        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderRightWing(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, double distance, double angle, boolean doLodLoading, MovementType movmentType) {
        GL11.glPushMatrix();
        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (part.getMarkerCount() > 0) {
            point = part.getMarker(0);
            axis = part.getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(SCALE * 0.5F, SCALE * 0.5F, SCALE * 0.5F);
        GL11.glTranslated(SCALE * point.getX(), SCALE * point.getY(), SCALE * point.getZ());

        switch (axis) {
        case UP:
            GL11.glRotated(angle, 0, 1, 0);
            break;
        case DOWN:
            GL11.glRotated(angle, 0, -1, 0);
            break;
        case SOUTH:
            GL11.glRotated(angle, 0, 0, -1);
            break;
        case NORTH:
            GL11.glRotated(angle, 0, 0, 1);
            break;
        case EAST:
            GL11.glRotated(angle, 1, 0, 0);
            break;
        case WEST:
            GL11.glRotated(angle, -1, 0, 0);
            break;
        }

        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);

        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }
}
