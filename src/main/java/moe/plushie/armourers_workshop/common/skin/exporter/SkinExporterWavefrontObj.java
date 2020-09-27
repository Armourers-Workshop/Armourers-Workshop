package moe.plushie.armourers_workshop.common.skin.exporter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class SkinExporterWavefrontObj implements ISkinExporter {

    private static final String CRLF = "\n";

    private int textureX;
    private int textureY;
    private int faceIndex;
    private float scale;

    @Override
    public String[] getFileExtensions() {
        return new String[] { "obj" };
    }

    @Override
    public void exportSkin(Skin skin, File filePath, String filename, float scale) {
        textureX = 0;
        textureY = 0;
        faceIndex = 0;
        this.scale = scale;

        File outputFile = new File(filePath, filename + ".obj");
        try (FileOutputStream fos = new FileOutputStream(outputFile); OutputStreamWriter os = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            int totalFaces = 0;
            for (int i = 0; i < skin.getPartCount(); i++) {
                SkinPart skinPart = skin.getParts().get(i);
                for (int j = 0; j < ClientProxy.getNumberOfRenderLayers(); j++) {
                    totalFaces += skinPart.getClientSkinPartData().vertexLists[j].size();
                }
            }
            ModLogger.log("Exporting skin with " + totalFaces + " total faces.");

            int textureSize = 0;
            while (textureSize * textureSize < totalFaces * 4) {
                ModLogger.log("Texture size " + textureSize);
                textureSize = getNextPowerOf2(textureSize + 1);
            }
            // textureSize = 128;
            BufferedImage texture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);

            os.write("# WavefrontObj" + CRLF);
            os.write("# This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);
            os.write("mtllib " + filename + ".mtl" + CRLF);
            // os.write("o " + filename + CRLF);

            for (int i = 0; i < skin.getPartCount(); i++) {
                SkinPart skinPart = skin.getParts().get(i);
                exportPart(skinPart, skin, os, texture, i);
            }

            os.flush();
            fos.flush();

            ImageIO.write(texture, "png", new File(filePath, filename + ".png"));

            createMtlFile(filePath, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportPart(SkinPart skinPart, Skin skin, OutputStreamWriter os, BufferedImage texture, int partIndex) throws IOException {
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();

        for (int i = 0; i < ClientProxy.getNumberOfRenderLayers(); i++) {
            exportLayer(skinPart, skin, os, texture, i, partIndex);
        }
    }

    private void exportLayer(SkinPart skinPart, Skin skin, OutputStreamWriter os, BufferedImage texture, int layer, int partIndex) throws IOException {
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        ArrayList<ColouredFace> faces = cspd.vertexLists[layer];
        if (faces.isEmpty()) {
            return;
        }

        String[] layerNames = {"opaque", "glowing", "transparent", "transparent-glowing"};
        ModLogger.log("Exporting part " + skinPart);

        os.write("o " + partIndex + "-" + skinPart.getPartType().getRegistryName().replace(":", "_") + "-" + layerNames[layer] + CRLF);
        os.write("usemtl basetexture" + CRLF);
        os.write("s 1" + CRLF);
        os.flush();

        ModLogger.log("faces to export " + faces.size());
        // Export vertex list.
        for (int i = 0; i < faces.size(); i++) {
            ColouredFace cf = faces.get(i);
            if (PaintTypeRegistry.getInstance().getPaintTypeFormByte(cf.t) == PaintTypeRegistry.PAINT_TYPE_NONE) {
                continue;
            }
            switch (cf.face) {
            case 0: // NegZFace
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y - scale);
                break;
            case 1: // PosZFace
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x, scale * -cf.y);
                break;
            case 2: // PosXFace
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x, scale * -cf.y);
                break;
            case 3: // NegXFace
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y - scale);
                break;
            case 4: // PosYFace
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y);
                break;
            case 5: // NegYFace
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y);
                writeVert(skinPart, os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y - scale);
                writeVert(skinPart, os, scale * -cf.z, scale * cf.x, scale * -cf.y - scale);
                break;
            default:
                break;
            }
        }

        int textureSize = texture.getWidth();
        float pixelSize = (1F / textureSize) * 2F;

        for (int i = 0; i < faces.size(); i++) {
            ColouredFace cf = faces.get(i);
            if (PaintTypeRegistry.getInstance().getPaintTypeFormByte(cf.t) == PaintTypeRegistry.PAINT_TYPE_NONE) {
                continue;
            }
            int colour = PaintingHelper.bytesToInt(new byte[] { cf.r, cf.g, cf.b, (byte) 255 });
            texture.setRGB(textureX, textureSize - 1 - textureY, colour);
            texture.setRGB(textureX + 1, textureSize - 1 - textureY, colour);

            texture.setRGB(textureX, textureSize - 1 - textureY - 1, colour);

            texture.setRGB(textureX + 1, textureSize - 1 - textureY - 1, colour);

            float shift = pixelSize / 4F;

            os.write(String.format("vt %f %f", textureX / 2F * pixelSize + pixelSize - shift, textureY / 2F * pixelSize + shift) + CRLF);
            os.write(String.format("vt %f %f", textureX / 2F * pixelSize + pixelSize - shift, textureY / 2F * pixelSize + pixelSize - shift) + CRLF);
            os.write(String.format("vt %f %f", textureX / 2F * pixelSize + shift, textureY / 2F * pixelSize + pixelSize - shift) + CRLF);
            os.write(String.format("vt %f %f", textureX / 2F * pixelSize + shift, textureY / 2F * pixelSize + shift) + CRLF);

            textureX++;
            textureX++;
            if (textureX >= textureSize) {
                textureX = 0;
                textureY++;
                textureY++;
            }
        }

        for (int i = 0; i < faces.size(); i++) {
            ColouredFace cf = faces.get(i);
            if (PaintTypeRegistry.getInstance().getPaintTypeFormByte(faces.get(i).t) == PaintTypeRegistry.PAINT_TYPE_NONE) {
                continue;
            }
            switch (cf.face) {
            case 0: // NegZFace
                os.write("vn 0 -0.1 0" + CRLF);
                break;
            case 1: // PosZFace
                os.write("vn 0 0.1 0" + CRLF);
                break;
            case 2: // PosXFace
                os.write("vn 0.1 0 0" + CRLF);
                break;
            case 3: // NegXFace
                os.write("vn -1 0 0" + CRLF);
                break;
            case 4: // PosYFace
                os.write("vn 0 0 1" + CRLF);
                break;
            case 5: // NegYFace
                os.write("vn 0 0 -1" + CRLF);
                break;
            default:
                break;
            }
        }

        for (int i = 0; i < faces.size(); i++) {
            if (PaintTypeRegistry.getInstance().getPaintTypeFormByte(faces.get(i).t) == PaintTypeRegistry.PAINT_TYPE_NONE) {
                continue;
            }
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

    private void writeVert(SkinPart skinPart, OutputStreamWriter os, float x, float y, float z) throws IOException {
        IPoint3D pos = skinPart.getPartType().getItemRenderOffset();
        os.write(String.format("v %f %f %f", x + -scale * pos.getZ(), z + -scale * pos.getY(), y * -1 + -scale * pos.getX()) + CRLF);
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
}
