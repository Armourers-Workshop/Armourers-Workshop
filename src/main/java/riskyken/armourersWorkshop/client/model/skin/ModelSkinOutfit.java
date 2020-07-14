package riskyken.armourersWorkshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.skin.SkinModelTexture;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinPaintCache;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.skin.type.wings.SkinWings.MovementType;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.SkinUtils;

public class ModelSkinOutfit extends AbstractModelSkin {

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        if (skin == null) {
            return;
        }
        ArrayList<SkinPart> parts = skin.getParts();

        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }

        if (ClientProxy.isJrbaClientLoaded()) {
            this.isChild = false;
        }

        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinOutfit);
        RenderHelper.enableGUIStandardItemLighting();

        if (skin.hasPaintData() & showSkinPaint) {
            if (extraColour == null) {
                extraColour = PaintingHelper.getLocalPlayerExtraColours();
            }
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, skinDye, extraColour);
            st.bindTexture();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (!itemRender) {
                GL11.glTranslated(0, -12 * SCALE, 0);
            }
            bipedHead.render(SCALE);
            bipedBody.render(SCALE);
            bipedLeftArm.render(SCALE);
            bipedRightArm.render(SCALE);
            bipedLeftLeg.render(SCALE);
            bipedRightLeg.render(SCALE);
            GL11.glPopAttrib();
        }

        boolean overrideLeftArm = SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.getValue(skin.getProperties());
        boolean overrideRightArm = SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.getValue(skin.getProperties());

        double angle = 45D;
        MovementType movmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skin.getProperties()));

        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);

            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }

            if (part.getPartType().getRegistryName().equals("armourers:head.base")) {
                GL11.glPushMatrix();
                if (isChild) {
                    float f6 = 2.0F;
                    GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                    GL11.glTranslatef(0.0F, 16.0F * SCALE, 0.0F);
                }

                GL11.glColor3f(1F, 1F, 1F);
                GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
                GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleY), 0, 1, 0);
                GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleX), 1, 0, 0);

                if (isSneak) {
                    GL11.glTranslated(0, 1 * SCALE, 0);
                }

                renderHead(skin.getParts().get(0), SCALE, skinDye, extraColour, distance, doLodLoading);

                GL11.glPopMatrix();
            }

            if (part.getPartType().getRegistryName().equals("armourers:chest.base")) {
                renderChest(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:chest.leftArm")) {
                renderLeftArm(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading, overrideLeftArm);
            } else if (part.getPartType().getRegistryName().equals("armourers:chest.rightArm")) {
                renderRightArm(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading, overrideRightArm);
            }

            if (part.getPartType().getRegistryName().equals("armourers:legs.leftLeg")) {
                renderLeftLeg(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:legs.rightLeg")) {
                renderRightLeg(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:legs.skirt")) {
                renderSkirt(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            }

            if (part.getPartType().getRegistryName().equals("armourers:feet.leftFoot")) {
                renderLeftFoot(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getRegistryName().equals("armourers:feet.rightFoot")) {
                renderRightFoot(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            }

            if (isSneak) {
                GL11.glRotated(28F, 1F, 0, 0);
            }

            if (part.getPartType().getRegistryName().equals("armourers:wings.leftWing")) {
                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
                renderLeftWing(part, SCALE, skinDye, extraColour, distance, angle, doLodLoading, movmentType);
            }
            if (part.getPartType().getRegistryName().equals("armourers:wings.rightWing")) {
                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
                renderRightWing(part, SCALE, skinDye, extraColour, distance, -angle, doLodLoading, movmentType);
            }

            GL11.glPopMatrix();
        }

        GL11.glColor3f(1F, 1F, 1F);
    }

    private void renderHead(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColours, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderChest(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glRotated(28F, 1F, 0, 0);
        }
        GL11.glColor3f(1F, 1F, 1F);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftArm(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading, boolean override) {
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

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);

        GL11.glPopMatrix();
    }

    private void renderRightArm(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading, boolean override) {
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

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftLeg(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * scale, 0);
        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderRightLeg(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * scale, 0);
        GL11.glTranslated(-2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderSkirt(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * scale, 0);

        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        if (isRiding) {
            GL11.glRotated(-70, 1F, 0F, 0F);
        }

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftFoot(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * scale, 0);

        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderRightFoot(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * scale, 0);
        GL11.glTranslated(-2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderLeftWing(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, double distance, double angle, boolean doLodLoading, MovementType movmentType) {
        GL11.glPushMatrix();

        Point3D point = new Point3D(0, 0, 0);
        ForgeDirection axis = ForgeDirection.DOWN;

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
        case UNKNOWN:
            break;
        }

        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }

    private void renderRightWing(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, double distance, double angle, boolean doLodLoading, MovementType movmentType) {
        GL11.glPushMatrix();
        Point3D point = new Point3D(0, 0, 0);
        ForgeDirection axis = ForgeDirection.DOWN;

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
        case UNKNOWN:
            break;
        }

        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);

        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }
}