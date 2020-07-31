package com.google.sps.servlets;

import com.google.sps.classes.Comment;

import io.grpc.netty.shaded.io.netty.handler.codec.base64.Base64Decoder;

import com.google.sps.classes.AuthData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.appengine.api.datastore.Blob;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/speech")
public class RecognitionServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String base64Audio = request.getReader().readLine();
    byte[] decodedAudio = Base64.getDecoder().decode(base64Audio);
    /*
     * Path path = Paths.get( "/resources/audio.mp3" ); byte[] data =
     * Files.readAllBytes(path);
     */
    /*
     * System.out.println(decodedAudio.equals(data));
     * System.out.println(decodedAudio.length);
     * System.out.println(Arrays.equals(decodedAudio, data));
     */

    // System.out.println(base64Audio);
    // System.out.println();
    SpeechClient speechClient = SpeechClient.create();
    RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.FLAC;
    String languageCode = "en-US";
    RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding).setAudioChannelCount(2)
        .setLanguageCode(languageCode).build();
    //String uri = "gs://stefanescua-step-2020.appspot.com/audio.flac";
    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(decodedAudio)).build();
    RecognizeResponse apiResponse = speechClient.recognize(config, audio);
    List<String> lst = new ArrayList<>();

    for (SpeechRecognitionResult result : apiResponse.getResultsList()) {
      SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
      lst.add(alternative.getTranscript());
    }

    lst.add("Salut");
    response.setContentType("application/json;");
    Gson gson = new Gson();
    // if (lst.size() == 0)
    response.getWriter().print(gson.toJson(lst));
    // else response.getWriter().println(lst.get(0));
    // response.getWriter().println(gson.toJson(lst));

  }
}
