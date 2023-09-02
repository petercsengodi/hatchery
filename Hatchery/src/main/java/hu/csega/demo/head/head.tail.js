
scene.background = new THREE.Color(0x404040);

var counter = 0;
var textureIndex = 0;

function animate() {
    requestAnimationFrame(animate);

    headMesh.rotation.z += 0.01;
    faceMesh.rotation.z += 0.01;
    topMesh.rotation.z += 0.01;

    counter++;
    if(counter > 20) {
      // change face texture
      textureIndex = 1 - textureIndex;

      if(textureIndex == 0) {
        faceMesh.material.map = textureFaceSmile;
      } else {
        faceMesh.material.map = textureFaceSad;
      }

      faceMesh.material.needsUpdate = true;
      counter = 0;
    }

    renderer.render(scene, camera);
}

animate();