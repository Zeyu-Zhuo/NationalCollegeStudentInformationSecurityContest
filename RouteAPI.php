<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/


use Illuminate\Support\Facades\Route;

// 攻击流程API
Route::post('/pic/{attackId}/{picIndex}', ['as'=>'uploadPicUrl', 'uses'=>'AttackController@uploadPic'])
    ->where(['attackId'=>'[a-zA-Z]+[0-9]+', 'picIndex'=>'[0-9]+']);

Route::get('/pic/attack_list', ['as'=>'getAttackIdListUrl', 'uses'=>'AttackController@getAttackIdList']);

Route::get('/pic/{attackId}/qrcode', ['as'=>'loadPicUrl', 'uses'=>'AttackController@getQRCodeList'])
    ->where(['attackId'=>'[a-zA-Z]+[0-9]+']);

// 防护系统API
Route::post('/pay/login', ['as'=>'payLogin', 'uses'=>'SecPayController@loginForCustomer']);

Route::post('/pay/login/merchant', ['as'=>'payLoginForMerchant', 'uses'=>'SecPayController@loginForMerchant']);

Route::post('/pay/logout', ['as'=>'payLogout', 'uses'=>'SecPayController@logout']);

Route::post('/pay/usrInfo', ['as'=>'payUsrInfo', 'uses'=>'SecPayController@getUsrInfo']);

Route::post('/pay/pushMsg', ['as'=>'pushMsg', 'uses'=>'SecPayController@pushMsg']);

Route::post('/pay/pullMsg', ['as'=>'pullMsg', 'uses'=>'SecPayController@pullMsg']);

Route::post('/pay/checkMsg', ['as'=>'checkMsg', 'uses'=>'SecPayController@checkMsg']);

Route::post('/pay/billList', ['as'=>'billList', 'uses'=>'SecPayController@getBillList']);

Route::post('/pay/newPayRequest', ['as'=>'newPayRequest', 'uses'=>'SecPayController@newPayRequest']);
