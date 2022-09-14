#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
// in ivec2 UV2;  // we use static light
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;

uniform mat3 IViewRotMat;
uniform mat3 INormalMat;

uniform int FogShape;

uniform ivec2 LightModulator;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
    vec3 normal1 = Normal * INormalMat;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, normal1, Color);
    lightMapColor = texelFetch(Sampler2, LightModulator / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    normal = ProjMat * ModelViewMat * vec4(normal1, 0.0);
}
