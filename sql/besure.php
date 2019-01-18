


<?php

//插入电话，姓名，密码（hash）
// ´´½¨Á¬½Ó
$con=mysqli_connect("localhost","root","","cae");
// 检测连接
if (mysqli_connect_errno())
{
    echo "mysqli_connect_errno " . mysqli_connect_error();
}


$couriertel='0';
$couriertel=$_GET["couriertel"];

$expressid='0';
$expressid=$_GET["expressid"];


$result = mysqli_query($con,"UPDATE express SET flag='$couriertel' WHERE id =  '$expressid'");

// echo $result;
echo "insert_successful";


// if (password_verify($pwd,'$2y$10$6NGa5S46HZeQwDP3rrRo5OANfouCiiJ9HHxKErzQGRFlYqY8e9nDm')) {
// echo "密码正确";
// } else { 
// echo "密码错误";
// }

?>









