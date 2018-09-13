<?php
/*
 * Created by PhpStorm.
 * User: Jiacy-PC
 * Date: 2018/4/8
 * Time: 17:28
 */

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\DB;
use MongoDB\BSON\Timestamp;

class SecPayController extends Controller {

    public function loginForCustomer(Request $request) {
        $usr_id = $request->input('usr_id');
        $password = $request->input('password');
        $ret = DB::select('SELECT * FROM pay_login WHERE usr_id=?', [$usr_id]);

        if (sizeof($ret) == 0) {
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 400;
            $arr['msg'] = 'usr_id error!';
            $arr['token'] = '-1';
            return json_encode($arr);
        }
        $is_normal_usr = array_column($ret, 'is_normal_usr')[0];

        if (sizeof($ret)!=0 && $is_normal_usr) {
            // 帐号正确，普通用户
            $md5password = array_column($ret, 'md5passwd')[0];
            $password = md5($password);

            if (strcmp($password, $md5password)==0) {
                // 密码正确
                $ip = $request->getClientIp();
                $time = time();
                $token = md5($md5password.$ip.$time);

                DB::update('UPDATE pay_login SET token=?, ip=? WHERE usr_id=?', [$token, $ip, $usr_id]);
                $arr = array();
                $arr['usr_id'] = $usr_id;
                $arr['code'] = 200;
                $arr['msg'] = 'login succeed!';
                $arr['token'] = $token;
                return json_encode($arr);
            } else {
                // 密码错误，普通用户
                $arr = array();
                $arr['usr_id'] = $usr_id;
                $arr['code'] = 300;
                $arr['msg'] = 'password error!';
                $arr['token'] = '-1';
                return json_encode($arr);
            }

        } else if ($is_normal_usr) {
            // 帐号错误
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 400;
            $arr['msg'] = 'usr_id error!';
            $arr['token'] = '-1';
            return json_encode($arr);
        } else {
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 404;
            $arr['msg'] = 'usr_id error!';
            $arr['token'] = '-1';
            return json_encode($arr);
        }
    }

    public function loginForMerchant(Request $request) {
        $usr_id = $request->input('usr_id');
        $password = $request->input('password');
        $ret = DB::select('SELECT * FROM pay_login WHERE usr_id=?', [$usr_id]);

        if (sizeof($ret) == 0) {
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 400;
            $arr['msg'] = 'usr_id error!';
            $arr['token'] = '-1';
            return json_encode($arr);
        }
        $is_normal_usr = array_column($ret, 'is_normal_usr')[0];

        if (sizeof($ret)!=0 && !$is_normal_usr) {
            // 帐号正确，商家用户
            $md5password = array_column($ret, 'md5passwd')[0];
            $password = md5($password);

            if (strcmp($password, $md5password)==0) {
                // 密码正确
                $ip = $request->getClientIp();
                $time = time();
                $token = md5($md5password.$ip.$time);

                DB::update('UPDATE pay_login SET token=?, ip=? WHERE usr_id=?', [$token, $ip, $usr_id]);
                $arr = array();
                $arr['usr_id'] = $usr_id;
                $arr['code'] = 200;
                $arr['msg'] = 'login succeed!';
                $arr['token'] = $token;
                return json_encode($arr);
            } else {
                // 密码错误，商家用户
                $arr = array();
                $arr['usr_id'] = $usr_id;
                $arr['code'] = 300;
                $arr['msg'] = 'password error!';
                $arr['token'] = '-1';
                return json_encode($arr);
            }

        } else if (!$is_normal_usr) {
            // 帐号错误
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 400;
            $arr['msg'] = 'usr_id error!';
            $arr['token'] = '-1';
            return json_encode($arr);
        } else {
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 404;
            $arr['msg'] = 'usr_id error!';
            $arr['token'] = '-1';
            return json_encode($arr);
        }
    }



