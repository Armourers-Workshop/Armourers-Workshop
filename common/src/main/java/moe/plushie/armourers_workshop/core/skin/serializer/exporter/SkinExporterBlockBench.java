package moe.plushie.armourers_workshop.core.skin.serializer.exporter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModLog;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;

public class SkinExporterBlockBench extends SkinExporter {

    @Override
    public Collection<String> extensions() {
        return Collections.singleton("bbmodel");
    }

    @Override
    public void exportSkin(Skin skin, File filePath, String filename, float scale) throws Exception {
        // collect all colors from the part.
        var colors = new LinkedHashSet<Integer>();
        Collections.eachTree(skin.parts(), SkinPart::children, part -> {
            for (var geometry : part.geometries()) {
                if (geometry instanceof SkinCube cube) {
                    for (var dir : OpenDirection.values()) {
                        var color = cube.getPaintColor(dir);
                        if (color.paintType() == SkinPaintTypes.NONE) {
                            continue;
                        }
                        colors.add(color.argb() | 0xff000000);
                    }
                }
            }
        });
        // export to block bench format model.
        var task = new Task(skin, scale, new TextureImage(filename, colors));
        var model = task.export(filename);
        // write to file.
        var outputFile = new File(filePath, filename + ".bbmodel");
        var outputStream = new FileOutputStream(outputFile, false);
        var os = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        new GsonBuilder().create().toJson(model, os);
        os.flush();
        outputStream.flush();
        outputStream.close();
    }

    private static class Task {

        private final Skin skin;

        private final TextureImage image;

        private final float scale;

        private Task(Skin skin, float scale, TextureImage image) {
            this.skin = skin;
            this.scale = scale;
            this.image = image;
        }

        public JsonObject export(String filename) throws Exception {
            var meta = new JsonObject();
            meta.addProperty("format_version", "4.10");
            meta.addProperty("model_format", "free");
            meta.addProperty("box_uv", false);

            var resolution = new JsonObject();
            resolution.addProperty("width", image.width());
            resolution.addProperty("height", image.height());

            var elements = new JsonArray();
            var outliner = new JsonArray();
            var textures = new JsonArray();

            var root = new JsonObject();
            root.addProperty("name", skin.type().registryName().path());
            root.addProperty("uuid", UUID.randomUUID().toString());

            var children = new JsonArray();
            for (var part : skin.parts()) {
                var poseStack = new OpenPoseStack();
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
                children.add(exportPart(poseStack, part, elements, children.size()));
            }

            root.add("children", children);
            outliner.add(root);

            textures.add(exportTexture(image));

            var model = new JsonObject();
            model.addProperty("name", filename);
            model.addProperty("model_identifier", filename);
            model.add("meta", meta);
            model.add("resolution", resolution);
            model.add("elements", elements);
            model.add("outliner", outliner);
            model.add("textures", textures);
            return model;
        }

        private JsonObject exportPart(OpenPoseStack poseStack, SkinPart part, JsonArray elements, int partIndex) {
            var outliner = new JsonObject();
            outliner.addProperty("name", partName(part, partIndex));
            outliner.addProperty("uuid", UUID.randomUUID().toString());
            //outliner.addProperty("export", true);

            var children = new JsonArray();
            for (var geometry : part.geometries()) {
                if (geometry instanceof SkinCube cube) {
                    var cubeObject = exportCube(cube, poseStack);
                    if (cubeObject != null) {
                        elements.add(cubeObject);
                        children.add(cubeObject.get("uuid").getAsString());
                    }
                } else {
                    ModLog.debug("skip unsupported blockbench export geometry: {}", geometry.type());
                }
            }

            var childIndex = 0;
            for (var child : part.children()) {
                poseStack.pushPose();
                part.transform().apply(poseStack);
                children.add(exportPart(poseStack, child, elements, childIndex++));
                poseStack.popPose();
            }
            outliner.add("children", children);
            return outliner;
        }

        private JsonObject exportCube(SkinCube cube, OpenPoseStack poseStack) {
            var faces = new JsonObject();
            for (var dir : OpenDirection.values()) {
                var color = cube.getPaintColor(dir);
                if (color.paintType() == SkinPaintTypes.NONE) {
                    continue;
                }
                var pos = image.get(color.argb() | 0xff000000);
                var face = new JsonObject();
                face.add("uv", array(pos.x(), pos.y(), pos.x() + 1, pos.y() + 1));
                face.addProperty("texture", 0);
                faces.add(transformDirection(dir, poseStack).serializedName(), face);
            }
            if (faces.size() == 0) {
                return null;
            }

            var box = transformBox(cube.boundingBox(), poseStack);
            var transform = cube.transform();
            var cubeObject = new JsonObject();
            cubeObject.addProperty("name", "cube");
            cubeObject.addProperty("type", "cube");
            cubeObject.addProperty("uuid", UUID.randomUUID().toString());
            //cubeObject.addProperty("export", true);
            //cubeObject.addProperty("box_uv", false);
            //cubeObject.addProperty("render_order", renderOrder(cube));
            cubeObject.add("from", vector(box.minX(), box.minY(), box.minZ()));
            cubeObject.add("to", vector(box.maxX(), box.maxY(), box.maxZ()));
            cubeObject.add("origin", vector(transform.pivot().transforming(poseStack.last().pose())));
            cubeObject.add("rotation", vector(transform.rotation().transforming(poseStack.last().normal())));
            cubeObject.add("faces", faces);
            return cubeObject;
        }

