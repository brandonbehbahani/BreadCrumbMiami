package com.example.komron.breadcrumb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask<String, Void, String>{

    Context context;
    public BackgroundWorker(Context context) {
        this.context = context;
    }
    private String writeData = "";
    private String insertURL = ""; // Todo: the url of the insert_crumb php code
    private String crumbSector;
    private String crumbLatitude;
    private String crumbLongitude;
    private String userName;
    private String crumbTitle;
    private String crumbContent;
    private String crumbColor;
    private String crumbDate;

    @Override
    protected String doInBackground(String... strings) {
        String result = "";

        crumbSector = strings[0];
        crumbLatitude = strings[1];
        crumbLongitude = strings[2];
        userName = strings[3];
        crumbTitle = strings[4];
        crumbContent = strings[5];
        crumbColor = strings[6];


        try{
            String post_data =
                    URLEncoder.encode("sector", "UTF-8") + "=" +
                    URLEncoder.encode(crumbSector, "UTF-8") + "&" +
                    URLEncoder.encode("latitude", "UTF-8") + "=" +
                    URLEncoder.encode(crumbLatitude, "UTF-8") + "&" +
                    URLEncoder.encode("longitude", "UTF-8") + "=" +
                    URLEncoder.encode(crumbLongitude, "UTF-8") + "&" +
                    URLEncoder.encode("name", "UTF-8") + "=" +
                    URLEncoder.encode(userName, "UTF-8") + "&" +
                    URLEncoder.encode("title", "UTF-8") + "=" +
                    URLEncoder.encode(crumbTitle, "UTF-8") + "&" +
                    URLEncoder.encode("content", "UTF-8") + "=" +
                    URLEncoder.encode(crumbContent, "UTF-8") + "&" +
                    URLEncoder.encode("color", "UTF-8") + "=" +
                    URLEncoder.encode(crumbColor, "UTF-8");
            URL url = new URL(insertURL);
            Log.v("test", post_data);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            writeData = result;
            bufferedReader.close();
            inputStream.close();

            httpURLConnection.disconnect();

        }catch (MalformedURLException i){
            i.printStackTrace();
        }
        catch (IOException i){
            i.printStackTrace();
        }

        return result;
    }

    public void onPostExecute(String s){
        getWriteData();
    }

    public String getWriteData(){
        return writeData;
    }
    //alt + insert

}
