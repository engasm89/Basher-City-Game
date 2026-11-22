package com.example.opencity.engine

import android.opengl.GLES20
import android.util.Log

object ShaderHelper {

    private const val TAG = "ShaderHelper"

    const val VERTEX_SHADER_CODE = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        uniform vec3 uLightDir;
        uniform vec3 uSunColor;
        uniform vec3 uAmbientColor;
        uniform vec3 uHeadlightPos;
        uniform vec3 uHeadlightDir;
        uniform float uHeadlightEnabled;
        uniform float uNightStreetLight;
        uniform float uUnlitEmissive;
        uniform vec3 uCameraPos;
        
        attribute vec4 aPosition;
        attribute vec3 aNormal;
        attribute vec4 aColor;
        
        varying vec4 vColor;
        varying float vDistance;

        float calcStreetGridDistance(float wx, float wz) {
            float d = 999.0;
            // East-West Boulevards
            if (wx > -210.0 && wx < 210.0) {
                if (wz < -45.0) {
                    // Uniform 40m grid: -180, -140, -100, -60
                    float snapped = clamp(floor((wz + 180.0) * 0.025 + 0.5), 0.0, 3.0) * 40.0 - 180.0;
                    d = min(d, abs(wz - snapped));
                } else {
                    d = min(d, abs(wz - (-30.0)));
                    d = min(d, abs(wz));
                    d = min(d, abs(wz - 35.0));
                    d = min(d, abs(wz - 65.0));
                    d = min(d, abs(wz - 100.0));
                    d = min(d, abs(wz - 135.0));
                }
            }
            if (wx > -210.0 && wx < 360.0) {
                d = min(d, abs(wz - 175.0));
            }
            if (wx > -12.0 && wx < 360.0 && wz > 220.0) {
                d = min(d, abs(wz - 256.0));
                d = min(d, abs(wz - 340.0));
            }
            // North-South Avenues: 0 and symmetric +/- {35, 75, 115, 155, 195}
            if (wz > -195.0 && wz < 190.0) {
                float ax = abs(wx);
                d = min(d, ax);
                if (ax > 14.0) {
                    float snappedX = clamp(floor((ax - 35.0) * 0.025 + 0.5), 0.0, 4.0) * 40.0 + 35.0;
                    d = min(d, abs(ax - snappedX));
                }
            } else if (wz >= 190.0 && wz < 355.0) {
                d = min(d, abs(wx));
                d = min(d, abs(wx - 345.0));
            }
            return d;
        }
        
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vec4 worldPos = uModelMatrix * aPosition;
            vDistance = gl_Position.z;