        private JsonObject exportTexture(TextureImage image) throws Exception {
            var texture = new JsonObject();
            texture.addProperty("name", image.name());
            texture.addProperty("uuid", UUID.randomUUID().toString());
            //texture.addProperty("id", "0");
            //texture.addProperty("particle", true);
            //texture.addProperty("render_mode", "default");
            texture.addProperty("mode", "bitmap");
            texture.addProperty("saved", true);
            texture.addProperty("width", image.width());
            texture.addProperty("height", image.height());
            texture.addProperty("uv_width", image.width());
            texture.addProperty("uv_height", image.height());
            texture.addProperty("source", exportTextureSource(image));
            return texture;
        }

        private String exportTextureSource(TextureImage image) throws Exception {
            var outputStream = new ByteArrayOutputStream();
            ImageIO.write(image.image(), "png", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }

        private OpenRectangle3f transformBox(OpenRectangle3f box, OpenPoseStack poseStack) {
            var matrix = poseStack.last().pose();
            var x1 = box.minX();
            var y1 = box.minY();
            var z1 = box.minZ();
            var x2 = box.maxX();
            var y2 = box.maxY();
            var z2 = box.maxZ();
            var first = new OpenVector3f(x1, y1, z1).transforming(matrix);
            var minX = first.x();
            var minY = first.y();
            var minZ = first.z();
            var maxX = first.x();
            var maxY = first.y();
            var maxZ = first.z();
            for (var point : new OpenVector3f[]{
                    new OpenVector3f(x2, y1, z1), new OpenVector3f(x1, y2, z1), new OpenVector3f(x1, y1, z2), new OpenVector3f(x2, y2, z1),
                    new OpenVector3f(x2, y1, z2), new OpenVector3f(x1, y2, z2), new OpenVector3f(x2, y2, z2)
            }) {
                var value = point.transforming(matrix);
                minX = Math.min(minX, value.x());
                minY = Math.min(minY, value.y());
                minZ = Math.min(minZ, value.z());
                maxX = Math.max(maxX, value.x());
                maxY = Math.max(maxY, value.y());
                maxZ = Math.max(maxZ, value.z());
            }
            return new OpenRectangle3f(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
        }

        private OpenDirection transformDirection(OpenDirection direction, OpenPoseStack poseStack) {
            var normal = new OpenVector3f(direction.stepX(), direction.stepY(), direction.stepZ());
            normal.transform(poseStack.last().normal());
            var ax = Math.abs(normal.x());
            var ay = Math.abs(normal.y());
            var az = Math.abs(normal.z());
            if (ax >= ay && ax >= az) {
                return OpenDirection.get(normal.x() >= 0 ? OpenDirection.AxisDirection.POSITIVE : OpenDirection.AxisDirection.NEGATIVE, OpenDirection.Axis.X);
            }
            if (ay >= ax && ay >= az) {
                return OpenDirection.get(normal.y() >= 0 ? OpenDirection.AxisDirection.POSITIVE : OpenDirection.AxisDirection.NEGATIVE, OpenDirection.Axis.Y);
            }
            return OpenDirection.get(normal.z() >= 0 ? OpenDirection.AxisDirection.POSITIVE : OpenDirection.AxisDirection.NEGATIVE, OpenDirection.Axis.Z);
        }

        private String renderOrder(SkinCube cube) {
            return switch (cube.options().renderOrder()) {
                case 1 -> "behind";
                case 2 -> "in_front";
                default -> "default";
            };
        }

        private String partName(SkinPart part, int partIndex) {
            var name = part.name();
            if (name != null && !name.isEmpty()) {
                return name;
            }
            return partIndex + "-" + part.type().registryName().path();
        }

        private JsonArray vector(OpenVector3f value) {
            return vector(value.x(), value.y(), value.z());
        }

        private JsonArray vector(Number x, Number y, Number z) {
            return array(x, y, z);
        }

        private JsonArray array(Number... values) {
            var array = new JsonArray();
            for (var value : values) {
                if (value instanceof Float) {
                    array.add(strip(value));
                } else {
                    array.add(value);
                }
            }
            return array;
        }

        private JsonArray array(String... values) {
            var array = new JsonArray();
            for (var value : values) {
                array.add(value);
            }
            return array;
        }

        private Number strip(Number value) {
            var strippedValue = BigDecimal.valueOf(value.doubleValue()).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
            if (strippedValue.scale() <= 0) {
                return strippedValue.intValue();
            }
            return strippedValue.doubleValue();
        }
    }
}
