package com.google.sps.servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.gson.Gson;

public class CaptchaVerifier {
  private final static String url = "https://www.google.com/recaptcha/api/siteverify";
  private final static String secret_key = "6LfSp7QZAAAAAIgO2NgYxUQ0Xcp-1tv8p_EAczRx";

  public static boolean Verifier(String response) throws IOException {
    URL api_url = new URL(url);
    HttpsURLConnection connection = (HttpsURLConnection) api_url.openConnection();

    connection.setRequestMethod("POST");
    String post_parameters = "secret=" + secret_key + "&response=" + response;
    connection.setDoOutput(true);
    
    DataOutputStream write = new DataOutputStream(connection.getOutputStream());
    write.writeBytes(post_parameters);
    write.flush();
    write.close();
    
    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String line;
    StringBuffer ReadBuffer = new StringBuffer();
    line = input.readLine();
    line = input.readLine();
    
    if (line.contains("true"))
      return true;
    return false;
  }
}
