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

const spotLight = new THREE.SpotLight( 0xff0000 );
spotLight.position.set( 10, 10, 10 );
// spotLight.map = new THREE.TextureLoader().load( url );

spotLight.castShadow = true;

spotLight.shadow.mapSize.width = 1024;
spotLight.shadow.mapSize.height = 1024;

spotLight.shadow.camera.near = 500;
spotLight.shadow.camera.far = 4000;
spotLight.shadow.camera.fov = 30;

scene.add( spotLight );

