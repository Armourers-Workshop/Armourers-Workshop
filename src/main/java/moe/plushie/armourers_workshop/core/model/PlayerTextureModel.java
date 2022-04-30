package moe.plushie.armourers_workshop.core.model;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class PlayerTextureModel {

    public static final int TEXTURE_OLD_WIDTH = 64;
    public static final int TEXTURE_OLD_HEIGHT = 32;
    public static final int TEXTURE_OLD_SIZE = TEXTURE_OLD_WIDTH * TEXTURE_OLD_HEIGHT;


    // steve 0x1, alex 0x2, v1 only 0x4, v2 only 0x8
    private final static PlayerTextureModel[] PLAYER_TEXTURED_MODELS = {
            // entity + version
            new PlayerTextureModel(SkinPartTypes.BIPED_HAT, -4, -8, -4, 8, 8, 8, 32, 0, 0x0),
            new PlayerTextureModel(SkinPartTypes.BIPED_HEAD, -4, -8, -4, 8, 8, 8, 0, 0, 0x0),
            new PlayerTextureModel(SkinPartTypes.BIPED_CHEST, -4, 0, -2, 8, 12, 4, 16, 16, 0x0),
            new PlayerTextureModel(SkinPartTypes.BIPED_RIGHT_LEG, -2, 0, -2, 4, 12, 4, 0, 16, 0x0),
            new PlayerTextureModel(SkinPartTypes.BIPED_LEFT_LEG, -2, 0, -2, 4, 12, 4, 0, 16, 0x4).setMirror(true), // Mirror Right LEG
            // variant for player
            new PlayerTextureModel(SkinPartTypes.BIPED_LEFT_LEG, -2, 0, -2, 4, 12, 4, 16, 48, 0x8),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_LEFT_PANTS, -2, 0, -2, 4, 12, 4, 0, 48, 0x8),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_RIGHT_PANTS, -2, 0, -2, 4, 12, 4, 0, 32, 0x8),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_JACKET, -4, 0, -2, 8, 12, 4, 16, 32, 0x8),
            // variant for player/steve
            new PlayerTextureModel(SkinPartTypes.BIPED_RIGHT_ARM, -3, -2, -2, 4, 12, 4, 40, 16, 0x1),
            new PlayerTextureModel(SkinPartTypes.BIPED_LEFT_ARM, -1, -2, -2, 4, 12, 4, 40, 16, 0x5).setMirror(true), // Mirror Right ARM
            new PlayerTextureModel(SkinPartTypes.BIPED_LEFT_ARM, -1, -2, -2, 4, 12, 4, 32, 48, 0x9),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_LEFT_SLEEVE, -1, -2, -2, 4, 12, 4, 48, 48, 0x9),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_RIGHT_SLEEVE, -3, -2, -2, 4, 12, 4, 40, 32, 0x9),
            // variant for player/alex
            new PlayerTextureModel(SkinPartTypes.BIPED_RIGHT_ARM, -2, -2, -2, 3, 12, 4, 40, 16, 0x2),
            new PlayerTextureModel(SkinPartTypes.BIPED_LEFT_ARM, -1, -2, -2, 3, 12, 4, 40, 16, 0x6).setMirror(true), // Mirror Right ARM
            new PlayerTextureModel(SkinPartTypes.BIPED_LEFT_ARM, -1, -2, -2, 3, 12, 4, 32, 48, 0xA),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_LEFT_SLEEVE, -1, -2, -2, 3, 12, 4, 48, 48, 0xA),
