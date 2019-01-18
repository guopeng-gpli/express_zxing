

<?php
$con=mysqli_connect("localhost","root","","cae");
// 检测连接
if (mysqli_connect_errno())
{
    echo "mysqli_connect_errno " . mysqli_connect_error();
}



$tel='0';
$tel=$_GET["tel"];

$pwd='0';
$pwd=$_GET["pwd"];

$type='0';
$type=$_GET["type"];

$res_login='0';
$pwd_in_db='0';
if (strcmp($type, 'courier')) {
    //不相等，即是用户或者管理员
    if (strcmp($type, 'admin')) {
    //不相等，是用户
          $result = mysqli_query($con,"SELECT pwd FROM userinfo WHERE tel='$tel'");

        while($row = mysqli_fetch_array($result))
        {
        $pwd_in_db=$row['pwd'];
        }
    } else {
        //相等，是管理员

        $result = mysqli_query($con,"SELECT pwd FROM admin WHERE tel='$tel'");

        while($row = mysqli_fetch_array($result))
        {
        $pwd_in_db=$row['pwd'];
        }
    }
    
  
} 
else {
    $result = mysqli_query($con,"SELECT pwd FROM courier WHERE tel='$tel'");

    while($row = mysqli_fetch_array($result))
    {
    $pwd_in_db=$row['pwd'];

    }
}


if (password_verify($pwd,$pwd_in_db)) {
    $res_login='1';

echo $res_login;

} else { 
echo $res_login;
}





// if (password_verify($pwd,'$2y$10$6NGa5S46HZeQwDP3rrRo5OANfouCiiJ9HHxKErzQGRFlYqY8e9nDm')) {
// echo "密码正确";
// } else { 
// echo "密码错误";
// }

?>