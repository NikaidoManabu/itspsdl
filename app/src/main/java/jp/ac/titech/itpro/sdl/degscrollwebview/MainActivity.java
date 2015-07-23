package jp.ac.titech.itpro.sdl.degscrollwebview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import android.widget.Button;
import android.view.View;

public class MainActivity extends Activity  implements SensorEventListener{
    private SensorManager manager;
    private WebView  myWebView;
    private TextView values;
    private TextView values2;
    private HashMap<String,Float> sensor_value;
    private HashMap<String,Float> sensor_value_base;
    android.os.Handler customHandler;
    private boolean is_button_clicked=false;
    private int scroll_speed_inv=-5;
    private int scroll_direction=1;//1 or -1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //レイアウトで指定したWebViewのIDを指定する。
        myWebView = (WebView)findViewById(R.id.web_view);

        //リンクをタップしたときに標準ブラウザを起動させない
        myWebView.setWebViewClient(new WebViewClient());

        //最初にYahoo! Japanのページを表示する。
        myWebView.loadUrl("http://www.yahoo.co.jp/");
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setJavaScriptEnabled(true);

        values = (TextView)findViewById(R.id.header_text);
        values2 = (TextView)findViewById(R.id.header_text2);
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor_value=new HashMap<String,Float>();
        sensor_value.put("傾斜角", 0.0F);
        sensor_value.put("回転角", 0.0F);
        sensor_value_base=new HashMap<String,Float>();
        sensor_value_base.put("傾斜角", 0.0F);
        sensor_value_base.put("回転角", 0.0F);

        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);



        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_button_clicked=true;
                // ボタンがクリックされた時に呼び出されます
                sensor_value_base.put("傾斜角", sensor_value.get("傾斜角"));
                sensor_value_base.put("回転角", sensor_value.get("回転角"));
                values2.setText(values.getText());
            }
        });
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            //write here whaterver you want to repeat
            customHandler.postDelayed(this, 5);
            myWebView.scrollBy(0,get_scroll_speed());
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Listenerの登録解除
        manager.unregisterListener(this);
     }

    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ORIENTATION);
        if(sensors.size() > 0) {
            Sensor s = sensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
     }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            sensor_value.put("傾斜角", event.values[1]);
            sensor_value.put("回転角", event.values[2]);
            if(!is_button_clicked){//初回の角度設定まではスクロールしない
                sensor_value_base.put("傾斜角", event.values[1]);
                sensor_value_base.put("回転角", event.values[2]);
            }
            String str = "傾きセンサー値:"
                        + "\n傾斜角:" + sensor_value.get("傾斜角")
                        + "\n回転角:" + sensor_value.get("回転角")
                        + "\nスクロール: "+get_scroll_speed()
                        + "\n反転:"+((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation();

            ;
            values.setText(str);
        }
    }

    private int get_scroll_speed(){
        switch (((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation()){
            case 0:
                return (int) (sensor_value.get("傾斜角")-sensor_value_base.get("傾斜角"))/scroll_speed_inv*scroll_direction;
            case 1:
                return (int) (sensor_value.get("回転角")-sensor_value_base.get("回転角"))/scroll_speed_inv*(-1)*scroll_direction;
            case 2:
                return (int) (sensor_value.get("傾斜角")-sensor_value_base.get("傾斜角"))/scroll_speed_inv*(-1)*scroll_direction;
            case 3:
                return (int) (sensor_value.get("回転角")-sensor_value_base.get("回転角"))/scroll_speed_inv*scroll_direction;
            default:
                return 0;
        }
    }

}
