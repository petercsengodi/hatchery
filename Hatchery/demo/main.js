import * as THREE from 'three';

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
camera.position.x = 250;
camera.position.y = 0;
camera.position.z = -50;
camera.lookAt(0, 0, 0);

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

var i, j, k;
var colors = [ 0, 128, 255 ];

var materials = [];
for(i = 0; i < 3; i++) {
    for(j = 0; j < 3; j++) {
        for(k = 0; k < 3; k++) {
            materials.push(new THREE.MeshBasicMaterial({
                color: (colors[i] * 256 * 256 + colors[j] * 256 + colors[k]),
                side: THREE.DoubleSide
            }));
        }
    }
}

for(i = 0; i < 9; i++) {
    materials.push(new THREE.MeshBasicMaterial({
        color: ((25*(i+1)-10) *256 * 256 + (25*(i+1)-10) * 256 + 25*(i+1)-10),
        side: THREE.DoubleSide
    }));
}

var triangles = [];
var triangleAnimations = [];
var numberOfTriangles = 1000;

for(i = 0; i < numberOfTriangles; i++) {
    var geometry = new THREE.BufferGeometry();
    var vertices = new Float32Array([ 1, 0, 0, 0, 1, 0, 0, 0, 1 ]);
    var indices = [ 0, 1, 2 ];
    geometry.setIndex(indices);
    geometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3));
    geometry.computeVertexNormals();
    var mesh = new THREE.Mesh(geometry, materials[2]);
    mesh.matrixAutoUpdate = false;
    triangles.push(mesh);
    scene.add(mesh);

    var animation = {
        source: [0,100,-200,0,100,-200,0,100,-200],
        target: [0,100,-200,0,100,-200,0,100,-200],
        delay: 0,
        t: 0
    };

    triangleAnimations.push(animation);
}

var currentTriangleIndex = 0;
const t1 = new THREE.Vector3(0, 0, 0);
const t2 = new THREE.Vector3(0, 0, 0);
const t3 = new THREE.Vector3(0, 0, 0);

const spotLight = new THREE.SpotLight( 0xffffff );
spotLight.position.set( 10, 10, 10 );
// spotLight.map = new THREE.TextureLoader().load( url );

spotLight.castShadow = true;

spotLight.shadow.mapSize.width = 1024;
spotLight.shadow.mapSize.height = 1024;

spotLight.shadow.camera.near = 500;
spotLight.shadow.camera.far = 4000;
spotLight.shadow.camera.fov = 30;

scene.add( spotLight );

const geometryHat = new THREE.BufferGeometry();

const verticesHat = new Float32Array([
	-60.0, 140.0,  0.0,
	60.0, 140.0,  0.0,
	-120.0, 80.0,  0.0,
	120.0, 80.0,  0.0,
	-120.0, -60.0,  0.0,
	120.0, -60.0,  0.0,
	-40.0, -120.0,  0.0,
	60.0, -120.0,  0.0,
	-40.0, 60.0,  40.0,
	-20.0, 80.0,  40.0,
	20.0, 80.0,  40.0,
	40.0, 60.0,  40.0,
	-40.0, -20.0,  40.0,
	-20.0, -40.0,  40.0,
	20.0, -40.0,  40.0,
	40.0, -20.0,  40.0,
	-20.0, 40.0,  120.0,
	20.0, 40.0,  120.0,
	-20.0, -20.0,  100.0,
	20.0, -20.0,  100.0,
	60.0, -30.0,  160.0,
	0.0, 20.0,  40.0,
]);

