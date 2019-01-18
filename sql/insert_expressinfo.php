

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

$id='0';
$id=$_GET["id"];

$send_tel='0';
$send_tel=$_GET["send_tel"];

$send_name='0';
//$send_name=$_GET["send_name"];

$rec_tel='0';
$rec_tel=$_GET["rec_tel"];

$rec_name='0';
$rec_name=$_GET["rec_name"];

$address='0';
$address=$_GET["address"];

$flag='0';
$flag=$_GET["flag"];

$result = mysqli_query($conn,"SELECT name FROM userinfo WHERE tel='$send_tel'");
// echo $result;
while($row = mysqli_fetch_array($result))
{
  //$res["name"]=$row['name'];
//$res["num"]=$row['num'];
$send_name=$row['name'];
//echo json_encode($res);

}



//$hash = password_hash($pwd, PASSWORD_DEFAULT);
///echo $hash;
//$name1 = mb_convert_encoding($name, "UTF-8", "GBK");
$send_name1=characet($send_name);
$rec_name1=characet($rec_name);
$address1=characet($address);
$flag1=characet($flag);
$recd=characet("未派送");
$sql = "INSERT INTO express (id,send_tel,send_name,rec_tel,rec_name,address,flag,recd)
VALUES ('$id','$send_tel','$send_name1','$rec_tel','$rec_name1','$address1','$flag1','$recd');";


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



include 'phpqrcode.php';  //引入phpqrcode类文件
include 'aes.php';
//单号没有加密，是谁派件根本就不会显示出来
//$value = '发件人手机号码：'.$send_tel.PHP_EOL.'发件人姓名：'.$send_name1.PHP_EOL.'收件人手机号码：'.$rec_tel.PHP_EOL.'收件人姓名:'.$rec_name1.PHP_EOL.'收件人地址:'.$address1;
//$value = $num.$openid; //二维码内容
$value = '发件人手机号码：'.$send_tel.'啊'.'发件人姓名：'.$send_name1.'啊'.'收件人手机号码：'.$rec_tel.'啊'.'收件人姓名:'.$rec_name1.'啊'.'收件人地址:'.$address1;
//echo $value;
$id2='快递单号：'.$id.PHP_EOL;

$value1 = $value; 
//echo $value1;
$key = 'www.helloweba.com'; 
$token = encrypt($value1, 'E', $key); 
//echo $token;
$token=$id2.$token;
//echo '加密:'.encrypt($str, 'E', $key); 
//echo '解密：'.encrypt($token, 'D', $key);




$errorCorrectionLevel = 'H';//容错级别

$matrixPointSize = 4;//生成图片大小

//生成二维码图片
//二维码中存的是快递单号：00000000+密文
QRcode::png($token, 'qrcode.png', $errorCorrectionLevel, $matrixPointSize, 2);

$logo = 'logo.png';//准备好的logo图片  需要加入到二维码中的logo

$QR = 'qrcode.png';//已经生成的原始二维码图



if ($logo !== FALSE) {

    $QR = imagecreatefromstring(file_get_contents($QR));

    $logo = imagecreatefromstring(file_get_contents($logo));

    $QR_width = imagesx($QR);//二维码图片宽度

    $QR_height = imagesy($QR);//二维码图片高度

    $logo_width = imagesx($logo);//logo图片宽度

    $logo_height = imagesy($logo);//logo图片高度

    $logo_qr_width = $QR_width / 5;

    $scale = $logo_width/$logo_qr_width;

    $logo_qr_height = $logo_height/$scale;

    $from_width = ($QR_width - $logo_qr_width) / 2;

//重新组合图片并调整大小

    imagecopyresampled($QR, $logo, $from_width, $from_width, 0, 0, $logo_qr_width,

        $logo_qr_height, $logo_width, $logo_height);

}

//输出图片

imagepng($QR, "$id.png");

//echo '<img src="$id.png">';





?>