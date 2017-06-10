var Message = {
		READY_TO_WORK                   : 0,
		KEY_PLACE_AREAS		            : 1,    
		LIST_OF_OTHER_AGENTS            : 2,
		LOCATION_MAP                    : 3,
		ASSIGNED_AREA                   : 4,
		PERSONS_DETAILS                 : 5,
		SUSPICIOUS_PERSON               : 6,
		CURRENT_BATTERY_VOLTAGE         : 7,
		CRITICAL_BATTERY_LEVEL          : 8,
		PERSON_DETECTED                 : 9,
		CURRENT_LOCATION                : 10,
		AGENT_COMMUNICATION_STOPPED     : 11,
		BATTERY_STATUS  				: 12,
		LOCATION_STATUS 				: 13
} 
	
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
	                 [ 200, 150, 350, 300 ],   	//Inner Region1
	                 [ 650, 150, 350, 300 ],   	//Inner Region2
	                 [ 550, 450, 20, 100 ],
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
	                 [525,500],
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
	
	//////////////create web socket connection to real time updates //////////////////
	var webSocket = new WebSocket("ws://localhost:8080/Coordinator/websocketserver");
	
	var message = document.getElementById("message");
	webSocket.onopen = function(message){ wsOpen(message);};
	webSocket.onmessage = function(message){ wsGetMessage(message);};
	webSocket.onclose = function(message){ wsClose(message);};
	webSocket.onerror = function(message){ wsError(message);};
	function wsOpen(message){
		
	}
	function wsSendMessage(){
		webSocket.send(message.value);		
	}
	function wsCloseConnection(){
		webSocket.close();
	}
	function wsGetMessage(message){
		processMessage(message)
	}
	function wsClose(message){
		
	}

	function wserror(message){
		
	}

}


function processMessage(msg){
	msg = msg.data;
	var obj = JSON.parse(msg);
	var rowCount = $('#agentList tr').length;
	if(obj.Message == Message.READY_TO_WORK){	
		for(var i = 0 ; i < rowCount ; i++){
			var x = $('#agentList').find("tr:eq("+i+") td:eq(0)").html();
			if(x == obj.AgentId){
				return
			}
		}		
		$('#agentList tbody').append("<tr><th scope=\"row\">"+rowCount+"</th><td>-</td><td>-</td><td>-</td></tr>");
		$('#agentList').find("tr:eq("+rowCount+") td:eq(0)").html(obj.AgentId);
	}else if(obj.Message == Message.AGENT_COMMUNICATION_STOPPED){		
		for(var i = 0 ; i < rowCount ; i++){
			var x = $('#agentList').find("tr:eq("+i+") td:eq(0)").html();
			if(x == obj.AgentId){
				$('#agentList').find("tr:eq("+i+")").remove();
			}
		}
		
	}else if(obj.Message == Message.BATTERY_STATUS){
		for(var i = 0 ; i < rowCount ; i++){
			var x = $('#agentList').find("tr:eq("+i+") td:eq(0)").html();
			if(x == obj.AgentId){
				var values = JSON.stringify(obj.Battery).split("-----");
				values[0] = values[0].replace('[','');
				values[0] = values[0].replace('"','');
				values[1] = values[1].replace(']','');
				values[1] = values[1].replace('"','');
				values[1] = values[1].replace('\\','');
				values[1] = values[1].replace('\\','');
				values[1] = values[1].replace('r','');
				values[1] = values[1].replace('n','');
				$('#agentList').find("tr:eq("+i+") td:eq(1)").html(values[0]);				
				$('#agentList').find("tr:eq("+i+") td:eq(2)").html(values[1]);
			}
		}		
	}else if(obj.Message == Message.SUSPICIOUS_PERSON){
		var rowCount2 = $('#personList tr').length;
		$('#personList tbody').append("<tr><th scope=\"row\">"+rowCount2+"</th><td>"+obj.AgentId+"</td><td>-</td><td>-</td><td><img src=\"data:image/png;base64,"+obj.Person+"\"></td></tr>");
	}
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


function readURL(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();

		reader.onload = function(e) {
			$('#upimage').attr('src', e.target.result);
			$('#dispalyedImage').attr('src', e.target.result);
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
    		ratio : 0.65,
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
    
    $("#displayedName").html($("#person-name").val())
    $("#displayedHeight").html($("#person-height").val())
    $("#displayedUpperColour").attr('style',  "background-color:"+$("#person-upperBodyColour").val());
    $("#displayedLowerColour").attr('style',  "background-color:"+$("#person-lowerBodyColour").val());

    
  	
})

$('#removeperson').click(function(){
	
	$('#personList tr').empty();
	
	 $.ajax({
         url: 'http://localhost:8080/Coordinator/stopsearch',
         type: 'post',
         contentType: false,
         cache: false,
         dataType: 'json',
     });
	
});

