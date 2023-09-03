
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

    counter++;
    if(counter <= 40) {
        HeadShape.rotate(0, 0, -0.005);
    } else if(counter <= 80) {
        HeadShape.rotate(0, 0, +0.005);
    } else {
      // change face texture
      textureIndex++;

      if(textureIndex >= 3) {
        HeadShape.setFace(HeadShape.textureFaceSmile);
        textureIndex = 0;
      } else if(textureIndex == 1) {
        HeadShape.setFace(HeadShape.textureFaceSad);
      } else {
        HeadShape.setFace(HeadShape.textureFaceTired);
      }

      counter = 0;
    }

    renderer.render(scene, camera);
}

animate();