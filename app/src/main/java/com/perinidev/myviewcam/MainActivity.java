package com.perinidev.myviewcam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button hdButton, connectButton;
    private ImageButton cameraButton, setButton;
    private ImageView monitor;
    private TextView rssi_text;
    private EditText ip_text2, ip_text;
    private Switch switchCam1, switchCam2;

    private String URL;

    private String stream_url;
    private String still_url; //= "http://engperini.ddns.net:32521/capture";
    private String[] URL_HELPER;
    private String stream_url_http;
    private String command;

    private Handler handler = new Handler();
    private HttpURLConnection connection;

    boolean connected = false;
    BufferedInputStream bis = null;
    FileOutputStream fos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.connect);
        cameraButton = findViewById(R.id.camerabutton);
        setButton = findViewById(R.id.setbutton);

        hdButton = findViewById(R.id.flash);
        monitor = findViewById(R.id.monitor);
        rssi_text = findViewById(R.id.rssi);


        switchCam1 = findViewById(R.id.switchcam1);
        switchCam2 = findViewById(R.id.switchcam2);

        // Obter uma instância da classe SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Recuperar os valores das preferências "signature" e "reply"
        String urlValue1 = sharedPreferences.getString("signature", ""); //url1
        String urlValue2 = sharedPreferences.getString("signature2", "");//url2
        //String replyValue = sharedPreferences.getString("reply", ""); //choice cam+stream



        //initiate buttons and url
        switchCam1.setChecked(true);
        if (!urlValue1.isEmpty()) {
            URL = urlValue1;
            stream_url = "http://" + URL; //"http://engperini.ddns.net:32522/stream";
            try {
                URI uri = new URI(stream_url);
                // Extrair a porta e o comando da URL
                int porta = uri.getPort();
                String comando = uri.getPath().substring(1); // Removendo a barra inicial
                // Exibindo os valores extraídos
                System.out.println("Porta: " + porta);
                System.out.println("Comando: " + comando);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        // settings buttom
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

        });

        // Ouvinte para o Switch 1
        switchCam1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Switch 1 ativado, desativar Switch 2
                    //switchCam1.setEnabled(false);
                    //switchCam2.setEnabled(true);

                    switchCam2.setChecked(false);
                    if (!urlValue1.isEmpty()) {
                        // Use o valor da preferência "url1"
                        URL = urlValue1;
                    }
                    if (connection != null) {
                        connection.disconnect();
                        connectButton.setText("ON ");}
                    stream_url = "http://" + URL;
                    showToast(stream_url);}

                else {
                    // Switch 1 desativado, ativar Switch 2
                    switchCam2.setChecked(true);

                    if (connection != null) {
                        connection.disconnect();}
                    //stream_url = "http://" + URL;

                }
            }
        });

        // Ouvinte para o Switch 2
        switchCam2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Switch 2 ativado, desativar Switch 1
//                    switchCam2.setEnabled(false);
//                    switchCam1.setEnabled(true);

                    switchCam1.setChecked(false);

                    if (!urlValue2.isEmpty()) {
                        // Use o valor da preferência "url1"
                        URL = urlValue2;}

                    if (connection != null) {
                        connection.disconnect();
                        connectButton.setText("ON ");
                    }

                    stream_url = "http://" + URL;
                    showToast(stream_url);}

                else {
                    // Switch 2 desativado, ativar Switch 1
                    switchCam1.setChecked(true);
                    if (connection != null) {
                        connection.disconnect();}

                    //stream_url = "http://" + URL;
                }
            }
        });


        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (connected) {
                    // Disconnect
                    connection.disconnect();
                    connectButton.setText("ON ");
                    connected = false;

                } else {
                    // Connect
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(stream_url);
                                try {

                                    //http parametros
                                    connection = (HttpURLConnection) url.openConnection();
                                    connection.setRequestMethod("GET");
                                    connection.setConnectTimeout(1000 * 5);
                                    connection.setReadTimeout(1000 * 5);
                                    connection.setDoInput(true);
                                    connection.connect();

                                    if (connection.getResponseCode() == 200) {
                                        //se a conexão for bem sucedida tratar stream video

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                connectButton.setText("OFF");
                                            }
                                        });

                                        connected = true;
                                        Log.d("TAG", "Connected to server.");
                                        InputStream in = connection.getInputStream();
                                        InputStreamReader isr = new InputStreamReader(in);
                                        BufferedReader br = new BufferedReader(isr);

                                        String data;
                                        int len;
                                        byte[] buffer;

                                        while ((data = br.readLine()) != null) {
                                            if (data.contains("Content-Type:")) {
                                                data = br.readLine();
                                                len = Integer.parseInt(data.split(":")[1].trim());
                                                bis = new BufferedInputStream(in);
                                                buffer = new byte[len];

                                                int t = 0;
                                                while (t < len) {
                                                    t += bis.read(buffer, t, len - t);
                                                }

                                                Bytes2ImageFile(buffer, getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");
                                                final Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //connectButton.setText("OFF");
                                                        monitor.setImageBitmap(bitmap);
                                                    }
                                                });
                                            }
                                        }

                                    }


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (bis != null) {
                                        bis.close();}

                                    if (fos != null) {
                                        fos.close();}

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.start();
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //to do togle button connect and disconnect

                Thread thread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        BufferedInputStream bis = null;
                        FileOutputStream fos = null;
                        String still_url = "http://engperini.ddns.net:32521/capture";

                        try {
                            //do get camera
                            URL url = new URL(still_url);

                            try {

                                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                                huc.setRequestMethod("GET");
                                huc.setConnectTimeout(1000 * 5);
                                huc.setReadTimeout(1000 * 5);
                                huc.setDoInput(true);
                                huc.connect();
                                if (huc.getResponseCode() == 200) {
                                    InputStream in = huc.getInputStream();
                                    bis = new BufferedInputStream(in);

                                    // You can set the file path and name where you want to save the image
                                    String appName = getResources().getString(R.string.app_name); // Get the name of your app from resources
                                    String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + appName;

                                    // Create the folder if it doesn't exist
                                    File folder = new File(folderPath);
                                    if (!folder.exists()) {
                                        folder.mkdirs();
                                    }


                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                    //String fileName = "sophia2.jpg"; // The desired file name
                                    String fileName = "IMG_" + timeStamp + ".jpg"; // Unique file name with timestamp

                                    String filePath = folderPath + "/" + fileName;

                                    fos = new FileOutputStream(filePath);

                                    byte[] buffer = new byte[1024];
                                    int bytesRead;

                                    while ((bytesRead = bis.read(buffer)) != -1) {
                                        fos.write(buffer, 0, bytesRead);
                                    }

                                    fos.flush();
                                    fos.getFD().sync();

                                    final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            monitor.setImageBitmap(bitmap);
                                        }
                                    });
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast("Great! The file was saved");
                                        }
                                    });



                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("Sorry, check your internet or app permissions");
                                    }
                                });


                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (bis != null) {
                                    bis.close();
                                }
                                if (fos != null) {
                                    fos.close();
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                thread1.start();
            }
        });
    }


    private void Bytes2ImageFile(byte[] bytes, String fileName)
    {
        try
        {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    @Override
    protected void onPause() {
        super.onPause();

        // Close the HTTP connection
        if (connection != null) {
            connection.disconnect();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the HTTP connection
        if (connection != null) {
            connection.disconnect();
            }
        }


    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    }

