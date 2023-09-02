

// PUTTING THE SCENE TOGETHER //////////////////////////////////////////////////////////////////////////////////////////

const scene = new THREE.Scene();
scene.background = new THREE.Color(0x000000);

const camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
camera.position.x = 25;
camera.position.y = 10;
camera.position.z = -15;
camera.lookAt(0, 0, 0);

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

// Creating a mesh with the generated texture: (TextureGenerator.texture)
const perlinNoiseMaterial = new THREE.MeshLambertMaterial({ emissive: new THREE.Color(0xffffff), emissiveMap: TextureGenerator.texture });
const perlinNoiseGeometry = new THREE.BoxGeometry(20, 20, 20);
const perlinNoiseMesh = new THREE.Mesh(perlinNoiseGeometry, perlinNoiseMaterial);
scene.add(perlinNoiseMesh);

// We may set if we want to clear the texture to full black between trail line generations:
LineTrails.fullyClearCanvasBetweenTrailGenerations = true;

// Just to make sure everything is nicely clean:
TextureGenerator.clearCanvas();

const FPS_UPDATE = 60, // fps rate to update ( low fps for low CPU use, but choppy video )
FPS_MOVEMENT = 60;     // fps rate to move object by that is independent of frame update rate
const FRAME_MAX = 600;
let secs = 0,
    frame = 0,
    lt = new Date();

function animate() {
	const now = new Date(),
    secs = (now - lt) / 1000;
    requestAnimationFrame(animate);
    if(secs > 1 / FPS_UPDATE) {
        let a = Math.floor(frame) / FRAME_MAX;


        // Moving the cube to check out from different angles.
        perlinNoiseMesh.rotation.y = Math.PI * 2 * a;


        // Animate the texture on the cube.
        TextureGenerator.update();


        renderer.render(scene, camera);
        frame += FPS_MOVEMENT * secs;
        frame %= FRAME_MAX;
        lt = now;
    }
}

animate();