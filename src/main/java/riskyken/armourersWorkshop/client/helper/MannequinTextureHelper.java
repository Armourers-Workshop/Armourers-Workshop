package riskyken.armourersWorkshop.client.helper;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import riskyken.armourersWorkshop.client.texture.PlayerTexture;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin.TextureType;
import riskyken.armourersWorkshop.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public final class MannequinTextureHelper {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    private static final PlayerTexture NO_TEXTURE = new PlayerTexture(null, TextureType.USER);
    
    private MannequinTextureHelper() {}
    
    public static PlayerTexture getMannequinTexture(ItemStack itemStack) {
        PlayerTexture playerTexture = NO_TEXTURE;
        GameProfile gameProfile = null;
        String imageUrl = null;
        
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compound = itemStack.getTagCompound();
            if (compound.hasKey(TAG_OWNER, Constants.NBT.TAG_COMPOUND)) {
                gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
            }
            if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
                imageUrl = compound.getString(TAG_IMAGE_URL);
            }
        }
        
        if (gameProfile != null) {
            playerTexture = getMannequinTexture(gameProfile.getName(), TextureType.USER);
        }
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            playerTexture = getMannequinTexture(imageUrl, TextureType.URL);
        }
        return playerTexture;
    }
    
    public static PlayerTexture getMannequinTexture(TileEntityMannequin tileEntity) {
        PlayerTexture playerTexture = NO_TEXTURE;
        if (tileEntity.getGameProfile() != null && tileEntity.getTextureType() == TextureType.USER) {
            String name = tileEntity.getGameProfile().getName();
            playerTexture = getMannequinTexture(name, TextureType.USER);
        }
        if (!StringUtils.isNullOrEmpty(tileEntity.getImageUrl()) &&  tileEntity.getTextureType() == TextureType.URL) {
            playerTexture = getMannequinTexture(tileEntity.getImageUrl(), TextureType.URL);
        }
        return playerTexture;
    }
    
    private static PlayerTexture getMannequinTexture(String textureString, TextureType textureType) {
        return ClientProxy.playerTextureDownloader.getPlayerTexture(textureString, textureType);
    }
}
