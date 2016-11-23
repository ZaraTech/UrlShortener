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
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>"
                            + "<div class='alert alert-info lead'><a target='_blank' href='"
                            + msg.qr
                            + "'>"
                            + "Get QR here!"
                            + "</a></div>"+
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri+"/+'"
                            + ">"
                            + msg.uri+"/+"
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });
