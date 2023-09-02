import * as THREE from 'three';

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
camera.position.x = 30;
camera.position.y = 12;
camera.position.z = -17;
camera.lookAt(0, 0, 0);

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);


//////////////////////////////////////////////////////////////////////////////////////////////

const textureCrisis = new THREE.TextureLoader().load( "crisis.png" );
const textureFaceSmile = new THREE.TextureLoader().load( "face-smile.png" );
const textureFaceSad = new THREE.TextureLoader().load( "face-sad.png" );

var r1 = 10;
var r2 = 1;

var rLarge = 10;
var hLarge = 8;

var rNeck = 6.1;
var hNeck = 3;

var rBoop = 6;
var hBoop = 3;

var rCurve = (rLarge - rBoop);

var delta = 0.3;
var cylinderAngleDelta = delta / rLarge;
var curveAngleDelta = delta / rCurve;

var PI2 = 2 * Math.PI;
var PIper2 = Math.PI / 2;
var FaceFrom = - Math.PI / 4;
var FaceTo = Math.PI / 4;
var FaceWidth = FaceTo - FaceFrom;
var HeadFrom = Math.PI / 4;
var HeadTo = PI2 - Math.PI / 4;

// -----------------------------------------------------------

var headVerticesRaw = [];
var headIndices = [];
var headIndex = 0;

function partNeckCylinder() {
  var finishHeight = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = headIndex;

  var height;
  for(height = 0; ; height += delta) {

      if(height >= hNeck) {
        height = hNeck;
        finishHeight = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = 0; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= PI2) {
          cylinderAngle = PI2;
          finish = true;
        }

        headVerticesRaw.push(Math.cos(cylinderAngle) * rNeck);
        headVerticesRaw.push(height);
        headVerticesRaw.push(Math.sin(cylinderAngle) * rNeck);
        headIndex++;

        if(height <= 0) { numberOfIndicesInARow++; }
        if(height > 0 && cylinderAngle > 0) {
            headIndices.push(currentIndex - numberOfIndicesInARow - 1);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - numberOfIndicesInARow);
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

      rowIndex++;
  } // end for height

}

