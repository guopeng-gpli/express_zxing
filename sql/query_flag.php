<?php
$con=mysqli_connect("localhost","root","","cae");
// 检测连接
if (mysqli_connect_errno())
{
    echo "mysqli_connect_errno " . mysqli_connect_error();
}
$id='0';
$id=$_GET["id"];

$self_tel='0';
$self_tel=$_GET["self_tel"];

$en='0';
$en=$_GET["en"];

$de='0';
$flag='0';
$recd='0';
//echo $car_num;
$result = mysqli_query($con,"SELECT flag,recd FROM express WHERE id='$id'");
// echo $result;
while($row = mysqli_fetch_array($result))
{
	//$res["name"]=$row['name'];
//$res["num"]=$row['num'];
$flag=$row['flag'];
$recd=$row['recd'];
//echo json_encode($res);

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

$weifenpei="未分配";
$weifenpei1=characet($weifenpei);
$recd1=characet("已派送");

if (strcmp($recd1, $recd)) {
	//没派送
	if (strcmp($flag, $self_tel)) {
	//不相等
	if (strcmp($flag ,$weifenpei1)) {
		echo "该件不由您派送";
	} else {
		
		echo "该件暂时未分配快递员";
	}
	
	
} else {
	//相等，是自己的件，把密文解密

include 'aes.php';

$key = 'www.helloweba.com'; 
//echo $en;
$token = encrypt($en, 'D', $key); 
$de='快递单号：'.$id.'啊'.$token;
//echo $de;
//echo $de;
//$de=str_replace("啊", "\n", $de);
echo $de;
//echo '加密:'.encrypt($str, 'E', $key); 
//echo '解密：'.encrypt($token, 'D', $key);

}
} else {
	//派送了
	echo "该件已被派送";
}

//int strcmp(string str1,string str2)               //区分字符串中字母大小写地比较,相等则返回0




?>