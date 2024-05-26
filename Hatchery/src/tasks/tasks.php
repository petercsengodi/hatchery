<?php
  header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
  header("Expires: Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past

  session_start();

  if($_SESSION["granted"] !== "1") {
    header("Location: index.php");
    exit;
  }

?><!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Tasks</title>
</head>
<body>

<div style="width: 60px; position: absolute; top: 10px; right: 20px; background: #F0F0F0;">
  <button style="width: 60px;" onclick="if(confirm('Are you sure?')) { window.location = 'index.php?action=logout'; }">Logout</button>
</div>

<script type="text/javascript">

	async function doSomething() {
        const connectUrl = `https://csega.hu/csega/tasks/connect.php`;

        var payload = {
            'action': `load`
        };

        var formBody = [];
        for (var property in payload) {
            var encodedKey = encodeURIComponent(property);
            var encodedValue = encodeURIComponent(payload[property]);
            formBody.push(encodedKey + "=" + encodedValue);
        }

        formBody = formBody.join("&");

        const connectResponse = await fetch(connectUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json",
            },
            body: formBody,
            redirect: "manual"
        });

	    const responseJSON = await connectResponse.json();
	    var action = responseJSON["action"];
	    console.log("action: " + action);

	    if(action == 'task') {
	        var script = responseJSON["script"];
	        console.log("script: " + script);

	        var result = JSON.stringify(eval(script));
	        console.log("result: " + result);

            var payload = {
                'action': `save`,
                'result': result
            };

            var formBody = [];
            for (var property in payload) {
                var encodedKey = encodeURIComponent(property);
                var encodedValue = encodeURIComponent(payload[property]);
                formBody.push(encodedKey + "=" + encodedValue);
            }

            formBody = formBody.join("&");

            const connectResponse = await fetch(connectUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Accept": "application/json",
                },
                body: formBody,
                redirect: "manual"
            });
	    }
	}

    setInterval(doSomething, 5000);

</script>
</body>
</html>