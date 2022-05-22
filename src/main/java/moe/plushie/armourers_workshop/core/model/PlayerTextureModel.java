package moe.plushie.armourers_workshop.core.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.SkyBox;
import net.minecraft.util.Direction;

import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerTextureModel {

    public static final int TEXTURE_OLD_WIDTH = 64;
    public static final int TEXTURE_OLD_HEIGHT = 32;
    public static final int TEXTURE_OLD_SIZE = TEXTURE_OLD_WIDTH * TEXTURE_OLD_HEIGHT;

    public static final PlayerTextureModel STAVE_V1 = new PlayerTextureModel(builder -> {
        builder.put(SkinPartTypes.BIPED_HAT, new SkyBox(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPED_HEAD, new SkyBox(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPED_CHEST, new SkyBox(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPED_RIGHT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPED_LEFT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 0, 16, true)); // Mirror Right Leg
        builder.put(SkinPartTypes.BIPED_RIGHT_ARM, new SkyBox(-3, -2, -2, 4, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPED_LEFT_ARM, new SkyBox(-1, -2, -2, 4, 12, 4, 40, 16, true)); // Mirror Right Arm
    });

    public static final PlayerTextureModel STAVE_V2 = new PlayerTextureModel(builder -> {
        builder.put(SkinPartTypes.BIPED_HAT, new SkyBox(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPED_HEAD, new SkyBox(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPED_CHEST, new SkyBox(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPED_RIGHT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPED_LEFT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 16, 48));
        builder.put(SkinPartTypes.BIPED_RIGHT_ARM, new SkyBox(-3, -2, -2, 4, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPED_LEFT_ARM, new SkyBox(-1, -2, -2, 4, 12, 4, 32, 48));
    });

    public static final PlayerTextureModel ALEX_V1 = new PlayerTextureModel(builder -> {
       builder.put(SkinPartTypes.BIPED_HAT, new SkyBox(-4, -8, -4, 8, 8, 8, 32, 0));
       builder.put(SkinPartTypes.BIPED_HEAD, new SkyBox(-4, -8, -4, 8, 8, 8, 0, 0));
       builder.put(SkinPartTypes.BIPED_CHEST, new SkyBox(-4, 0, -2, 8, 12, 4, 16, 16));
       builder.put(SkinPartTypes.BIPED_RIGHT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 0, 16));
       builder.put(SkinPartTypes.BIPED_LEFT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 0, 16, true)); // Mirror Right Leg
       builder.put(SkinPartTypes.BIPED_RIGHT_ARM, new SkyBox(-2, -2, -2, 3, 12, 4, 40, 16));
       builder.put(SkinPartTypes.BIPED_LEFT_ARM, new SkyBox(-1, -2, -2, 3, 12, 4, 40, 16, true)); // Mirror Right Arm
    });

    public static final PlayerTextureModel ALEX_V2 = new PlayerTextureModel(builder -> {
        builder.put(SkinPartTypes.BIPED_HAT, new SkyBox(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPED_HEAD, new SkyBox(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPED_CHEST, new SkyBox(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPED_RIGHT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPED_LEFT_LEG, new SkyBox(-2, 0, -2, 4, 12, 4, 16, 48));
        builder.put(SkinPartTypes.BIPED_RIGHT_ARM, new SkyBox(-2, -2, -2, 3, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPED_LEFT_ARM, new SkyBox(-1, -2, -2, 3, 12, 4, 32, 48));
    });

    public static PlayerTextureModel of(int width, int height, boolean slim) {
        if (slim) {
            if (height <= 32) {
                return ALEX_V1;
            }
            return ALEX_V2;
        } else {
            if (height <= 32) {
                return STAVE_V1;
            }
            return STAVE_V2;
        }
    }

    protected final ImmutableMap<ISkinPartType, SkyBox> skyBoxes;

    public PlayerTextureModel(Consumer<ImmutableMap.Builder<ISkinPartType, SkyBox>> consumer) {
        ImmutableMap.Builder<ISkinPartType, SkyBox> builder = ImmutableMap.builder();
        consumer.accept(builder);
        this.skyBoxes = builder.build();
    }

    public SkyBox get(ISkinPartType partType) {
        if (partType == SkinPartTypes.BIPED_LEFT_FOOT) {
            return get(SkinPartTypes.BIPED_LEFT_LEG);
        }
        if (partType == SkinPartTypes.BIPED_RIGHT_FOOT) {
            return get(SkinPartTypes.BIPED_RIGHT_LEG);
        }
        return skyBoxes.get(partType);
    }

    public Point get(ISkinPartType partType, int x, int y, int z, Direction dir) {
        SkyBox box = get(partType);
        if (box != null) {
            return box.get(x, y, z, dir);
        }
        return null;
    }

    public ImmutableSet<Map.Entry<ISkinPartType, SkyBox>> entrySet() {
        return skyBoxes.entrySet();
    }
}
