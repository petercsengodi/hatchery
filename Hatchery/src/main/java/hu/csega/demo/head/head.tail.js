
scene.background = new THREE.Color(0x000000);
renderer.shadowMapEnabled = true
renderer.shadowMapType = THREE.PCFSoftShadowMap;

const ambientLight = new THREE.AmbientLight(0xffffff, 0.15);
scene.add(ambientLight);

const light = new THREE.DirectionalLight();
light.position.set(250, 200, 200);
light.castShadow = true;
light.shadow.mapSize.width = 512;
light.shadow.mapSize.height = 512;
light.shadow.camera.near = 0.5;
light.shadow.camera.far = 10000;
scene.add(light);

const lightColor = { color: light.color.getHex() };
light.color.set(lightColor.color);

const planeGeometry = new THREE.PlaneGeometry(1000, 1000);
const plane = new THREE.Mesh(planeGeometry, new THREE.MeshPhongMaterial({ color: 0xffffff }));
plane.rotateX(-Math.PI / 2);
plane.position.y = -20;
plane.receiveShadow = true;
scene.add(plane);

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