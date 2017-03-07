package com.killerrin.andre.top10downloader;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {

    private Button btnParse;
    private ListView listApps;

    DownloadData downloadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Download Data
        downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

        // Get the Views
        btnParse = (Button) findViewById(R.id.btnParse);
        listApps = (ListView) findViewById(R.id.xmlListView);

        // Set Listeners
        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add Parse Activation Code
                if (downloadData.getIsDataDownloaded()) {
                    ParseApplications parseApplications = new ParseApplications(downloadData.getFileContents());
                    parseApplications.process();

                    ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                            MainActivity.this, R.layout.list_item, parseApplications.getApplications());
                    listApps.setAdapter(arrayAdapter);
                }
            }
        });
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private boolean mIsDataDownloaded = false;
        private String mFileContents = "";

        public boolean getIsDataDownloaded() {
            return mIsDataDownloaded;
        }
        public String getFileContents() {
            return mFileContents;
        }

        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLFile(params[0]);
            if (mFileContents == null) {
                Log.d("DownloadData", "Error downloading");
            }

            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("DownloadData", "Result was: " + result);

            if (result != null) {
                mFileContents = result;
                mIsDataDownloaded = true;
            }
        }

        private String downloadXMLFile(String urlPath) {
            mFileContents = "";
            mIsDataDownloaded = false;

            StringBuilder tempBuffer = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("DownloadData", "The response code was " + response);

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }

                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();
            } catch (IOException e) {
                Log.d("DownloadData", "IO Exception reading data: " + e.getMessage());
            }
            catch (SecurityException e) {
                Log.d("DownloadData", "Security Exception. Needs permissions?");
            }

            return null;
        }
    }
}