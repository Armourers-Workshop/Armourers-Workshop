package moe.plushie.armourers_workshop.client.render.tileentities;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.helper.MannequinTextureHelper;
import moe.plushie.armourers_workshop.client.model.ModelHelper;
import moe.plushie.armourers_workshop.client.model.ModelMannequin;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.Contributors;
import moe.plushie.armourers_workshop.common.Contributors.Contributor;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations.BipedPart;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import moe.plushie.armourers_workshop.common.inventory.MannequinSlotType;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer<TileEntityMannequin> {
    
    private static final ResourceLocation circle = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/other/nanoha-circle.png");
    
    private static RenderBlockMannequinItems renderItems = new RenderBlockMannequinItems();
    private static boolean isHalloweenSeason;
    private static boolean isHalloween;
    private final static float SCALE = 0.0625F;
    private static long lastTextureBuild = 0;
    private final ModelMannequin modelSteve;
    private final ModelMannequin modelAlex;
    //private MannequinFakePlayer mannequinFakePlayer;
    //private final RenderPlayer renderPlayer;
    private final Minecraft mc;
    
    public RenderBlockMannequin() {
        //renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
        modelSteve = new ModelMannequin(false);
        modelAlex = new ModelMannequin(true);
    }
    
    @Override
    public void render(TileEntityMannequin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        mc.profiler.startSection("armourersMannequin");
        mc.profiler.startSection("holidayCheck");
        isHalloweenSeason = ModHolidays.HALLOWEEN_SEASON.isHolidayActive();
        isHalloween = ModHolidays.HALLOWEEN.isHolidayActive();
        //mannequinFakePlayer = null; // TODO make a new fake player
        mc.profiler.endStartSection("move");
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableRescaleNormal();

        int rotaion = te.PROP_ROTATION.get();
        GlStateManager.translate(x + 0.5F + te.PROP_OFFSET_X.get(), y + 1F + te.PROP_OFFSET_Y.get(), z + 0.5F + te.PROP_OFFSET_Z.get());
        BipedRotations rots = te.PROP_BIPED_ROTATIONS.get();
        GlStateManager.rotate((float) Math.toDegrees(rots.getPartRotations(BipedPart.CHEST)[0]), 1F, 0F, 0F);
        GlStateManager.rotate((float) Math.toDegrees(rots.getPartRotations(BipedPart.CHEST)[1]), 0F, 1F, 0F);
        GlStateManager.rotate((float) Math.toDegrees(rots.getPartRotations(BipedPart.CHEST)[2]), 0F, 0F, 1F);
        GlStateManager.translate(0, 0.5D, 0);
        
        GlStateManager.scale(SCALE * 15, SCALE * 15, SCALE * 15);
        GlStateManager.translate(0, SCALE * -1.6F, 0);
        
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.rotate(rotaion * 22.5F, 0, 1, 0);
        
        if (te.PROP_DOLL.get()) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, SCALE * 24, 0);
        }
        
        mc.profiler.endStartSection("getTexture");
        ResourceLocation rl;
        boolean slimModel = false;
        boolean download;
        PlayerTexture playerTexture = MannequinTextureHelper.getMannequinTexture(te);
        rl = playerTexture.getResourceLocation();
        slimModel = playerTexture.isSlimModel();
        download = playerTexture.isDownloaded();
        
        ModelMannequin model = modelSteve;
        if (slimModel) {
            model = modelAlex;
        }

        mc.profiler.endStartSection("fakePlayer");
        /*
        if (mannequinFakePlayer == null) {
            mannequinFakePlayer = new MannequinFakePlayer(te.getWorld(), new GameProfile(null, "[Mannequin]"));
            mannequinFakePlayer.posX = x;
            mannequinFakePlayer.posY = y;
            mannequinFakePlayer.posZ = z;
            mannequinFakePlayer.prevPosX = x;
            mannequinFakePlayer.prevPosY = y;
            mannequinFakePlayer.prevPosZ = z;
        }

        if (te.getGameProfile() != null) {
            if (te.getWorldObj() != null & fakePlayer != null) {
                fakePlayer.setEntityId(te.xCoord * 31 * -te.zCoord);
                fakePlayer.isAirBorne = te.isFlying();
                fakePlayer.capabilities.isFlying = te.isFlying();
            }
        } else {
            mannequinFakePlayer.setEntityId(te.xCoord * 31 * -te.zCoord);
            mannequinFakePlayer.isAirBorne = te.isFlying();
            mannequinFakePlayer.capabilities.isFlying = te.isFlying();
        }
        if (fakePlayer != null) {
            fakePlayer.setEntityId(te.xCoord * 31 * -te.zCoord);
            fakePlayer.isAirBorne = te.isFlying();
            fakePlayer.capabilities.isFlying = te.isFlying();
        }

        if (te.getBipedRotations() != null) {
            te.getBipedRotations().applyRotationsToBiped(model);
            te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelArmor);
            te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelArmorChestplate);
        }

        ApiRegistrar.INSTANCE.onRenderMannequin(te, te.getGameProfile());

        model.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        model.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        model.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        model.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        model.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        model.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
*/
        rots.applyRotationsToBiped(model);

        model.bipedBody.rotateAngleX = 0;
        model.bipedBody.rotateAngleY = 0;
        model.bipedBody.rotateAngleZ = 0;
        if (isHalloween) {
            double dX = -x - 0.5F;
            double dY = -y - 0;
            double dZ = -z - 0.5F;

            double yaw = Math.atan2(dZ, dX);
            double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;

            yaw -= Math.toRadians(rotaion * 22.5F - 90F);
            pitch += Math.PI / 2D;

            model.bipedHead.rotateAngleX = (float) (pitch);
            model.bipedHead.rotateAngleY = (float) (yaw);
            model.bipedHead.rotateAngleZ = 0F;
            model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
            model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
            model.bipedHeadwear.rotateAngleZ = 0F;
        }

        mc.profiler.endStartSection("textureBuild");
        
        /*if (te.haveSkinsUpdated()) {
            te.sp = getSkinPointers(te);
        }
        if (te.sp != null) {
            ISkinPointer[] sp = te.sp;
            Skin[] skins = new Skin[sp.length];
            ISkinDye[] dyes = new ISkinDye[sp.length];
            boolean hasPaintedSkin = false;
            
            for (int i = 0; i < sp.length; i++) {
                if (sp[i] != null) {
                    skins[i] = ClientSkinCache.INSTANCE.getSkin(sp[i]);
                    dyes[i] = sp[i].getSkinDye();
                    if (skins[i] != null) {
                        if (skins[i].hasPaintData() | SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skins[i].getProperties())) {
                            hasPaintedSkin = true;
                        }
                    }
                }
            }
            
            if (hasPaintedSkin) {
                if (te.skinTexture == null) {
                    te.skinTexture = new EntityTextureInfo();
                }
                
                te.skinTexture.updateTexture(rl);
                te.skinTexture.updateSkinColour(te.getSkinColour());
                te.skinTexture.updateHairColour(te.getHairColour());
                te.skinTexture.updateSkins(skins);
                te.skinTexture.updateDyes(dyes);
                
                if (te.skinTexture.getNeedsUpdate()) {
                    if (lastTextureBuild + 200L < System.currentTimeMillis()) {
                        lastTextureBuild = System.currentTimeMillis();
                        rl = te.skinTexture.preRender();
                    }
                } else {
                    rl = te.skinTexture.preRender();
                }
            }*/

        mc.profiler.endStartSection("textureBind");
        bindTexture(rl);
        
        
        mc.profiler.endStartSection("selectModelRender");
        /*te.getBipedRotations().hasCustomHead = hasCustomHead(te);
        
        boolean selectingColour = false;
        GuiMannequinTabSkinHair tabSkinHair = null;
        
        if (mc.currentScreen instanceof GuiMannequin) {
            GuiMannequin screen = (GuiMannequin) mc.currentScreen;
            if (screen.tileEntity == te) {
                tabSkinHair = screen.tabSkinAndHair;
                if (tabSkinHair.selectingSkinColour | tabSkinHair.selectingHairColour) {
                    selectingColour = true;
                }
            }

        }
        
        if (selectingColour) {
            GL11.glDisable(GL11.GL_LIGHTING);
            if (te.isVisible() & !(te.getGameProfile() != null && te.getGameProfile().getName().equalsIgnoreCase("null"))) {
                renderModel(te, model, fakePlayer);
            }
            tabSkinHair.hoverColour = getColourAtPos(Mouse.getX(), Mouse.getY());
            GL11.glEnable(GL11.GL_LIGHTING);
        }*/
        
        
        mc.profiler.endStartSection("modelRender");
        if (te.PROP_VISIBLE.get() & !(te.PROP_OWNER.get() != null && te.PROP_OWNER.get().getName().equalsIgnoreCase("null"))) {
            renderModel(te, model);
            /*long time = System.currentTimeMillis();
            int fadeTime = 1000;
            int fade = (int) (time - playerTexture.getDownloadTime());
            if (playerTexture.isDownloaded() & fade < fadeTime) {
                bindTexture(AbstractClientPlayer.locationStevePng);
                renderModel(te, model, fakePlayer);
                bindTexture(rl);
                ModRenderHelper.enableAlphaBlend();
                GL11.glColor4f(1, 1, 1, fade / 1000F);
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(-3F, -3F);
                renderModel(te, model, fakePlayer);
                GL11.glPolygonOffset(0F, 0F);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                ModRenderHelper.disableAlphaBlend();
                GL11.glColor4f(1, 1, 1, 1);
            } else {
                renderModel(te, model, fakePlayer);
            }*/
            if (isHalloweenSeason) {
                GlStateManager.pushMatrix();
                if (model.isChild) {
                    ModelHelper.enableChildModelScale(true, SCALE);
                }
                
                GlStateManager.rotate(180, 0, 0, 1);
                GlStateManager.rotate((float) Math.toDegrees(model.bipedHead.rotateAngleZ), 0, 0, 1);
                GlStateManager.rotate((float) Math.toDegrees(model.bipedHead.rotateAngleY), 0, -1, 0);
                GlStateManager.rotate((float) Math.toDegrees(model.bipedHead.rotateAngleX), -1, 0, 0);
                GlStateManager.translate(0, 4 * SCALE, 0);
                mc.getRenderItem().renderItem(new ItemStack(Blocks.PUMPKIN), TransformType.FIXED);
                if (model.isChild) {
                    ModelHelper.disableChildModelScale();
                }
                
                GlStateManager.popMatrix();
            }
        }
        

        
        
        mc.profiler.endStartSection("magicCircle");
        //Magic circle.
        if (te.PROP_RENDER_EXTRAS.get() & te.PROP_VISIBLE.get()) {
            Contributor contributor = Contributors.INSTANCE.getContributor(te.PROP_OWNER.get());
            if (contributor != null) {
                int offset = te.getPos().hashCode();
                renderMagicCircle(mc, contributor.r, contributor.g, contributor.b, partialTicks, offset, te.PROP_BIPED_ROTATIONS.get().isChild());
            }
        }
        
        //Render items.
        mc.profiler.endStartSection("equippedItems");
        double distance = Minecraft.getMinecraft().player.getDistanceSq(te.getPos());
        if (distance <= ConfigHandlerClient.renderDistanceMannequinEquipment) {
            //renderEquippedItems(te, fakePlayer, model, distance);
        }
        
        
        mc.profiler.endStartSection("reset");
        model.bipedLeftLeg.rotateAngleZ = 0F;
        model.bipedRightLeg.rotateAngleZ = 0F;
        model.bipedHead.rotateAngleZ = 0F;
        model.bipedHeadwear.rotateAngleZ = 0F;
        
        /*renderPlayer.modelArmor.bipedLeftLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmor.bipedRightLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmor.bipedHead.rotateAngleZ = 0F;
        renderPlayer.modelArmor.bipedHeadwear.rotateAngleZ = 0F;
        
        renderPlayer.modelArmorChestplate.bipedLeftLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmorChestplate.bipedRightLeg.rotateAngleZ = 0F;
        renderPlayer.modelArmorChestplate.bipedHead.rotateAngleZ = 0F;
        renderPlayer.modelArmorChestplate.bipedHeadwear.rotateAngleZ = 0F;*/
        mc.profiler.endStartSection("pop");
        
        
        
        GlStateManager.disableRescaleNormal();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        
        mc.profiler.endSection();
        mc.profiler.endSection();
    }
    
    public static void renderMagicCircle(Minecraft mc, byte r, byte g, byte b, float partialTickTime, int offset, boolean isChild) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        if (isChild) {
            ModelHelper.enableChildModelScale(false, SCALE);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.translate(0F, 1.48F, 0F);
        ModRenderHelper.disableLighting();
        float circleScale = 2.0F;
        GlStateManager.scale(circleScale, circleScale, circleScale);
        float rotation = (float)((mc.world.getTotalWorldTime() + offset) / 0.8D % 360D) + partialTickTime;
        GL11.glRotatef(rotation, 0, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        mc.renderEngine.bindTexture(circle);
        Tessellator tess = Tessellator.getInstance();
        tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        tess.getBuffer().pos(-1, 0, -1).tex(1, 0).color(r & 0xFF, g & 0xFF, b & 0xFF, 255).endVertex();
        tess.getBuffer().pos(1, 0, -1).tex(0, 0).color(r & 0xFF, g & 0xFF, b & 0xFF, 255).endVertex();
        tess.getBuffer().pos(1, 0, 1).tex(0, 1).color(r & 0xFF, g & 0xFF, b & 0xFF, 255).endVertex();
        tess.getBuffer().pos(-1, 0, 1).tex(1, 1).color(r & 0xFF, g & 0xFF, b & 0xFF, 255).endVertex();
        tess.draw();
        ModRenderHelper.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (isChild) {
            ModelHelper.disableChildModelScale();
        }
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
    
    private void renderModel(TileEntityMannequin te, ModelBiped targetBiped) {
        if (!hasCustomHead(te)) {
            if (te.PROP_BIPED_ROTATIONS.get().isChild()) {
                ModelHelper.enableChildModelScale(true, SCALE);
            }
            targetBiped.bipedHead.render(SCALE);
            GL11.glDisable(GL11.GL_CULL_FACE);
            targetBiped.bipedHeadwear.render(SCALE);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (te.PROP_BIPED_ROTATIONS.get().isChild()) {
                ModelHelper.disableChildModelScale();
            };
        }
        
        if (te.PROP_BIPED_ROTATIONS.get().isChild()) {
            ModelHelper.enableChildModelScale(false, SCALE);
        }

        targetBiped.bipedBody.render(SCALE);
        targetBiped.bipedRightArm.render(SCALE);
        targetBiped.bipedLeftArm.render(SCALE);
        targetBiped.bipedRightLeg.render(SCALE);
        targetBiped.bipedLeftLeg.render(SCALE);
        
        if (te.PROP_BIPED_ROTATIONS.get().isChild()) {
            ModelHelper.disableChildModelScale();
        }
    }
    
    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r,g,b);
    }
    
    /*private void renderEquippedItems(TileEntityMannequin te, MannequinFakePlayer fakePlayer, ModelMannequin targetBiped, double distance) {
        RenderItem ri = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        MannequinFakePlayer renderEntity = fakePlayer;
        if (renderEntity == null) {
            renderEntity = mannequinFakePlayer;
        }
        
        Color skinColour = new Color(te.getSkinColour());
        Color hairColour = new Color(te.getHairColour());
        
        byte[] extraColours = new byte[6];
        extraColours[0] = (byte) skinColour.getRed();
        extraColours[1] = (byte) skinColour.getGreen();
        extraColours[2] = (byte) skinColour.getBlue();
        extraColours[3] = (byte) hairColour.getRed();
        extraColours[4] = (byte) hairColour.getGreen();
        extraColours[5] = (byte) hairColour.getBlue();
        
        for (int i = 0; i < te.getSizeInventory(); i++) {
            ItemStack stack = te.getStackInSlot(i);
            if (renderEntity != null) {
                if (i == 0 & isHalloweenSeason) {
                    renderEquippedItem(renderEntity, new ItemStack(Blocks.lit_pumpkin), targetBiped, i, extraColours, distance, te.getBipedRotations());
                } else {
                    if (stack != null) {
                        renderEquippedItem(renderEntity, stack, targetBiped, i, extraColours, distance, te.getBipedRotations());
                    }
                }
            }
        }
    }*/
    
    public ItemStack getStackInMannequinSlot(IInventory inventory, MannequinSlotType slot) {
        return inventory.getStackInSlot(slot.ordinal());
    }
    
    private boolean hasCustomHead(IInventory inventory) {
        ItemStack stack = getStackInMannequinSlot(inventory, MannequinSlotType.HEAD);
        if (stack != null) {
            if (stack.getItem() instanceof ItemBlock) {
                return true;
            }
        }
        if (isHalloweenSeason) {
            return true;
        }
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer, false);
            if (skin != null) {
                return SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skin.getProperties());
            }
        }
        return false;
    }
    
    
    /*private void renderEquippedItem(MannequinFakePlayer fakePlayer, ItemStack stack, ModelMannequin targetBiped, int slot, byte[] extraColours, double distance, BipedRotations rots) {
        Item targetItem = stack.getItem();
        RenderManager rm = RenderManager.instance;
        slot = slot % 7;
        String[] slotName = {"head", "chest", "legs", "unused", "feet", "rightArm", "leftArm"};
        
        mc.profiler.startSection(slotName[slot]);
        GL11.glPushMatrix();
        
        boolean isChild = targetBiped.isChild;
        
        if (isChild) {
            ModelHelper.enableChildModelScale(slot == 0, SCALE);
        }
        targetBiped.isChild = false;
        switch (slot) {
        case 0:
            renderItems.renderHeadStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 1:
            renderItems.renderChestStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 2:
            renderItems.renderLegsStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 3:
            renderItems.renderFeetStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 4:
            renderItems.renderRightArmStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 5:
            renderItems.renderLeftArmStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            if (rots != null) {
                rots.applyRotationsToBiped(targetBiped);
            }
            break;
        case 6:
            renderItems.renderWingsStack(fakePlayer, stack, targetBiped, rm, extraColours, distance);
            break;
        }
        
        targetBiped.isChild = isChild;
        if (isChild) {
            ModelHelper.disableChildModelScale();
        }
        GL11.glPopMatrix();
        mc.profiler.endSection();
    }*/
    
    private ISkinDescriptor[] getSkinPointers(TileEntityMannequin te) {
        ISkinDescriptor[] skinPointers = new ISkinDescriptor[4 * TileEntityMannequin.CONS_INVENTORY_ROWS_COUNT];
        
        for (int i = 0; i < TileEntityMannequin.CONS_INVENTORY_ROWS_COUNT; i++) {
            skinPointers[0 + i * 4] = getSkinPointerForSlot(te, 0 + i * 7);
            skinPointers[1 + i * 4] = getSkinPointerForSlot(te, 1 + i * 7);
            skinPointers[2 + i * 4] = getSkinPointerForSlot(te, 2 + i * 7);
            skinPointers[3 + i * 4] = getSkinPointerForSlot(te, 3 + i * 7);
        }

        return skinPointers;
    }
    
    private ISkinDescriptor getSkinPointerForSlot(TileEntityMannequin te, MannequinSlotType slotType) {
        return SkinNBTHelper.getSkinDescriptorFromStack(getStackInMannequinSlot(te, slotType));
    }
    
    private ISkinDescriptor getSkinPointerForSlot(TileEntityMannequin te, int slotIndex) {
        return SkinNBTHelper.getSkinDescriptorFromStack(te.getStackInSlot(slotIndex));
    }
}
