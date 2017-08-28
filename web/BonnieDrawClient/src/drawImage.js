// draw dot text function
var context = document.querySelector("canvas").getContext("2d");
var dots = [
  // { x: 100, y: 100, radius: 10},
  // { x: 105, y: 100, radius: 10},
  // { x: 110, y: 100, radius: 10},
  // { x: 115, y: 100, radius: 10},
  // { x: 120, y: 100, radius: 10},
  // { x: 125, y: 100, radius: 10},
  // { x: 130, y: 100, radius: 10},
  // { x: 135, y: 100, radius: 10},
  // { x: 140, y: 100, radius: 10},
];


for(x=200;x<=400;x+=5){
  var dot = { x: x, y: 150, radius: 10};
  dots.push(dot);
}
for(y=50;y<=250;y+=5){
  var dot = { x: 300, y: y, radius: 10};
  dots.push(dot);
}
for(x=125;x<=475;x+=5){
  var dot = { x: x, y:250 , radius: 10};
  dots.push(dot);
}

var i=0;
function loop(){
  setTimeout(function(){
    if(i>=dots.length){
      setTimeout(function(){
        context.clearRect(0, 0, 600, 300);
        i=0;
        loop();
      },2500)
    }else{
      drawDot(dots[i]);
    }
  }, 10);
}
loop();

function drawDot(dot) {
  context.beginPath();
  context.arc(dot.x, dot.y, dot.radius, 0, 2 * Math.PI, false);
  context.fillStyle = '#000000';
  context.fill();
  i++;
  loop();
}

// draw text animate function
// ========================================================
// var ctx = document.querySelector("canvas").getContext("2d"),
//     dashLen = 220, dashOffset = dashLen, speed = 5,
//     txt = "STROKE-ON CANVAS", x = 30, i = 0;

// ctx.font = "50px Comic Sans MS, cursive, TSCu_Comic, sans-serif"; 
// ctx.lineWidth = 5; ctx.lineJoin = "round"; ctx.globalAlpha = 2/3;
// ctx.strokeStyle = ctx.fillStyle = "#1f2f90";

// (function loop() {
//   ctx.clearRect(x, 0, 60, 150);
//   ctx.setLineDash([dashLen - dashOffset, dashOffset - speed]); // create a long dash mask
//   dashOffset -= speed;                                         // reduce dash length
//   ctx.strokeText(txt[i], x, 90);                               // stroke letter

//   if (dashOffset > 0) requestAnimationFrame(loop);             // animate
//   else {
//     ctx.fillText(txt[i], x, 90);                               // fill final letter
//     dashOffset = dashLen;                                      // prep next char
//     x += ctx.measureText(txt[i++]).width + ctx.lineWidth * Math.random();
//     ctx.setTransform(1, 0, 0, 1, 0, 3 * Math.random());        // random y-delta
//     ctx.rotate(Math.random() * 0.005);                         // random rotation
//     if (i < txt.length) requestAnimationFrame(loop);
//   }
// })();
// =========================================================


// draw ball function
// =========================================================
// var canvas = document.getElementById('canvas');
// var ctx = canvas.getContext('2d');
// var ball = {
//   x: 100,
//   y: 100,
//   radius: 25,
//   color: 'blue',
//   draw: function() {
//     ctx.beginPath();
//     ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2, true);
//     ctx.closePath();
//     ctx.fillStyle = this.color;
//     ctx.fill();
//   }
// };

// function draw() {
//   ctx.clearRect(0,0, canvas.width, canvas.height);
//   ball.draw();
//   ball.x += ball.vx;
//   ball.y += ball.vy;
//   raf = window.requestAnimationFrame(draw);
// }

// canvas.addEventListener('mouseover', function(e) {
//   raf = window.requestAnimationFrame(draw);
// });

// canvas.addEventListener('mouseout', function(e) {
//   window.cancelAnimationFrame(raf);
// });

// ball.draw();
// =========================================================