    public function logout(Request $request) {
        $usr_id = $request->input('usr_id');
        $token = $request->input('token');
        $ret = DB::select('SELECT * FROM pay_login WHERE usr_id=? AND token=?', [$usr_id, $token]);

        if (sizeof($ret)!=0) {
            // 帐号正确
            DB::update('UPDATE pay_login SET token=?, ip=? WHERE usr_id=?', [null, null, $usr_id]);
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 200;
            $arr['msg'] = 'logout succeed!';
            return json_encode($arr);
        } else {
            // 帐号错误
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 404;
            $arr['msg'] = 'logout error!';
            return json_encode($arr);
        }
    }

    public function getUsrInfo(Request $request) {
        $usr_id = $request->input('usr_id');
        $token = $request->input('token');
        $ret = DB::select('SELECT * FROM pay_login WHERE usr_id=? AND token=?', [$usr_id, $token]);

        if (sizeof($ret)!=0) {
            // 帐号正确
            $ret = DB::select('SELECT * FROM pay_usr_info WHERE usr_id=?', [$usr_id]);
            $usr_name = array_column($ret, 'usr_name')[0];
            $usr_loc = array_column($ret, 'loc')[0];

            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 200;
            $arr['msg'] = 'getting user information succeed!';
            $arr['usr_name'] = $usr_name;
            $arr['usr_loc'] = $usr_loc;
            return json_encode($arr);
        } else {
            // 帐号错误
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 404;
            $arr['msg'] = 'getting user information error!';
            $arr['usr_name'] = 'usr_name';
            $arr['usr_loc'] = 'usr_loc';
            return json_encode($arr);
        }
    }

    public function pushMsg(Request $request) {
        $usr_id = $request->input('usr_id');
//        $token = $request->input('token');
        $ret = DB::select('SELECT ip FROM pay_login WHERE usr_id=?', [$usr_id]);

        if (sizeof($ret)!=0) {
            // 正确
            $msg = $request->input('msg');
            $loc = $request->input('loc');
            $push_time = $request->input('push_time');
            $ret = DB::select('SELECT max(msg_id) FROM pay_alert_msg WHERE usr_id=?', [$usr_id]);
            $msg_id = array_column($ret, 'max(msg_id)')[0];
            $msg_id++;

            DB::insert('INSERT INTO pay_alert_msg (usr_id, msg_id, msg, loc, push_time) VALUES (?,?,?,?,?)',
                [$usr_id, $msg_id, $msg, $loc, $push_time]);
            $arr = array();
            $arr['code'] = 512;
            $arr['msg'] = 'push msg succeed!';
            return json_encode($arr);
        } else {
            // 错误
            $arr = array();
            $arr['code'] = 404;
            $arr['msg'] = 'push msg error!';
            return json_encode($arr);
        }
    }

    public function pullMsg(Request $request) {
        $usr_id = $request->input('usr_id');
        $token = $request->input('token');
        $ret = DB::select('SELECT ip FROM pay_login WHERE usr_id=? AND token=?', [$usr_id, $token]);

        if (sizeof($ret)!=0) {
            // 正确
            $ret = DB::select('SELECT msg_id,msg,loc,push_time,is_checked FROM pay_alert_msg WHERE usr_id=? AND is_checked=FALSE ', [$usr_id]);
//            $msg = array_column($ret, 'msg')[0];
//            $loc = array_column($ret, 'loc')[0];
//            $push_time = array_column($ret, 'push_time')[0];
//            for ($i=0; $i<sizeof($ret); $i++) {
//                array_column($ret, 'is_checked')[$i] = false;
//            }

            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 200;
            $arr['msg'] = 'pull msg succeed!';
            $arr['content'] = $ret;
            return json_encode($arr);
        } else {
            // 错误
            $arr = array();
            $arr['usr_id'] = $usr_id;
            $arr['code'] = 404;
            $arr['msg'] = 'pull msg error!';
            return json_encode($arr);
        }
    }

