package moe.plushie.armourers_workshop.core.skin.serializer.exporter;

import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFaceCuller;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SkinExporterPolygon implements SkinExporter {

    private static final String CRLF = "\n";

    @Override
    public Collection<String> getExtensions() {
        return Collections.singleton("ply");
    }

    @Override
    public void exportSkin(Skin skin, File filePath, String filename, float scale) throws Exception {
        var partIndex = 0;
        for (var skinPart : skin.getParts()) {
            exportPart(skinPart, skin, filePath, filename, scale, partIndex++);
        }
    }

    private void exportPart(SkinPart skinPart, Skin skin, File filePath, String filename, float scale, int partIndex) throws IOException {
        var task = new Task(skin, skinPart);
        // user maybe need apply some effects for the glass or glowing blocks,
        // so we need split the glass and glowing block into separate layers.
        var faces = new HashMap<SkinGeometryType, ArrayList<SkinCubeFace>>();
        for (var face : task.cubeFaces) {
            if (face.isVisible()) {
                faces.computeIfAbsent(face.getType(), k -> new ArrayList<>()).add(face);
            }
        }
        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        for (var i = 0; i < SkinGeometryTypes.getTotalCubes(); ++i) {
            var faces1 = faces.get(SkinGeometryTypes.byId(i));
            if (faces1 != null && !faces1.isEmpty()) {
                exportLayer(faces1, skinPart, skin, filePath, filename, scale, layerNames[i], partIndex);
            }
        }
    }

    private void exportLayer(ArrayList<SkinCubeFace> faces, SkinPart skinPart, Skin skin, File filePath, String filename, float scale, String layer, int partIndex) throws IOException {
        ModLog.debug("export {} layer of {}:{}, faces: {}", layer, partIndex, skinPart.getType(), faces.size());

        var finalName = filename;
        finalName += "-" + partIndex;
        finalName += "-" + skinPart.getType().getRegistryName().getPath();
        finalName += "-" + layer;
        finalName += ".ply";

        var outputStream = new FileOutputStream(new File(filePath, finalName), false);

        var os = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII);
        os.write("ply" + CRLF);
        os.write("format ascii 1.0" + CRLF);
        os.write("comment made by RiskyKen" + CRLF);
        os.write("comment This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);

        os.write("element vertex " + faces.size() * 4 + CRLF);
        os.write("property float x" + CRLF);
        os.write("property float y" + CRLF);
        os.write("property float z" + CRLF);
        os.write("property uchar red" + CRLF);
        os.write("property uchar green" + CRLF);
        os.write("property uchar blue" + CRLF);
        os.write("element face " + faces.size() + CRLF);
        os.write("property list uchar int vertex_index" + CRLF);
        os.write("end_header" + CRLF);
        os.flush();

        // apply the render context matrix.
        var poseStack = new OpenPoseStack();
        poseStack.scale(scale, scale, scale);
        poseStack.scale(-1, -1, 1);
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(90));

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
                writeVert(poseStack, os, x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d, face.getColor());
            }
        }

        for (var i = 0; i < faces.size(); i++) {
            os.write(String.format("4 %d %d %d %d", 4 * i, 4 * i + 1, 4 * i + 2, 4 * i + 3) + CRLF);
        }

        os.flush();
        outputStream.flush();
        outputStream.close();
    }

    private void writeVert(OpenPoseStack poseStack, OutputStreamWriter os, float x, float y, float z, SkinPaintColor color) throws IOException {
        var q = new OpenVector4f(x, y, z, 1);
        q.transform(poseStack.last().pose());
        os.write(String.format("%s %s %s %d %d %d", f2s(q.x()), f2s(q.y()), f2s(q.z()), color.getRed(), color.getGreen(), color.getBlue()) + CRLF);
    }

    private String f2s(float value) {
        return SkinExportManager.FLOAT_FORMAT.format(value);
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
}
