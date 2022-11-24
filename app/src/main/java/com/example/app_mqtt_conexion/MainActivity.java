package com.example.app_mqtt_conexion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Build;//para obtener el nombre del dispositivo
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String nombre_Dispositivo;   //string para obtener el nombre del dispositivo
    String publicaste;                         //string para mostras el mensaje apublicar
    boolean permiso_publicar=false;          //para permitir o no hacer publicaciones
    boolean intento_publicar=false;           //para saber si intento publicar
    //private TextView tvNombreDispositivo;      //TexView para monitorear

    //parametros del broker la siguiente variable con el broker de shiftr.io
    static String MQTTHOST = "tcp://68.183.119.177"; //el broker
    //static String USERNAME = "accesobroker";          //el token de acceso
    //static String PASSWORD = "zxcvbnmz";             //la contraceña del token

    MqttAndroidClient client;              //  clienteMQTT este dispositivo
    MqttConnectOptions options;            // para meter parametros a la conexion

    TextView txtTopic1;
    TextView txtTopic2;
    TextView txtTopic3;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//obtenemos el nombre del Dispositivo
       //obtenemos el nombre del Dispositivo
       obtener_nombre_Dispositivo();
       //para conextar al broker   //generamos un clienteMQTT
       String clientId = nombre_Dispositivo;//MqttClient.generateClientId();//noombre del celular
       client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
       //para agregar los parametros
       options = new MqttConnectOptions();
       //options.setUserName(USERNAME);
       //options.setPassword(PASSWORD.toCharArray());
       checar_conexion();//revisamos la conexion

       txtTopic1 = findViewById(R.id.temp);
       txtTopic2 = findViewById(R.id.humidity);
       txtTopic3 = findViewById(R.id.pressure);
    }
    private void obtener_nombre_Dispositivo() {
        String fabricante = Build.MANUFACTURER;
        String modelo = Build.MODEL;
        nombre_Dispositivo=fabricante+" "+modelo;
        //tvNombreDispositivo =(TextView) findViewById(R.id.tv_g);//para mostrar el modelo del celular
        //tvNombreDispositivo.setText(nombre_Dispositivo);//para mostrar en el tv_g e modelo del celular



    }

    public void conexionBroker() {

        //para conextar al broker   //generamos un clienteMQTT
        String clientId = nombre_Dispositivo; //MqttClient.generateClientId();//noombre del celular
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        //para agregar los parametros
        MqttConnectOptions options = new MqttConnectOptions();
        //options.setUserName(USERNAME);
        //options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);//intenta la conexion
            token.setActionCallback(new IMqttActionListener() {

                @Override//metodo de conectado con exito
                public void onSuccess(IMqttToken asyncActionToken) {
                    // mensaje de conectado
                    Toast.makeText(getBaseContext(), "Conectado ", Toast.LENGTH_SHORT).show();
                    sub("cmet/01","cmet/02","cmet/03");
                }

                @Override//si falla la conexion
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // mensaje de que no se conecto
                    Toast.makeText(getBaseContext(), "NO Conectado ", Toast.LENGTH_SHORT).show();
                }


            });


        } catch (MqttException e) {
            e.printStackTrace();
        }






    }

    public  void checar_conexion(){
//si el cliente esta desconectado se conecta falso=no conectado
        if(!client.isConnected()) {
            permiso_publicar=false;// no tienes permiso para publiar
            conexionBroker();//intenta conectarce

        }else{permiso_publicar=true;}//si puedes publicar


    }

    public  void  publicarD1 (View v) {
        String tema="st";///corrrespode al tema de LED
        String  menssage="ON";
        publicaste= "publicaras " + tema + " " + menssage; //concatenamos el dato a publicar
        intento_publicar=true;//si intento publicar
        checar_conexion();//revisamos la conexion

        if (permiso_publicar){

            try {
                int qos=0;//indica la prioridad del mensaje.
                // 0:envio una vez,
                // 1:se envia hasta garantizar la entrega en caso de fallo resive duplicados
                //2: se  garantiza que se entrege al subcribtor unicamente una vez
                //retenid=false;//true es que el mensaje se quede guardado en el broker asta su actualizacion
                client.publish(tema, menssage.getBytes(),qos, false);
                Toast.makeText(getBaseContext(), publicaste, Toast.LENGTH_SHORT).show();
            }catch (Exception e){e.printStackTrace();}
        }

    }
    public  void  publicarD2 (View v) {
        String tema="st";///corrrespode al tema de LED
        String  menssage="OFF";
        publicaste= "publicaras " + tema + " " + menssage; //concatenamos el dato a publicar
        intento_publicar=true;//si intento publicar
        checar_conexion();//revisamos la conexion

        if (permiso_publicar){

            try {
                int qos=0;//indica la prioridad del mensaje.
                // 0:envio una vez,
                // 1:se envia hasta garantizar la entrega en caso de fallo resive duplicados
                //2: se  garantiza que se entrege al subcribtor unicamente una vez
                //retenid=false;//true es que el mensaje se quede guardado en el broker asta su actualizacion
                client.publish(tema, menssage.getBytes(),qos, false);
                Toast.makeText(getBaseContext(), publicaste, Toast.LENGTH_SHORT).show();
            }catch (Exception e){e.printStackTrace();}
        }

    }

    public void sub(String topic1,String topic2,String topic3)
    {
        try {
            client.subscribe(topic1, 0);
            client.subscribe(topic2, 0);
            client.subscribe(topic3, 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause)
                {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception
                {
                    String resTemp;
                    String resHumidity;
                    String resPressure;
                    if(topic.matches(topic1))
                    {
                        resTemp = new String(message.getPayload());
                        txtTopic1.setText(resTemp + " °C");
                    }
                    if(topic.matches(topic2))
                    {
                        resHumidity = new String(message.getPayload());
                        txtTopic2.setText(resHumidity + " %");
                    }
                    if(topic.matches(topic3))
                    {
                        resPressure = new String(message.getPayload());
                        txtTopic3.setText(resPressure + " hectopascal");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token)
                {

                }
            });
        }catch (MqttException e){
            e.printStackTrace();
        }

    }

}