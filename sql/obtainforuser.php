<?php
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
// $num='0';
// $num=$_GET["num"];
//echo $car_num;

// $result = mysqli_query($con,"SELECT name,num FROM real_data WHERE num='$num'");
// while($row = mysqli_fetch_array($result))
// {
// 	$res["name"]=$row['name'];
// $res["num"]=$row['num'];

// echo json_encode($res);
// }
/** 
     * 将数组里面带有中文的字串保留以JSON格式返回 
     * 
     * @param   array $arr  数组 
     * @return  string JSON格式的字符串 
     */  
  
 function toJson($arr)  
    {  
          
        $ajax = ToUrlencode($arr);  
        $str_json = json_encode($ajax);  
        return urldecode($str_json);  
    }  
  
    /** 
     * 将数组里面带有中文的字串用urlencode转换格式返回 
     * 
     * @param   array $arr  数组 
     * @return  array 
     */  
 function ToUrlencode($arr)  
    {  
  
        $temp = array();  
        if (is_array($arr))  
        {  
            foreach ($arr AS $key => $row)  
            {  
                $temp[$key] = $row;  
                if (is_array($temp[$key]))  
                {  
                    $temp[$key] = ToUrlencode($temp[$key]);  
                }  
                else  
                {  
                    $temp[$key] = urlencode($row);  
                }  
            }  
        }  
        else  
        {  
            $temp = $arr;  
        }  
        return $temp;  
    }  
    
    // $arr = array('我的wod','我的wod','我的wod');
    // echo json_encode($arr);
    // echo "\n";
    // echo toJson($arr);
    // echo "\n";
$user='0';
$user=$_GET["user"];
$recd='0';
$time='0';

$result=mysqli_query($con,"SELECT * from express WHERE send_tel='$user'");






$arr = array(); 

while($row=mysqli_fetch_array($result))
{

	$res["id"]=$row['id'];
	$res["send_tel"]=$row['send_tel'];
	$res["send_name"]=$row['send_name'];
	$res["rec_tel"]=$row['rec_tel'];
	$res["rec_name"]=$row['rec_name'];
	$res["address"]=$row['address'];
	$res["flag"]=$row['flag'];
    $res["recd"]=$row['recd'];
     
    if (strcmp($res["recd"],characet("未派送") )) {
        //不相等
 $res["time"]=$row['time'];
    } else {
        //相等
 $res["time"]=characet("未派送");

    }
    
  
	array_push($arr,$res); 
}

    json_encode($arr);
	echo toJson($arr);
mysqli_close($con); 

?>