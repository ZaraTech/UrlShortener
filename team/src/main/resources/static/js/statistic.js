$(document).ready(
    getStatistics()
);

function getStatistics() {
    $. ajax({
        type : "GET",
        url : "/statistics",
        success : function(msg) {
            var data="";
            data+=
                "<div class='text-left'><table class='table table-hover table-bordered'>"
                + "<thead>"
                + "<tr>"
                + "<td>Browser</td>"
                + "<td>Version</td>"
                + "<td>Stats</td>"
                + "</tr>"
                + "</thead><tbody>";

            var browserLen = msg.browserList.length;

            for(var i=0;i<browserLen;i++) {
                var versionLen=msg.versionList[i].length;
                for(var j=0;j<versionLen;j++){
                    // guardar datos de browsers
                    data += "<tr><td>" +msg.browserList[i]+"</td>"+"<td>" +msg.versionList[i][j]+"</td>"+"<td>" +(msg.clicksforversion[i][j]/msg.clicks)*100+"</td></tr>";
                }

            }
            data+="</tbody></table></div>";
            data+=
                "<div class='text-left'><table class='table table-hover table-bordered'>"
                + "<thead>"
                + "<tr>"
                + "<td>Os</td>"
                + "<td>Stats</td>"
                + "</tr>"
                + "</thead><tbody>";
            var osLen=msg.osList.length;
            for(var i=0;i<osLen;i++) {
                // guardar datos de browsers
                data += "<tr><td>" +msg.osList[i]+"</td>"+"<td>" +(msg.clicksforos[i]/msg.clicks)*100+"</td></tr>";
            }
            data+="</tbody></table></div>";
            $("#result").html(data);
        }
    } );
}