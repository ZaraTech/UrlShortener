<!DOCTYPE html>
<html>
<head>
    <title>URL Shortener</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css"
    href="../webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
<script type="text/javascript" src="../webjars/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript"
    src="../webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
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
						<li class="active"><a href="/multi">Multi URL Shortener<span class="sr-only">(current)</span></a></li>
					</ul>

					<ul class="nav navbar-nav navbar-right">
						<li><a href="#">Contact</a></li>
					</ul>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>
		<div class="row">
			<div class="col-lg-12 text-center">
				<h1>Welcome to BUS</h1>
				<p class="lead">The Best URL Shortener</p>
				<br>
				<p>
					This is the details page for the id ${id}
				</p>
				<p>
					 Date Creation ${date}
				</p>
				<p>
					URI destination ${target} with ${clicks} clicks and ${visitors} unique visitors
				</p>
			</div>
		</div>
	</div>
</body>
</html>
