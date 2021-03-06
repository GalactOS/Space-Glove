var myFirebaseRef = new Firebase("https://fiery-inferno-6630.firebaseio.com/");

var message = myFirebaseRef.child("message");
var selection = myFirebaseRef.child("selection");
var gyr_x = myFirebaseRef.child("gyr_x");
var gyr_y = myFirebaseRef.child("gyr_y");
var gyr_z = myFirebaseRef.child("gyr_z");
var acc_x = myFirebaseRef.child("acc_x");
var acc_y = myFirebaseRef.child("acc_y");
var acc_z = myFirebaseRef.child("acc_z");

var go = myFirebaseRef.child("go");

var reset = myFirebaseRef.child("reset");

var $right = $(".glyphicon-chevron-right");
var $left = $(".glyphicon-chevron-left");

var $first = $('div:first', '#menu'),
	$last = $('div:last', '#menu');

dir = {}

var going = false;

dir.gyr_x_val = 0;
dir.gyr_y_val = 0;
dir.gyr_z_val = 0;
dir.acc_x_val = 0;
dir.acc_y_val = 0;
dir.acc_z_val = 0;


message.on("value", function(snapshot) {
	if(snapshot.val() != null) {
		$("#message").text(snapshot.val());
	}
});

selection.on("value", function(snapshot) {
  	if(snapshot.val() == "up") {
  		var $prev, $selected = $(".selected");

	    $prev = $selected.prev('div').length ? $selected.prev('div') : $last;
	    $selected.removeClass("selected");
	    $prev.addClass('selected');
  	} else if(snapshot.val() == "down") {
  		var $next, $selected = $(".selected");

	    $next = $selected.next('div').length ? $selected.next('div') : $first;
	    $selected.removeClass("selected");
	    $next.addClass('selected');
  	} else if(snapshot.val() == "Toggle") {
  		console.log("toggle");
  		$selected = $(".selected");
  		$selected.click();
  	}
  	selection.set(null);
});

gyr_x.on("value", function(snapshot) {
	var val = Math.round( snapshot.val() * 10) / 10
	if(val > 2) {
		dir.gyr_x_val = 1
	} else if (val < -2) {
		dir.gyr_x_val = -1
	} else if (val > -.05 && val < .05){
		dir.gyr_x_val = 0;
	}
	$("#gyr_x").text(dir.gyr_x_val);
});

gyr_y.on("value", function(snapshot) {
	var val = Math.round( snapshot.val() * 10) / 10
	if(val > 2) {
		dir.gyr_y_val = 1
	} else if (val < -2) {
		dir.gyr_y_val = -1
	} else if (val > -.05 && val < .05) {
		dir.gyr_y_val = 0;
	}
	$("#gyr_y").text(dir.gyr_y_val);
});

gyr_z.on("value", function(snapshot) {
	var val = Math.round( snapshot.val() * 10) / 10
	if(val > 2) {
		dir.gyr_z_val = 1
	} else if (val < -2) {
		dir.gyr_z_val = -1
	} else if (val > -.1 && val < .1){
		dir.gyr_z_val = 0;
	}
	$("#gyr_z").text(dir.gyr_z_val);
});

acc_x.on("value", function(snapshot) {
	dir.acc_x_val += snapshot.val();
	acc_x.set(0);
	$("#acc_x").text(dir.acc_x_val);
});

acc_y.on("value", function(snapshot) {
	dir.acc_y_val += snapshot.val();
	acc_y.set(0);
	$("#acc_y").text(dir.acc_y_val);
});

acc_z.on("value", function(snapshot) {
	dir.acc_z_val += snapshot.val();
	acc_z.set(0);
	$("#acc_z").text(dir.acc_z_val);
});

go.on("value", function(snapshot) {
	going = snapshot.val();
})

reset.on("value", function(snapshot) {
	if(snapshot.val() != null) {
		console.log("RESET");
		resetVals();
		reset.set(null);
	}
})

$( ".settings" ).click(function() {
	console.log("settings");
	$("#settings").toggle();
});

$( ".jetpack_dir" ).click(function() {
	console.log("jetpack_dir");
	$("#jetpack").toggle();
});

$(".message").click(function() {
	$("#message").text("Messages sent!")
	$("#message").fadeOut( "slow" , function() {
		$("#message").text("");
		$("#message").show();
	});
})

var renderer	= new THREE.WebGLRenderer({
	antialias	: true
});
renderer.setSize( window.innerWidth, window.innerHeight );
document.getElementById('hud').appendChild( renderer.domElement );
renderer.shadowMapEnabled	= true

var onRenderFcts= [];
var scene	= new THREE.Scene();
var camera	= new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.01, 1000 );
camera.position.z = 1;
var light	= new THREE.AmbientLight( 0x222222 )
scene.add( light )
var light	= new THREE.DirectionalLight( 0xffffff, 1 )
light.position.set(5,5,5)
scene.add( light )
light.castShadow	= true
light.shadowCameraNear	= 0.01
light.shadowCameraFar	= 15
light.shadowCameraFov	= 45
light.shadowCameraLeft	= -1
light.shadowCameraRight	=  1
light.shadowCameraTop	=  1
light.shadowCameraBottom= -1
// light.shadowCameraVisible	= true
light.shadowBias	= 0.001
light.shadowDarkness	= 0.2
light.shadowMapWidth	= 1024
light.shadowMapHeight	= 1024

//////////////////////////////////////////////////////////////////////////////////
//		added starfield							//
//////////////////////////////////////////////////////////////////////////////////

var starSphere	= THREEx.Planets.createStarfield()
scene.add(starSphere)