//            new SkinTexturedModel(SkinPartTypes.PLAYER_RIGHT_SLEEVE, -2, -2, -2, 3, 12, 4, 40, 32, 0xA),
    };

    private final int u;
    private final int v;
    private final int flags;
    private final Rectangle3i bounds;
    private final ISkinPartType partType;
    private final Map<Direction, Rectangle> faces;

    private boolean mirror = false;

    public PlayerTextureModel(ISkinPartType partType, int x, int y, int z, int width, int height, int depth, int u, int v, int flags) {
        this.u = u;
        this.v = v;
        // x = -1, y = v/h, z = u/d
        // x = 1, y = v/h, z = -u/d
        // x = u/w, y = 1, z = -v/d
        // x = u/w, y = -1, z = -v/d
        // x = u/w, y = v/h, z = 1
        // x = -u/w, y = v/h, z = -1
        this.faces = new EnumMap<>(Direction.class);
        this.faces.put(Direction.UP, new Rectangle(depth, 0, width, -depth)); // -y
        this.faces.put(Direction.DOWN, new Rectangle(depth + width, 0, width, -depth)); // +y
        this.faces.put(Direction.NORTH, new Rectangle(depth + width + depth, depth, -width, height)); // -z
        this.faces.put(Direction.SOUTH, new Rectangle(depth, depth, width, height)); // +z
        this.faces.put(Direction.WEST, new Rectangle(depth + width, depth, depth, height)); // -x
        this.faces.put(Direction.EAST, new Rectangle(0, depth, -depth, height)); // +x
        this.flags = flags;
        this.bounds = new Rectangle3i(x, y, z, width, height, depth);
        this.partType = partType;
    }

    public static ArrayList<PlayerTextureModel> getPlayerModels(int width, int height, boolean slim) {
        int flags = 0;
        flags |= slim ? 0x2 : 0x1;
        flags |= (height <= 32) ? 0x4 : 0x8;
        ArrayList<PlayerTextureModel> playerModels = new ArrayList<>();
        for (PlayerTextureModel model : PLAYER_TEXTURED_MODELS) {
            if ((model.flags & flags) == model.flags) {
                playerModels.add(model);
                if (model.partType == SkinPartTypes.BIPED_LEFT_LEG) {
                    playerModels.add(model.copy(SkinPartTypes.BIPED_LEFT_FOOT));
                }
                if (model.partType == SkinPartTypes.BIPED_RIGHT_LEG) {
                    playerModels.add(model.copy(SkinPartTypes.BIPED_RIGHT_FOOT));
                }
            }
        }
        return playerModels;
    }

    public PlayerTextureModel setMirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    public void forEach(IConsumer consumer) {
        for (Direction dir : Direction.values()) {
            Rectangle rect = faces.get(resolveDirection(dir));
            int x = bounds.getMinX() + (bounds.getWidth() - 1) * Math.max(-dir.getStepX(), 0);
            int y = bounds.getMinY() + (bounds.getHeight() - 1) * Math.max(-dir.getStepY(), 0);
            int z = bounds.getMinZ() + (bounds.getDepth() - 1) * Math.max(-dir.getStepZ(), 0);
            int sx = Math.abs(dir.getStepX());
            int sy = Math.abs(dir.getStepY());
            //
            int width = rect.width;
            int height = rect.height;
            if (mirror) {
                width = -width;
            }
            //
            int width1 = Math.abs(width);
            int height1 = Math.abs(height);
            for (int iy = 0; iy < height1; ++iy) {
                for (int ix = 0; ix < width1; ++ix) {
                    int tx = ix * (1 - sx);
                    int ty = iy * (1 - sy);
                    int tz = ix * sx + iy * sy;
                    int iu = width < 0 ? width1 - ix - 1 : ix;
                    int iv = height < 0 ? height1 - iy - 1 : iy;
                    consumer.consume(u + rect.x + iu, v + rect.y + iv, x + tx, y + ty, z + tz, dir);
                }
            }
        }
    }

    public PlayerTextureModel copy(ISkinPartType partType) {
        int x = bounds.getX();
        int y = bounds.getY();
        int z = bounds.getZ();
        int width = bounds.getWidth();
        int height = bounds.getHeight();
        int depth = bounds.getDepth();
        return new PlayerTextureModel(partType, x, y, z, width, height, depth, u, v, flags);
    }

    public Rectangle3i getBounds() {
        return bounds;
    }

    public ISkinPartType getPartType() {
        return partType;
    }

    private Direction resolveDirection(Direction dir) {
        // only invert of the left and right directions
        if (mirror && dir == Direction.EAST) {
            return Direction.WEST;
        }
        if (mirror && dir == Direction.WEST) {
            return Direction.EAST;
        }
        return dir;
    }

    public interface IConsumer {
        void consume(int u, int v, int x, int y, int z, Direction dir);
    }
}