function partLowerCurve() {
  var finishCurve = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = headIndex;

  var curveAngle;
  for(curveAngle = 0; ; curveAngle += curveAngleDelta) {

      if(curveAngle >= PIper2) {
        curveAngle = PIper2;
        finishCurve = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = 0; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= PI2) {
          cylinderAngle = PI2;
          finish = true;
        }

        headVerticesRaw.push(Math.cos(cylinderAngle) * (rLarge - rCurve * (1- Math.cos(curveAngle))));
        headVerticesRaw.push(hNeck + rCurve * (1 - Math.sin(curveAngle)));
        headVerticesRaw.push(Math.sin(cylinderAngle) * (rLarge - rCurve * (1- Math.cos(curveAngle))));
        headIndex++;

        if(curveAngle <= 0) { numberOfIndicesInARow++; }
        if(curveAngle > 0 && cylinderAngle > 0) {
            headIndices.push(currentIndex - numberOfIndicesInARow - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
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

      rowIndex++;
  } // end for curveAngle

}

function partLargeCylinder() {
  var finishHeight = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = headIndex;

  var height;
  for(height = 0; ; height += delta) {

      if(height >= hLarge) {
        height = hLarge;
        finishHeight = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = HeadFrom; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= HeadTo) {
          cylinderAngle = HeadTo;
          finish = true;
        }

        headVerticesRaw.push(Math.cos(cylinderAngle) * rLarge);
        headVerticesRaw.push(hNeck + rCurve + height);
        headVerticesRaw.push(Math.sin(cylinderAngle) * rLarge);
        headIndex++;

        if(height <= 0) { numberOfIndicesInARow++; }
        if(height > 0 && cylinderAngle > HeadFrom) {
            headIndices.push(currentIndex - numberOfIndicesInARow - 1);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - numberOfIndicesInARow);
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

      rowIndex++;
  } // end for height

}

function partUpperCurve() {
  var finishCurve = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = headIndex;

  var curveAngle;
  for(curveAngle = 0; ; curveAngle += curveAngleDelta) {

      if(curveAngle >= PIper2) {
        curveAngle = PIper2;
        finishCurve = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = 0; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= PI2) {
          cylinderAngle = PI2;
          finish = true;
        }

        headVerticesRaw.push(Math.cos(cylinderAngle) * (rLarge - rCurve + rCurve * Math.cos(curveAngle)));
        headVerticesRaw.push(hNeck + rCurve + hLarge + rCurve * Math.sin(curveAngle));
        headVerticesRaw.push(Math.sin(cylinderAngle) * (rLarge - rCurve + rCurve * Math.cos(curveAngle)));
        headIndex++;

        if(curveAngle <= 0) { numberOfIndicesInARow++; }
        if(curveAngle > 0 && cylinderAngle > 0) {
            headIndices.push(currentIndex - numberOfIndicesInARow - 1);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - numberOfIndicesInARow);
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

      rowIndex++;
  } // end for curveAngle

}

function partBoopCylinder() {
  var finishHeight = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = headIndex;

  var height;
  for(height = 0; ; height += delta) {

      if(height >= hBoop) {
        height = hBoop;
        finishHeight = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = 0; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= PI2) {
          cylinderAngle = PI2;
          finish = true;
        }

        headVerticesRaw.push(Math.cos(cylinderAngle) * rBoop);
        headVerticesRaw.push(hNeck + rCurve + hLarge + rCurve + height);
        headVerticesRaw.push(Math.sin(cylinderAngle) * rBoop);
        headIndex++;

        if(height <= 0) { numberOfIndicesInARow++; }
        if(height > 0 && cylinderAngle > 0) {
            headIndices.push(currentIndex - numberOfIndicesInARow - 1);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - numberOfIndicesInARow);
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

      rowIndex++;
  } // end for height

}

function partBottom() {
  var y = delta; // Should be zero.
  headVerticesRaw.push(0);
  headVerticesRaw.push(y);
  headVerticesRaw.push(0);

  var centerIndex = headIndex;
  headIndex++;

  var firstRow = true;
  var finishRadius = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = headIndex;

  var radius;
  for(radius = delta; ; radius += delta) {

      if(radius >= rBoop) {
        radius = rBoop;
        finishRadius = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = 0; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= PI2) {
          cylinderAngle = PI2;
          finish = true;
        }

        headVerticesRaw.push(Math.cos(cylinderAngle) * radius);
        headVerticesRaw.push(y);
        headVerticesRaw.push(Math.sin(cylinderAngle) * radius);

        headIndex++;

        if(firstRow) {
          numberOfIndicesInARow++;
          if(cylinderAngle > 0) {
            headIndices.push(centerIndex);
            headIndices.push(currentIndex-1);
            headIndices.push(currentIndex);
          }
        }

        if(!firstRow && cylinderAngle > 0) {
            headIndices.push(currentIndex - numberOfIndicesInARow -1);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - numberOfIndicesInARow);
            headIndices.push(currentIndex - 1);
            headIndices.push(currentIndex);
        }

        ++currentIndex;

        if(finish) {
          break;
        }

      } // end for cylinderAngle

      if(finishRadius) {
        break;
      }

      firstRow = false;
      rowIndex++;
  } // end for curveAngle

}

partNeckCylinder();
partLowerCurve();
partLargeCylinder();
partUpperCurve();
partBoopCylinder();
partBottom();

const headGeometry = new THREE.BufferGeometry();
const headVertices = new Float32Array(headVerticesRaw);
headGeometry.setIndex(headIndices);
headGeometry.setAttribute('position', new THREE.BufferAttribute(headVertices, 3));
headGeometry.computeVertexNormals();
const headMaterial = new THREE.MeshPhongMaterial( { map: textureCrisis, ambient: 0x050505, specular: 0x555555, shininess: 30 });
const headMesh = new THREE.Mesh(headGeometry, headMaterial);
headMesh.castShadow = true;
scene.add(headMesh);

// ---------------------------------------------

var faceVerticesRaw = [];
var faceTextureRaw = [];
var faceIndices = [];
var faceIndex = 0;

