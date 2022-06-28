package moe.plushie.armourers_workshop.core.skin.exporter;

import moe.plushie.armourers_workshop.api.skin.ISkin;
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
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.utils.extened.AWMatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

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
        SkinCubeData cubeData = skinPart.getCubeData();
        Rectangle3i bounds = new Rectangle3i(cubeData.getRenderShape().bounds());
        // user maybe need apply some effects for the glass or glowing blocks,
        // so we need split the glass and glowing block into separate layers.
        HashMap<ISkinCube, ArrayList<SkinCubeFace>> faces = new HashMap<>();
        for (SkinCubeFace face : SkinCuller.cullFaces(cubeData, bounds)) {
            if (face.getPaintType() != SkinPaintTypes.NONE) {
                faces.computeIfAbsent(face.getCube(), k -> new ArrayList<>()).add(face);
            }
        }
        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        for (int i = 0; i < SkinCubes.getTotalCubes(); ++i) {
            ArrayList<SkinCubeFace> faces1 = faces.get(SkinCubes.byId(i));
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
        AWMatrixStack matrixStack = AWMatrixStack.create();
        matrixStack.scale(scale, scale, scale);
        matrixStack.scale(-1, -1, 1);
        matrixStack.mul(TrigUtils.rotate(Vector3f.YP, 90, true));

        for (SkinCubeFace face : faces) {
            byte[][] vertexes = SkinUtils.getRenderVertexes(face.getDirection());
            for (int i = 0; i < 4; ++i) {
                writeVert(matrixStack, os, face.x + vertexes[i][0], face.y + vertexes[i][1], face.z + vertexes[i][2], face.getColor());
            }
        }

        for (int i = 0; i < faces.size(); i++) {
            os.write(String.format("4 %d %d %d %d", 4 * i, 4 * i + 1, 4 * i + 2, 4 * i + 3) + CRLF);
        }

        os.flush();
        outputStream.flush();
        outputStream.close();
    }

    private void writeVert(AWMatrixStack matrixStack, OutputStreamWriter os, float x, float y, float z, PaintColor color) throws IOException {
        Vector4f q = new Vector4f(x, y, z, 1);
        matrixStack.applyPose(q);
        os.write(String.format("%s %s %s %d %d %d", f2s(q.x()), f2s(q.y()), f2s(q.z()), color.getRed(), color.getGreen(), color.getBlue()) + CRLF);
    }

    private String f2s(float value) {
        return SkinExportManager.FLOAT_FORMAT.format(value);
    }
}
