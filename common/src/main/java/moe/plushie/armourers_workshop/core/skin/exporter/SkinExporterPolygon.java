package moe.plushie.armourers_workshop.core.skin.exporter;

import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkin;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinExporter;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class SkinExporterPolygon implements ISkinExporter {

    private static final String CRLF = "\n";

    @Override
    public Collection<String> getExtensions() {
        return Collections.singleton("ply");
    }

    @Override
    public void exportSkin(ISkin skinIn, File filePath, String filename, float scale) throws Exception {
        Skin skin = (Skin) skinIn;
        int partIndex = 0;
        for (SkinPart skinPart : skin.getParts()) {
            exportPart(skinPart, skin, filePath, filename, scale, partIndex++);
        }
    }

    private void exportPart(SkinPart skinPart, Skin skin, File filePath, String filename, float scale, int partIndex) throws IOException {
        SkinCubes cubeData = skinPart.getCubeData();
        Rectangle3i bounds = new Rectangle3i(cubeData.getShape().bounds());
        // user maybe need apply some effects for the glass or glowing blocks,
        // so we need split the glass and glowing block into separate layers.
        HashMap<ISkinCubeType, ArrayList<SkinCubeFace>> faces = new HashMap<>();
        for (SkinCubeFace face : SkinCuller.cullFaces(cubeData, bounds)) {
            if (face.getPaintType() != SkinPaintTypes.NONE) {
                faces.computeIfAbsent(face.getType(), k -> new ArrayList<>()).add(face);
            }
        }
        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        for (int i = 0; i < SkinCubeTypes.getTotalCubes(); ++i) {
            ArrayList<SkinCubeFace> faces1 = faces.get(SkinCubeTypes.byId(i));
            if (faces1 != null && !faces1.isEmpty()) {
                exportLayer(faces1, skinPart, skin, filePath, filename, scale, layerNames[i], partIndex);
            }
        }
    }

    private void exportLayer(ArrayList<SkinCubeFace> faces, SkinPart skinPart, Skin skin, File filePath, String filename, float scale, String layer, int partIndex) throws IOException {
        ModLog.debug("export {} layer of {}:{}, faces: {}", layer, partIndex, skinPart.getType(), faces.size());

        String finalName = filename;
        finalName += "-" + partIndex;
        finalName += "-" + skinPart.getType().getRegistryName().getPath();
        finalName += "-" + layer;
        finalName += ".ply";

        FileOutputStream outputStream = new FileOutputStream(new File(filePath, finalName), false);

        OutputStreamWriter os = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII);
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
        OpenPoseStack poseStack = new OpenPoseStack();
        poseStack.scale(scale, scale, scale);
        poseStack.scale(-1, -1, 1);
        poseStack.rotate(Vector3f.YP.rotationDegrees(90));

        for (SkinCubeFace face : faces) {
            IRectangle3f shape = face.getShape();
            float x = shape.getX();
            float y = shape.getY();
            float z = shape.getZ();
            float w = shape.getWidth();
            float h = shape.getHeight();
            float d = shape.getDepth();
            float[][] vertexes = SkinUtils.getRenderVertexes(face.getDirection());
            for (int i = 0; i < 4; ++i) {
                writeVert(poseStack, os, x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d, face.getColor());
            }
        }

        for (int i = 0; i < faces.size(); i++) {
            os.write(String.format("4 %d %d %d %d", 4 * i, 4 * i + 1, 4 * i + 2, 4 * i + 3) + CRLF);
        }

        os.flush();
        outputStream.flush();
        outputStream.close();
    }

    private void writeVert(OpenPoseStack poseStack, OutputStreamWriter os, float x, float y, float z, IPaintColor color) throws IOException {
        Vector4f q = new Vector4f(x, y, z, 1);
        q.transform(poseStack.last().pose());
        os.write(String.format("%s %s %s %d %d %d", f2s(q.x()), f2s(q.y()), f2s(q.z()), color.getRed(), color.getGreen(), color.getBlue()) + CRLF);
    }

    private String f2s(float value) {
        return SkinExportManager.FLOAT_FORMAT.format(value);
    }
}
