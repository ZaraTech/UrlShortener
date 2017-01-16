$(document).ready(
    function() {

        getStatistics();
        $('#form').submit(
            function(event){
                event.preventDefault();

                var formData = $(this).serialize();

                getStatisticsFiltered(formData);
            }
        );
    }
);

function getStatistics() {
    $. ajax({
        type : "GET",
        url : "/statistics",
        success : function(msg) {
            loadData(msg);
            loadCalendar();
            loadChart(msg);
        },
        error : function (jqXHR, exception) {
            var msg = '';
            if (jqXHR.status === 0) {
                msg = 'Can not connect to the server. Verify Network.';
            } else if (jqXHR.status == 400) {
                msg = 'Bad resquest. [HTTP Code 400]';
            } else if (jqXHR.status == 404) {
                msg = 'Requested page not found. [HTTP Code 404]';
            } else if (jqXHR.status == 500) {
                msg = 'Internal Server Error. [HTTP Code 500]';
            } else if (exception === 'parsererror') {
                msg = 'Requested JSON parse failed.';
            } else if (exception === 'timeout') {
                msg = 'Time out error.';
            } else if (exception === 'abort') {
                msg = 'Ajax request aborted.';
            } else {
                msg = 'Uncaught Error.\n' + jqXHR.responseText;
            }
            $("#result").html(
                "<div class='alert alert-danger lead'>ERROR: "+ msg +"</div>");
        }
    } );
}

function getStatisticsFiltered(data) {
    $. ajax({
        type : "GET",
        url : "/statistics",
        data: data,
        success : function(msg) {
            loadData(msg);
            loadCalendar();
            loadChart(msg);
        },
        error : function (jqXHR, exception) {
            var msg = '';
            if (jqXHR.status === 0) {
                msg = 'Can not connect to the server. Verify Network.';
            } else if (jqXHR.status == 400) {
                msg = 'Bad resquest. [400]';
            } else if (jqXHR.status == 404) {
                msg = 'Requested page not found. [404]';
            } else if (jqXHR.status == 500) {
                msg = 'Internal Server Error. [500]';
            } else if (exception === 'parsererror') {
                msg = 'Requested JSON parse failed.';
            } else if (exception === 'timeout') {
                msg = 'Time out error.';
            } else if (exception === 'abort') {
                msg = 'Ajax request aborted.';
            } else {
                msg = 'Uncaught Error.\n' + jqXHR.responseText;
            }
            $("#result").html(
                "<div class='alert alert-danger lead'>ERROR: "+ msg +"</div>");
        }
    } );
}

function loadData(msg) {
    console.log('Mostrando estadisticas ');
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
