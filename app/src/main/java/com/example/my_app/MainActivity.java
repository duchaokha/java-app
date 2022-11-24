package com.example.my_app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    Button BtnLogOut;
    private TextView tvText;
    private TextView deviceStatusText;
    private TextView lightView;
    public String Topic = "";
    public String subMess = "";
    public int statusDevice;

    //Create mqtt client
    Mqtt3AsyncClient client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("my-mqtt-client-id")
            .serverHost("0638a97ea59941ee967ef85f112d8e0b.s2.eu.hivemq.cloud")
            .serverPort(8883)
            .useSslWithDefaultConfig()
            .buildAsync();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        Topic = i.getStringExtra("Topic");


        tvText = findViewById(R.id.tvText);
        deviceStatusText = findViewById(R.id.Result);
        lightView = findViewById(R.id.Result1);
        btnSpeak = findViewById(R.id.btnSpeak);
        BtnLogOut =  findViewById(R.id.BtnLogOut);
        connect(client);
        subcribe(client, Topic);
        setView();
        btnSpeak.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
            try
            {
                startActivityForResult(intent, RESULT_SPEECH);
                tvText.setText("");
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Your device does not support Speech to Text", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        BtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent( MainActivity.this,Login_Active.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });

    }

    //Connecting to broker
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void connect(Mqtt3AsyncClient client) {
        client.connectWith()
                .simpleAuth()
                .username("abcef")
                .password("12345678".getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("mqtt:", "There's some error when connect");
                    } else {
                        Log.e("mqtt:", "Connect successful");
                    }
                });
    }
    public void setView()
    {
        if(statusDevice==0)
        {
            lightView.setBackgroundResource(R.drawable.img_3);
        }
        else
        {
            lightView.setBackgroundResource(R.drawable.img_4);
        }
    }
    //Publishing to a topic
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void publish(Mqtt3AsyncClient client, String topicName, String mess) {
        client.publishWith()
                .topic(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .retain(true)
                .payload(mess.getBytes())
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        Log.e("mqtt:", "There's some error when publish");
                    }
                    else {
                        Log.e("mqtt:", "Publish successful");
                    }
                });
    }

    //Subcribe to a topic
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void subcribe(Mqtt3AsyncClient client, String topicName)
    {
        client.subscribeWith()
                .topicFilter(topicName)
                .callback(publish -> {
                    subMess = new String(publish.getPayloadAsBytes());
                    Log.e("mqtt:", subMess);
                    statusDevice = Integer.parseInt(subMess);
                    if(statusDevice==0)
                    {
                        deviceStatusText.setText("Đèn đang tắt");
                        lightView.setBackgroundResource(R.drawable.img_3);
                    }
                    if(statusDevice==1)
                    {
                        deviceStatusText.setText("Đèn đang bật");
                        lightView.setBackgroundResource(R.drawable.img_4);
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("mqtt:", "There's some error when subcribe");
                    }
                    else {
                        Log.e("mqtt:", "Subcribe successful");
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvText.setText(text.get(0));
                    int deviceStatus = Result(text.get(0).toLowerCase(Locale.ROOT));
                    switch (deviceStatus)
                    {
                        case 0:
                            publish(client, Topic ,"0");

                            break;
                        case 1:
                            publish(client, Topic,"1");

                            break;
                        case 2:
                            Toast.makeText(this, "Không nhận diện được lệnh", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                break;
        }
    }

    public int Result(String str)
    {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{M}", "");
        if(str.contains("tat đen"))  return 0;
        if(str.contains("bat đen"))  return 1;
        return 2;
    }

}