<?php 
    header('Access-Control-Allow-Origin: *');
    header("Access-Control-Allow-Credentials: true");
    header('Access-Control-Allow-Methods: GET,  POST');

    $udid = trim($_REQUEST['udid']);
    $mobile = trim($_REQUEST['mobile']);

    $con = mysql_connect("localhost","root","Atomix@123$%^");
    mysql_select_db("fh4c2dv5yu" , $con);

    $sql = "select * from users where ud_id='". $udid ."'  order by id desc limit 1";
    $result = mysql_query($sql);   
    $count =  mysql_num_rows($result);
   
     if($count > 0 ) {        
        showData($udid); 	
     }else{       
        $last_user = mysql_query("select max(id) from users");
        $last_user_res = mysql_fetch_row($last_user);
        $last_id = $last_user_res[0]+1;
        $user_agent = $_SERVER['HTTP_USER_AGENT'];
        $ip = $_SERVER['REMOTE_ADDR'];
        $client_id = 'U-'.$last_id;
       



        $date = date('Y-m-d H:i:s');

         $new_user = "INSERT INTO `users`( `client_id`, `user_agent`, `adcode`, `memo`,
           `status`, `ip_address`, `http_refer`, `created_at`, `updated_at`, `ud_id`, `password`, `flag_status`,`mobile`)
         VALUES ('$client_id' , '$user_agent' , 'test' , '' , 1 , '$ip' , '' , '$date' , '$date' , '$udid' , '' , 0,'$mobile')";    
        mysql_query($new_user);   
        showData($udid);    
   }

   function showData($udid)
   {
     $sql = "select * from users where ud_id='". $udid ."'  order by id desc limit 1";
     $result = mysql_query($sql);                
     $row = mysql_fetch_array($result);


     $strtel = mysql_query("select * from setting_telephone where id=1");
     $rows = mysql_fetch_array($strtel); 


     $data[] = array(
          'id' => $row['id'] ,
          'client_id' => $row['client_id'] ,
          'user_agent' => $row['user_agent'] ,
          'adcode' => $row['adcode'] ,
          'status' => $row['status'] ,
          'ip_address' => $row['ip_address'] ,
          'ud_id' => $row['ud_id'] ,
          'mobile' => $row['mobile'] ,
          'flag_status' => $row['flag_status'] ,
          'created_at' => $row['created_at'] ,
          'telephone' => $rows['telephone'] ,
          'telset1' => $rows['telset1'] 
     );


     header('Content-type: application/json');     
      echo json_encode($data);
   }

?>