            if (uUnlitEmissive > 0.5) {
                vColor = clamp(aColor, 0.0, 1.0);
            } else {
                // Calculate normal in world space
                vec3 worldNormal = normalize(mat3(uModelMatrix) * aNormal);
                
                // Directional sunlight / moonlight with soft wrap lighting (uLightDir pre-normalized on CPU)
                float NdotL = dot(worldNormal, uLightDir);
                float wrapDiffuse = max(NdotL * 0.72 + 0.28, 0.0);
                
                // Sky & Ground hemisphere ambient tinting (rich depth)
                float upFactor = worldNormal.y * 0.5 + 0.5;
                vec3 hemiAmbient = mix(uAmbientColor * 0.80, uAmbientColor * 1.18, upFactor);
                
                vec3 litColor = aColor.rgb * (hemiAmbient + uSunColor * wrapDiffuse);
                
                // Subtle specular sheen on surfaces facing light (metallic and glass sheen)
                if (NdotL > 0.0) {
                    vec3 viewDir = normalize(uCameraPos - worldPos.xyz);
                    vec3 halfVec = normalize(uLightDir + viewDir);
                    float spec = pow(max(dot(worldNormal, halfVec), 0.0), 16.0);
                    litColor += uSunColor * (spec * 0.22);
                }

                // City-wide Night Street-Light Illumination ("أضواء تضيء كل شوارع المدينة ليلاً")
                if (uNightStreetLight > 0.01 && worldPos.y < 38.0) {
                    float roadDist = calcStreetGridDistance(worldPos.x, worldPos.z);
                    if (roadDist < 26.0) {
                        float streetFactor = 1.0 - roadDist * 0.0384615;
                        float heightFade = clamp(1.0 - max(worldPos.y - 3.5, 0.0) * 0.0294118, 0.22, 1.0);
                        float warmStrength = streetFactor * heightFade * uNightStreetLight;
                        litColor += aColor.rgb * vec3(1.15, 0.98, 0.68) * warmStrength;
                        litColor += vec3(0.24, 0.20, 0.12) * (streetFactor * clamp(1.0 - worldPos.y * 0.125, 0.0, 1.0) * uNightStreetLight);
                    }
                }
                
                // Headlights long-range spotlight effect (uHeadlightDir pre-normalized on CPU)
                if (uHeadlightEnabled > 0.5) {
                    vec3 lightToPos = worldPos.xyz - uHeadlightPos;
                    float distSq = dot(lightToPos, lightToPos);
                    if (distSq > 0.01 && distSq < 7225.0) {
                        float invDist = inversesqrt(distSq);
                        float dist = distSq * invDist;
                        vec3 dirToPoint = lightToPos * invDist;
                        float spotDot = dot(dirToPoint, uHeadlightDir);
                        if (spotDot > 0.65) {
                            float intensity = (spotDot - 0.65) * 2.85714;
                            float attenuation = 1.0 - (dist * 0.0117647);
                            litColor += (aColor.rgb * 0.7 + vec3(0.3)) * vec3(1.0, 0.96, 0.82) * (intensity * attenuation * 2.2);
                        }
                    }
                }
                
                vColor = vec4(clamp(litColor, 0.0, 1.0), aColor.a);
            }
        }
    """

    const val FRAGMENT_SHADER_CODE = """
        precision mediump float;
        
        uniform vec3 uFogColor;
        uniform float uFogDensity;
        uniform float uFilterMode;
        
        varying vec4 vColor;
        varying float vDistance;

        vec3 applyCityFilter(vec3 c, float mode) {
            if (mode < 0.5) return c;
            float luma = dot(c, vec3(0.299, 0.587, 0.114));
            if (mode < 1.5) {
                // 1.0: Vivid City HDR Clarity (most common default — checked first)
                vec3 sat = mix(vec3(luma), c, 1.24);
                return clamp((sat - 0.5) * 1.07 + 0.5, 0.0, 1.0);
            } else if (mode < 2.5) {
                // 2.0: Golden Sunset Cinema
                vec3 sat = mix(vec3(luma), c, 1.20);
                vec3 contrasted = (sat - 0.5) * 1.08 + 0.5;
                return clamp(contrasted * vec3(1.12, 1.01, 0.82), 0.0, 1.0);
            } else if (mode < 3.5) {
                // 3.0: Cyber Neon Glow
                vec3 sat = mix(vec3(luma), c, 1.38);
                vec3 neonTint = mix(vec3(0.85, 0.95, 1.18), vec3(1.16, 0.88, 1.14), clamp(luma * 1.2, 0.0, 1.0));
                return clamp(sat * neonTint, 0.0, 1.0);
            } else if (mode < 4.5) {
                // 4.0: Cool Arctic Breeze
                vec3 sat = mix(vec3(luma), c, 1.12);
                return clamp(sat * vec3(0.88, 0.99, 1.14), 0.0, 1.0);
            } else {
                // 5.0: Classic Retro Cinema (Warm Sepia Contrast)
                vec3 sepia = vec3(
                    dot(c, vec3(0.393, 0.769, 0.189)),
                    dot(c, vec3(0.349, 0.686, 0.168)),
                    dot(c, vec3(0.272, 0.534, 0.131))
                );
                return clamp(mix(c, sepia, 0.68) * vec3(1.04, 1.0, 0.94), 0.0, 1.0);
            }
        }
        
        void main() {
            vec3 baseRgb = vColor.rgb;
            if (uFogDensity > 0.00001) {
                float fogFactor = clamp(exp(-vDistance * uFogDensity), 0.0, 1.0);
                baseRgb = mix(uFogColor, baseRgb, fogFactor);
            }
            vec3 finalRgb = applyCityFilter(baseRgb, uFilterMode);
            gl_FragColor = vec4(finalRgb, vColor.a);
        }
    """

    fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) {
            Log.e(TAG, "Error creating shader of type $type")
            return 0
        }
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Compilation error: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        if (program == 0) {
            Log.e(TAG, "Could not create program")
            return 0
        }
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Link error: " + GLES20.glGetProgramInfoLog(program))
            GLES20.glDeleteProgram(program)
            return 0
        }
        return program
    }
}
