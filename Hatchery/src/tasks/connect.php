<?php

  header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
  header("Expires: Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past

  session_start();

  if($_SESSION["granted"] !== "1") {
    header("Location: index.php");
    exit;
  }

  header('Content-type: application/json');

  // ini_set('error_reporting', E_ALL);
  // error_reporting(E_ALL);
  // ini_set("display_errors", 1);

  require_once "credentials.php";

  $framework_configuration_db_host = "sql";
  $framework_configuration_db_user = $DB_USER;
  $framework_configuration_db_password = $DB_PASSWORD;
  $framework_configuration_db_select = $DB_USER;
  $framework_configuration_table_prefix = "tasks_";

  $action = (isset($_POST["action"]) ? $_POST["action"] : false);
  if(!$action || $action === "version") {

    $version = "";
    $version->version = "JsonComponents v0.0.2";

    echo json_encode($version);
    exit;
  } // end $action === "version"

  global $framework_database_connection;

  $framework_database_connection = mysql_connect(
    $framework_configuration_db_host,
    $framework_configuration_db_user,
    $framework_configuration_db_password);

  mysql_select_db(
    $framework_configuration_db_select,
    $framework_database_connection);

  mysql_query(
    "SET NAMES utf8",
    $framework_database_connection);

//  mysql_query(
//    "SET CHARACTER SET utf8",
//    $framework_database_connection);

  function framework_safe_query(){
    $numargs = func_num_args();
    if($numargs < 1){
      die("Query argument is missing!");
    }

    $arg_list = func_get_args();
    $query_string = $arg_list[0];

    return framework_safe_query_explicit_params($query_string, $numargs, $arg_list);
  } // end of function framework_safe_query

  function framework_safe_query_explicit_params($query_string, $numargs, $arg_list){
    global $framework_database_connection;
    global $framework_configuration_table_prefix;

    $query_string = str_replace("[x]", $framework_configuration_table_prefix,
        $query_string);

    $matches = array();
    preg_match_all("/{[0-9]+}/", $query_string, $matches, PREG_OFFSET_CAPTURE);

    $query = "";
    $last_pos = 0;

    // insert query arguments
    foreach($matches as $subarray){
      foreach($subarray as $param){

        // cut until next expression
        $index = $param[1];
        if($last_pos < $index){
          $query .= substr($query_string, $last_pos, $index - $last_pos);
        }

        // get argument index
        $expression = $param[0];
        $arg = intval(substr($expression, 1, strlen($expression) - 2)) + 1;
        if($arg >= $numargs){
          die("Query has missing arguments! ($query_string)");
        }

        // safety transform of argument value
        $value = $arg_list[$arg];
        $answer = "null";
        if($value === false){
          $answer = "'0'";
        } else if($value === true){
          $answer = "'1'";
        } else if($value === true){
          $answer = "'1'";
        } else if($value !== NULL) {
          $answer = "'" . str_replace(array("\\", "'"), array("\\\\", "\\'"), strval($value)) . "'";
        }

        // add argument to query
        $query .= $answer;

        // updateing last position
        $last_pos = $index + strlen($expression);

      } // end foreach $param
    } // end foreach

    // add end of query
    $len = strlen($query_string);
    if($last_pos < $len){
      $query .= substr($query_string, $last_pos);
    }

    $reply = mysql_query($query, $framework_database_connection);

    // report error if exists
    $error_message = mysql_error();
    if($error_message) {
      die("Error in query:<br/>\n($error_message)<br/>\n$query");
    }

    return $reply;
  } // end of function

  function framework_get_single_answer($reply) {
    $ret = false;
    if($result = mysql_fetch_row($reply)) {
      $ret = $result[0];
    }

    return $ret;
  }

  // collects all available pages, and returns as one big JSON object
  if($action == "list") {
    $response = "";
    $response->list = array();

    $reply = framework_safe_query("select url, sequence, title, body from [x]pages");
    while($result = mysql_fetch_row($reply)) {
      $page = "";
      $page->url = $result[0];
      $page->sequence = $result[1];
      $page->title = $result[2];
      $page->body = $result[3];
      $response->list[] = $page;
    }

    echo json_encode($response);
    exit;
  } // end $action === "list"

  // creates a new page, and returns the result
  if($action == "new") {
    $url = "" . time();
    $sequence = -1;
    $title = "Add title...";
    $body = "Write article here...";

    framework_safe_query("insert into [x]main (url, sequence, title, body) values ({0}, {1}, {2}, {3})", $url, $sequence, $title, $body);

    $response = "";

    $reply = framework_safe_query("select url, sequence, title, body from [x]pages where url={0}", $url);
    if($result = mysql_fetch_row($reply)) {
      $response->url = $result[0];
      $response->sequence = $result[1];
      $response->title = $result[2];
      $response->body = $result[3];
    } else {
      $response->error = "Could not create entry!";
    }

    echo json_encode($response);
    exit;
  } // end $action === "new"

  // gets a page, saves it, and returns the current data
  if($action == "save") {
    $url = $_POST["url"];
    $sequence = $_POST["sequence"];
    $title = $_POST["title"];
    $body = $_POST["body"];

    framework_safe_query("update [x]pages set sequence={1}, title={2}, body={3} where url={0}", $url, $sequence, $title, $body);

    $response = "";

    $reply = framework_safe_query("select url, sequence, title, body from [x]pages where url={0}", $url);
    if($result = mysql_fetch_row($reply)) {
      $response->url = $result[0];
      $response->sequence = $result[1];
      $response->title = $result[2];
      $response->body = $result[3];
    } else {
      $response->error = "Could get entry when returning result!";
    }

    echo json_encode($response);
    exit;
  } // end $action === "save"

  // loads one specific page
  if($action == "load") {
    $soft_lock = date("Y-m-d H:i:s") . ' # ' . rand();
    $soft_lock_limit = date("Y-m-d H:i:s", (time()-60));
    $response = "";

    $reply = framework_safe_query("select task_id, task_name, source, result, soft_lock from [x]main where result is null and (soft_lock is null or soft_lock < {0}) limit 1", $soft_lock_limit);
    if($result = mysql_fetch_row($reply)) {
      $response->action = "task";
      $response->id = $result[0];
      $response->name = $result[1];
      $response->script = $result[2];
      $response->result = $result[3];
      $response->soft_lock = $result[4];
    } else {
      $response->action = "nothing";
    }

    /* TEST
    $response->action = "task";
    $response->script = "(function() { console.log('hi!'); return 'hi';})();";
    */

    echo json_encode($response);
    exit;
  } // end $action === "load"

  // delete one specific page
  if($action == "delete") {
    $url = $_POST["url"];
    $response = "";

    $reply = framework_safe_query("delete from [x]pages where url={0}", $url);
    $response->message = "ok";

    echo json_encode($response);
    exit;
  } // end $action === "delete"

?>