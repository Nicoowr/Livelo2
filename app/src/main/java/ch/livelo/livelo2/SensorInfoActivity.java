package ch.livelo.livelo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SensorInfoActivity extends AppCompatActivity {
    String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);
        id = getIntent().getStringExtra("id");



    }
}
