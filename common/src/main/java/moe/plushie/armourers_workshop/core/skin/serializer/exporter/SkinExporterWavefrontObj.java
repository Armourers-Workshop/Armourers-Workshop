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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class SkinExporterWavefrontObj extends SkinExporter {

    private static final String CRLF = "\n";

    private int faceIndex;

    @Override
    public Collection<String> extensions() {
        return Collections.singleton("obj");
    }

    @Override
    public void exportSkin(Skin skin, File filePath, String filename, float scale) throws Exception {
        this.faceIndex = 0;

        var outputFile = new File(filePath, filename + ".obj");
        var fos = new FileOutputStream(outputFile);
        var os = new OutputStreamWriter(fos, StandardCharsets.UTF_8);

        var tasks = new ArrayList<Task>();
        var colors = new LinkedHashSet<Integer>();

        int totalFaces = 0;
        for (var skinPart : skin.parts()) {
            var task = new Task(skin, skinPart);
            for (var face : task.cubeFaces) {
                if (!face.isVisible()) {
                    continue;
                }
                colors.add(face.color().argb() | 0xff000000);
            }
            tasks.add(task);
            totalFaces += task.cubeFaces.size();
        }
        ModLog.debug("create task with {} total faces.", totalFaces);

        var textureImage = new TextureImage(filename, colors);
        ModLog.debug("create {}x{} texture of {}", textureImage.width(), textureImage.height(), colors.size());

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
            var pos = part.type().renderOffset();
            poseStack.translate(pos.x(), pos.y(), pos.z());
            // apply the marker rotation and offset.
            transform.apply(poseStack);
            exportPart(poseStack, task.cubeFaces, part, task.skin, os, textureImage, partIndex++);
        }

        os.flush();
        fos.flush();

        ImageIO.write(textureImage.image(), "png", new File(filePath, filename + ".png"));

        createMtlFile(filePath, filename);
    }

    private void exportPart(OpenPoseStack poseStack, ArrayList<SkinCubeFace> allFaces, SkinPart skinPart, Skin skin, OutputStreamWriter os, TextureImage texture, int partIndex) throws IOException {
        // user maybe need apply some effects for the glass or glowing blocks,
        // so we need split the glass and glowing block into separate layers.
        var faces = new HashMap<SkinGeometryType, ArrayList<SkinCubeFace>>();
        for (var face : allFaces) {
            if (face.isVisible()) {
                faces.computeIfAbsent(face.type(), k -> new ArrayList<>()).add(face);
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

    private void exportLayer(OpenPoseStack poseStack, ArrayList<SkinCubeFace> faces, SkinPart skinPart, Skin skin, OutputStreamWriter os, TextureImage texture, String layer, int partIndex) throws IOException {
        ModLog.debug("export {} layer of {}:{}, faces: {}", layer, partIndex, skinPart.type(), faces.size());

        os.write("o " + partIndex + "-" + skinPart.type().registryName().path() + "-" + layer + CRLF);
        os.write("usemtl basetexture" + CRLF);
        os.write("s 1" + CRLF);
        os.flush();

        // Export vertex list.
        for (var face : faces) {
            var shape = face.boundingBox();
            var x = shape.x();
            var y = shape.y();
            var z = shape.z();
            var w = shape.width();
            var h = shape.height();
            var d = shape.depth();
            var vertexes = SkinCubeFace.getBaseVertices(face.direction());
            for (var i = 0; i < 4; ++i) {
                writeVert(poseStack, os, x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d);
            }
        }

        // TODO: add adv skin support.
        var widthScale = 1.0 / texture.width();
        var heightScale = 1.0 / texture.height();
        for (var face : faces) {
            var pos = texture.get(face.color().argb() | 0xff000000);

            var x0 = pos.x();
            var y0 = texture.height() - pos.y() - 1;
            var x1 = x0 + 1;
            var y1 = y0 + 1;

            writeTexture(os, (x1 - 0.02f) * widthScale, (y0 + 0.02f) * heightScale);
            writeTexture(os, (x1 - 0.02f) * widthScale, (y1 - 0.02f) * heightScale);
            writeTexture(os, (x0 + 0.02f) * widthScale, (y1 - 0.02f) * heightScale);
            writeTexture(os, (x0 + 0.02f) * widthScale, (y0 + 0.02f) * heightScale);
        }

        for (var face : faces) {
            var vertexes = SkinCubeFace.getBaseVertices(face.direction());
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

    private String f2s(float value) {
        return FLOAT_FORMAT.format(value);
    }

    private String f2s(double value) {
        return DOUBLE_FORMAT.format(value);
    }

    private static class Task {
        private final Skin skin;
        private final SkinPart skinPart;
        private final ArrayList<SkinCubeFace> cubeFaces;

        public Task(Skin skin, SkinPart skinPart) {
            var geometries = skinPart.geometries();
            var bounds = new OpenRectangle3i(geometries.shape().bounds());
            this.skin = skin;
            this.skinPart = skinPart;
            this.cubeFaces = Collections.collect(SkinCubeFaceCuller.cullFaces(geometries, bounds), SkinCubeFace.class);
        }
    }
}
