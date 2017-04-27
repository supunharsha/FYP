Array.prototype.indexOfForArrays = function(search)
{
  var searchJson = JSON.stringify(search); // "[3,566,23,79]"
  var arrJson = this.map(JSON.stringify); // ["[2,6,89,45]", "[3,566,23,79]", "[434,677,9,23]"]
  return arrJson.indexOf(searchJson);
};



function createImage(){
	
	var barriers = [[400,401],[400,402],[400,403],[400,404]];
	
	// create an offscreen canvas
    var canvas = document.createElement("canvas");
    var ctx = canvas.getContext("2d");

    // size the canvas to your desired image
    canvas.width = 1200;
    canvas.height = 700;

    // get the imageData and pixel array from the canvas
    var imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    var data = imgData.data;

    // manipulate some pixel elements
    for (var i = 0; i < data.length; i += 4) {
    	   	
        data[i]   = 0; // set every red pixel element to 255
        data[i+1] = 255;
        data[i+2] = 255;
        data[i+3] = 255; // make this pixel opaque        
    }

    // put the modified pixels back on the canvas
    ctx.putImageData(imgData, 0, 0);

    // create a new img object
    var image = new Image();

    // set the img.src to the canvas data url
    image.src = canvas.toDataURL();

    // append the new img object to the page
    $( "#map" ).empty();
    $( "#map" ).append( image );
    $( "#map" ).children().addClass('img-rounded img-responsive');

}	    
$( document ).ready(function() {
	
   createImage();
});