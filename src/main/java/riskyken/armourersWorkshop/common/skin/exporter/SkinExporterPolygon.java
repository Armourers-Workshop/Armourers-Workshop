package riskyken.armourersWorkshop.common.skin.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.utils.ModLogger;

public class SkinExporterPolygon implements ISkinExporter {

    @Override
    public void exportSkin(Skin skin, File file, float scale) {
        try {
            exportPart(skin.getParts().get(0), skin, file, scale);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void exportPart(SkinPart skinPart, Skin skin, File file, float scale) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            IOUtils.closeQuietly(outputStream);
            e.printStackTrace();
            return;
        }
        ModLogger.log("Exporting part " + skinPart);
        
        String CRLF = "\n";
        
        OutputStreamWriter os = new OutputStreamWriter(outputStream, Charsets.US_ASCII);
        os.write("ply" + CRLF);
        os.write("format ascii 1.0" + CRLF);
        os.write("comment made by RiskyKen" + CRLF);
        os.write("comment This file was exported from the Minecraft mod Armourer's Workshop" + CRLF);
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        ModLogger.log("poo part " +  cspd.vertexLists);
        ArrayList<ColouredFace> faces =  cspd.vertexLists[0];
        
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
        
        ModLogger.log("faces to export " +  faces.size());
        for (int i = 0; i < faces.size(); i++) {
            ColouredFace cf = faces.get(i);
            //ModLogger.log("writing face " +  cf.face);
            switch (cf.face) {
            case 0: // NegZFace
                
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x + scale, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x + scale, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                
                break;
            case 1: // PosZFace
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x + scale, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x + scale, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                break;
            case 2: // PosXFace
                
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x + scale, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x + scale, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);

                break;
            case 3: // NegXFace
                
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x + scale, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x + scale, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x , scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                
                break;
            case 4: // PosYFace
                
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x + scale, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x + scale, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x + scale, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x + scale, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                
                break;
            case 5: // NegYFace

                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x, scale * (float)-cf.y, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z - scale, scale * (float)cf.x, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                writeVert(os, scale * (float)-cf.z, scale * (float)cf.x, scale * (float)-cf.y - scale, cf.r, cf.g, cf.b);
                
                break;
            default:
                break;
            }
        }
        
        
        for (int i = 0; i < faces.size(); i++) {
            ColouredFace cf = faces.get(i);
            os.write(String.format("4 %d %d %d %d", 4 * i, 4 * i + 1, 4 * i + 2, 4 * i + 3) + CRLF);
        }
        os.flush();
        outputStream.flush();
        outputStream.close();
    }
    
    private void writeVert(OutputStreamWriter os, float x, float y, float z, byte r, byte g, byte b) throws IOException {
        String CRLF = "\n";
        os.write(String.format("%f %f %f %d %d %d", x, y, z, (r & 0xFF), (g & 0xFF), (b & 0xFF)) + CRLF);
    }
}
