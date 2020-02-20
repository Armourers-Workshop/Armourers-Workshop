package moe.plushie.armourers_workshop.client.helper;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MannequinTextureHelper {

    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    private static final PlayerTexture NO_TEXTURE = new PlayerTexture("", TextureType.USER);

    private MannequinTextureHelper() {
    }

    public static PlayerTexture getMannequinTexture(ItemStack itemStack) {
        PlayerTexture playerTexture = NO_TEXTURE;
        GameProfile gameProfile = null;
        String imageUrl = null;

        if (itemStack.hasTagCompound()) {
            NBTTagCompound compound = itemStack.getTagCompound();
            if (compound.hasKey(TAG_OWNER, Constants.NBT.TAG_COMPOUND)) {
                gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
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
        if (tileEntity.PROP_OWNER.get() != null && tileEntity.PROP_TEXTURE_TYPE.get() == TextureType.USER) {
            String name = tileEntity.PROP_OWNER.get().getName();
            playerTexture = getMannequinTexture(name, TextureType.USER);
        }
        if (!StringUtils.isNullOrEmpty(tileEntity.PROP_IMAGE_URL.get()) && tileEntity.PROP_TEXTURE_TYPE.get() == TextureType.URL) {
            playerTexture = getMannequinTexture(tileEntity.PROP_IMAGE_URL.get(), TextureType.URL);
        }
        return playerTexture;
    }

    private static PlayerTexture getMannequinTexture(String textureString, TextureType textureType) {
        return ClientProxy.playerTextureDownloader.getPlayerTexture(textureString, textureType);
    }
}