    public function checkMsg(Request $request) {
        $usr_id = $request->input('usr_id');
        $token = $request->input('token');
        $ret = DB::select('SELECT ip FROM pay_login WHERE usr_id=? AND token=?', [$usr_id, $token]);

        if (sizeof($ret)!=0) {
            // 正确
            $msg_id = $request->input('msg_id');
            $is_abnormal_pay = $request->input('is_abnormal_pay');

            DB::update("UPDATE pay_alert_msg SET is_checked=?,is_abnormal_pay=? WHERE usr_id=? AND msg_id=?",
                [true, $is_abnormal_pay, $usr_id, $msg_id]);

            $arr = array();
            $arr['code'] = 200;
            $arr['msg'] = 'check msg succeed!';
            return json_encode($arr);
        } else {
            // 错误
            $arr = array();
            $arr['code'] = 404;
            $arr['msg'] = 'check msg error!';
            return json_encode($arr);
        }
    }

    public function getBillList(Request $request) {
        $usr_id = $request->input('usr_id');
        $token = $request->input('token');
        $ret = DB::select('SELECT ip FROM pay_login WHERE usr_id=? AND token=?', [$usr_id, $token]);

        if (sizeof($ret)!=0) {
            // 正确
            $page = $request->input('page');
            $ret = DB::select('SELECT pay_id,pay_content,pay_class,pay_time,pay_amount
                                FROM pay_bill
                                WHERE usr_id=?
                                ORDER BY index_num DESC, pay_time DESC
                                LIMIT ?, 15',
                [$usr_id, ($page-1)*15]);

            $arr = array();
            $arr['code'] = 200;
            $arr['msg'] = 'pull bills succeed!';
            $arr['content'] = $ret;
            return json_encode($arr);
        } else {
            // 错误
            $arr = array();
            $arr['code'] = 404;
            $arr['msg'] = 'pull bills error!';
            return json_encode($arr);
        }
    }

    public function newPayRequest(Request $request) {
        $merchant_usr_id = $request->input('merchant_usr_id');
        $token = $request->input('token');
        $ret = DB::select('SELECT ip FROM pay_login WHERE usr_id=? AND token=? AND is_normal_usr=?', [$merchant_usr_id, $token, false]);

        if (sizeof($ret)!=0) {
            // 正确
            $pay_amount = $request->input('pay_amount');
            $pay_code = $request->input('pay_code');
            $pay_content = $request->input('pay_content');
            $pay_class = $request->input('pay_class');
            $ret = DB::select('SELECT usr_id FROM pay_code WHERE pay_code=?', [$pay_code]);
            if (sizeof($ret) == 0) {
                $arr = array();
                $arr['code'] = 512;
                $arr['msg'] = "pay code error!";
                return json_encode($arr);
            }

            $customer_usr_id = array_column($ret, 'usr_id')[0];
            $ret = DB::select('SELECT usr_money FROM pay_usr_info WHERE usr_id=?', [$customer_usr_id]);
            $customer_usr_money = array_column($ret, 'usr_money')[0];

            $ret = DB::select('SELECT usr_money FROM pay_usr_info WHERE usr_id=?', [$merchant_usr_id]);
            $merchant_usr_money = array_column($ret, 'usr_money')[0];

            if ($customer_usr_money > $pay_amount && $pay_amount >0 && $customer_usr_money>=0) {
                $customer_usr_money -= $pay_amount;
                $merchant_usr_money += $pay_amount;
                DB::update('UPDATE pay_usr_info SET usr_money=? WHERE usr_id=?', [$customer_usr_money, $customer_usr_id]);
                DB::update('UPDATE pay_usr_info SET usr_money=? WHERE usr_id=?', [$merchant_usr_money, $merchant_usr_id]);
                DB::insert('INSERT INTO pay_bill (usr_id,pay_id,pay_content,pay_class,pay_time,pay_amount) VALUES (?,?,?,?,?,?)',
                    [$customer_usr_id, time(), $pay_content, $pay_class, date('Y-m-d H:i:s', strtotime('+8 hours')), $pay_amount]);
                $arr = array();
                $arr['code'] = 200;
                $arr['msg'] = "pay request succeed!";
                return json_encode($arr);
            } else {
                $arr = array();
                $arr['code'] = 300;
                $arr['msg'] = "customer's money is not enough!";
                return json_encode($arr);
            }
        } else {
            // 错误
            $arr = array();
            $arr['code'] = 404;
            $arr['msg'] = 'pay request error!';
            return json_encode($arr);
        }
    }
}
