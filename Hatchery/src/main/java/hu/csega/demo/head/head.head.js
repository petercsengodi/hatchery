import * as THREE from 'three';

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
camera.position.x = 40;
camera.position.y = 5;
camera.position.z = 0;
camera.lookAt(0, 0, 0);

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);


//////////////////////////////////////////////////////////////////////////////////////////////

var HeadShape = {
    textureCrisis: new THREE.TextureLoader().load("crisis.png"),
    textureFaceSmile: new THREE.TextureLoader().load("face-smile.png"),
    textureFaceSad: new THREE.TextureLoader().load("face-sad.png"),
    textureFaceTired: new THREE.TextureLoader().load("face-tired.png"),

    castShadow: true,
    receiveShadow: false, // If true, the shadow casted on itself is not perfect.

    init: function() {
        this.numberOfVerticesInARow = 0;
        this.headIndex = 0;

        this.rLarge = 10;
        this.hLarge = 8;

        this.rNeck = 6.1;
        this.hNeck = 3;

        this.rBoop = 6;
        this.hBoop = 3;

        this.rCurve = (this.rLarge - this.rBoop);

        this.delta = 0.3;
        this.cylinderAngleDelta = this.delta / this.rLarge;
        this.curveAngleDelta = this.delta / this.rCurve;

        this.PI2 = 2 * Math.PI;
        this.PIper2 = Math.PI / 2;
        this.FaceTo = Math.PI / 6;
        this.FaceFrom = - this.FaceTo;
        this.FaceWidth = this.FaceTo - this.FaceFrom;
        this.HeadFrom = this.FaceTo;
        this.HeadTo = this.PI2 - this.FaceTo;

        var cylinderAngle;
        for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
            this.numberOfVerticesInARow++;
            if(cylinderAngle >= this.PI2) {
                break;
            }
        }

        var headVertices = [];
        var headIndices = [];

        this.partBottom(headVertices, headIndices);
        this.partNeckCylinder(headVertices, headIndices);
        this.partLowerCurve(headVertices, headIndices);
        this.partBackOfHead(headVertices, headIndices);
        this.partUpperCurve(headVertices, headIndices);
        this.partBoopCylinder(headVertices, headIndices);

        const headGeometry = new THREE.BufferGeometry();
        headGeometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(headVertices), 3));
        headGeometry.setIndex(headIndices);
        headGeometry.computeVertexNormals();
        const headMaterial = new THREE.MeshPhongMaterial( { map: this.textureCrisis, ambient: 0x050505, specular: 0x555555, shininess: 30 });
        this.headMesh = new THREE.Mesh(headGeometry, headMaterial);
        this.headMesh.castShadow = this.castShadow;
        this.headMesh.receiveShadow = this.receiveShadow;

        this.partTop();

        this.partFace();
    },

    partBottom: function(headVertices, headIndices) {
        var y = 0;
        headVertices.push(0);
        headVertices.push(y);
        headVertices.push(0);

        var centerIndex = this.headIndex;
        this.headIndex++;

        var firstRow = true;
        var finishRadius = false;
        var currentIndex = this.headIndex;

        var radius;
        for(radius = this.delta; ; radius += this.delta) {

            if(radius >= this.rBoop) {
                radius = this.rBoop;
                finishRadius = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.PI2) {
                    cylinderAngle = this.PI2;
                    finish = true;
                }

                headVertices.push(Math.cos(cylinderAngle) * radius);
                headVertices.push(y);
                headVertices.push(Math.sin(cylinderAngle) * radius);
                this.headIndex++;

                if(firstRow && cylinderAngle > 0) {
                    headIndices.push(centerIndex);
                    headIndices.push(currentIndex-1);
                    headIndices.push(currentIndex);
                }

                if(!firstRow && cylinderAngle > 0) {
                    headIndices.push(currentIndex - this.numberOfVerticesInARow - 1);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishRadius) {
                break;
            }

            firstRow = false;
        } // end for curveAngle

    }, // end of function partBottom(...)

    partNeckCylinder: function(headVertices, headIndices) {
        var finishHeight = false;
        var currentIndex = this.headIndex;

        var height;
        for(height = this.delta; ; height += this.delta) {
            if(height >= this.hNeck) {
                height = this.hNeck;
                finishHeight = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.PI2) {
                    cylinderAngle = this.PI2;
                    finish = true;
                }

                headVertices.push(Math.cos(cylinderAngle) * this.rNeck);
                headVertices.push(height);
                headVertices.push(Math.sin(cylinderAngle) * this.rNeck);
                this.headIndex++;

                if(cylinderAngle > 0) {
                    headIndices.push(currentIndex - this.numberOfVerticesInARow - 1);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

                } // end for cylinderAngle

                if(finishHeight) {
                    break;
                }

        } // end for height

    }, // end of function partNeckCylinder(...)

    partLowerCurve: function(headVertices, headIndices) {
        var finishCurve = false;
        var currentIndex = this.headIndex;

        var curveAngle;
        for(curveAngle = this.curveAngleDelta; ; curveAngle += this.curveAngleDelta) {
            if(curveAngle >= this.PIper2) {
                curveAngle = this.PIper2;
                finishCurve = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.PI2) {
                    cylinderAngle = this.PI2;
                    finish = true;
                }

                headVertices.push(Math.cos(cylinderAngle) * (this.rLarge - this.rCurve + this.rCurve * Math.cos(curveAngle - this.PIper2)));
                headVertices.push(this.hNeck + this.rCurve + this.rCurve * (Math.sin(curveAngle - this.PIper2)));
                headVertices.push(Math.sin(cylinderAngle) * (this.rLarge - this.rCurve + this.rCurve * Math.cos(curveAngle - this.PIper2)));
                this.headIndex++;

                if(curveAngle > 0 && cylinderAngle > 0) {
                    headIndices.push(currentIndex - this.numberOfVerticesInARow - 1);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishCurve) {
                break;
            }

        } // end for curveAngle

    }, // end of function partLowerCurve(...)

    partBackOfHead: function(headVertices, headIndices) {
        var finishHeight = false;
        var backOfHeadVerticesInARow = 0;
        var currentIndex = this.headIndex;

        var height;
        for(height = 0; ; height += this.delta) {

            if(height >= this.hLarge) {
                height = this.hLarge;
                finishHeight = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = this.HeadFrom; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.HeadTo) {
                    cylinderAngle = this.HeadTo;
                    finish = true;
                }

                headVertices.push(Math.cos(cylinderAngle) * this.rLarge);
                headVertices.push(this.hNeck + this.rCurve + height);
                headVertices.push(Math.sin(cylinderAngle) * this.rLarge);
                this.headIndex++;

                if(height <= 0) {
                    backOfHeadVerticesInARow++;
                }

                if(height > 0 && cylinderAngle > this.HeadFrom) {
                    headIndices.push(currentIndex - backOfHeadVerticesInARow - 1);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex - backOfHeadVerticesInARow);
                    headIndices.push(currentIndex - backOfHeadVerticesInARow);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishHeight) {
                break;
            }

        } // end for height

    }, // end of function partBackOfHead(...)

    partUpperCurve: function(headVertices, headIndices) {
        var finishCurve = false;
        var currentIndex = this.headIndex;

        var curveAngle;
        for(curveAngle = 0; ; curveAngle += this.curveAngleDelta) {
            if(curveAngle >= this.PIper2) {
                curveAngle = this.PIper2;
                finishCurve = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.PI2) {
                    cylinderAngle = this.PI2;
                    finish = true;
                }

                headVertices.push(Math.cos(cylinderAngle) * (this.rLarge - this.rCurve + this.rCurve * Math.cos(curveAngle)));
                headVertices.push(this.hNeck + this.rCurve + this.hLarge + this.rCurve * Math.sin(curveAngle));
                headVertices.push(Math.sin(cylinderAngle) * (this.rLarge - this.rCurve + this.rCurve * Math.cos(curveAngle)));
                this.headIndex++;

                if(curveAngle > 0 && cylinderAngle > 0) {
                    headIndices.push(currentIndex - this.numberOfVerticesInARow - 1);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishCurve) {
                break;
            }

        } // end for curveAngle

    }, // end of function partUpperCurve(...)

    partBoopCylinder: function(headVertices, headIndices) {
        var finishHeight = false;
        var currentIndex = this.headIndex;

        var height;
        for(height = 0; ; height += this.delta) {
            if(height >= this.hBoop) {
                height = this.hBoop;
                finishHeight = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.PI2) {
                    cylinderAngle = this.PI2;
                    finish = true;
                }

                headVertices.push(Math.cos(cylinderAngle) * this.rBoop);
                headVertices.push(this.hNeck + this.rCurve + this.hLarge + this.rCurve + height);
                headVertices.push(Math.sin(cylinderAngle) * this.rBoop);
                this.headIndex++;

                if(height > 0 && cylinderAngle > 0) {
                    headIndices.push(currentIndex - this.numberOfVerticesInARow - 1);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - this.numberOfVerticesInARow);
                    headIndices.push(currentIndex - 1);
                    headIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishHeight) {
                break;
            }

        } // end for height

    }, // end of function partBoopCylinder(...)

    partTop: function() {
        var topVertices = [];
        var topTexture = [];
        var topIndices = [];
        var topIndex = 0;

        var y = this.hNeck + this.rCurve + this.hLarge + this.rCurve + this.hBoop;
        topVertices.push(0);
        topVertices.push(y);
        topVertices.push(0);

        topTexture.push(0.5);
        topTexture.push(0.5);

        var centerIndex = topIndex;
        topIndex++;

        var firstRow = true;
        var finishRadius = false;
        var currentIndex = topIndex;

        var radius;
        for(radius = this.delta; ; radius += this.delta) {
            if(radius >= this.rBoop) {
                radius = this.rBoop;
                finishRadius = true;
            }

            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = 0; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.PI2) {
                    cylinderAngle = this.PI2;
                    finish = true;
                }

                topVertices.push(Math.cos(cylinderAngle) * radius);
                topVertices.push(y);
                topVertices.push(Math.sin(cylinderAngle) * radius);

                topTexture.push(0.5 - Math.sin(cylinderAngle) * (0.48 * radius / this.rBoop));
                topTexture.push(0.5 - Math.cos(cylinderAngle) * (0.48 * radius / this.rBoop));

                topIndex++;

                if(firstRow && cylinderAngle > 0) {
                    topIndices.push(centerIndex);
                    topIndices.push(currentIndex);
                    topIndices.push(currentIndex-1);
                }

                if(!firstRow && cylinderAngle > 0) {
                    topIndices.push(currentIndex - this.numberOfVerticesInARow -1);
                    topIndices.push(currentIndex - this.numberOfVerticesInARow);
                    topIndices.push(currentIndex - 1);
                    topIndices.push(currentIndex - 1);
                    topIndices.push(currentIndex - this.numberOfVerticesInARow);
                    topIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishRadius) {
                break;
            }

            firstRow = false;
        } // end for curveAngle

        const topGeometry = new THREE.BufferGeometry();
        topGeometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(topVertices), 3));
        topGeometry.setAttribute('uv', new THREE.BufferAttribute(new Float32Array(topTexture), 2));
        topGeometry.setIndex(topIndices);
        topGeometry.computeVertexNormals();
        const topMaterial = new THREE.MeshPhongMaterial( { map: this.textureCrisis, ambient: 0x050505, specular: 0x555555, shininess: 30 });
        this.topMesh = new THREE.Mesh(topGeometry, topMaterial);
        this.topMesh.castShadow = this.castShadow;
        this.topMesh.receiveShadow = this.receiveShadow;
    }, // end of function partTop(...)

    partFace: function() {
        var faceVertices = [];
        var faceTexture = [];
        var faceIndices = [];
        var faceIndex = 0;

        var finishHeight = false;
        var numberOfFaceVerticesInARow = 0;
        var currentIndex = faceIndex;

        var height;
        for(height = 0; ; height += this.delta) {
            if(height >= this.hLarge) {
                height = this.hLarge;
                finishHeight = true;
            }

            var v = (height / this.hLarge);
            var finish = false;

            var cylinderAngle;
            for(cylinderAngle = this.FaceFrom; ; cylinderAngle += this.cylinderAngleDelta) {
                if(cylinderAngle >= this.FaceTo) {
                    cylinderAngle = this.FaceTo;
                    finish = true;
                }

                faceVertices.push(Math.cos(cylinderAngle) * this.rLarge);
                faceVertices.push(this.hNeck + this.rCurve + height);
                faceVertices.push(Math.sin(cylinderAngle) * this.rLarge);
                faceIndex++;

                var u = (cylinderAngle - this.FaceFrom) / this.FaceWidth;
                faceTexture.push(u * 0.94 + 0.03);
                faceTexture.push(v * 0.94 + 0.03);

                if(height <= 0) {
                    numberOfFaceVerticesInARow++;
                }

                if(height > 0 && cylinderAngle > this.FaceFrom) {
                    faceIndices.push(currentIndex - numberOfFaceVerticesInARow - 1);
                    faceIndices.push(currentIndex - 1);
                    faceIndices.push(currentIndex - numberOfFaceVerticesInARow);
                    faceIndices.push(currentIndex - numberOfFaceVerticesInARow);
                    faceIndices.push(currentIndex - 1);
                    faceIndices.push(currentIndex);
                }

                currentIndex++;

                if(finish) {
                    break;
                }

            } // end for cylinderAngle

            if(finishHeight) {
                break;
            }

        } // end for height

        const faceGeometry = new THREE.BufferGeometry();
        faceGeometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(faceVertices), 3));
        faceGeometry.setAttribute('uv', new THREE.BufferAttribute(new Float32Array(faceTexture), 2));
        faceGeometry.setIndex(faceIndices);
        faceGeometry.computeVertexNormals();
        const faceMaterial = new THREE.MeshPhongMaterial( { map: this.textureFaceSmile, ambient: 0x050505, specular: 0x555555, shininess: 30 });
        this.faceMesh = new THREE.Mesh(faceGeometry, faceMaterial);
        this.faceMesh.castShadow = this.castShadow;
        this.faceMesh.receiveShadow = this.receiveShadow;

    }, // end of function partFace(...)

    position: function(x, y, z) {
        this.headMesh.position.set(x, y, z);
        this.faceMesh.position.set(x, y, z);
        this.topMesh.position.set(x, y, z);
    },

    scale: function(x, y, z) {
        this.headMesh.scale.set(x, y, z);
        this.faceMesh.scale.set(x, y, z);
        this.topMesh.scale.set(x, y, z);
    },

    rotate: function(x, y, z) {
        if(x != 0) {
            this.headMesh.rotation.x += x;
            this.faceMesh.rotation.x += x;
            this.topMesh.rotation.x += x;
        }

        if(y != 0) {
            this.headMesh.rotation.y += y;
            this.faceMesh.rotation.y += y;
            this.topMesh.rotation.y += y;
        }

        if(z != 0) {
            this.headMesh.rotation.z += z;
            this.faceMesh.rotation.z += z;
            this.topMesh.rotation.z += z;
        }
    },

    setFace: function (texture) {
        this.faceMesh.material.map = texture;
        this.faceMesh.material.needsUpdate = true;
    },

    addToScene: function(scene) {
        scene.add(this.headMesh);
        scene.add(this.topMesh);
        scene.add(this.faceMesh);
    }
};

//////////////////////////////////////////////////////////////////////////////////////////////


HeadShape.init();
HeadShape.addToScene(scene);

// HeadShape.rotate(0, Math.PI/2, 0);
HeadShape.position(0, -10, 0);

