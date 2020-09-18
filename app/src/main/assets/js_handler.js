var globalGraphValues = null;

window.onload = function () {
    globalGraphValues = JsHandler.graphInputValues();
    loadGlobalGraph();
}

function loadGlobalGraph(){
    var localGraphValues = globalGraphValues.split(",");
    var index1 = localGraphValues[0] == "empty" ? null : localGraphValues[0];
    var index2 = localGraphValues[1] == "empty" ? null : localGraphValues[1];
    var index3 = localGraphValues[2] == "empty" ? null : localGraphValues[2];
    var index4 = localGraphValues[3] == "empty" ? null : localGraphValues[3];
    var index5 = localGraphValues[4] == "empty" ? null : localGraphValues[4];
    var index6 = localGraphValues[5] == "empty" ? null : localGraphValues[5];
    var index7 = localGraphValues[6] == "empty" ? null : localGraphValues[6];
    var index8 = localGraphValues[7] == "empty" ? null : localGraphValues[7];
    var index9 = localGraphValues[8] == "empty" ? null : localGraphValues[8];
    var index10 = localGraphValues[9] == "empty" ? null : localGraphValues[9];
    var index11 = localGraphValues[10] == "empty" ? null : localGraphValues[10];

   new Chart(document.getElementById("chart"), {
   			type: 'line',
   			data: {
   			  labels: ["1","2", "3", "4", "5", "6","7","8","9","10"],
   			  datasets: [
   				{
   				  showLine: true,
   				  fill:false,
   				  borderColor: "rgb(0,153,153)",
   				  data: [0,index1,index2,index3,index4,index5,index6,index7,index8,index9]
   				},

   				{
   				  showLine: true,
   				  fill:false,
   				  borderColor: "red",
   				  borderWidth: 5,
   				  data: [null,null,null,null,null,index10,index11,null,null]
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
   				text: 'Gj. jord',
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
   							labelString: "Målepunkter (EC)",
   							fontSize: 18
   						}
   					}]
   				}
   			}
   		});
}