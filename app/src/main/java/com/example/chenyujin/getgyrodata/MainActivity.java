package com.example.chenyujin.getgyrodata;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean startCollect = false;
    private Thread timeThread;
    private boolean cancelThread = false;
    public static Button Button1;
    public static TextView dataTextView;
    public static TextView dataTextViewOri;
    public static TextView dataTextViewAcc;
    public static TextView dataTextViewGro;
    public static TextView dataTextViewGra;
    private static SensorManager sManager;
    private SensorEventListener gSensorListener;
    private SensorEventListener mSensorListener;
    private SensorEventListener oSensorListener;
    private SensorEventListener aSensorListener;
    private SensorEventListener graSensorListener;

    private Sensor msensor;
    private Sensor osensor;
    private Sensor asensor;
    private Sensor gsensor;
    private Sensor grasensor;
    private float[] temp_m = new float[3];
    private float[] temp_g = new float[3];
    private float[] temp_o = new float[3];
    private float[] temp_a = new float[3];
    private float[] temp_gra = new float[3];
    private boolean isStartCollect=false;
    private boolean Collecting=false;
    public static int datacount=0;
    public ArrayList<ArrayList<Float>> dataMagnetic = new ArrayList<ArrayList<Float>>(3);
    public ArrayList<ArrayList<Float>> dataOrientation = new ArrayList<ArrayList<Float>>(3);
    public ArrayList<ArrayList<Float>> dataAccelerate = new ArrayList<ArrayList<Float>>(3);
    public ArrayList<ArrayList<Float>> dataGyroscope = new ArrayList<ArrayList<Float>>(3);
    public ArrayList<ArrayList<Float>> dataGravity = new ArrayList<ArrayList<Float>>(3);
    public ArrayList<ArrayList<String>> dataTime = new ArrayList<ArrayList<String>>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context=getBaseContext();


        Button1=(Button) findViewById(R.id.Button1);
        dataTextView = (TextView) findViewById(R.id.dataTextView);
        dataTextViewOri = (TextView) findViewById(R.id.dataTextViewOri);
        dataTextViewAcc = (TextView) findViewById(R.id.dataTextViewAcc);
        dataTextViewGro = (TextView) findViewById(R.id.dataTextViewGyro);
        dataTextViewGra = (TextView) findViewById(R.id.dataTextViewGra);

        sManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        msensor=sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gsensor=sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        osensor=sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        asensor=sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        grasensor=sManager.getDefaultSensor(Sensor.TYPE_GRAVITY);



        gSensorListener = new GSensorListener();
        mSensorListener = new MSensorListener();
        aSensorListener = new ASensorListener();
        oSensorListener = new OSensorListener();
        graSensorListener = new GraSensorListener();
        sManager.registerListener(gSensorListener, gsensor, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(mSensorListener, msensor, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(aSensorListener, asensor, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(oSensorListener, osensor, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(graSensorListener, grasensor, SensorManager.SENSOR_DELAY_NORMAL);


        Button1.setOnClickListener(this);

    }

    private class MSensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            temp_m[0] = event.values[0];
            temp_m[1] = event.values[1];
            temp_m[2] = event.values[2];
            // 在这里显示一下地磁数据试试
            if (Collecting==true){
                MainActivity.dataTextView.setText("地磁数据: " + temp_m[0] + " "
                        + temp_m[1] + " " + temp_m[2]);
                updataSensorData();
                datacount++;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
    private class GSensorListener implements SensorEventListener {//陀螺仪

        @Override
        public void onSensorChanged(SensorEvent event) {
            temp_g[0] = event.values[0];
            temp_g[1] = event.values[1];
            temp_g[2] = event.values[2];

            if (Collecting==true){
                MainActivity.dataTextViewGro.setText("陀螺仪数据: " + temp_g[0] + " "
                        + temp_g[1] + " " + temp_g[2]);

                updataSensorData();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    private class OSensorListener implements SensorEventListener {//方向

        @Override
        public void onSensorChanged(SensorEvent event) {
            temp_o[0] = event.values[0];
            temp_o[1] = event.values[1];
            temp_o[2] = event.values[2];

            if (Collecting==true){
                MainActivity.dataTextViewOri.setText("方向数据: " + temp_o[0] + " "
                        + temp_o[1] + " " + temp_o[2]);

                updataSensorData();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    private class ASensorListener implements SensorEventListener {//加速度

        @Override
        public void onSensorChanged(SensorEvent event) {
            temp_a[0] = event.values[0];
            temp_a[1] = event.values[1];
            temp_a[2] = event.values[2];

            if (Collecting==true){
                MainActivity.dataTextViewAcc.setText("加速度数据: " + temp_a[0] + " "
                        + temp_a[1] + " " + temp_a[2]);

                updataSensorData();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    private class GraSensorListener implements SensorEventListener {//重力

        @Override
        public void onSensorChanged(SensorEvent event) {
            temp_gra[0] = event.values[0];
            temp_gra[1] = event.values[1];
            temp_gra[2] = event.values[2];

            if (Collecting==true){
                MainActivity.dataTextViewGra.setText("重力数据: " + temp_gra[0] + " "
                        + temp_gra[1] + " " + temp_gra[2]);

                updataSensorData();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    private void dataInit() {
        for (int i = 0; i < 3; i++) {
            dataMagnetic.add(new ArrayList<Float>());
            dataOrientation.add(new ArrayList<Float>());
            dataAccelerate.add(new ArrayList<Float>());
            dataGyroscope.add(new ArrayList<Float>());
            dataGravity.add(new ArrayList<Float>());
        }
        dataTime.add(new ArrayList<String>());
    }

    public void dataClear() {
        for (int i = 0; i < dataMagnetic.size(); i++) {
            dataMagnetic.get(i).clear();
            dataOrientation.get(i).clear();
            dataAccelerate.get(i).clear();
            dataGyroscope.get(i).clear();
            dataGravity.get(i).clear();
        }
    }


    public void updataSensorData(){
        //保持与地磁更新同步
        if(datacount>dataMagnetic.get(0).size()){
            dataMagnetic.get(0).add(Float.valueOf(temp_m[0]));
            dataMagnetic.get(1).add(Float.valueOf(temp_m[1]));
            dataMagnetic.get(2).add(Float.valueOf(temp_m[2]));
            dataGyroscope.get(0).add(Float.valueOf(temp_g[0]));
            dataGyroscope.get(1).add(Float.valueOf(temp_g[1]));
            dataGyroscope.get(2).add(Float.valueOf(temp_g[2]));
            dataOrientation.get(0).add(Float.valueOf(temp_o[0]));
            dataOrientation.get(1).add(Float.valueOf(temp_o[1]));
            dataOrientation.get(2).add(Float.valueOf(temp_o[2]));
            dataAccelerate.get(0).add(Float.valueOf(temp_a[0]));
            dataAccelerate.get(1).add(Float.valueOf(temp_a[1]));
            dataAccelerate.get(2).add(Float.valueOf(temp_a[2]));
            dataGravity.get(0).add(Float.valueOf(temp_gra[0]));
            dataGravity.get(1).add(Float.valueOf(temp_gra[1]));
            dataGravity.get(2).add(Float.valueOf(temp_gra[2]));
            dataTime.get(0).add((String) getTime());
        }
    }


    public void onClick(View v) {
        if (isStartCollect == true) {
            Button1.setText("正在记录");
            dataInit();
            isStartCollect = false;
            Collecting=true;
        } else {
            if(Collecting==true){//区别初始情况
                Button1.setText("停止记录");
                saveData();
                dataClear();
            }
            isStartCollect = true;
            Collecting=false;

        }
    }
    //注销监听器
    public void unregist() {
        sManager.unregisterListener(gSensorListener);
        sManager.unregisterListener(mSensorListener);
        sManager.unregisterListener(oSensorListener);
        sManager.unregisterListener(aSensorListener);
        sManager.unregisterListener(graSensorListener);
    }



    public void saveData() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();//获取的路径为手机内存
            File directory = new File(sdCard.getAbsolutePath() + "/ChenRss-DataCollect");
            //String baseDir=Environment.getRootDirectory().getAbsolutePath();
            //File directory=new File(baseDir+"/ChenRss-DataCollect");
            //File directory=new File("/sdcard/ChenRss-DataCollect");
            directory.mkdirs();

            File file = new File(directory, "Sensor"+ System.currentTimeMillis() + ".txt");
            //file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStream fos = fOut;
            DataOutputStream dos = new DataOutputStream(fos);
            //FileOutputStream dos=new FileOutputStream(file);

            for (int i = 0; i < MainActivity.datacount; i++) {
                // 存传感器数据，rss后面增加15个int

                String outString = "Mag\t" + dataMagnetic.get(0).get(i) + "\t"
                        + dataMagnetic.get(1).get(i) + "\t"
                        + dataMagnetic.get(2).get(i) + "\t"
                        + "Gyro\t" + dataGyroscope.get(0).get(i) + "\t"
                        + dataGyroscope.get(1).get(i) + "\t"
                        + dataGyroscope.get(2).get(i) + "\t"
                        + "Acc\t" + dataAccelerate.get(0).get(i) + "\t"
                        + dataAccelerate.get(1).get(i) + "\t"
                        + dataAccelerate.get(2).get(i) + "\t"
                        + "Ori\t" + dataOrientation.get(0).get(i) + "\t"
                        + dataOrientation.get(1).get(i) + "\t"
                        + dataOrientation.get(2).get(i) + "\t"
                        + "Gra\t" + dataGravity.get(0).get(i) + "\t"
                        + dataGravity.get(1).get(i) + "\t"
                        + dataGravity.get(2).get(i) + "\t"
                        + "Time\t" +dataTime.get(0).get(i) +"\n";

                dos.write(outString.getBytes());
                //fileData.write(outString);
            }
            dos.close();
            //fileData.flush();

            Toast toast = Toast.makeText(MainActivity.this,"存储至“/ChenRss-DataCollect”", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "存储失败1。",
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "存储失败2。",
                    Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public String getTime(){
        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date d1=new Date(time);
        String t1=format.format(d1);
        return t1;
    }
}
