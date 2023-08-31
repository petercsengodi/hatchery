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

const canvasMod = {};
function setup(api) {
    //-------- ----------
    // built in draw methods
    //-------- ----------
    const DRAW = {};
    // square draw method
    DRAW.square = (canObj, ctx, canvas, state) => {
        ctx.fillStyle = canObj.palette[0]
        ctx.lineWidth = 1;
        ctx.fillRect(0.5, 0.5, canvas.width - 1, canvas.height - 1);
        ctx.strokeStyle = canObj.palette[1]
        ctx.strokeRect(0.5, 0.5, canvas.width - 1, canvas.height - 1);
    };
    // random using palette colors
    DRAW.rnd = (canObj, ctx, canvas, state) => {
        let i = 0;
        const gSize =  state.gSize === undefined ? 5 : state.gSize;
        const len = gSize * gSize;
        const pxSize = canObj.size / gSize;
        while(i < len){
            const ci = Math.floor( canObj.palette.length * Math.random() );
            const x = i % gSize;
            const y = Math.floor(i / gSize);
            ctx.fillStyle = canObj.palette[ci];
            ctx.fillRect(0.5 + x * pxSize, 0.5 + y * pxSize, pxSize, pxSize);
            i += 1;
        }
    };
    //-------- ----------
    // HELEPRS
    //-------- ----------
    // parse draw option helper
    const parseDrawOption = (opt) => {
        // if opt.draw is false for any reason return DRAW.square
        if(!opt.draw){
            return DRAW.square;
        }
        // if a string is given assume it is a key for a built in draw method
        if(typeof opt.draw === 'string'){
            return DRAW[opt.draw];
        }
        // assume we where given a custom function
        return opt.draw;
    };
    //-------- ----------
    // PUBLIC API
    //-------- ----------
    // create and return a canvas texture
    api.create = function (opt) {
        opt = opt || {};
        // create canvas, get context, set size
        const canvas = document.createElement('canvas'),
        ctx = canvas.getContext('2d');
        opt.size = opt.size === undefined ? 16 : opt.size;
        canvas.width = opt.size;
        canvas.height = opt.size;
        // create canvas object
        const canObj = {
            texture: null,
            size: opt.size,
            canvas: canvas, ctx: ctx,
            palette: opt.palette || ['black', 'white'],
            state: opt.state || {},
            draw: parseDrawOption(opt)
        };
        // create texture object
        canObj.texture = new THREE.CanvasTexture(canvas);
        canObj.texture.magFilter = THREE.NearestFilter;
        canObj.texture.minFilter = THREE.NearestFilter;
        api.update(canObj);
        return canObj;
    };
    // update
    api.update = (canObj) => {
        canObj.draw.call(canObj, canObj, canObj.ctx, canObj.canvas, canObj.state);
        canObj.texture.needsUpdate = true;
    };
}

setup(canvasMod);


const opt = {
    size: 64,
    state: {
        rPer: 0.2
    },
    draw: function (canObj, ctx, canvas, state) {
        ctx.fillStyle = canObj.palette[1];
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = canObj.palette[0];
        ctx.beginPath();
        const hw = canvas.width / 2, sx = hw, sy = canvas.height / 2,
        radius = hw - hw * state.rPer;
        ctx.arc(sx, sy, radius, 0, Math.PI * 2);
        ctx.fill();
    }
};

var canvas = document.createElement('canvas'),
ctx = canvas.getContext('2d');
canvas.width = 8;
canvas.height = 8;
ctx.fillStyle = '#000000';
ctx.fillRect(0, 0, canvas.width, canvas.height);
ctx.strokeStyle = '#ff00ff';
ctx.strokeRect(0, 0, canvas.width, canvas.height);

var texture = new THREE.CanvasTexture(canvas);
texture.magFilter = THREE.NearestFilter;
texture.minFilter = THREE.NearestFilter;

// const material = new THREE.MeshBasicMaterial({ map: texture });
// const material = new THREE.MeshBasicMaterial({color: 0xff0000});
// const material = new THREE.MeshLambertMaterial({ emissive: new THREE.Color(0xffffff), emissiveMap: texture });

const canvasObject = canvasMod.create(opt);
const material = new THREE.MeshLambertMaterial({ emissive: new THREE.Color(0xffffff), emissiveMap: canvasObject.texture });

const geo = new THREE.BoxGeometry(10, 10, 10);
const mesh = new THREE.Mesh( geo, material);
scene.add(mesh);

scene.background = new THREE.Color(0x000000);

// update
const update = function(frame, frameMax) {
    let a = frame / frameMax;
    mesh.rotation.y = Math.PI * 2 * a;

    if(canvasObject.state.reduce) {
        canvasObject.state.rPer -= 0.01;
        if(canvasObject.state.rPer <= 0) {
            canvasObject.state.rPer = 0;
            canvasObject.state.reduce = false;
        }
    } else {
        canvasObject.state.rPer += 0.01;
        if(canvasObject.state.rPer >= 1) {
            canvasObject.state.rPer = 1;
            canvasObject.state.reduce = true;
        }
    }

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
