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

class AttackController extends Controller {

    public function uploadPic(Request $request, $attackId, $picIndex) {
        if ($request->isMethod('post')){
            $ret = DB::select('SELECT * FROM pic_info WHERE attack_id=?', [$attackId]);

            if (sizeof($ret) != 0 && $picIndex == 0) {
                    Storage::disk('attack')->deleteDirectory($attackId);
                    DB::delete('DELETE FROM pic_info WHERE attack_id=?', [$attackId]);
            }

            if (!$request->hasFile('picFile')){
                return '{"code":404, "msg":"Picture file is not found"}';
            }
            $file = $request->file('picFile');
            if ($file->isValid()) {
                $ext = $file->getClientOriginalExtension();
                if (strcmp($ext,'jpg') && strcmp($ext,'png') && strcmp($ext,'jpeg')){
                    return '{"code":300, "msg":"The file is not picture"}';
                }

                $real_path = $file->getRealPath();
                $fileName = $attackId.'/'.$picIndex.".".$ext;
                $life_time = date('Y-m-d H:i:s');
                DB::insert('INSERT INTO pic_info VALUES  (?,?,?,?,?)', [$attackId, $picIndex, $real_path, null, $life_time]);
                Storage::disk('attack')->delete($fileName);
                $bool = Storage::disk('attack')->put($fileName, file_get_contents($real_path));
                system("python ./storage/attack/getQRCode1.py $attackId $picIndex");
                system("python ./storage/attack/getQRCode2.py $attackId $picIndex");
                ob_clean();
                $ret = DB::select('SELECT qrcode FROM pic_info WHERE attack_id=? AND pic_index=?', [$attackId, $picIndex]);
                $qrcode = array_column($ret, 'qrcode')[0];
                if ($qrcode == null)
                    return '{"code":200, "msg":"Upload succeed"}';
                else
                    return '{"code":200, "msg":"Getting qrcode succeed"}';
            } else {
                return '{"code":500, "msg":"Upload failed"}';
            }
        }
        return '{"code":520, "msg":"Love U"}';
    }

    public function getAttackIdList(Request $request) {
        $now_time = date('Y-m-d H:i:s');
        $before_time = date('Y-m-d H:i:s', strtotime('-3 minutes'));
       $this->deleteOldPicture($before_time);

        $ret = DB::select('SELECT attack_id FROM pic_info WHERE life_time<=? AND life_time>=?', [$now_time, $before_time]);
        $arr = array_column($ret, 'attack_id');
        $arr = array_unique($arr);
        $list = '{"size":'.sizeof($arr).',"attack_id":';
        $arr_attack_id = array();
        foreach ($arr as $key=>$value) {
            array_push($arr_attack_id, $value);
        }
        $list .=json_encode($arr_attack_id);
        $list .='}';

        return $list;
    }

    public function getQRCodeList(Request $request, $attackId) {
        $now_time = date('Y-m-d H:i:s');
        $before_time = date('Y-m-d H:i:s', strtotime('-3 minutes'));
       $this->deleteOldPicture();

        $ret = DB::select('SELECT qrcode FROM pic_info WHERE attack_id=? AND life_time<=? AND life_time>=?',
            [$attackId, $now_time, $before_time]);
        $arr = array_column($ret, 'qrcode');
        $arr = array_unique($arr);
        $arr = array_filter($arr);
        $list = '{"size":'.sizeof($arr).',"qrcode":';
        $arr_qrcode = array();
        foreach ($arr as $key=>$value) {
            array_push($arr_qrcode, $value);
        }
        $list .=json_encode($arr_qrcode);
        $list .='}';

        return $list;
    }

    private function deleteOldPicture() {
        $before_time = date('Y-m-d H:i:s', strtotime('-5 minutes'));

        $ret = DB::select('SELECT * FROM pic_info WHERE life_time<?', [$before_time]);
        for ($i=0; $i<sizeof($ret); $i++) {
            $attack_id = array_column($ret, 'attack_id')[$i];
            $pic_index = array_column($ret, 'pic_index')[$i];
            Storage::disk('attack')->delete($attack_id.$pic_index.'.jpg');
            DB::delete('DELETE FROM pic_info WHERE attack_id=? AND pic_index=?', [$attack_id, $pic_index]);
        }
    }

}
