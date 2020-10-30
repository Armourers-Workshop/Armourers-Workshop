package moe.plushie.armourers_workshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.TexturePaintType;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public class ModelSkinOutfit extends ModelTypeHelper {

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
    }

    @Override
    public void render(Entity entity, Skin skin, SkinRenderData renderData) {
        if (skin == null) {
            return;
        }
        ArrayList<SkinPart> parts = skin.getParts();

        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
        }

        GlStateManager.pushAttrib();
        RenderHelper.enableGUIStandardItemLighting();

        if (skin.hasPaintData() & renderData.isShowSkinPaint() & ClientProxy.getTexturePaintType() == TexturePaintType.TEXTURE_REPLACE) {
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, renderData.getSkinDye(), renderData.getExtraColours());
            st.bindTexture();
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (!renderData.isItemRender()) {
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(-2, 1);
                //GL11.glTranslated(0, -12 * SCALE, 0);
            }
            bipedHead.render(SCALE);
            bipedBody.render(SCALE);
            bipedLeftArm.render(SCALE);
            bipedRightArm.render(SCALE);
            
            GL11.glTranslated(0, 0 , 0.005F);
            GL11.glTranslated(0.02F, 0 , 0);
            bipedLeftLeg.render(SCALE);
            GL11.glTranslated(-0.02F, 0 , 0);
            bipedRightLeg.render(SCALE);
            GL11.glTranslated(0, 0 , -0.005F);
            if (!renderData.isItemRender()) {
                GlStateManager.doPolygonOffset(0F, 0F);
                GlStateManager.disablePolygonOffset();
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        boolean overrideChest = SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skin.getProperties());

        double angle = 45D;

        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);

            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }

            if (part.getPartType().getRegistryName().equals("armourers:head.base")) {
                boolean doHead = true;
                // Fix to stop head skins rendering when using the Real First-Person Render mod.
                if (entity != null && entity.equals(Minecraft.getMinecraft().player)) {
                    if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
                        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
                        for (int j = 0; j < traceElements.length; j++) {
                            StackTraceElement traceElement = traceElements[j];
                            if (traceElement.toString().contains("realrender") | traceElement.toString().contains("rfpf")) {
                                doHead = false;
                                break;
                            }
                        }
                    }
                }
                if (doHead) {
                    renderHead(new SkinPartRenderData(part, renderData));
                }
            }

            if (part.getPartType().getRegistryName().equals("armourers:chest.base")) {
                renderChest(new SkinPartRenderData(part, renderData));
            } else if (part.getPartType().getRegistryName().equals("armourers:chest.leftArm")) {
                renderLeftArm(new SkinPartRenderData(part, renderData), overrideChest);
            } else if (part.getPartType().getRegistryName().equals("armourers:chest.rightArm")) {
                renderRightArm(new SkinPartRenderData(part, renderData), overrideChest);
            }

            if (part.getPartType().getRegistryName().equals("armourers:legs.leftLeg")) {
                renderLeftLeg(new SkinPartRenderData(part, renderData));
            } else if (part.getPartType().getRegistryName().equals("armourers:legs.rightLeg")) {
                renderRightLeg(new SkinPartRenderData(part, renderData));
            } else if (part.getPartType().getRegistryName().equals("armourers:legs.skirt")) {
                renderSkirt(new SkinPartRenderData(part, renderData));
            }

            if (part.getPartType().getRegistryName().equals("armourers:feet.leftFoot")) {
                renderLeftFoot(new SkinPartRenderData(part, renderData));
            } else if (part.getPartType().getRegistryName().equals("armourers:feet.rightFoot")) {
                renderRightFoot(new SkinPartRenderData(part, renderData));
            }

            if (part.getPartType().getRegistryName().equals("armourers:wings.leftWing")) {
                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
                renderLeftWing(new SkinPartRenderData(part, renderData), angle);
            }
            if (part.getPartType().getRegistryName().equals("armourers:wings.rightWing")) {
                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
                renderRightWing(new SkinPartRenderData(part, renderData), -angle);
            }

            GL11.glPopMatrix();
        }
        GlStateManager.popAttrib();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void renderHead(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.translate(0.0F, 1 * partRenderData.getScale(), 0.0F);
        }
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleY), 0, 1, 0);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleX), 1, 0, 0);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderChest(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
        }
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftArm(SkinPartRenderData partRenderData, boolean override) {
        GL11.glPushMatrix();
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);

        GL11.glTranslatef(5.0F * partRenderData.getScale(), 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * partRenderData.getScale(), 0F);
        if (slim & !override) {
            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
        }

        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);

        if (slim & !override) {
            GL11.glTranslatef(-0.25F * partRenderData.getScale(), 0F, 0F);
            GL11.glScalef(0.75F, 1F, 1F);
        }
        renderPart(partRenderData);

        GL11.glPopMatrix();
    }

    private void renderRightArm(SkinPartRenderData partRenderData, boolean override) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);

        GL11.glTranslatef(-5.0F * partRenderData.getScale(), 0F, 0F);
        GL11.glTranslatef(0, 2.0F * partRenderData.getScale(), 0F);
        if (slim & !override) {
            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
        }

        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);

        if (slim & !override) {
            GL11.glTranslatef(0.25F * partRenderData.getScale(), 0F, 0F);
            GL11.glScalef(0.75F, 1F, 1F);
        }

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftLeg(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.translate(0, -3 * partRenderData.getScale(), 4 * partRenderData.getScale());
        }
        // if (!itemRender) {
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        // }
        GL11.glTranslated(2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightLeg(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.translate(0, -3 * partRenderData.getScale(), 4 * partRenderData.getScale());
        }
        // if (!itemRender) {
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        // }
        GL11.glTranslated(-2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftFoot(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.translate(0, -3 * partRenderData.getScale(), 4 * partRenderData.getScale());
        }
        // if (!itemRender) {
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        // }
        GL11.glTranslated(2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightFoot(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.translate(0, -3 * partRenderData.getScale(), 4 * partRenderData.getScale());
        }
        // if (!itemRender) {
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        // }
        GL11.glTranslated(-2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderSkirt(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glColor3f(1F, 1F, 1F);
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.translate(0, -3 * partRenderData.getScale(), 4 * partRenderData.getScale());
        }
        // if (!itemRender) {
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        // }
        if (isRiding) {
            GL11.glRotated(-70, 1F, 0F, 0F);
        }

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftWing(SkinPartRenderData partRenderData, double angle) {
        GL11.glPushMatrix();
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
        }
        GL11.glTranslated(0, 0, partRenderData.getScale() * 2);

        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (partRenderData.getSkinPart().getMarkerCount() > 0) {
            point = partRenderData.getSkinPart().getMarker(0);
            axis = partRenderData.getSkinPart().getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F);
        GL11.glTranslated(partRenderData.getScale() * point.getX(), partRenderData.getScale() * point.getY(), partRenderData.getScale() * point.getZ());

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

        GL11.glTranslated(partRenderData.getScale() * -point.getX(), partRenderData.getScale() * -point.getY(), partRenderData.getScale() * -point.getZ());
        GL11.glTranslated(partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightWing(SkinPartRenderData partRenderData, double angle) {
        GL11.glPushMatrix();
        if (isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
        }
        GL11.glTranslated(0, 0, partRenderData.getScale() * 2);
        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (partRenderData.getSkinPart().getMarkerCount() > 0) {
            point = partRenderData.getSkinPart().getMarker(0);
            axis = partRenderData.getSkinPart().getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F);
        GL11.glTranslated(partRenderData.getScale() * point.getX(), partRenderData.getScale() * point.getY(), partRenderData.getScale() * point.getZ());

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

        GL11.glTranslated(partRenderData.getScale() * -point.getX(), partRenderData.getScale() * -point.getY(), partRenderData.getScale() * -point.getZ());
        GL11.glTranslated(partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }
}
