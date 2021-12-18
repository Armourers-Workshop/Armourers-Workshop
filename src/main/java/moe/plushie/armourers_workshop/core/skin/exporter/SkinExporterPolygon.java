package moe.plushie.armourers_workshop.core.skin.exporter;//package moe.plushie.armourers_workshop.core.skin.exporter;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinExporter;
//import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
//import moe.plushie.armourers_workshop.core.skin.model.bake.ColouredFace;
//import moe.plushie.armourers_workshop.core.skin.data.Skin;
//import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
//import moe.plushie.armourers_workshop.core.utils.SkinLogger;
//import org.apache.commons.io.IOUtils;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//
//public class SkinExporterPolygon implements ISkinExporter {
//
//    @Override
//    public String[] getFileExtensions() {
//        return new String[] { "ply" };
//    }
//
//    @Override
//    public void exportSkin(Skin skin, File filePath, String filename, float scale) {
//        try {
//            for (int i = 0; i < skin.getPartCount(); i++) {
//                SkinPart skinPart = skin.getParts().get(i);
//                exportPart(skinPart, skin, filePath, filename, scale);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void exportPart(SkinPart skinPart, Skin skin, File filePath, String filename, float scale) throws IOException {
////        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
////
////        for (int i = 0; i < ClientProxy.getNumberOfRenderLayers(); i++) {
////            exportLayer(skinPart, skin, filePath, filename, scale, i);
////        }
//    }
//
//    private void exportLayer(SkinPart skinPart, Skin skin, File filePath, String filename, float scale, int layer) throws IOException {
//        String finalName = filename;
//        finalName += "-" + skin.getType().getName().toLowerCase();
//        finalName += "-" + skinPart.getType().getPartName().toLowerCase();
//        finalName += "-" + layer;
//        finalName += ".ply";
//
//        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
//        ArrayList<ColouredFace> faces = cspd.vertexLists[layer];
//        if (faces.isEmpty()) {
//            return;
//        }
//
//        FileOutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream(new File(filePath, finalName), false);
//        } catch (FileNotFoundException e) {
//            IOUtils.closeQuietly(outputStream);
//            e.printStackTrace();
//            return;
//        }
//        SkinLogger.log("Exporting part " + skinPart);
//
//        String CRLF = "\n";
//
//        OutputStreamWriter os = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII);
//        os.write("ply" + CRLF);
//        os.write("format ascii 1.0" + CRLF);
//        os.write("comment made by RiskyKen" + CRLF);
//        os.write("comment This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);
//
//        os.write("element vertex " + faces.size() * 4 + CRLF);
//        os.write("property float x" + CRLF);
//        os.write("property float y" + CRLF);
//        os.write("property float z" + CRLF);
//        os.write("property uchar red" + CRLF);
//        os.write("property uchar green" + CRLF);
//        os.write("property uchar blue" + CRLF);
//        os.write("element face " + faces.size() + CRLF);
//        os.write("property list uchar int vertex_index" + CRLF);
//        os.write("end_header" + CRLF);
//        os.flush();
//
//        SkinLogger.log("faces to export " + faces.size());
//        for (int i = 0; i < faces.size(); i++) {
//            ColouredFace cf = faces.get(i);
//            // ModLogger.log("writing face " + cf.face);
//            switch (cf.face) {
//            case 0: // NegZFace
//
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x + scale, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x + scale, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//
//                break;
//            case 1: // PosZFace
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x + scale, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x + scale, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                break;
//            case 2: // PosXFace
//
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x + scale, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x + scale, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//
//                break;
//            case 3: // NegXFace
//
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x + scale, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x + scale, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//
//                break;
//            case 4: // PosYFace
//
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x + scale, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x + scale, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x + scale, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x + scale, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//
//                break;
//            case 5: // NegYFace
//
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x, scale * (float) -cf.y, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z - scale, scale * (float) cf.x, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//                writeVert(os, scale * (float) -cf.z, scale * (float) cf.x, scale * (float) -cf.y - scale, cf.r, cf.g, cf.b);
//
//                break;
//            default:
//                break;
//            }
//        }
//
//        for (int i = 0; i < faces.size(); i++) {
//            ColouredFace cf = faces.get(i);
//            os.write(String.format("4 %d %d %d %d", 4 * i, 4 * i + 1, 4 * i + 2, 4 * i + 3) + CRLF);
//        }
//        os.flush();
//        outputStream.flush();
//        outputStream.close();
//    }
//
//    private void writeVert(OutputStreamWriter os, float x, float y, float z, byte r, byte g, byte b) throws IOException {
//        String CRLF = "\n";
//        os.write(String.format("%f %f %f %d %d %d", x, y, z, (r & 0xFF), (g & 0xFF), (b & 0xFF)) + CRLF);
//    }
//}
