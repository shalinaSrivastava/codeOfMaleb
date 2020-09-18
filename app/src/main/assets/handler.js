var localGraphValues = null;
var labelString = "Målepunkter (EC)";

window.onload = function () {
    localGraphValues = JsHandler.localGraphInputValues();
    loadLocalGraph();
}

function loadLocalGraph(){
    var graphVal = localGraphValues.split(",");
    var ind1 = graphVal[0] == "empty" ? null : graphVal[0];
    var ind2 = graphVal[1] == "empty" ? null : graphVal[1];
    var ind3 = graphVal[2] == "empty" ? null : graphVal[2];
    var ind4 = graphVal[3] == "empty" ? null : graphVal[3];
    var ind5 = graphVal[4] == "empty" ? null : graphVal[4];
    var ind6 = graphVal[5] == "empty" ? null : graphVal[5];
    var ind7 = graphVal[6] == "empty" ? null : graphVal[6];
    var ind8 = graphVal[7] == "empty" ? null : graphVal[7];
    var ind9 = graphVal[8] == "empty" ? null : graphVal[8];
    var ind10 = graphVal[9] == "empty" ? null : graphVal[9];
    var ind11 = graphVal[10] == "empty" ? null : graphVal[10];

   new Chart(document.getElementById("lchart"), {
   			type: 'line',
   			data: {
   			  labels: ["1","2", "3", "4", "5", "6","7","8","9","10"],
   			  datasets: [
   				{
   				  showLine: true,
   				  fill:false,
   				  borderColor: "rgb(0,153,153)",
   				  data: [0,ind1,ind2,ind3,ind4,ind5,ind6,ind7,ind8,ind9]
   				},

   				{
   				  showLine: true,
   				  fill:false,
   				  borderColor: "red",
   				  borderWidth: 5,
   				  data: [null,null,null,null,null,ind10,ind11,null,null]
   				}
   			  ]
   			},
   			options: {
   			  legend: { display: false },
   			  elements: {
   					line: {
   						tension: 0
   					}
   			  },
   			  title: {
   				display: true,
   				text: 'Lokal Elektode',
   				fontSize: 18
   			  },

   			  scales: {
   					yAxes: [{
   						ticks: {
   							beginAtZero:true
   						},
   						scaleLabel: {
   							display: true,
   							labelString: 'ohm',
   							fontSize: 18
   						}
   					}],

   					xAxes: [{
   						scaleLabel: {
   							display: true,
   							labelString: labelString,
   							fontSize: 18
   						}
   					}]
   				}
   			}
   		});
}