package moe.plushie.armourers_workshop.core.skin.serializer.exporter;

import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFaceCuller;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModLog;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SkinExporterWavefrontObj implements SkinExporter {

    private static final String CRLF = "\n";

    private int faceIndex;
    private HashMap<Integer, Integer> colors;

    @Override
    public Collection<String> getExtensions() {
        return Collections.singleton("obj");
    }

    @Override
    public void exportSkin(Skin skin, File filePath, String filename, float scale) throws Exception {
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
            for (var face : task.cubeFaces) {
                if (!face.isVisible()) {
                    continue;
                }
                int color = face.getColor().getRGB() | 0xff000000;
                if (!colors.containsKey(color)) {
                    colors.put(color, colorIndex++);
                }
            }
            tasks.add(task);
            totalFaces += task.cubeFaces.size();
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
            var transform = new SkinPartTransform(part, OpenTransform3f.IDENTITY);
            // apply the render context matrix.
            poseStack.scale(scale, scale, scale);
            poseStack.scale(-1, -1, 1);
            poseStack.rotate(OpenVector3f.YP.rotationDegrees(90));
            // apply the origin offset.
            var pos = part.getType().getRenderOffset();
            poseStack.translate(pos.x(), pos.y(), pos.z());
            // apply the marker rotation and offset.
            transform.apply(poseStack);
            exportPart(poseStack, task.cubeFaces, part, task.skin, os, textureBuilder, partIndex++);
        }

        os.flush();
        fos.flush();

        ImageIO.write(textureBuilder.build(), "png", new File(filePath, filename + ".png"));

        createMtlFile(filePath, filename);
    }

    private void exportPart(OpenPoseStack poseStack, ArrayList<SkinCubeFace> allFaces, SkinPart skinPart, Skin skin, OutputStreamWriter os, TextureBuilder texture, int partIndex) throws IOException {
        // user maybe need apply some effects for the glass or glowing blocks,
        // so we need split the glass and glowing block into separate layers.
        var faces = new HashMap<SkinGeometryType, ArrayList<SkinCubeFace>>();
        for (var face : allFaces) {
            if (face.isVisible()) {
                faces.computeIfAbsent(face.getType(), k -> new ArrayList<>()).add(face);
            }
        }
        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        for (int i = 0; i < SkinGeometryTypes.getTotalCubes(); ++i) {
            var faces1 = faces.get(SkinGeometryTypes.byId(i));
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
            var shape = face.getBoundingBox();
            var x = shape.x();
            var y = shape.y();
            var z = shape.z();
            var w = shape.width();
            var h = shape.height();
            var d = shape.depth();
            var vertexes = SkinCubeFace.getBaseVertices(face.getDirection());
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
            var vertexes = SkinCubeFace.getBaseVertices(face.getDirection());
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
        var v = new OpenVector4f(x, y, z, 1);
        v.transform(poseStack.last().pose());
        os.write(String.format("v %s %s %s", f2s(v.x()), f2s(v.y()), f2s(v.z())) + CRLF);
    }

    private void writeNormal(OpenPoseStack poseStack, OutputStreamWriter os, float x, float y, float z) throws IOException {
        var v = new OpenVector3f(x, y, z);
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
        final ArrayList<SkinCubeFace> cubeFaces;

        Task(Skin skin, SkinPart skinPart) {
            var geometries = skinPart.getGeometries();
            var bounds = new OpenRectangle3i(geometries.getShape().bounds());
            this.skin = skin;
            this.skinPart = skinPart;
            this.cubeFaces = Collections.collect(SkinCubeFaceCuller.cullFaces(geometries, bounds), SkinCubeFace.class);
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
