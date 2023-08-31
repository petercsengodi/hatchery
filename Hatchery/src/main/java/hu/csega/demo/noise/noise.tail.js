
scene.background = new THREE.Color(0x000000);

// update
const update = function(frame, frameMax) {
    let a = frame / frameMax;
    mesh.rotation.y = Math.PI * 2 * a;
    canvasMod.update(canvasObject);
};

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
        // update, render
        update( Math.floor(frame), FRAME_MAX);
        renderer.render(scene, camera);
        // step frame
        frame += FPS_MOVEMENT * secs;
        frame %= FRAME_MAX;
        lt = now;
    }
}

animate();