//////////////////////////////////////////////////////////////////////////////////
//		add an object and make it move					//
//////////////////////////////////////////////////////////////////////////////////
// var datGUI	= new dat.GUI()
var containerEarth	= new THREE.Object3D()
//containerEarth.rotateZ(-23.4 * Math.PI/180)
containerEarth.position.z	= 0
scene.add(containerEarth)
var moonMesh	= THREEx.Planets.createMoon()
moonMesh.position.set(0.5,0.5,0.5)
moonMesh.scale.multiplyScalar(1/5)
moonMesh.receiveShadow	= true
//moonMesh.castShadow	= true
containerEarth.add(moonMesh)
var earthMesh	= THREEx.Planets.createEarth()
earthMesh.receiveShadow	= true
//earthMesh.castShadow	= true
containerEarth.add(earthMesh)
// onRenderFcts.push(function(delta, now){
// 	earthMesh.rotation.y += 1/32 * delta;		
// })
var geometry	= new THREE.SphereGeometry(0.5, 32, 32)
var material	= THREEx.createAtmosphereMaterial()
material.uniforms.glowColor.value.set(0x00b3ff)
material.uniforms.coeficient.value	= 0.8
material.uniforms.power.value		= 2.0
var mesh	= new THREE.Mesh(geometry, material );
mesh.scale.multiplyScalar(1.01);
containerEarth.add( mesh );

// new THREEx.addAtmosphereMaterial2DatGui(material, datGUI)
var geometry	= new THREE.SphereGeometry(0.5, 32, 32)
var material	= THREEx.createAtmosphereMaterial()
material.side	= THREE.BackSide
material.uniforms.glowColor.value.set(0x00b3ff)
material.uniforms.coeficient.value	= 0.5
material.uniforms.power.value		= 4.0
var mesh	= new THREE.Mesh(geometry, material );
mesh.scale.multiplyScalar(1.15);
containerEarth.add( mesh );

// new THREEx.addAtmosphereMaterial2DatGui(material, datGUI)
var earthCloud	= THREEx.Planets.createEarthCloud()
earthCloud.receiveShadow	= true
//earthCloud.castShadow	= true
containerEarth.add(earthCloud)
onRenderFcts.push(function(delta, now){
	earthCloud.rotation.y += 1/8 * delta;		
})

//////////////////////////////////////////////////////////////////////////////////
//		Camera Controls							//
//////////////////////////////////////////////////////////////////////////////////
// var mouse	= {x : 0, y : 0}
// document.addEventListener('mousemove', function(event){
// 	mouse.x	= (dir.gyr_x_val / window.innerWidth ) - 0.5
// 	mouse.y	= (dir.gyr_y_val / window.innerHeight) - 0.5
// }, false)
onRenderFcts.push(function(delta, now){
	//camera.position.x -= (dir.acc_x_val/100 )//- camera.position.x) * (delta*3)
	//camera.position.y -= (dir.acc_z_val/100) //- camera.position.y) * (delta*3)
	if(dir.gyr_x_val >= 1) {
		camera.rotation.x +=.005;
	} else if (dir.gyr_x_val <= -1) {
		camera.rotation.x -= .005;
	} 
	if(dir.gyr_z_val >= 1) {
		camera.rotation.y +=.005;
	} else if (dir.gyr_z_val <= -1) {
		camera.rotation.y -= .005;
	}
	if(dir.gyr_y_val >= 1) {
		camera.rotation.z -=.005;
	} else if (dir.gyr_y_val <= -1) {
		camera.rotation.z += .005;
	}
	/*
	if(going) {
		camera.position.z -= .001
	}*/
	
	camera.position.x += dir.acc_x_val / 1000;
	camera.position.z -= dir.acc_y_val / 1000;
	camera.position.y += dir.acc_z_val / 1000;

	
	//camera.position.z += (dir.gyr_x_val/100) //- camera.position.z) //* (delta*3)
	// camera.rotation.x += (dir.gyr_x_val/100 )//- camera.rotation.x) * (delta*3)
	// camera.rotation.y += (dir.gyr_y_val/100 )//- camera.rotation.y) * (delta*3)
	// camera.rotation.z += (dir.gyr_z_val/100 )//- camera.rotation.z) * (delta*3)
	
	// camera.position.x += (gyr_x*5 - camera.position.x) * (delta*3);
	// camera.position.y += (gyr_y*5 - camera.position.y) * (delta*3);
	camera.lookAt( scene.position )


})
//////////////////////////////////////////////////////////////////////////////////
//		render the scene						//
//////////////////////////////////////////////////////////////////////////////////
onRenderFcts.push(function(){
	renderer.render( scene, camera );		
})

var resetVals = function() {
	camera.position.x = 0;
	camera.position.y = 0;
	camera.position.z = 1;
	camera.rotation.x = 0;
	camera.rotation.y = 0;
	camera.rotation.z = 0;
	for (var property in dir) {
	    if (dir.hasOwnProperty(property)) {
	        dir[property] = 0;
	    }
	}
}

//////////////////////////////////////////////////////////////////////////////////
//		loop runner							//
//////////////////////////////////////////////////////////////////////////////////
var lastTimeMsec= null
requestAnimationFrame(function animate(nowMsec){
	// keep looping
	requestAnimationFrame( animate );
	// measure time
	lastTimeMsec	= lastTimeMsec || nowMsec-1000/60
	var deltaMsec	= Math.min(200, nowMsec - lastTimeMsec)
	lastTimeMsec	= nowMsec
	// call each update function
	onRenderFcts.forEach(function(onRenderFct){
		onRenderFct(deltaMsec/1000, nowMsec/1000)
	})
})