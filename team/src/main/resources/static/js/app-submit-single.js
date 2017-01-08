$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link-single",
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
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });
