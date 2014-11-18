package riskyken.armourersWorkshop.client.render;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private final ModelMannequin modelMannequin;
    private RenderPlayer renderPlayer = new RenderPlayer();
    
    public RenderBlockMannequin(ModelMannequin modelMannequin) {
        this.modelMannequin = modelMannequin;
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        
        TileEntityMannequin te = (TileEntityMannequin) tileEntity;
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        
        if (te.getGameProfile() != null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            Map map = minecraft.func_152342_ad().func_152788_a(te.getGameProfile());
            if (map.containsKey(Type.SKIN)) {
                resourcelocation = minecraft.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
            }
            if (te.getGameProfile() != null & te.getWorldObj() != null) {
                if (fakePlayer == null) {
                    fakePlayer = new MannequinFakePlayer(te.getWorldObj(),te.getGameProfile());
                    te.setFakePlayer(fakePlayer);
                }
                if (fakePlayer != null) {
                    
                }
            }
        }
        
        
        if (fakePlayer != null) {
            RenderPlayerEvent.Specials.Pre preEvent = new RenderPlayerEvent.Specials.Pre(fakePlayer, renderPlayer, 0);
            MinecraftForge.EVENT_BUS.post(preEvent);
        }
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
        ApiRegistrar.INSTANCE.onRenderMannequin(tileEntity, te.getGameProfile());
        modelMannequin.render(null, 0, 0.0001F, 0, 0, 0, scale, true);
        
        if (fakePlayer != null) {
            RenderPlayerEvent.Specials.Post postEvent = new RenderPlayerEvent.Specials.Post(fakePlayer, renderPlayer, 0);
            MinecraftForge.EVENT_BUS.post(postEvent);
        }
        
        if (player.getDistance(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) < 40) {
            if (tileEntity instanceof TileEntityMannequin) {
                EquipmentPlayerRenderCache.INSTANCE.renderMannequinEquipment(((TileEntityMannequin)tileEntity), modelMannequin);
            }
        }
        
        GL11.glPopMatrix();
    }
}
