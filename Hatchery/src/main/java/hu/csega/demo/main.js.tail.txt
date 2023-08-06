
var sceneIndex = 0;
var partIndex = 0;

function animate() {
	requestAnimationFrame(animate);

    sceneIndex++;
    if(sceneIndex >= numberOfScenesRun) {
        sceneIndex = 0;
    }

    for(partIndex = 0; partIndex < parts.length; partIndex++) {
        parts[partIndex].matrix.copy(animationRun[sceneIndex][partIndex]);
    }

	renderer.render(scene, camera);
}

animate();