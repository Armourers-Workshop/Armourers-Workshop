package moe.plushie.armourers_workshop.common.skin.exporter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class SkinExporterWavefrontObj implements ISkinExporter {

    @Override
    public String[] getFileExtensions() {
        return new String[] { "obj" };
    }

    @Override
    public void exportSkin(Skin skin, File filePath, String filename, float scale) {
        try {
            for (int i = 0; i < skin.getPartCount(); i++) {
                SkinPart skinPart = skin.getParts().get(i);
                exportPart(skinPart, skin, filePath, filename, scale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportPart(SkinPart skinPart, Skin skin, File filePath, String filename, float scale) throws IOException {
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();

        for (int i = 0; i < ClientProxy.getNumberOfRenderLayers(); i++) {
            exportLayer(skinPart, skin, filePath, filename, scale, i);
        }
    }

    private void exportLayer(SkinPart skinPart, Skin skin, File filePath, String filename, float scale, int layer) throws IOException {
        String finalName = filename;
        finalName += "-" + skin.getSkinType().getName().toLowerCase();
        finalName += "-" + skinPart.getPartType().getPartName().toLowerCase();
        finalName += "-" + layer;

        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        ArrayList<ColouredFace> faces = cspd.vertexLists[layer];
        if (faces.isEmpty()) {
            return;
        }
        
        int textureSize = 64;

        BufferedImage texture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(filePath, finalName + ".obj"), false);
        } catch (FileNotFoundException e) {
            IOUtils.closeQuietly(outputStream);
            e.printStackTrace();
            return;
        }
        ModLogger.log("Exporting part " + skinPart);

        String CRLF = "\n";

        OutputStreamWriter os = new OutputStreamWriter(outputStream, Charsets.US_ASCII);
        os.write("# WavefrontObj" + CRLF);
        os.write("# This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);
        os.write("mtllib " + finalName + ".mtl" + CRLF);
        os.write("o " + skinPart.getPartType().getPartName().toLowerCase() + CRLF);
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
                writeVert(os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z, scale * cf.x, scale * -cf.y - scale);
                break;
            case 1: // PosZFace
                writeVert(os, scale * -cf.z, scale * cf.x, scale * -cf.y);
                writeVert(os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y);
                writeVert(os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y);
                writeVert(os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y);
                break;
            case 2: // PosXFace
                writeVert(os, scale * -cf.z, scale * cf.x, scale * -cf.y);
                writeVert(os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y);
                writeVert(os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z, scale * cf.x, scale * -cf.y - scale);
                break;
            case 3: // NegXFace
                writeVert(os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y);
                writeVert(os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y);
                break;
            case 4: // PosYFace
                writeVert(os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y);
                writeVert(os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y);
                writeVert(os, scale * -cf.z - scale, scale * cf.x + scale, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z, scale * cf.x + scale, scale * -cf.y - scale);
                break;
            case 5: // NegYFace
                writeVert(os, scale * -cf.z, scale * cf.x, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y - scale);
                writeVert(os, scale * -cf.z - scale, scale * cf.x, scale * -cf.y);
                writeVert(os, scale * -cf.z, scale * cf.x, scale * -cf.y);
                break;
            default:
                break;
            }
        }

        int ix = 0;
        int iy = 0;
        float pixelSize = (1F / textureSize) * 2F;

        for (int i = 0; i < faces.size(); i++) {
            ColouredFace cf = faces.get(i);
            if (PaintTypeRegistry.getInstance().getPaintTypeFormByte(cf.t) == PaintTypeRegistry.PAINT_TYPE_NONE) {
                continue;
            }
            int colour = PaintingHelper.bytesToInt(new byte[] { cf.r, cf.g, cf.b, (byte) 255 });
            texture.setRGB(ix, textureSize - 1 - iy, colour);
            texture.setRGB(ix + 1, textureSize - 1  - iy, colour);

            texture.setRGB(ix, textureSize - 1  - iy - 1, colour);

            texture.setRGB(ix + 1, textureSize - 1  - iy - 1, colour);

            float shift = pixelSize / 4F;

            os.write(String.format("vt %f %f", ix / 2F * pixelSize + pixelSize - shift, iy / 2F * pixelSize + shift) + CRLF);
            os.write(String.format("vt %f %f", ix / 2F * pixelSize + pixelSize - shift, iy / 2F * pixelSize + pixelSize - shift) + CRLF);
            os.write(String.format("vt %f %f", ix / 2F * pixelSize + shift, iy / 2F * pixelSize + pixelSize - shift) + CRLF);
            os.write(String.format("vt %f %f", ix / 2F * pixelSize + shift, iy / 2F * pixelSize + shift) + CRLF);

            ix++;
            ix++;
            if (ix >= textureSize) {
                ix = 0;
                iy++;
                iy++;
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

        os.write("usemtl basetexture" + CRLF);
        os.write("s 1" + CRLF);

        int index = 0;
        for (int i = 0; i < faces.size(); i++) {
            if (PaintTypeRegistry.getInstance().getPaintTypeFormByte(faces.get(i).t) == PaintTypeRegistry.PAINT_TYPE_NONE) {
                continue;
            }
            // Vertex / texture index / normal index
            os.write("f");
            os.write(String.format(" %d/%d/%d", 4 * index + 1, 4 * index + 1, index + 1));
            os.write(String.format(" %d/%d/%d", 4 * index + 2, 4 * index + 2, index + 1));
            os.write(String.format(" %d/%d/%d", 4 * index + 3, 4 * index + 3, index + 1));
            os.write(String.format(" %d/%d/%d", 4 * index + 4, 4 * index + 4, index + 1));
            os.write(CRLF);
            index++;
        }
        os.flush();
        outputStream.flush();
        outputStream.close();

        ImageIO.write(texture, "png", new File(filePath, finalName + ".png"));
    }

    private void writeVert(OutputStreamWriter os, float x, float y, float z) throws IOException {
        String CRLF = "\n";
        os.write(String.format("v %f %f %f", x, z, y) + CRLF);
    }
}
