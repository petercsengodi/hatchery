import * as THREE from 'three';

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
camera.position.x = 25;
camera.position.y = 10;
camera.position.z = -15;
camera.lookAt(0, 0, 0);

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);


//////////////////////////////////////////////////////////////////////////////////////////////

function Gradient(x, y, z) {
  this.x = x; this.y = y; this.z = z;
}

Gradient.prototype.dot = function(x, y, z) {
  return this.x*x + this.y*y + this.z*z;
};


var PerlinNoise = {

    gradients: [new Gradient(1,1,0),new Gradient(-1,1,0),new Gradient(1,-1,0),new Gradient(-1,-1,0),
                new Gradient(1,0,1),new Gradient(-1,0,1),new Gradient(1,0,-1),new Gradient(-1,0,-1),
                new Gradient(0,1,1),new Gradient(0,-1,1),new Gradient(0,1,-1),new Gradient(0,-1,-1)],

    p: [151,160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
        190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,88,237,149,56,87,174,
        20,125,136,171,168,68,175,74,165,71,134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,
        230,220,105,92,41,55,46,245,40,244,102,143,54,65,25,63,161,1,216,80,73,209,76,132,187,208, 89,18,
        169,200,196,135,130,116,188,159,86,164,100,109,198,173,186,3,64,52,217,226,250,124,123,5,202,38,
        147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,223,183,170,213,119,248,152,2,
        44,154,163,70,221,153,101,155,167,43,172,9,129,22,39,253,19,98,108,110,79,113,224,232,178,185,
        112,104,218,246,97,228,251,34,242,193,238,210,144,12,191,179,162,241,81,51,145,235,249,14,239,
        107,49,192,214,31,181,199,106,157,184,84,204,176,115,121,50,45,127,4,150,254,138,236,205,93,222,
        114,67,29,24,72,243,141,128,195,78,66,215,61,156,180],

    perm: new Array(512),
    gradP: new Array(512),

    fade: function (t) {
              var result = t*t*t*(t*(t*6-15)+10);
              // if(result < 0.4) { result = 0; }
              return result;
          },

    lerp: function (a, b, t) {
              return (1-t)*a + t*b;
          },

    seed: function(seed) {
        if(seed > 0 && seed < 1) {
            // Scale the seed out
            seed *= 65536;
        }

        seed = Math.floor(seed);
        if(seed < 256) {
            seed |= seed << 8;
        }

        for(var i = 0; i < 256; i++) {
            var v;
            if (i & 1) {
                v = this.p[i] ^ (seed & 255);
            } else {
                v = this.p[i] ^ ((seed>>8) & 255);
            }

            this.perm[i] = this.perm[i + 256] = v;
            this.gradP[i] = this.gradP[i + 256] = this.gradients[v % 12];
        }
    },

    perlin: function(x, y, z) {
        // Find unit grid cell containing point
        var X = Math.floor(x), Y = Math.floor(y), Z = Math.floor(z);
        // Get relative xyz coordinates of point within that cell
        x = x - X; y = y - Y; z = z - Z;
        // Wrap the integer cells at 255 (smaller integer period can be introduced here)
        X = X & 255; Y = Y & 255; Z = Z & 255;

        // Calculate noise contributions from each of the eight corners
        var n000 = this.gradP[X+  this.perm[Y+  this.perm[Z  ]]].dot(x,   y,     z);
        var n001 = this.gradP[X+  this.perm[Y+  this.perm[Z+1]]].dot(x,   y,   z-1);
        var n010 = this.gradP[X+  this.perm[Y+1+this.perm[Z  ]]].dot(x,   y-1,   z);
        var n011 = this.gradP[X+  this.perm[Y+1+this.perm[Z+1]]].dot(x,   y-1, z-1);
        var n100 = this.gradP[X+1+this.perm[Y+  this.perm[Z  ]]].dot(x-1,   y,   z);
        var n101 = this.gradP[X+1+this.perm[Y+  this.perm[Z+1]]].dot(x-1,   y, z-1);
        var n110 = this.gradP[X+1+this.perm[Y+1+this.perm[Z  ]]].dot(x-1, y-1,   z);
        var n111 = this.gradP[X+1+this.perm[Y+1+this.perm[Z+1]]].dot(x-1, y-1, z-1);

        // Compute the fade curve value for x, y, z
        var u = this.fade(x);
        var v = this.fade(y);
        var w = this.fade(z);

        // Interpolate
        return this.lerp(
            this.lerp(
                this.lerp(n000, n100, u),
                this.lerp(n001, n101, u), w),
                this.lerp(
                    this.lerp(n010, n110, u),
                    this.lerp(n011, n111, u), w),
                v);
    }

};

