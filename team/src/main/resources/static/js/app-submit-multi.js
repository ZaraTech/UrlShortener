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

                        for(var i = 0; i < len; i++){
                            $("#result").append(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg[i].uri
                                + "'>"
                                + msg[i].uri
                                + "</a></div>"+
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg[i].uri+"/+'"
                                + ">"
                                + msg[i].uri+"/+"
                                + "</a></div>");
                        }
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });
