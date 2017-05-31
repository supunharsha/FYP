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
	
	var keyPlaces = [
	                 [125,125],
	                 [275,125],
	                 [425,125],
	                 [575,125],
	                 [725,125],
	                 [875,125],
	                 [1025,125],
	                 
	                 [125,313],
	                 [1025,313],
	                 
	                 [125,500],
	                 [275,500],
	                 [425,500],
	                 [575,500],
	                 [725,500],
	                 [875,500],
	                 [1025,500],
	                 
	                 
	                ]
	
	var initialPlace = keyPlaces[0];

	for (var i = 0; i < obstacles.length; i += 1) {
		ctx.strokeRect(obstacles[i][0],obstacles[i][1],obstacles[i][2],obstacles[i][3]);
	}
	
	for (var i = 0; i < keyPlaces.length; i += 1) {
		ctx.fillRect(keyPlaces[i][0],keyPlaces[i][1],5,5);
	}
	
	
	
	
	///// Send obstacle list to backend //////////////////////
	
	var data1 = {
			height 	: canvas.height,
			width  	: canvas.width,
			data1	: obstacles,
			data2	: keyPlaces,
			data3	: initialPlace
	}
	
	 $.ajax({
         url: 'http://localhost:8080/Coordinator/areamap',
         type: 'post',
         contentType: false,
         cache: false,
         dataType: 'application/json',
         processData: false,
         success: function (data) {            
         },
         data: JSON.stringify(data1)
     });
	
	//////////////////////////////////////////////////////////////
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

$('#submit').click(function(){
	var files = $('#file-input').prop("files")
    
	var data = new FormData();
	
    $.each(files, function(key, value)
    {
        data.append(key, value);
    });
    
    	
    var person={
    		name : $("#person-name").val(),
    		upperBody: $("#person-upperBodyColour").val(),
    		lowerBody: $("#person-lowerBodyColour").val() 
    		
    }
    
  
	data.append(JSON.stringify(person),1);
	
	
        $.ajax({
            url: 'http://localhost:8080/Coordinator/request',
            type: 'post',
            contentType: false,
            cache: false,
            dataType: 'json',
            processData: false,
            success: function (data) {
               
            },
            data: data
        });
	
	
})

