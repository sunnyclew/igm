package com.igm.igm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.igm.igm.R;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 200;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Context mContext;
    @SuppressWarnings("unused")
    private static final String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //스튜디오 시뮬레이터는 기본적으로 블루투스 기능이 없어서 메인이 실행되지 않는다.
        //실제 device 사용시 주석해제
        canUseBluetooth();
        //실제 device 사용시 주석처리
//        startMain();
    }

    @SuppressWarnings("unused")
    private void canUseBluetooth() {
        if (mBluetoothAdapter == null) {
            showAlert(R.string.bt_err1);
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else {
                startMain();
            }
        }
    }

    @SuppressWarnings("unused")
    private void showMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
         if (resultCode == Activity.RESULT_OK) {
             //활성화 완료
             showConfirm(R.string.bt_on);
         } else {
             //활성화 불가능
             showAlert(R.string.bt_err2);
         }
        }
    }

    private void showAlert(int msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showConfirm(int msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> startMain())
                .show();
    }

    private void startMain() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }
}
