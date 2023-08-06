
// START custom code

var ind;
for(var i = 0; i < indicesHat.length / 3; i++) {
    var ind1 = indicesHat[i * 3 + 0];
    var ind2 = indicesHat[i * 3 + 1];
    var ind3 = indicesHat[i * 3 + 2];

    var ta = triangleAnimations[usedTriangleIndex];

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

    triangles[usedTriangleIndex].material = materials[colorsHat[i]];

    usedTriangleIndex++;
    if(usedTriangleIndex > numberOfTriangles) {
        usedTriangleIndex -= numberOfTriangles;
    }
}

var dragonPositionMatrix = new THREE.Matrix4();
var initiated = false;

function updateAnimation(timeSinceAnimationStart) {
    var sceneIndex = Math.floor(timeSinceAnimationStart / 15) % numberOfScenesDragon;

    /* FIXME LERP?
    var prevSceneIndex = sceneIndex - 1;
    if(prevSceneIndex < 0) {
        prevSceneIndex += numberOfScenesDragon;
    } */

    // Position of each triangle: Mesh position * Animation matrix * Position matrix
    var triangleIndex = usedTriangleIndex;
    var counter = 0;

    for(var j = 0; j < parts.length; j++) {
        var verticesDragon = verticesArray[j];
        var indicesDragon = indicesArray[j];
        var dragonAnimationMatrix = animationDragon[sceneIndex][j];

        for(var i = 0; i < indicesDragon.length / 3; i++) {
            var ind1 = indicesDragon[i * 3 + 0];
            var ind2 = indicesDragon[i * 3 + 1];
            var ind3 = indicesDragon[i * 3 + 2];

            var ta = triangleAnimations[triangleIndex];

            var v1 = new THREE.Vector4(verticesDragon[ind1 * 3 + 0], verticesDragon[ind1 * 3 + 1], verticesDragon[ind1 * 3 + 2], 1);
            v1.applyMatrix4(dragonAnimationMatrix);
            v1.applyMatrix4(dragonPositionMatrix);

            ta.target[0] = v1.x / v1.w;
            ta.target[1] = v1.y / v1.w;
            ta.target[2] = v1.z / v1.w;

            var v2 = new THREE.Vector4(verticesDragon[ind2 * 3 + 0], verticesDragon[ind2 * 3 + 1], verticesDragon[ind2 * 3 + 2], 1);
            v2.applyMatrix4(dragonAnimationMatrix);
            v2.applyMatrix4(dragonPositionMatrix);

            ta.target[3] = v2.x / v2.w;
            ta.target[4] = v2.y / v2.w;
            ta.target[5] = v2.z / v2.w;

            var v3 = new THREE.Vector4(verticesDragon[ind3 * 3 + 0], verticesDragon[ind3 * 3 + 1], verticesDragon[ind3 * 3 + 2], 1);
            v3.applyMatrix4(dragonAnimationMatrix);
            v3.applyMatrix4(dragonPositionMatrix);

            ta.target[6] = v3.x / v3.w;
            ta.target[7] = v3.y / v3.w;
            ta.target[8] = v3.z / v3.w;

            if(!initiated) {
                ta.t = 0;
                ta.delay = counter / 10;
            }

            triangles[triangleIndex].material = materials[colorsHat[i]];

            counter++;
            triangleIndex++;
            if(triangleIndex >= numberOfTriangles) {
                triangleIndex -= numberOfTriangles;
            }
        }
    }

    if(!initiated) {
        initiated = true;
    }
}


// END custom code

scene.background = new THREE.Color(0xf0f0f0);

var sceneIndex = 0;
var partIndex = 0;
var px = 0;
var py = 0;
var pz = 0;
var lx = 0;
var ly = 0;
var lz = 0;

function animate() {
	requestAnimationFrame(animate);

	var currentTime = Date.now() - startTime;
	if(cameraMovement.length >= 2 && currentTime >= cameraMovement[1].t) {
        cameraMovement.shift();
	}

	if(cameraMovement.length >= 2) {
	    var betweenTime = currentTime - cameraMovement[0].t;
	    var t = (currentTime - cameraMovement[0].t) / (cameraMovement[1].t - cameraMovement[0].t);

        px = (cameraMovement[1].position.x - cameraMovement[0].position.x) * t + cameraMovement[0].position.x;
        py = (cameraMovement[1].position.y - cameraMovement[0].position.y) * t + cameraMovement[0].position.y;
        pz = (cameraMovement[1].position.z - cameraMovement[0].position.z) * t + cameraMovement[0].position.z;

        lx = (cameraMovement[1].lookAt.x - cameraMovement[0].lookAt.x) * t + cameraMovement[0].lookAt.x;
        ly = (cameraMovement[1].lookAt.y - cameraMovement[0].lookAt.y) * t + cameraMovement[0].lookAt.y;
        lz = (cameraMovement[1].lookAt.z - cameraMovement[0].lookAt.z) * t + cameraMovement[0].lookAt.z;

	    camera.position.x = px;
        camera.position.y = py;
        camera.position.z = pz;
        camera.lookAt(lx, ly, lz);

	}

    var animationStart = 22500;
    if(currentTime > animationStart) {
        updateAnimation(currentTime - animationStart);
    }

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