// Rhino JS engine
//importPackage(Packages.purejs.util); 

// Nashorn JS Engine
var FileIO = Java.type("purejs.util.FileIO");

var numPtsPerMeasurement = 400;
var measurementInterval = 2;
var artefactOffset = 0.077;

function getFs(table){
  var sum = 0;
  var count = 0;
  for(var i = 1; i < 32; i++){
    sum += table.getAsNumber(1,i);
    count++;
  }
  return (sum/count) - artefactOffset;
}
function getFm(table){
  var sum = 0;
  var count = 0;
  for(var i = 120; i <= 130; i++){
    sum += table.getAsNumber(1,i);
    count++;
  }
  return (sum/count) - artefactOffset;
}

function calculateFluorescentParameters(masterTable){
	var slices  = masterTable.slice(numPtsPerMeasurement);
	F0=getFs(slices.get(0));
	println("F0="+F0);
	Fm=getFm(slices.get(0));
	println("Fm="+Fm);
	Fv=Fm-F0;
	println("Fv="+Fv);
	println("time (min.)\tPhi2\tNPQ");
	for(i = 0; i < slices.size(); i++){
		Fs=getFs(slices.get(i));
		Fmp=getFm(slices.get(i));
		Phi2 = (Fmp-Fs)/Fmp;
		NPQ = (Fm-Fmp)/Fm;
		println((measurementInterval*i)+"\t"+Phi2+"\t"+NPQ);
	}
}

var dataFile = FileIO.askForFile("Get data file");
println("Analyzing '"+dataFile+"'");
var dataTable = FileIO.readFileAsTable(dataFile);
calculateFluorescentParameters(dataTable);