PerlinNoise.seed(Math.random());
const PI2 = Math.PI * 2;
const FF = PI2 / 8;
var angleFF = 0;

const getAngle = () => {
	const angle = (PI2 * Math.random());
	return angleFF = angle - (angle % FF);
};

let phase = 0;
let trails = [];
const trailWidth = 10;

const createTrails = (state) => {
	let num = 400;

	while (num--) {
		trails.push({
			dead: false,
			x: state.widthHalf,
			y: state.heightHalf,
			width: trailWidth,
			vel: 1 + (2 * Math.random()),
			angle: getAngle(),
		});
	}
};

const resetTrails = (state) => {
    state.clearCanvas(); // ???
	trails = [];
	createTrails(state);
};

const updateTrail = (trail, state) => {
	const shouldChange = Math.random() > 0.97;
	const incAngle = Math.random() > 0.5;

	trail.dead = trail.x < 0 || trail.x > state.width || trail.y < 0 || trail.y > state.height || trail.width < 0.2;

	if (shouldChange && incAngle) {
		trail.angle += FF;
	} else if (shouldChange) {
		trail.angle -= FF;
	}

	trail.width *= 0.98;
}

const renderTrail = (trail, ctx, phase) => {
	let { x, y, length, vel, angle } = trail;

	const scale = 0.0009;
	const n = PerlinNoise.perlin(x * scale, y * scale, phase);
	const h = 180 * n;

	ctx.beginPath();
	ctx.lineWidth = trail.width;
	ctx.strokeStyle = `hsl(${h}, 100%, 50%)`;

	ctx.moveTo(trail.x, trail.y);

	trail.x += Math.cos(angle) * vel;
	trail.y += Math.sin(angle) * vel;

	ctx.lineTo(trail.x, trail.y);
	ctx.stroke();
	ctx.closePath();
}

function drawTrails(ctx, state) {
	state.fadeCanvas();

	trails.forEach((t) => {
		updateTrail(t, state);
		renderTrail(t, ctx, phase);
	});

	trails = trails.filter(t => !t.dead);

	if (!trails.length) {
		resetTrails(state);
	}

	phase += 0.004;
}

//////////////////////////////////////////////////////////////////////////////////////////////

const NoiseGenerator = {
    canvas: null,
    ctx: null,
    texture: null,

    size: 512,
    width: 512,
    height: 512,
    widthHalf: 256,
    heightHalf: 256,

    rPer: 0.2,

    init: function() {
        this.canvas = document.createElement('canvas');
        this.canvas.width = this.width;
        this.canvas.height = this.height;
        this.ctx = this.canvas.getContext('2d');

        this.ctx.fillStyle = '#000000';
        this.ctx.fillRect(0, 0, this.width, this.height);
        this.ctx.strokeStyle = '#ff00ff';
        this.ctx.strokeRect(0, 0, this.width, this.height);

        resetTrails(this); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        this.texture = new THREE.CanvasTexture(this.canvas);
        this.texture.magFilter = THREE.NearestFilter;
        this.texture.minFilter = THREE.NearestFilter;

        this.update();
    },

    update: function() {
        drawTrails(this.ctx, this);
        this.texture.needsUpdate = true;
    },

    clearCanvas: function() {
        this.ctx.fillStyle = '#000000';
        this.ctx.fillRect(0, 0, this.width, this.height);
    },

    fadeCanvas: function() {
    	this.ctx.fillStyle = 'rgba(0, 0, 0, 0.17)';
    	this.ctx.fillRect(0, 0, this.width, this.height);
    }

};

NoiseGenerator.init();

const perlinNoiseMaterial = new THREE.MeshLambertMaterial({ emissive: new THREE.Color(0xffffff), emissiveMap: NoiseGenerator.texture });
const perlinNoiseGeometry = new THREE.BoxGeometry(20, 20, 20);
const perlinNoiseMesh = new THREE.Mesh(perlinNoiseGeometry, perlinNoiseMaterial);
scene.add(perlinNoiseMesh);

scene.background = new THREE.Color(0x000000);

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
        perlinNoiseMesh.rotation.y = Math.PI * 2 * a;
        NoiseGenerator.update();

        renderer.render(scene, camera);

        // step frame
        frame += FPS_MOVEMENT * secs;
        frame %= FRAME_MAX;
        lt = now;
    }
}

animate();
