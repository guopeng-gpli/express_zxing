

<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "cae";
//插入电话，姓名，密码（hash）
// ´´½¨Á¬½Ó
$conn = new mysqli($servername, $username, $password, $dbname);
// ¼ì²âÁ¬½Ó
if ($conn->connect_error) {
    die("connect_error " . $conn->connect_error);
} 

function characet($data){
  if( !empty($data) ){
    $fileType = mb_detect_encoding($data , array('UTF-8','GBK','LATIN1','BIG5')) ;
    if( $fileType != 'UTF-8'){
      $data = mb_convert_encoding($data ,'utf-8' , $fileType);
    }
  }
  return $data;
}

$tel='0';
$tel=$_GET["tel"];
$name='0';
$name=$_GET["name"];
$pwd='0';
$pwd=$_GET["pwd"];

$hash = password_hash($pwd, PASSWORD_DEFAULT);
///echo $hash;
//$name1 = mb_convert_encoding($name, "UTF-8", "GBK");
$name1=characet($name);
$sql = "INSERT INTO userinfo (tel,name,pwd)
VALUES ('$tel','$name1','$hash');";


// if (password_verify($pwd,'$2y$10$6NGa5S46HZeQwDP3rrRo5OANfouCiiJ9HHxKErzQGRFlYqY8e9nDm')) {
// echo "密码正确";
// } else { 
// echo "密码错误";
// }

if ($conn->multi_query($sql) === TRUE) {
    echo "insert_successful";
} else {
    echo "insert_Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>