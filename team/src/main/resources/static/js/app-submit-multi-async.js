var jobUrl;

$(document).ready(
    function() {
        $("#shortener1").submit(
            function(event) {
                event.preventDefault();
                
                // delete previous results
                $("#result").html("");
                
                var data = new FormData(this);
                $.ajax({
                    type : "POST",
                    url : "/link-multi-async-file",
                    data: data,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success : function(msg) {
                        jobUrl=msg.jobUrl;
                        getTaskData();
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
                });
            }
        );
        
        $("#shortener2").submit(
            function(event) {
                event.preventDefault();
                
                // delete previous results
                $("#result").html("");
                
                $.ajax({
                    type : "POST",
                    url : "/link-multi-async-input",
                    data: $(this).serialize(),
                    success : function(msg) {
                        jobUrl=msg.jobUrl;
                        getTaskData();
                    },
                    error : function (jqXHR, exception) {
                        var msg = '';
                        if (jqXHR.status === 0) {
                            msg = 'Can not connect to the server. Verify Network.';
                        } else if (jqXHR.status == 404) {
                            msg = 'Requested page not found. [404]';
                        } else if (jqXHR.status == 500) {
                            msg = 'Internal Server Error [500].';
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
                });
            }
        );        
    }
);

function getTaskData(){
    $.ajax({
        type : "GET",
        url : jobUrl,
        success : function(taskData){
            var len=taskData.urlList.length;
            var data="";
            var pending = 0;
            
            for(var i = 0; i < len; i++){
                data +=
                    "<tr>"
                    + "<td>"
                    + "<a target='_blank' href='" + taskData.urlList[i].target + "'>" + taskData.urlList[i].target + "</a>"
                    + "</td>"
                    + "<td>";
                    
                if(taskData.urlList[i].progress == "pending"){
                    data += "<button class='btn btn-sm btn-warning' disabled><span class='glyphicon glyphicon-refresh glyphicon-refresh-animate'></span> Waiting...</button>";
                    pending+=1;
                } else if(taskData.urlList[i].progress == "error") {
                    data += "<button class='btn btn-sm btn-danger' disabled><span class='glyphicon glyphicon-remove glyphicon-remove-animate'></span> ERROR!</button>";
                } else {
                    data += "<button class='btn btn-sm btn-success' disabled><span class='glyphicon glyphicon-ok glyphicon-ok-animate'></span> Shortened!</button>";
                }

                if(taskData.urlList[i].hash != null){
                    data +=
                    "</td><td>"
                    + "<a target='_blank' href='" + taskData.urlList[i].uri + "'>" + taskData.urlList[i].hash + "</a>"
                    + "</td>"               
                    + "</tr>";
                } else {
                    data +=
                    "</td><td>"
                    + "</td>"               
                    + "</tr>";
                }
                
                
            }
            
            $("#result").html(
                "<div class='text-center'><table class='table table-hover table-bordered'>"
                + "<thead>"
                + "<tr>"
                + "<th class='text-center'>Target URL</th>"
                + "<th class='text-center'>Status</th>"
                + "<th class='text-center'>Short URL</th>"
                + "</tr>"
                + "</thead><tbody>"
                + data
                + "</tbody></table></div>");    
            
            if(pending > 0){
                setTimeout(getTaskData, 1000);
            }                       
            
        },
        error : function (jqXHR, exception) {
            var msg = '';
            if (jqXHR.status === 0) {
                msg = 'Can not connect to the server. Verify Network.';
            } else if (jqXHR.status == 404) {
                msg = 'Requested page not found. [404]';
            } else if (jqXHR.status == 500) {
                msg = 'Internal Server Error [500].';
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
    });
}
