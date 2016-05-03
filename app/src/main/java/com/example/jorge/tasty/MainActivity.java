package com.example.jorge.tasty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btn, button;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Era una vez..... Un usuario que tenia hambre", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Y bajo esta aplicación", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Acompañanos a seguir esta linda historia", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();


        btn = (Button) findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }


            btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                information.arrayList.clear();
                String sendMessage = "getRestaurants_-103.42229_20.69709";
                ClientAsyncTask clientAST = new ClientAsyncTask();
                clientAST.execute(new String[]{"192.168.43.179", "5003", sendMessage});
            }

                /* Test - This already works
                String sendMessage = "getRestaurants_-103.43958_20.65256";
                        ClientAsyncTask clientAST = new ClientAsyncTask();
                        clientAST.execute(new String[]{"192.168.100.8", "5003", sendMessage});
*/


            //  startMap();
        });

        // *********************************************************************Start threading [Hearing (Server)]
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create a server socket object and bind it to a port
                    ServerSocket socServer = new ServerSocket(5003);
                    //Create server side client socket reference
                    Socket socClient = null;
                    //Infinite loop will listen for client requests to connect
                    while (true) {
                        //Accept the client connection and hand over communication to server side client socket
                        socClient = socServer.accept();
                        //For each client new instance of AsyncTask will be created
                        ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                        //Start the AsyncTask execution
                        //Accepted client socket object will pass as the parameter
                        serverAsyncTask.execute(new Socket[]{socClient});
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // *********************************************************************End Start Hearing (Server)

    }

    public void startMap() {
        Intent i;
        i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void startGame() {
        Intent i;
        i = new Intent(this, juego.class);
        startActivity(i);
    }


    //***************************************************************************Server class
    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        //Background task which serve for the client
        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
            //Get the accepted socket object
            Socket mySocket = params[0];
            try {
                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);

                //Write data to the data output stream
                // Buffer the data input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
                //result = mySocket.getInetAddress().toString();
                //result = result + "_"+br.readLine();

                result = br.readLine();
                //information.ServerIP = result;
                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            //arrayList.add("Restaurant says: " + s);
            //Store information on respected file and update the arraylist......
            if (s.equals("begin")) {
                //
                //Toast.makeText(getApplicationContext(), "bad"+s, Toast.LENGTH_SHORT).show();
                information.arrayListRestaurantInformation.clear();
                information.arrayListRestaurant.clear();
                information.arrayListRestaurantIP.clear();

            } else if (s.equals("close")) {
                startMap();
            } else if(s.equals("No Information Found")){
                Toast.makeText(getApplicationContext(), "Lo sentimos no hay restaurantes cerca de usted", Toast.LENGTH_SHORT).show();
            }

            else {

                String[] parts = s.split("_");
                String Restaurant = parts[0];

                //Toast.makeText(getApplicationContext(), Restaurant, Toast.LENGTH_SHORT).show();


                    information.arrayListRestaurantInformation.add(s);
                    information.arrayListRestaurant.add(Restaurant);
                    information.arrayListRestaurantIP.add(parts[1]);

            }
        }
    }


    // *********************************************************************End[Hearing (Server)] Class
    //********************************************************************************
    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server

                // Socket socket = new Socket(params[0],
                //        Integer.parseInt(params[1]));

                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(params[0], Integer.parseInt(params[1])), 3000);
                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();
                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //Write data to the output stream of the client socket
                out.println(params[2]);
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                result = br.readLine();
                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                information.error = 1;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                information.error = 1;
            } catch (IOException e) {
                e.printStackTrace();
                information.error = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if (information.error == 1) {
                Toast.makeText(getApplicationContext(), "Service is not available!", Toast.LENGTH_SHORT).show();
                startGame();
            }
            information.error = 0;
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void gps() {


// getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        String longitude, latitude;
// check if GPS enabled
        if (isGPSEnabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                Log.d("msg", "Loc : " + longitude + ":" + latitude);
                Toast.makeText(getApplicationContext(), "latitud: " + latitude, Toast.LENGTH_LONG).show();


               // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
        /*
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);
         */

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    longitude = String.valueOf(location.getLongitude());
                    latitude = String.valueOf(location.getLatitude());
                    Toast.makeText(getApplicationContext(), "latitud: " + latitude, Toast.LENGTH_LONG).show();

                   // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                } else {
                    longitude = "0.00";
                    latitude = "0.00";
                    Toast.makeText(getApplicationContext(), "latitud no encontrada: " + latitude, Toast.LENGTH_LONG).show();
                }
            }
        }
    }}

