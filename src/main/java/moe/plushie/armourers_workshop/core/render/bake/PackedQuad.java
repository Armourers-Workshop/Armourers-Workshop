package moe.plushie.armourers_workshop.core.render.bake;

import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.color.PaintColor;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.model.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.utils.*;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class PackedQuad {

    private final HashMap<SkinRenderType, ArrayList<ColouredFace>> allFaces = new HashMap<>();

    private final Rectangle3i bounds;
    private final CustomVoxelShape renderShape;
    private final ColorDescriptor colorInfo = new ColorDescriptor();
//    private final int[] cubeTotals = new int[SkinCubes.getTotalCubes()];

    public PackedQuad(Rectangle3i bounds, CustomVoxelShape renderShape, ArrayList<ColouredFace> faces) {
        this.bounds = bounds;
        this.renderShape = renderShape;
        this.loadFaces(faces);
    }

    public static PackedQuad from(SkinCubeData data) {
        CustomVoxelShape renderShape = data.getRenderShape();
        Rectangle3i bounds = new Rectangle3i(renderShape.bounds());
        return new PackedQuad(bounds, renderShape, CubeLoader.cullFaces(data, bounds));
    }

    public static HashMap<PlayerTextureModel, PackedQuad> from(int width, int height, int[] paintData) {
        HashMap<PlayerTextureModel, PackedQuad> allQuads = new HashMap<>();
        if (paintData == null || paintData.length == 0) {
            return allQuads;
        }
        for (PlayerTextureModel texturedModel : PlayerTextureModel.getPlayerModels(width, height, false)) {
            ArrayList<ColouredFace> quads = new ArrayList<>();
            texturedModel.forEach((u, v, x, y, z, dir) -> {
                PaintColor paintColor = PaintColor.of(paintData[v * width + u]);
                if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
                    return;
                }
                quads.add(new ColouredFace(x, y, z, paintColor, 255, dir, SkinCubes.SOLID));
            });
            if (quads.size() != 0) {
                Rectangle3i bounds = texturedModel.getBounds();
                CustomVoxelShape renderShape = CustomVoxelShape.box(new Rectangle3f(bounds));
                allQuads.put(texturedModel, new PackedQuad(bounds, renderShape, quads));
            }
        }
        return allQuads;
    }

    public void forEach(BiConsumer<SkinRenderType, ArrayList<ColouredFace>> action) {
        allFaces.forEach(action);
    }

    private void loadFaces(ArrayList<ColouredFace> faces) {
        for (ColouredFace face : faces) {
            if (face.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            SkinRenderType renderType = SkinRenderType.by(face.getCube());
            allFaces.computeIfAbsent(renderType, k -> new ArrayList<>()).add(face);
            colorInfo.add(face.getColor());
        }
        for (ArrayList<ColouredFace> filteredFaces : allFaces.values()) {
            filteredFaces.sort(Comparator.comparingInt(f -> f.getDirection().get3DDataValue()));
        }
    }

    public ColorDescriptor getColorInfo() {
        return colorInfo;
    }

    public Rectangle3i getBounds() {
        return bounds;
    }

    public CustomVoxelShape getRenderShape() {
        return renderShape;
    }

    static class CubeLoader {

        static ArrayList<ColouredFace> cullFaces(SkinCubeData cubeData, Rectangle3i bounds) {
            BitSet[] flags = cullFaceFlags(cubeData, bounds);
            ArrayList<ColouredFace> faces = new ArrayList<>();
            for (int i = 0; i < cubeData.getCubeCount(); ++i) {
                BitSet flag = flags[i];
                for (Direction dir : Direction.values()) {
                    if (flag.get(dir.get3DDataValue())) {
                        ColouredFace face = cubeData.getCubeFace(i, dir);
                        faces.add(face);
                    }
                }
            }
            return faces;
        }

        static BitSet[] cullFaceFlags(SkinCubeData cubeData, Rectangle3i bounds) {
            BitSet[] flags = new BitSet[cubeData.getCubeCount()];

            int[][][] indexes = new int[bounds.getDepth()][bounds.getHeight()][bounds.getWidth()];
            for (int i = 0; i < cubeData.getCubeCount(); i++) {
                int x = cubeData.getCubePosX(i) - bounds.getX();
                int y = cubeData.getCubePosY(i) - bounds.getY();
                int z = cubeData.getCubePosZ(i) - bounds.getZ();
                flags[i] = new BitSet(6);
                indexes[z][y][x] = i + 1;
            }

            ArrayDeque<Vector3i> openList = new ArrayDeque<>();
            HashSet<Vector3i> closedSet = new HashSet<>();

            Rectangle3i searchArea = new Rectangle3i(-1, -1, -1, bounds.getWidth() + 1, bounds.getHeight() + 1, bounds.getDepth() + 1);
            Vector3i startPos = new Vector3i(-1, -1, -1);
            openList.add(startPos);
            closedSet.add(startPos);

            while (openList.size() > 0) {
                ArrayList<Vector3i> pendingList = new ArrayList<>();
                Vector3i pos = openList.poll();
                for (Direction dir : Direction.values()) {
                    Vector3i pos1 = pos.relative(dir, 1);
                    int targetIndex = getCubeIndex(pos1, bounds, indexes);
                    if (targetIndex == -1) {
                        pendingList.add(pos1);
                        continue;
                    }
                    boolean isBlank = false;
                    ISkinCube targetCube = cubeData.getCube(targetIndex);
                    if (targetCube.isGlass()) {
                        pendingList.add(pos1);
                        // when source cube and target cube is linked glass, ignore.
                        int sourceIndex = getCubeIndex(pos, bounds, indexes);
                        if (sourceIndex != -1) {
                            isBlank = cubeData.getCube(targetIndex).isGlass();
                        }
                    }
                    flags[targetIndex].set(dir.get3DDataValue(), !isBlank);
                }
                for (Vector3i pos1 : pendingList) {
                    if (!closedSet.contains(pos1)) {
                        closedSet.add(pos1);
                        if (searchArea.contains(pos1)) {
                            openList.add(pos1);
                        }
                    }
                }
            }
            return flags;
        }

        static int getCubeIndex(Vector3i pos, Rectangle3i bounds, int[][][] indexes) {
            if (pos.getX() >= 0 && pos.getX() < bounds.getWidth()) {
                if (pos.getY() >= 0 && pos.getY() < bounds.getHeight()) {
                    if (pos.getZ() >= 0 && pos.getZ() < bounds.getDepth()) {
                        return indexes[pos.getZ()][pos.getY()][pos.getX()] - 1;
                    }
                }
            }
            return -1;
        }
    }
}
