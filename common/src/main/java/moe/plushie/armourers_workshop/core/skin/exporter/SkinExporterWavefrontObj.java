package moe.plushie.armourers_workshop.core.skin.exporter;

import moe.plushie.armourers_workshop.api.skin.ISkin;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinExporter;
import moe.plushie.armourers_workshop.core.data.transform.SkinPartTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class SkinExporterWavefrontObj implements ISkinExporter {

    private static final String CRLF = "\n";

    private int faceIndex;
    private HashMap<Integer, Integer> colors;

    @Override
    public Collection<String> getExtensions() {
        return Collections.singleton("obj");
    }

    @Override
    public void exportSkin(ISkin skinIn, File filePath, String filename, float scale) throws Exception {
        Skin skin = (Skin) skinIn;
        this.colors = new HashMap<>();
        this.faceIndex = 0;

        var outputFile = new File(filePath, filename + ".obj");
        var fos = new FileOutputStream(outputFile);
        var os = new OutputStreamWriter(fos, StandardCharsets.UTF_8);

        var tasks = new ArrayList<Task>();

        int colorIndex = 0;
        int totalFaces = 0;
        for (var skinPart : skin.getParts()) {
            var task = new Task(skin, skinPart);
            for (var face : task.skinFaces) {
                if (face.getPaintType() == SkinPaintTypes.NONE) {
                    continue;
                }
                int color = face.getColor().getRGB() | 0xff000000;
                if (!colors.containsKey(color)) {
                    colors.put(color, colorIndex++);
                }
            }
            tasks.add(task);
            totalFaces += task.skinFaces.size();
        }
        ModLog.debug("create task with {} total faces.", totalFaces);

        var textureTotalSize = colors.size();
        var textureSize = 0;
        while (textureSize * textureSize < textureTotalSize * 4) {
            textureSize = getNextPowerOf2(textureSize + 1);
        }
        ModLog.debug("create {}x{} texture of {}", textureSize, textureSize, textureTotalSize);

        var textureBuilder = new TextureBuilder(textureSize, textureSize);
        colors.forEach((color, index) -> textureBuilder.setColor(index, color));

        os.write("# WavefrontObj" + CRLF);
        os.write("# This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);
        os.write("mtllib " + filename + ".mtl" + CRLF);

        var partIndex = 0;
        for (var task : tasks) {
            var poseStack = new OpenPoseStack();
            var part = task.skinPart;
            var transform = new SkinPartTransform(part, SkinTransform.IDENTITY);
            // apply the render context matrix.
            poseStack.scale(scale, scale, scale);
            poseStack.scale(-1, -1, 1);
            poseStack.rotate(Vector3f.YP.rotationDegrees(90));
            // apply the origin offset.
            var pos = part.getType().getRenderOffset();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            // apply the marker rotation and offset.
            transform.apply(poseStack);
            exportPart(poseStack, task.skinFaces, part, task.skin, os, textureBuilder, partIndex++);
        }

        os.flush();
        fos.flush();

        ImageIO.write(textureBuilder.build(), "png", new File(filePath, filename + ".png"));

        createMtlFile(filePath, filename);
    }

    private void exportPart(OpenPoseStack poseStack, ArrayList<SkinCubeFace> allFaces, SkinPart skinPart, Skin skin, OutputStreamWriter os, TextureBuilder texture, int partIndex) throws IOException {
        // user maybe need apply some effects for the glass or glowing blocks,
        // so we need split the glass and glowing block into separate layers.
        var faces = new HashMap<ISkinCubeType, ArrayList<SkinCubeFace>>();
        for (var face : allFaces) {
            if (face.getPaintType() != SkinPaintTypes.NONE) {
                faces.computeIfAbsent(face.getType(), k -> new ArrayList<>()).add(face);
            }
        }
        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        for (int i = 0; i < SkinCubeTypes.getTotalCubes(); ++i) {
            var faces1 = faces.get(SkinCubeTypes.byId(i));
            if (faces1 != null && !faces1.isEmpty()) {
                exportLayer(poseStack, faces1, skinPart, skin, os, texture, layerNames[i], partIndex);
            }
        }
    }

    private void exportLayer(OpenPoseStack poseStack, ArrayList<SkinCubeFace> faces, SkinPart skinPart, Skin skin, OutputStreamWriter os, TextureBuilder texture, String layer, int partIndex) throws IOException {
        ModLog.debug("export {} layer of {}:{}, faces: {}", layer, partIndex, skinPart.getType(), faces.size());

        os.write("o " + partIndex + "-" + skinPart.getType().getRegistryName().getPath() + "-" + layer + CRLF);
        os.write("usemtl basetexture" + CRLF);
        os.write("s 1" + CRLF);
        os.flush();

        // Export vertex list.
        for (var face : faces) {
            var shape = face.getShape();
            var x = shape.getX();
            var y = shape.getY();
            var z = shape.getZ();
            var w = shape.getWidth();
            var h = shape.getHeight();
            var d = shape.getDepth();
            var vertexes = SkinUtils.getRenderVertexes(face.getDirection());
            for (var i = 0; i < 4; ++i) {
                writeVert(poseStack, os, x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d);
            }
        }

        // TODO: add adv skin support.
        var scale = 1.0 / texture.width;
        for (var face : faces) {
            int index = colors.getOrDefault(face.getColor().getRGB() | 0xff000000, 0);

            var ix = texture.getX(index) + 0.5;
            var iy = texture.getY(index) + 0.5;

            writeTexture(os, (ix + 1) * scale, iy * scale);
            writeTexture(os, (ix + 1) * scale, (iy + 1) * scale);
            writeTexture(os, ix * scale, (iy + 1) * scale);
            writeTexture(os, ix * scale, iy * scale);
        }

        for (var face : faces) {
            var vertexes = SkinUtils.getRenderVertexes(face.getDirection());
            writeNormal(poseStack, os, vertexes[4][0], vertexes[4][1], vertexes[4][2]);
        }

        for (var face : faces) {
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

    private void writeVert(OpenPoseStack poseStack, OutputStreamWriter os, float x, float y, float z) throws IOException {
        var v = new Vector4f(x, y, z, 1);
        v.transform(poseStack.last().pose());
        os.write(String.format("v %s %s %s", f2s(v.x()), f2s(v.y()), f2s(v.z())) + CRLF);
    }

    private void writeNormal(OpenPoseStack poseStack, OutputStreamWriter os, float x, float y, float z) throws IOException {
        var v = new Vector3f(x, y, z);
        v.transform(poseStack.last().normal());
        os.write(String.format("vn %s %s %s", f2s(v.x()), f2s(v.y()), f2s(v.z())) + CRLF);
    }

    private void writeTexture(OutputStreamWriter os, double x, double y) throws IOException {
        os.write(String.format("vt %s %s", f2s(x), f2s(y)) + CRLF);
    }

    private void createMtlFile(File filePath, String filename) throws IOException {
        var outputFile = new File(filePath, filename + ".mtl");
        var fos = new FileOutputStream(outputFile);
        var os = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
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
    }

    private int getNextPowerOf2(int value) {
        return (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(value - 1));
    }

    private String f2s(float value) {
        return SkinExportManager.FLOAT_FORMAT.format(value);
    }

    private String f2s(double value) {
        return SkinExportManager.DOUBLE_FORMAT.format(value);
    }

    private static class Task {
        final Skin skin;
        final SkinPart skinPart;
        final ArrayList<SkinCubeFace> skinFaces;

        Task(Skin skin, SkinPart skinPart) {
            var cubeData = skinPart.getCubeData();
            var bounds = new Rectangle3i(cubeData.getShape().bounds());
            this.skin = skin;
            this.skinPart = skinPart;
            this.skinFaces = SkinCuller.cullFaces(cubeData, bounds);
        }
    }

    private static class TextureBuilder {

        final int width;
        final int height;
        final BufferedImage image;

        TextureBuilder(int width, int height) {
            this.width = width;
            this.height = height;
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        void setColor(int index, int color) {
            var ix = getX(index);
            var iy = height - 1 - getY(index);
            image.setRGB(ix, iy, color);
            image.setRGB(ix + 1, iy, color);
            image.setRGB(ix, iy - 1, color);
            image.setRGB(ix + 1, iy - 1, color);
        }

        int getX(int index) {
            return (index % (width / 2)) * 2;
        }

        int getY(int index) {
            return (index / (width / 2)) * 2;
        }

        BufferedImage build() {
            return image;
        }
    }
}
