$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                
                // delete previous results
                $("#result").html("");
                
                $.ajax({
                    type : "POST",
                    url : "/link-single-async-checks",
                    data : $(this).serialize(),
                    success : function(msg) {
                        
                        var data="";
                        
                        data +=
                            "<tr>"
                            + "<td>"
                            + "<a target='_blank' href='" + msg.target + "'>" + msg.target + "</a>"
                            + "</td>"
                            + "<td>"
                            + "<a target='_blank' href='" + msg.uri + "'>" + msg.hash + "</a>"
                            + "</td>"
                            + "<td>"
                            + "<a target='_blank' href='" + msg.qr + "'>view QR</a>"
                            + "</td>"
                            + "<td>"
                            + "<a target='_blank' href='" + msg.uri + "+'>view details</a>"
                            + "</td>"
                            + "</tr>";
                        
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
                            msg = 'The URI is not valid. [HTTP Code 400]';
                        } else if (jqXHR.status == 404) {
                            msg = 'Requested service not found. [HTTP Code 404]';
                        } else if (jqXHR.status == 500) {
                            msg = 'Internal Server Error.<br />Maybe the URI is not correct [HTTP Code 500]';
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
