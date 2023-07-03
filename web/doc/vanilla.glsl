#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
uniform mat4 aw_TextureMatrix;
vec2 awrt_UV0;
in vec2 UV0;
in ivec2 UV1;
uniform mat4 aw_LightmapTextureMatrix;
ivec2 awrt_UV2;
in ivec2 UV2;
uniform mat3 aw_NormalMatrix;
vec3 awrt_Normal;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 aw_ModelViewMat;
mat4 awrt_ModelViewMat;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

#ifdef GL_ES
uniform bool aw_MatrixFlags;
#else
uniform bool aw_MatrixFlags = false;
#endif
void awrt_main_pre() {
    if (aw_MatrixFlags) {
        awrt_UV2 = ivec2(aw_LightmapTextureMatrix * vec4(UV2, 0, 1));
        awrt_UV0 = vec2(aw_TextureMatrix * vec4(UV0, 0, 1));
        awrt_Normal = (Normal * aw_NormalMatrix);
        awrt_ModelViewMat = (ModelViewMat * aw_ModelViewMat);
    } else {
        awrt_UV2 = UV2;
        awrt_UV0 = UV0;
        awrt_Normal = Normal;
        awrt_ModelViewMat = ModelViewMat;
    }
}

void main() {
    awrt_main_pre();

    gl_Position = ProjMat * awrt_ModelViewMat * vec4(Position, 1.0);

    vertexDistance = fog_distance(awrt_ModelViewMat, IViewRotMat * Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, awrt_Normal, Color);
    lightMapColor = texelFetch(Sampler2, awrt_UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = awrt_UV0;
    normal = ProjMat * awrt_ModelViewMat * vec4(awrt_Normal, 0.0);
}