const indicesHat = [
	0, 2, 8,
	8, 9, 0,
	9, 10, 0,
	10, 1, 0,
	10, 11, 1,
	11, 3, 1,
	11, 15, 3,
	15, 5, 3,
	15, 14, 5,
	14, 7, 5,
	14, 13, 7,
	13, 6, 7,
	13, 12, 6,
	12, 4, 6,
	12, 8, 4,
	8, 2, 4,
	8, 16, 9,
	16, 10, 9,
	16, 17, 10,
	17, 11, 10,
	17, 19, 11,
	19, 15, 11,
	19, 14, 15,
	19, 13, 14,
	19, 18, 13,
	18, 12, 13,
	18, 16, 12,
	16, 8, 12,
	16, 20, 17,
	17, 20, 19,
	20, 18, 19,
	20, 16, 18,
	21, 0, 1,
	21, 2, 0,
	21, 4, 2,
	21, 6, 4,
	21, 7, 6,
	21, 5, 7,
	21, 3, 5,
	21, 1, 3,
];

const colorsHat = [
	20,
	17,
	18,
	18,
	11,
	17,
	17,
	24,
	16,
	16,
	10,
	11,
	10,
	11,
	11,
	15,
	12,
	13,
	19,
	14,
	19,
	15,
	16,
	12,
	14,
	14,
	17,
	17,
	19,
	22,
	19,
	16,
	24,
	26,
	21,
	15,
	18,
	23,
	27,
	22,
];


// START custom code

var ind;
for(var i = 0; i < indicesHat.length / 3; i++) {
    var ind1 = indicesHat[i * 3 + 0];
    var ind2 = indicesHat[i * 3 + 1];
    var ind3 = indicesHat[i * 3 + 2];

    var ta = triangleAnimations[i];

    ta.target[0] = verticesHat[ind1 * 3 + 0];
    ta.target[1] = verticesHat[ind1 * 3 + 1];
    ta.target[2] = verticesHat[ind1 * 3 + 2];

    ta.target[3] = verticesHat[ind2 * 3 + 0];
    ta.target[4] = verticesHat[ind2 * 3 + 1];
    ta.target[5] = verticesHat[ind2 * 3 + 2];

    ta.target[6] = verticesHat[ind3 * 3 + 0];
    ta.target[7] = verticesHat[ind3 * 3 + 1];
    ta.target[8] = verticesHat[ind3 * 3 + 2];

    ta.t = 0;
    ta.delay = i*2;

    triangles[i].material = materials[colorsHat[i]];
}

// END custom code

var sceneIndex = 0;
var partIndex = 0;

function animate() {
	requestAnimationFrame(animate);

    sceneIndex++;
    // if(sceneIndex >= numberOfScenesRun) {
        // sceneIndex = 0;
    // }

    for(var i = 0; i < numberOfTriangles; i++) {
      var a = triangleAnimations[i];
      if(a.delay <= 0) {
        a.delay = 0;
        if(a.t < 1) {
            a.t += 0.01;
        }

        if(a.t > 1) {
            a.t = 1;
        }

        if(a.t <= 0) {
            t1.set(a.source[0], a.source[1], a.source[2]);
            t2.set(a.source[3], a.source[4], a.source[5]);
            t3.set(a.source[6], a.source[7], a.source[8]);
        } else if(a.t >= 1) {
            t1.set(a.target[0], a.target[1], a.target[2]);
            t2.set(a.target[3], a.target[4], a.target[5]);
            t3.set(a.target[6], a.target[7], a.target[8]);
        } else {
            t1.set(a.source[0] * (1-a.t) + a.target[0] * a.t, a.source[1] * (1-a.t) + a.target[1] * a.t, a.source[2] * (1-a.t) + a.target[2] * a.t);
            t2.set(a.source[3] * (1-a.t) + a.target[3] * a.t, a.source[4] * (1-a.t) + a.target[4] * a.t, a.source[5] * (1-a.t) + a.target[5] * a.t);
            t3.set(a.source[6] * (1-a.t) + a.target[6] * a.t, a.source[7] * (1-a.t) + a.target[7] * a.t, a.source[8] * (1-a.t) + a.target[8] * a.t);
        }

        triangles[i].matrix.makeBasis(t1, t2, t3);
      } else {
        a.delay -= 0.1;
      }
    }

	renderer.render(scene, camera);
}

animate();
