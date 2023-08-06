
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

scene.background = new THREE.Color(0xf0f0f0);

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