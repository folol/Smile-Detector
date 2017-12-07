package com.example.devansh.sampleorai;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.glomadrian.grav.GravView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int CAMERA_RESULT = 1;
    TextView mButton;
    TextView mStatus;

  //  GravView gravView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatus = (TextView) findViewById(R.id.am_status);
      //  gravView = (GravView) findViewById(R.id.grav);
        mStatus.setVisibility(View.GONE);
        mButton = (TextView) findViewById(R.id.am_btn);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
       // gravView.start();
    }

    @Override
    public void onStop(){
        super.onStop();
       // gravView.stop();
    }



    @Override
    public void onClick(View view){
        int id = view.getId();
        if(id == R.id.am_btn){
            startActivityForResult(new Intent(this,RecordActivity.class),CAMERA_RESULT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_RESULT){
                String msg = data.getStringExtra("msg");
                if(msg != null && !msg.isEmpty()){
                    mButton.setEnabled(false);
                    mStatus.setVisibility(View.VISIBLE);
                    mStatus.setText(msg);
                }
            }
        }

    }
}
