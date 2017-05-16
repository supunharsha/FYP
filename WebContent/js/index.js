function createImage() {

	// create an offscreen canvas
	var canvas = document.createElement("canvas");
	var ctx = canvas.getContext("2d");
	ctx.strokeStyle="#000000";

	

	// size the canvas to your desired image
	canvas.width = 1200;
	canvas.height = 600;

	// get the imageData and pixel array from the canvas
	var imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
	var data = imgData.data;

	for (var i = 0; i < data.length; i += 4) {
		data[i] = 255;
		data[i + 1] = 255;
		data[i + 2] = 255;
		data[i + 3] = 255;
	}

	// put the modified pixels back on the canvas
	ctx.putImageData(imgData, 0, 0);

//	
//	var lineArray = [ [ 50, 50, 1150, 50 ], 
//	                  [ 50, 50, 50, 550 ],
//	                  [ 50, 550, 1150, 550 ]]
//	for (var i = 0; i < lineArray.length; i += 1) {
//		ctx.beginPath();
//		ctx.moveTo(lineArray[i][0],lineArray[i][1]);
//		ctx.lineTo(lineArray[i][2], lineArray[i][3]);
//		ctx.stroke();
//	}
	
	
	var obstacles = [
	                 [ 50, 50, 1100, 500 ], 	//Outer region 
	                 [ 200, 150, 800, 300 ],   	//Inner Region
	                 [ 150, 50, 100, 50 ], 		//Obstacle 1 	
	                 [ 300, 50, 100, 50 ], 		//Obstacle 2  	 
	                 [ 450, 50, 100, 50 ], 		//Obstacle 3  	     Northside desk set 
	                 [ 600, 50, 100, 50 ], 		//Obstacle 4	 
	                 [ 750, 50, 100, 50 ], 		//Obstacle 5	 
	                 [ 900, 50, 100, 50 ], 		//Obstacle 6	 
	                 
	                 [ 1100, 125, 50, 100 ], 	//Obstacle 7		
	                 [ 1100, 230, 50, 100 ], 	//Obstacle 8		Eastside desk set
	                 [ 1100, 335, 50, 100 ], 	//Obstacle 9
	                ]

	for (var i = 0; i < obstacles.length; i += 1) {
		ctx.strokeRect(obstacles[i][0],obstacles[i][1],obstacles[i][2],obstacles[i][3]);
	}
	
	// create a new img object
	var image = new Image();

	// set the img.src to the canvas data url
	image.src = canvas.toDataURL();

	// append the new img object to the page	
	$("#map").empty();
	$("#map").append(image);
	$("#map").children().addClass('img-rounded img-responsive');

}

$(document).ready(function() {
	createImage();
});

$(function() {
	$('#cp1').colorpicker({
		format : 'rgb'
	});
});

$(function() {
	$('#cp2').colorpicker({
		format : 'rgb'
	});
});

$('#upimage').click(function() {
	$('#file-input').trigger('click');
});

$("#file-input").change(function() {
	readURL(this);
})

// $('#submit').click(function() {
// $.ajax({
// type: "POST",
// timeout: 50000,
// url: url,
// data: dataString,
// success: function (data) {
// alert('success');
// return false;
// }
// });
// });

function readURL(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();

		reader.onload = function(e) {
			$('#upimage').attr('src', e.target.result);
		}

		reader.readAsDataURL(input.files[0]);
	}
}