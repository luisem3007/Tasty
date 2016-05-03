package com.example.jorge.tasty;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*
        // *********************************************************************Start threading [Hearing (Server)]
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create a server socket object and bind it to a port
                    ServerSocket socServer = new ServerSocket(5002);
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
*/
        // *********************************************************************End Start Hearing (Server)

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//-103.42229_20.69709
        LatLng myLoc = new LatLng(20.69709,-103.42229);
        mMap.addMarker(new MarkerOptions().position(myLoc).title("Marker in my location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16));


        for (int i = 0;i < information.arrayListRestaurantInformation.size(); i ++)
        {
            String[] Information = information.arrayListRestaurantInformation.get(i).toString().split("_");
            LatLng myLoc1 = new LatLng(Double.parseDouble(Information[3]), Double.parseDouble(Information[2]));
            mMap.addMarker(new MarkerOptions().position(myLoc1).title(Information[0]));
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 14));
        }
    }

    /**
     * AsyncTask which handles the commiunication with clients
     */
    // *********************************************************************[Hearing (Server)] Class

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
                result = mySocket.getInetAddress().toString();
                result = result + "_" + br.readLine();
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

        }
    }}