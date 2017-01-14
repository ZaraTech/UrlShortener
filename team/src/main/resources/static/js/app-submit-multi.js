$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                
                // delete previous results
                $("#result").html("");
                
                var data = new FormData(this);
                $.ajax({
                    type : "POST",
                    url : "/link-multi",
                    data: data,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success : function(msg) {

                        var len = msg.length;
                        var data="";
                        
                        for(var i = 0; i < len; i++){
                            data +=
                                "<tr>"
                                + "<td>"
                                + "<a target='_blank' href='" + msg[i].target + "'>" + msg[i].target + "</a>"
                                + "</td>"
                                + "<td>"
                                + "<a target='_blank' href='" + msg[i].uri + "'>" + msg[i].hash + "</a>"
                                + "</td>"
                                + "<td>"
                                + "<a target='_blank' href='" + msg[i].qr + "'>view QR</a>"
                                + "</td>"
                                + "<td>"
                                + "<a target='_blank' href='" + msg[i].uri + "+'>view details</a>"
                                + "</td>"
                                + "</tr>";
                        }
                        
                        $("#result").html(
                            "<div class='text-left'><table class='table table-hover table-bordered'>"
                            + "<thead>"
                            + "<tr>"
                            + "<th>Target URL</th>"
                            + "<th>Short URL</th>"
                            + "<th>QR</th>"
                            + "<th>Details</th>"
                            + "</tr>"
                            + "</thead><tbody>"
                            + data
                            + "</tbody></table></div>");
                    },
                    error : function (jqXHR, exception) {
                        var msg = '';
                        if (jqXHR.status === 0) {
                            msg = 'Can not connect to the server. Verify Network.';
                        } else if (jqXHR.status == 400) {
                            msg = 'There are one or more URIs that are not valid. [HTTP Code 400]';
                        } else if (jqXHR.status == 404) {
                            msg = 'Requested service not found. [HTTP Code 404]';
                        } else if (jqXHR.status == 500) {
                            msg = 'Internal Server Error.<br />Maybe an URI is not correct [HTTP Code 500]';
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
            });
    });
