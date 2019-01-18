


<?php

//插入电话，姓名，密码（hash）
// ´´½¨Á¬½Ó
$con=mysqli_connect("localhost","root","","cae");
// 检测连接
if (mysqli_connect_errno())
{
    echo "mysqli_connect_errno " . mysqli_connect_error();
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

$expressid='0';
$expressid=$_GET["id"];
$recd=characet("已派送");

$result = mysqli_query($con,"UPDATE express SET recd='$recd' WHERE id =  '$expressid'");

// echo $result;
echo "insert_successful";


// if (password_verify($pwd,'$2y$10$6NGa5S46HZeQwDP3rrRo5OANfouCiiJ9HHxKErzQGRFlYqY8e9nDm')) {
// echo "密码正确";
// } else { 
// echo "密码错误";
// }

?>









