package moe.plushie.armourers_workshop.core.skin.exporter;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinExporter;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SkinExporterWavefrontObj implements ISkinExporter {

    private static final String CRLF = "\n";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.###");


    private int textureX;
    private int textureY;
    private int faceIndex;
    private float scale;

    private HashMap<Integer, Integer> colors;


    public String[] getFileExtensions() {
        return new String[]{"obj"};
    }

    public void exportSkin(Skin skin, File filePath, String filename, float scale) {
        textureX = 0;
        textureY = 0;
        faceIndex = 0;
        this.scale = scale;
        this.colors = new HashMap<>();

        File outputFile = new File(filePath, filename + ".obj");
        try (FileOutputStream fos = new FileOutputStream(outputFile); OutputStreamWriter os = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            ArrayList<Binder> binders = new ArrayList<>();

            int colorIndex = 0;
            int totalFaces = 0;
            for (SkinPart skinPart : skin.getParts()) {
                SkinCubeData cubeData = skinPart.getCubeData();
                CustomVoxelShape renderShape = cubeData.getRenderShape();
                Rectangle3i bounds = new Rectangle3i(renderShape.bounds());
                Binder binder = new Binder();
                binder.skin = skin;
                binder.skinPart = skinPart;
                binder.skinFaces = SkinCuller.cullFaces(skinPart.getCubeData(), bounds);
                binders.add(binder);
                totalFaces += binder.skinFaces.size();
                for (SkinCubeFace face : binder.skinFaces) {
                    int color = face.getColor().getRGB() | 0xff000000;
                    if (!colors.containsKey(color)) {
                        colors.put(color, colorIndex++);
                    }
                }
            }
            ModLog.debug("Exporting skin with {} total faces.", totalFaces);

            int tsx = colors.size();
            int textureSize = 0;
            while (textureSize * textureSize < tsx * 4) {
                textureSize = getNextPowerOf2(textureSize + 1);
            }
            ModLog.debug("Texture size {}", textureSize);
            BufferedImage texture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
            colors.forEach((color, index) -> {
                int iw = texture.getWidth() / 2;
                int ih = texture.getHeight();
                int ix = (index % iw) * 2;
                int iy = (index / iw) * 2;
                texture.setRGB(ix, ih - 1 - iy, color);
                texture.setRGB(ix + 1, ih - 1 - iy, color);
                texture.setRGB(ix, ih - 1 - iy - 1, color);
                texture.setRGB(ix + 1, ih - 1 - iy - 1, color);
            });

            os.write("# WavefrontObj" + CRLF);
            os.write("# This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);
            os.write("mtllib " + filename + ".mtl" + CRLF);
            // os.write("o " + filename + CRLF);

            int partIndex = 0;
            for (Binder binder : binders) {
                SkinPart skinPart = binder.skinPart;
                MatrixStack matrixStack = new MatrixStack();
                matrixStack.scale(scale, scale, scale);
                Vector3i pos = skinPart.getType().getRenderOffset();
                matrixStack.scale(-1, -1, 1);
                matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                SkinUtils.apply(matrixStack, skinPart, 0, null);
                exportPart(matrixStack, binder.skinFaces, skinPart, binder.skin, os, texture, partIndex++);
            }

            os.flush();
            fos.flush();

            ImageIO.write(texture, "png", new File(filePath, filename + ".png"));

            createMtlFile(filePath, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportPart(MatrixStack matrixStack, ArrayList<SkinCubeFace> faces, SkinPart skinPart, Skin skin, OutputStreamWriter os, BufferedImage texture, int partIndex) throws IOException {
        HashMap<ISkinCube, ArrayList<SkinCubeFace>> splintedFaces = new HashMap<>();
        for (SkinCubeFace face : faces) {
            if (face.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            splintedFaces.computeIfAbsent(face.getCube(), k -> new ArrayList<>()).add(face);
        }
        for (int i = 0; i < SkinCubes.getTotalCubes(); ++i) {
            ArrayList<SkinCubeFace> faces1 = splintedFaces.get(SkinCubes.byId(i));
            if (faces1 != null && !faces1.isEmpty()) {
                exportLayer(matrixStack, faces1, skinPart, skin, os, texture, i, partIndex);
            }
        }
    }


    private void exportLayer(MatrixStack matrixStack, ArrayList<SkinCubeFace> faces, SkinPart skinPart, Skin skin, OutputStreamWriter os, BufferedImage texture, int layer, int partIndex) throws IOException {
        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        ModLog.debug("Exporting part " + skinPart);

        os.write("o " + partIndex + "-" + skinPart.getType().getRegistryName().getPath() + "-" + layerNames[layer] + CRLF);
        os.write("usemtl basetexture" + CRLF);
        os.write("s 1" + CRLF);
        os.flush();

        ModLog.debug("faces to export " + faces.size());
        // Export vertex list.
        for (SkinCubeFace face : faces) {
            byte[][] vertexes = SkinUtils.FACE_VERTEXES[face.getDirection().get3DDataValue()];
            for (int i = 0; i < 4; ++i) {
                writeVert(matrixStack, os, face.x + vertexes[i][0], face.y + vertexes[i][1], face.z + vertexes[i][2]);
            }
        }

        int textureSize = texture.getWidth();
        int iw = textureSize / 2;
        float pixelSize = (1F / textureSize) * 2F;
        double s = 1.0 / textureSize;

        for (SkinCubeFace face : faces) {
            int colour = face.getColor().getRGB() | 0xff000000;
            int index = colors.getOrDefault(colour, 0);

            double ix = (double)(index % iw) * 2 + 0.5;
            double iy = (double)(index / iw) * 2 + 0.5;

            os.write(String.format("vt %f %f", (ix + 1) * s, iy * s) + CRLF);
            os.write(String.format("vt %f %f", (ix + 1) * s, (iy + 1) * s) + CRLF);
            os.write(String.format("vt %f %f", ix * s, (iy + 1) * s) + CRLF);
            os.write(String.format("vt %f %f", ix * s, iy * s) + CRLF);
        }

        for (SkinCubeFace face : faces) {
            byte[][] vertexes = SkinUtils.FACE_VERTEXES[face.getDirection().get3DDataValue()];
            writeNormal(matrixStack, os, vertexes[4][0], vertexes[4][1], vertexes[4][2]);
        }

        for (SkinCubeFace face : faces) {
            // Vertex / texture index / normal index
            os.write("f");
            os.write(String.format(" %d/%d/%d", 4 * faceIndex + 1, 4 * faceIndex + 1, faceIndex + 1));
            os.write(String.format(" %d/%d/%d", 4 * faceIndex + 2, 4 * faceIndex + 2, faceIndex + 1));
            os.write(String.format(" %d/%d/%d", 4 * faceIndex + 3, 4 * faceIndex + 3, faceIndex + 1));
            os.write(String.format(" %d/%d/%d", 4 * faceIndex + 4, 4 * faceIndex + 4, faceIndex + 1));
            os.write(CRLF);
            faceIndex++;
        }
    }

    private void writeVert(MatrixStack matrixStack, OutputStreamWriter os, float x, float y, float z) throws IOException {
        Vector4f q = new Vector4f(x, y, z, 1);
        q.transform(matrixStack.last().pose());
        os.write(String.format("v %f %f %f", q.x(), q.y(), q.z()) + CRLF);
    }

    private void writeNormal(MatrixStack matrixStack, OutputStreamWriter os, float x, float y, float z) throws IOException {
        Vector3f q = new Vector3f(x, y, z);
        q.transform(matrixStack.last().normal());
        os.write(String.format("vn %f %f %f", q.x(), q.y(), q.z()) + CRLF);
    }

    private void createMtlFile(File filePath, String filename) {
        File outputFile = new File(filePath, filename + ".mtl");
        try (FileOutputStream fos = new FileOutputStream(outputFile); OutputStreamWriter os = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            os.write("newmtl basetexture" + CRLF);
            os.write("Ns 96.078431" + CRLF);
            os.write("Ka 1.000000 1.000000 1.000000" + CRLF);
            os.write("Kd 0.800000 0.800000 0.800000" + CRLF);
            os.write("Ks 0.500000 0.500000 0.500000" + CRLF);
            os.write("Ke 0.000000 0.000000 0.000000" + CRLF);
            os.write("Ni 1.000000" + CRLF);
            os.write("d 1.000000" + CRLF);
            os.write("illum 0" + CRLF);
            os.write("map_Kd " + filename + ".png" + CRLF);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNextPowerOf2(int value) {
        return (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(value - 1));
    }

    private static class Binder {
        Skin skin;
        SkinPart skinPart;
        ArrayList<SkinCubeFace> skinFaces;
    }
}
