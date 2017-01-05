$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
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
                                + "<a target='_blank' href='" + msg[i].target + "'><span>" + msg[i].target + "</span></a>"
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
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });
