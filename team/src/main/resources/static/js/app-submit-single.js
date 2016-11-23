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
<<<<<<< HEAD
                            + "<div class='alert alert-info lead'><a target='_blank' href='"
                            + msg.qr
                            + "'>"
                            + "Get QR here!"
                            + "</a></div>"+
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri+"/+'"
                            + ">"
                            + msg.uri+"/+"
=======
			    + "<div class='alert alert-info lead'><a target='_blank' href='"
                            + msg.qr
                            + "'>"
                            + "Get QR here!"
>>>>>>> c2c1faa5f125fe64d88f9effe36551d18c5f0ebb
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });
