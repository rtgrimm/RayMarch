#version 300 es

precision highp float;

out vec4 Color;
in vec2 FragUV;

uniform float Time;
uniform vec2 Resolution;
uniform vec2 Mouse;
uniform int ElementCount;

struct UniformData {
    float Time;
    vec2 Resolution;
    vec2 Mouse;
};


struct Element {
    //Ids.x = DistanceFunctionID, DistanceFunctionID.y =  MaterialID
    vec4 Ids;
    mat4 InverseTransform;

};

struct SceneData {
    float Dist;
    vec3 Ray;
    int DistanceId;
};

const int MaxElements = 8;

layout(std140) uniform ElementData {
    Element[MaxElements] Elements;
};

vec3[MaxElements] DirectionVectors;


//{DistanceField}

vec2 distanceFieldForElement(int index, vec3 ray) {
    Element element = Elements[index];


    mat4 inverseTransform = element.InverseTransform;

    return vec2(distanceField(int(element.Ids.x), (inverseTransform * vec4(ray, 1)).xyz), 0);
}

vec3 rayForElement(int index, vec3 eye, float offset) {
    return eye + DirectionVectors[index] * offset;
}

vec3 normal(vec3 ray, int distanceFieldId) {
 vec3 offset = vec3(0.0001, 0, 0);

 float x = distanceFieldForElement(distanceFieldId, ray + offset.xyy).x -
    distanceFieldForElement(distanceFieldId, ray - offset.xyy).x;

 float y = distanceFieldForElement(distanceFieldId, ray + offset.yxy).x -
    distanceFieldForElement(distanceFieldId, ray - offset.yxy).x;

 float z = distanceFieldForElement(distanceFieldId, ray + offset.yyx).x -
    distanceFieldForElement(distanceFieldId, ray - offset.yyx).x;


 return normalize(vec3(x, y, z));
}

SceneData scene(vec3 eye, float offset) {

    vec3 currentRay = rayForElement(0, eye, offset);
    float currentDist = distanceFieldForElement(0, currentRay).x;
    int currentId = 0;

    for(int i = 1; i < ElementCount; i++) {
        vec3 newRay = rayForElement(i, eye, offset);
        float newDist = distanceFieldForElement(i, newRay).x;

        if(newDist < currentDist) {
            currentRay = newRay;
            currentDist = newDist;
            currentId = i;
        }
    }

    SceneData sceneData;
    sceneData.Dist = currentDist;
    sceneData.DistanceId = currentId;
    sceneData.Ray = currentRay;

    return sceneData;
}

float vecAngle(vec3 a, vec3 b) {
  return acos(dot(a, b) / (length(a) * length(b)));
}



vec3 shade(vec3 normal, vec3 ray, vec3 eye) {
    vec2 radialUV = vec2(
        0.5 + (atan(ray.z, ray.x)/(2.0*3.14)),
        0.5 - (asin(ray.y) / 3.14)
    ) * 4.0;

    vec3 lightPos = vec3(0, 0.1, 0.0);
    vec3 color = vec3(1, 1, 0);


    float intensity = 10.0;

    vec3 v = lightPos - ray;
    vec3 l = eye - ray;

    vec3 h = normalize(v + l);



    vec3 specular = vec3(1) * pow(max(0.0, dot(normal, h)), 1024.0);
    vec3 diffuse = color * intensity * max(0.0, dot(lightPos, normal));
    return specular + diffuse;
}


vec4 rayTrace(vec2 fragCoord) {
    vec4 fragColor = vec4(0);

    float aspectRatio = Resolution.x / Resolution.y;
    float fov = 3.14 / 2.0;
    float fovRatio = tan(fov/4.0);

    vec2 uv = vec2(((fragCoord.x * 2.0)) * aspectRatio * fovRatio,
                  	((fragCoord.y * 2.0)) * fovRatio);



    vec2 mouseCoord = Mouse.xy / Resolution.xy;

    vec3 eye = vec3(((mouseCoord.x * 2.0) - 1.0) * 10.0, ((mouseCoord.y * 2.0) - 1.0) * 10.0, -1);
    vec3 up = vec3(0, 1, 0);

    float d = 0.01;

    vec3 forward = -normalize(eye);
    vec3 right = cross(up, forward);
    vec3 p = normalize(((up * uv.y) + (right * uv.x) + (forward * d)) - eye);

	fragColor = vec4(1, 1, 1, 1) * vecAngle(vec3(uv.x, uv.y, 0), forward) * 0.4;

    for(int i = 0; i < ElementCount; i++) {
        DirectionVectors[i] = normalize((Elements[i].InverseTransform * vec4(p, 0)).xyz);
    }

    float offset = 0.0;
    const int maxIt = 64;

    for(int i = 0; i < maxIt; i++) {
        SceneData data = scene(eye, offset);

        if(data.Dist < 0.0001) {
            vec3 norm = normal(data.Ray, data.DistanceId);
            
            fragColor = vec4(shade(norm, data.Ray, eye), 1);
            break;
        }

        offset += data.Dist;
    }

    return fragColor;
}

void main()
{
	vec2 coord = FragUV;

    vec4 rawColor = rayTrace(FragUV);

    Color = (rawColor);
}