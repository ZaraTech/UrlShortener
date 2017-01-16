var ws;
var oldData=null;

$(document).ready(
    function() {
    
        loadCalendar();
    
        ws = new WebSocket("wss://" + window.location.host + "/stats-ws");
        ws.onmessage = function(event) {
            var msg=JSON.parse(event.data);
            if(JSON.stringify(msg) !== JSON.stringify(oldData) ){
                loadData(msg);
                loadChart(msg);
                oldData = msg;
            }
            
        };
        
        ws.onclose = function (event) {
			var reason = "";
			
			if(event.code == 1000)
				;// do nothing
			else if(event.code == 1001)
				;// do nothing
			else if(event.code == 1002)
				reason = "Protocol error";
			else if(event.code == 1003)
				reason = "Type of data not accepted.";
			else if(event.code == 1005)
				reason = "No status code was actually present.";
			else if(event.code == 1006)
			   reason = "Connection was closed abnormally";
			else if(event.code == 1007)
				reason = "Received data within a message that was not consistent with the type of the message.";
			else if(event.code == 1008)
				reason = "Message that \"violates its policy\".";
			else if(event.code == 1009)
			   reason = "Message too big.";
			else if(event.code == 1010) // Note that this status code is not used by the server, because it can fail the WebSocket handshake instead.
				reason = "One or more extensions expected<br /> Specifically, the extensions that are needed are: " + event.reason;
			else if(event.code == 1011)
				reason = "Unexpected condition fulfilling the request.";
			else if(event.code == 1015)
				reason = "Failure to perform a TLS handshake.";
			else
				reason = "Unknown reason";
			
			if(reason != ""){
				$('#result').html("<div class='alert alert-danger lead'>ERROR: "+ reason +"</div>");
			}	
		};
          
        ws.onerror = function(event) {
            $('#result').html("<div class='alert alert-danger lead'>ERROR: There was an error with your connection.<br/>Reload the page please!</div>");
        };
        
        $('#form').submit(
            function(event){
                event.preventDefault();

                var formData = $(this).serialize();

                ws.send(formData);
            }
        );
    }
);

function loadData(msg) {
    var data="";
    data+=
        "<div class='text-left'><table class='table table-hover table-bordered' id='data'>"
        + "<thead>"
        + "<tr>"
        + "<th>Browser</th>"
        + "<th>Version</th>"
        + "<th>Click Stats</th>"
        + "</tr>"
        + "</thead><tbody>";

    var browserLen = msg.browserList.length;
    for(var i=0;i<browserLen;i++) {
        var versionLen=msg.versionList[i].length;
        for(var j=0;j<versionLen;j++){
            // save browsers data
            data += "<tr><td>" +msg.browserList[i]+"</td>"+"<td>" +msg.versionList[i][j]+"</td>"+"<td>"
                +((msg.clicksforversion[i][j]/msg.clicks)*100).toFixed(2)+" %</td></tr>";
        }

    }data+="</tbody></table></div>";

    data+=
        "<div class='text-left'><table class='table table-hover table-bordered'>"
        + "<thead>"
        + "<tr>"
        + "<th>Operative System</th>"
        + "<th>Click Stats</th>"
        + "</tr>"
        + "</thead><tbody>";
        
    var osLen=msg.osList.length;    
    for(var i=0;i<osLen;i++) {
        // save os data
        data += "<tr><td>" +msg.osList[i]+"</td>"+"<td>" +((msg.clicksforos[i]/msg.clicks)*100).toFixed(2)+" %</td></tr>";
    }
    data+="</tbody></table></div>";

    $("#result").html(data);
}

function loadCalendar(){
    $( "#datepicker" ).datepicker({ dateFormat: 'yy-mm-dd' });
    $( "#datepicker2" ).datepicker({ dateFormat: 'yy-mm-dd' });
}

function loadChart(msg){
    var infoOs = msg.jsonOs;
    var infoBrowser = msg.jsonVersion;

    (function (Highcharts) {
        Highcharts.wrap(Highcharts.seriesTypes.pie.prototype, 'render', function (proceed) {

            var chart = this.chart,
                center = this.center || (this.yAxis && this.yAxis.center),
                titleOption = this.options.title,
                box;

            proceed.call(this);

            if (center && titleOption) {
                box = {
                    x: chart.plotLeft + center[0] - 0.5 * center[2],
                    y: chart.plotTop + center[1] - 0.5 * center[2],
                    width: center[2],
                    height: center[2]
                };
                if (!this.title) {
                    this.title = this.chart.renderer.label(titleOption.text)
                        .css(titleOption.style)
                        .add()
                }
                var labelBBox = this.title.getBBox();
                if (titleOption.align == "center")
                    box.x -= labelBBox.width/2;
                else if (titleOption.align == "right")
                    box.x -= labelBBox.width;
                this.title.align(titleOption, null, box);
            }
        });

    } (Highcharts));

    // Clicks chart
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'chart',
            type: 'pie'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>'
        },
        legend: {
            enabled: true,
            borderWidth: 1,
            borderColor: 'gray',
            align: 'center',
            verticalAlign: 'top',
            layout: 'horizontal',
            x: 0, y: 50
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                showInLegend: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    distance: -20,
                    format: '<b>{point.name}</b>: {point.percentage:.2f} %',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                },
                size: '80%'
            }
        },
        series: [{
            type: 'pie',
            data: infoOs,
            sliced: true,
            selected: true,
            startAngle: 45,
            name:"Clicks",
            colors: ['#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
            title: {
                align: 'left',
                text: '<b>Operative System</b><br>Clicks stats',
                verticalAlign: 'top',
                y: -40
            },
            center: ['30%', '50%']
        },
            {
                type: 'pie',
                data: infoBrowser,
                sliced: true,
                selected: true,
                startAngle: 45,
                name:"Clicks",
                title: {
                    align: 'right',
                    text: '<b>Browser Version</b><br>Clicks stats',
                    verticalAlign: 'top',
                    y: -40
                },
                center: ['70%', '50%']
            }]
    });
    chart.setTitle({text: ""});
}
