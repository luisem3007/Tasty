package com.example.jorge.tasty;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class chatrestaruante extends AppCompatActivity {
    private EditText editTxt;
    private Button btn, button;
    private ListView list;
    private ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    String fileName = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatrestaruante);

        setTitle(information.restauranteNombre);
        editTxt = (EditText) findViewById(R.id.txt);
        btn = (Button) findViewById(R.id.button);
        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_layout, arrayList);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FileInputStream fileIn= null;
        try {
            String IP = "/"+information.ServerIP;
            IP = IP.replaceAll("\\.", "a");
            IP = IP.replaceAll("/", "b");
            fileIn = openFileInput(fileName);
        InputStreamReader InputRead= new InputStreamReader(fileIn);
        BufferedReader br = new BufferedReader(InputRead);
            arrayList.clear();
        String a="";
        arrayList.clear();
        while ((a = br.readLine()) != null)
        {
            arrayList.add(a);
        }

        adapter.notifyDataSetChanged();
        InputRead.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {
            FileInputStream fileIn= null;
            fileName = information.selectedFile;
            fileIn = openFileInput(fileName);
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            BufferedReader br = new BufferedReader(InputRead);

            String s="";
            arrayList.clear();
            while ((s = br.readLine()) != null)
            {
                arrayList.add(s);
            }

            adapter.notifyDataSetChanged();
            InputRead.close();
            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    FileInputStream fileIn= null;
                     //               fileName = information.selectedFile;
                                    fileIn = openFileInput(fileName);
                                    InputStreamReader InputRead= new InputStreamReader(fileIn);
                                    BufferedReader br = new BufferedReader(InputRead);

                                    String s="";
                                    arrayList.clear();
                                    while ((s = br.readLine()) != null)
                                    {
                                        arrayList.add(s);
                                    }

                                    adapter.notifyDataSetChanged();
                                    InputRead.close();
                                    //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


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

        //************************************************************************************Button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendMessage = editTxt.getText().toString();
                //                Information.arrayList.add("Client says: " + sendMessage);
                //              adapter.notifyDataSetChanged();
                editTxt.setText(" ");
                ClientAsyncTask clientAST = new ClientAsyncTask();
            //    Toast.makeText(getApplicationContext(), information.ServerIP, Toast.LENGTH_SHORT).show();
                arrayList.add("Tú: " + sendMessage);
                //sendMessage = "Tú: " + sendMessage;
                adapter.notifyDataSetChanged();

                FileOutputStream fileout = null;
                try {
                    fileout = openFileOutput(fileName, Context.MODE_APPEND);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write(sendMessage + "\n");
                outputWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                clientAST.execute(new String[]{information.ServerIP, "5002", sendMessage});
            }
        });
    }

    //********************************************************************************
    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
                //Socket socket = new Socket(params[0],
                //        Integer.parseInt(params[1]));
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(params[0], Integer.parseInt(params[1])), 1000);
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

            if (information.error==1)
            {
                Toast.makeText(getApplicationContext(), "User is not available!", Toast.LENGTH_SHORT).show();
            }

            information.error = 0;
        }
    }





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
                result = mySocket.getInetAddress().toString(); //comentar
                result = result + "_" + br.readLine();              //comentar
                //result = br.readLine();                  Volver a descomentar
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


            String[] parts = s.split("_");
            String IP = parts[0];
            IP = IP.replaceAll("\\.", "a");
            IP = IP.replaceAll("/", "b");
            //Toast.makeText(getBaseContext(), IP, Toast.LENGTH_SHORT).show();
            fileName = IP;
            String message = parts[1];

            try {
                //fileName = s;
                FileOutputStream fileout = openFileOutput(fileName, Context.MODE_APPEND);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write("Restaurante: "+message + "\n");
                outputWriter.close();

                FileInputStream fileIn= null;
                fileIn = openFileInput(fileName);
                InputStreamReader InputRead= new InputStreamReader(fileIn);
                BufferedReader br = new BufferedReader(InputRead);

                String a="";
                arrayList.clear();
                while ((a = br.readLine()) != null)
                {
                    arrayList.add(a);
                }

                adapter.notifyDataSetChanged();
                InputRead.close();
                // arrayList.add("Restaurante: " + s);
                //adapter.notifyDataSetChanged();
                //  Toast.makeText(getApplicationContext(), "Message Recieved", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


