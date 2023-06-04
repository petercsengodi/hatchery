/*
 * Vertex shader for the game's main part.
 */
#version 330

// Incoming vertex position, Model Space.
in vec3 position;

// Incoming vertex position, Model Space.
in vec3 normal;

// Incoming texture coordinates.
in vec2 texCoord;

// Uniform matrix from Model Space to Clip Space.
uniform mat4 modelToClipMatrix;

// Uniform matrix from Model Space to Clip Space.
uniform mat4 modelViewMatrix;

// Outgoing texture coordinates.
out vec2 interpolatedTexCoord;

out vec3 transformedNormal;

void main() {

    // Normally gl_Position is in Clip Space and we calculate it by multiplying 
    // it with the modelToClipMatrix.
    gl_Position = modelToClipMatrix * vec4(position, 1);

    // We assign the texture coordinate to the outgoing variable.
    interpolatedTexCoord = texCoord;

    transformedNormal = mat3(transpose(inverse(modelViewMatrix))) * normal;
}
