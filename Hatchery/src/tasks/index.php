<?php

  header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
  header("Expires: Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past

  require_once "credentials.php";

  session_start();

  if(isset($_POST["tasks-user"]) && isset($_POST["tasks-pass"]) && $_POST["tasks-user"] == $APP_USER && $_POST["tasks-pass"] == $APP_PASSWORD) {
    $_SESSION["granted"] = "1";
    $_SESSION["user"] = "csega";
  }

  if(isset($_GET["action"]) && $_GET["action"] == "logout") {
    $_SESSION["granted"] = "0";
    $_SESSION["user"] = "";
    header("Location: index.php");
    exit;
  }

  if($_SESSION["granted"] === "1") {
    header("Location: tasks.php");
    exit;
  }

?><!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Tasks</title>
</head>
<body>

  <form method="post" style="margin: 10xp; padding: 20px; border: solid 2px #000000; background: #F0F0F0; width: 200px;">
    Username: <input name="tasks-user" /><br/>
    Password: <input name="tasks-pass" type="password" /><br/>
    <input type="submit" value="Login" />
  </form>

</body>
</html>