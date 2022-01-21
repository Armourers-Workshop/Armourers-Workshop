package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPart;
import moe.plushie.armourers_workshop.core.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.model.bake.PackedCubeFace;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.SkinLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.IResource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PlayerTextureReader {

    BufferedImage bufferedImage;
    HashMap<ISkinPartType, HashMap<Integer, Integer>> mm = new HashMap<>();

    public PlayerTextureReader(ClientPlayerEntity player) {
        ResourceLocation skinLocation = player.getSkinTextureLocation();
        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(skinLocation);
            bufferedImage = ImageIO.read(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (SkinTexturedModel model : SkinTexturedModel.getPlayerModels(width, height, false)) {
            HashMap<Integer, Integer> mz = mm.computeIfAbsent(model.getPartType(), k -> new HashMap<>());
            model.forEach((u, v, x, y, z, dir) -> {
                int c = bufferedImage.getRGB(u, v);
                if (((c >> 24) & 0xFF) == 0) {
                    return;
                }
                int key = (dir.get3DDataValue() & 0xff) << 24 | (z & 0xff) << 16 | (y & 0xff) << 8 | (x & 0xff);
                mz.put(key, c);
            });
        }

    }

    public int getColor(int x, int y, int z, Direction dir, ISkinPartType partType) {
        HashMap<Integer, Integer> mz = mm.get(partType);
        if (mz == null) {
            return 0;
        }
        int key = (dir.get3DDataValue() & 0xff) << 24 | (z & 0xff) << 16 | (y & 0xff) << 8 | (x & 0xff);
        return mz.getOrDefault(key, 0);
    }

    public void getParts() {
    }


}

