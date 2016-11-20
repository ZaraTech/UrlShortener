<!DOCTYPE html>
<html>
<head>
<title>URL Shortener</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
<script type="text/javascript" src="webjars/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript"
	src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/app-submit-multi.js">
</script>
</head>
<body>
	<div class="container-full">
		<div class="row">
			<div class="col-lg-12 text-center">
				<h1>Welcome to BUS</h1>
				<p class="lead">The Best URL Shortener</p>
				<br>
				<form class="col-lg-12" role="form" id="shortener" action="">
					<div class="input-group col-sm-offset-4 col-sm-4">
						<textarea type="text" class="form-control custom-control"
							title="Enter a URL" placeholder="Enter a list of URLs (one per line)" name="url" rows="7" style="resize:none"></textarea>
						<span class="input-group-addon"><button
								class="btn btn-lg btn-primary" type="submit">Short me!</button></span>
					</div>
				</form>
			</div>
		</div>
		<div class="row">
			<div class="col-lg-12 text-center">
				<div class="col-sm-offset-4 col-sm-4 text-center">
					<br />
					<div id="result" />
				</div>
			</div>
		</div>
	</div>
</body>
</html>
