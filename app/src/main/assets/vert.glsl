#version 300 es

layout(location = 0) in vec3 Vert;
layout(location = 1) in vec2 UV;

out vec2 FragUV;

void main() {
    FragUV = UV;
    gl_Position = vec4(Vert, 1);
}