function partFace() {
  var finishHeight = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = faceIndex;

  var height;
  for(height = 0; ; height += delta) {

      if(height >= hLarge) {
        height = hLarge;
        finishHeight = true;
      }

      var v = (height / hLarge);
      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = FaceFrom; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= FaceTo) {
          cylinderAngle = FaceTo;
          finish = true;
        }

        faceVerticesRaw.push(Math.cos(cylinderAngle) * rLarge);
        faceVerticesRaw.push(hNeck + rCurve + height);
        faceVerticesRaw.push(Math.sin(cylinderAngle) * rLarge);
        faceIndex++;

        var u = (cylinderAngle - FaceFrom) / FaceWidth;
        faceTextureRaw.push(u);
        faceTextureRaw.push(v);

        if(height <= 0) { numberOfIndicesInARow++; }
        if(height > 0 && cylinderAngle > FaceFrom) {
            faceIndices.push(currentIndex - numberOfIndicesInARow - 1);
            faceIndices.push(currentIndex - 1);
            faceIndices.push(currentIndex - numberOfIndicesInARow);
            faceIndices.push(currentIndex - numberOfIndicesInARow);
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

      rowIndex++;
  } // end for height

}

partFace();

const faceGeometry = new THREE.BufferGeometry();
const faceVertices = new Float32Array(faceVerticesRaw);
faceGeometry.setIndex(faceIndices);
faceGeometry.setAttribute('position', new THREE.BufferAttribute(faceVertices, 3));
faceGeometry.setAttribute('uv', new THREE.BufferAttribute(new Float32Array(faceTextureRaw), 2));
faceGeometry.computeVertexNormals();
const faceMaterial = new THREE.MeshPhongMaterial( { map: textureFaceSmile, ambient: 0x050505, specular: 0x555555, shininess: 30 });
const faceMesh = new THREE.Mesh(faceGeometry, faceMaterial);
faceMesh.castShadow = true;
scene.add(faceMesh);

// ----------------------------------------------

var topVerticesRaw = [];
var topTextureRaw = [];
var topIndices = [];
var topIndex = 0;

function partTop() {
  var y = hNeck + rCurve + hLarge + rCurve + hBoop;
  topVerticesRaw.push(0);
  topVerticesRaw.push(y);
  topVerticesRaw.push(0);

  topTextureRaw.push(0.5);
  topTextureRaw.push(0.5);

  var centerIndex = topIndex;
  topIndex++;

  var firstRow = true;
  var finishRadius = false;
  var numberOfIndicesInARow = 0;
  var rowIndex = 0;
  var currentIndex = topIndex;

  var radius;
  for(radius = delta; ; radius += delta) {

      if(radius >= rBoop) {
        radius = rBoop;
        finishRadius = true;
      }

      var finish = false;

      var cylinderAngle;
      for(cylinderAngle = 0; ; cylinderAngle += cylinderAngleDelta) {
        if(cylinderAngle >= PI2) {
          cylinderAngle = PI2;
          finish = true;
        }

        topVerticesRaw.push(Math.cos(cylinderAngle) * radius);
        topVerticesRaw.push(y);
        topVerticesRaw.push(Math.sin(cylinderAngle) * radius);

        topTextureRaw.push(0.5 - Math.sin(cylinderAngle) * (0.5 * radius / rBoop));
        topTextureRaw.push(0.5 - Math.cos(cylinderAngle) * (0.5 * radius / rBoop));

        topIndex++;

        if(firstRow) {
          numberOfIndicesInARow++;
          if(cylinderAngle > 0) {
            topIndices.push(centerIndex);
            topIndices.push(currentIndex);
            topIndices.push(currentIndex-1);
          }
        }

        if(!firstRow && cylinderAngle > 0) {
            topIndices.push(currentIndex - numberOfIndicesInARow -1);
            topIndices.push(currentIndex - numberOfIndicesInARow);
            topIndices.push(currentIndex - 1);
            topIndices.push(currentIndex - 1);
            topIndices.push(currentIndex - numberOfIndicesInARow);
            topIndices.push(currentIndex);
        }

        ++currentIndex;

        if(finish) {
          break;
        }

      } // end for cylinderAngle

      if(finishRadius) {
        break;
      }

      firstRow = false;
      rowIndex++;
  } // end for curveAngle

}

partTop();

const topGeometry = new THREE.BufferGeometry();
topGeometry.setIndex(topIndices);
topGeometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(topVerticesRaw), 3));
topGeometry.setAttribute('uv', new THREE.BufferAttribute(new Float32Array(topTextureRaw), 2));
topGeometry.computeVertexNormals();
const topMaterial = new THREE.MeshPhongMaterial( { map: textureCrisis, ambient: 0x050505, specular: 0x555555, shininess: 30 });
const topMesh = new THREE.Mesh(topGeometry, topMaterial);
topMesh.castShadow = true;
scene.add(topMesh);

//////////////////////////////////////////////////////////////////////////////////////////////

