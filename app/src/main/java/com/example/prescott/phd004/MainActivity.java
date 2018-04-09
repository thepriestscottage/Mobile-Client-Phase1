package com.example.prescott.phd004;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    //<phd004> is written per contract PHD-004 with d2b, Inc.
    //This serves as a mobile client for a ****************
    //server, for ***************.

    //Client will send entered <String message> to the server.

    private BufferedReader in;
    private PrintWriter out;
    String message="";
    String check="";
    String status="";
    String ipadr;
    Socket client = null;

    Button send, con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (Button) findViewById(R.id.send);
        con = (Button) findViewById(R.id.connect);

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ipText = (EditText) findViewById(R.id.ip);
                ipadr = ipText.getText().toString();
                if(ipadr==null){}
                else{
                    final connect c = new connect(ipadr);
                    c.execute();
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.editText);
                TextView textView = (TextView) findViewById(R.id.textView);
                message = editText.getText().toString();
                editText.setText("");
                textView.setText(status);
            }
        });
    }

    public class connect extends AsyncTask<Void, Void, Void> {

        String IPaddress;

        connect(String ip){
            IPaddress=ip;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                client = new Socket(IPaddress, 8080);

                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);

                TextView textView = (TextView) findViewById(R.id.textView);
                // Consume the initial welcoming messages from the server
                for (int i = 0; i < 3; i++) {
                    status = status + in.readLine() + "\n";
                }
                textView.setText(status);

                while(true){
                    if(message!=check){
                        out.println(message);
                        String response="";
                        try {
                            response = in.readLine();
                            if (response == null || response.equals(".")) {
                                System.exit(0);
                            }
                        } catch (IOException ex) {
                            response = "Error: " + ex;
                        }
                        status = status + response + "\n";
                        check=message;
                    }
                }
            }
            catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText("UnknownHostException: " + e.toString() + "\n");
            }
            catch (IOException e){
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText("Could not connect.");
            }
            finally{
                if(client != null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
