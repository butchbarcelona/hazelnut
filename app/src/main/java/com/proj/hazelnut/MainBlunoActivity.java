package com.proj.hazelnut;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.proj.hazelnut.ble.BlunoLibrary;

public class MainBlunoActivity extends BlunoLibrary implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onCreateProcess();
        serialBegin(115200);
        Button btnRestart1, btnRestart2, btnShutdown1, btnShutdown2;
        btnRestart1 = (Button) findViewById(R.id.btn_restart1);
        btnRestart1.setOnClickListener(this);
        btnShutdown1 = (Button) findViewById(R.id.btn_shutdown1);
        btnShutdown1.setOnClickListener(this);
/*        btnRestart2 = (Button) findViewById(R.id.btn_restart2);
        btnRestart2.setOnClickListener(this);
        btnShutdown2 = (Button) findViewById(R.id.btn_shutdown2);
        btnShutdown2.setOnClickListener(this);*/

    }

    protected void onResume() {
        super.onResume();
        onResumeProcess();                                                        //onResume Process by BlunoLibrary
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
    }

    @Override
    public void onSerialReceived(String bleData) {
        bleData = bleData.trim().toUpperCase();
        Log.e("bluno", "data:"+ bleData);

    }

    @Override
    public void onClick(View v) {

        if(!cannotClick) {
            switch (v.getId()) {
                case R.id.btn_restart1:
                    serialSend("1");
                    break;
                case R.id.btn_shutdown1:
                    serialSend("2");
                    break;
               /* case R.id.btn_restart2:
                    serialSend("3");
                    break;
                case R.id.btn_shutdown2:
                    serialSend("4");
                    break;*/
            }
            startThread();
        }

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main_nav, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.action_bluetooth_connect ) {
            buttonScanOnClickProcess();

            return true;
        }
        if( id == R.id.action_logout ) {
            startActivity(new Intent(MainBlunoActivity.this,LoginActivity.class));
            finish();

            return true;
        }

        return super.onOptionsItemSelected( item );
    }


    boolean cannotClick = false;
    public void startThread(){

        cannotClick = true;
    /*    Thread secondThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sleep(1000);
            }
        });
        secondThread.start();*/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cannotClick = false;
            }
        }, 1000);

    }

}
