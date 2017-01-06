<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>URL Shortener</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css"
          href="../webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="css/jquery-ui-1.7.2.custom.css" />
    <script type="text/javascript" src="../webjars/jquery/2.1.4/jquery.min.js"></script>
    <script type="text/javascript"
            src="../webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
    <script type="text/javascript">
        jQuery(function($){
            $.datepicker.regional['es'] = {
                closeText: 'Cerrar',
                prevText: '&#x3c;Ant',
                nextText: 'Sig&#x3e;',
                currentText: 'Hoy',
                monthNames: ['Enero','Febrero','Marzo','Abril','Mayo','Junio',
                    'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'],
                monthNamesShort: ['Ene','Feb','Mar','Abr','May','Jun',
                    'Jul','Ago','Sep','Oct','Nov','Dic'],
                dayNames: ['Domingo','Lunes','Martes','Mi&eacute;rcoles','Jueves','Viernes','S&aacute;bado'],
                dayNamesShort: ['Dom','Lun','Mar','Mi&eacute;','Juv','Vie','S&aacute;b'],
                dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','S&aacute;'],
                weekHeader: 'Sm',
                dateFormat: 'dd/mm/yy',
                firstDay: 1,
                isRTL: false,
                showMonthAfterYear: false,
                yearSuffix: ''};
            $.datepicker.setDefaults($.datepicker.regional['es']);
        });

        $(document).ready(function() {
            $("#datepicker").datepicker({
                minDate: new Date(2015, 5, 1),
                maxDate: new Date(2017, 9, 30),
                dateFormat: 'yy-mm-dd',
                constrainInput: true,
            });
            $("#datepicker2").datepicker({
                minDate: new Date(2015, 5, 1),
                maxDate: new Date(2017, 9, 30),
                dateFormat: 'yy-mm-dd',
                constrainInput: true,
            });
            $('#filtrar').click(function(){

                var dataString = $('#form').serialize();
                $.ajax({
                    type: "POST",
                    url: "statistics.jsp",
                    data: dataString,
                    success: function(data) {

                    }
                });
            });
        });
    </script>
</head>
<body>
<div class="container-full">
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">ZaraTech BUS</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li><a href="/single">Single URL Shortener</a></li>
                    <li><a href="/multi">Multi URL Shortener</a></li>
                </ul>

                <ul class="nav navbar-nav navbar-right">
                    <li><a href="#">Contact</a></li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>
    <div class="row">
        <form action="" class="form-inline" id="form">
            <div class="form-group">
                <label class="control-label col-xs-3"> Desde</label>
                <div class="col-xs-9">
                    <input type="text" name="datepicker" id="datepicker" readonly="readonly" size="12" />
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-xs-3"> Hasta</label>
                <div class="col-xs-9">
                    <input type="text" name="datepicker" id="datepicker2" readonly="readonly" size="12" />
                </div>
            </div>
            <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                    <input type="submit" class="btn btn-primary" id="filtrar" value="Filtrar">
                </div>
            </div>
        </form>
        <table class="table">
            <tr>
                <th>&nbsp Browser&nbsp</th>
                <th>&nbsp Version&nbsp</th>
                <th>&nbsp OS&nbsp</th>
                <th>&nbsp Date&nbsp</th>
            </tr>
            <c:forEach items="${clicks}" var="click">
                <tr>
                    <td>&nbsp${click.browser}</td>
                    <td>&nbsp${click.version}</td>
                    <td>&nbsp${click.os}</td>
                    <td>&nbsp${click.created}</td>
                </tr>
            </c:forEach>
        </table>
        <h1>${browser}</h1>
    </div>
    <body>

    </body>
</div>
</body>
</html>