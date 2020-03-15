package moe.plushie.armourers_workshop.client.handler;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.init.items.ItemMannequin;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MannequinPlacementHandler {

    private final ModelPlayer modelPlayerSmall = new ModelPlayer(0F, true);
    private final ModelPlayer modelPlayerNormal = new ModelPlayer(0F, false);
    
    public MannequinPlacementHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        drawMannequinBounds(event);
        drawMannequinPlacementGhost(event);
    }
    
    private void drawMannequinBounds(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        World world = event.getPlayer().getEntityWorld();
        RayTraceResult target = event.getTarget();

        if (target != null && target.typeOfHit != RayTraceResult.Type.ENTITY) {
            return;
        }

        if (!(target.entityHit instanceof EntityMannequin)) {
            return;
        }
        EntityMannequin mannequin = (EntityMannequin) target.entityHit;

        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

        AxisAlignedBB aabb = mannequin.getEntityBoundingBox();
        aabb = aabb.offset(-xOff, -yOff, -zOff);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.glLineWidth(1F);
        //GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();

        RenderGlobal.drawSelectionBoundingBox(aabb, 0.1F, 0.1F, 0.1F, 0.75F);

        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }
    
    private void drawMannequinPlacementGhost(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        World world = event.getPlayer().getEntityWorld();
        RayTraceResult target = event.getTarget();
        
        if (target != null && target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = target.getBlockPos();
        
        EnumFacing facing = target.sideHit;
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != ModItems.MANNEQUIN) {
            return;
        }
        
        pos = pos.offset(facing);
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
        
        TextureData textureData = ItemMannequin.getTextureData(stack);
        float size = ItemMannequin.getScale(stack);
        PlayerTexture playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(textureData);
        ModelPlayer targetModel = modelPlayerNormal;
        
        double angle = TrigUtils.getAngleDegrees(xOff, zOff, target.hitVec.x, target.hitVec.z) + 90D;
        if (player.isSneaking()) {
            int l = MathHelper.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
            angle = l * 22.5F + 180F;
        }
        
        if (playerTexture.isSlimModel()) {
            targetModel = modelPlayerSmall;
        }
        float scale = 0.0625F;
        GlStateManager.pushMatrix();
        //ModLogger.log(target.hitVec);
        
        if (player.isSneaking()) {
            GlStateManager.translate(pos.getX() + 0.5F - xOff, pos.getY() - yOff, pos.getZ() + 0.5F - zOff);
        } else {
            GlStateManager.translate(target.hitVec.x - xOff, pos.getY() - yOff, target.hitVec.z - zOff);
        }
        
        GlStateManager.scale(15F * scale, -15F * scale, -15F * scale);
        GlStateManager.scale(size, size, size);
        GlStateManager.translate(0, -24F * scale, 0);
        GlStateManager.rotate((float) angle, 0, 1, 0);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.glLineWidth(1F);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.75F);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(1.0F);
        targetModel.setRotationAngles(0F, 0F, 0F, 0F, 0F, 0F, Minecraft.getMinecraft().player);
        
        BipedRotations bipedRotations = new BipedRotations();
        
        bipedRotations.applyRotationsToBiped(targetModel);
        
        targetModel.bipedHead.render(scale);
        //targetModel.bipedHeadwear.render(scale);
        
        targetModel.bipedBody.render(scale);
        //targetModel.bipedBodyWear.render(scale);
        
        targetModel.bipedLeftArm.render(scale);
        //targetModel.bipedLeftArmwear.render(scale);
        
        targetModel.bipedRightArm.render(scale);
        //targetModel.bipedRightArmwear.render(scale);
        
        
        targetModel.bipedLeftLeg.render(scale);
        //targetModel.bipedLeftLegwear.render(scale);
        
        targetModel.bipedRightLeg.render(scale);
        //targetModel.bipedRightLegwear.render(scale);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
