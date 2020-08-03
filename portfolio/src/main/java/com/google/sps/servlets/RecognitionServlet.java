package com.google.sps.servlets;

import java.io.IOException;
import java.util.Base64;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.protobuf.ByteString;
import com.google.sps.classes.AllowedLanguageCodes;
import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/speech")
public class RecognitionServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String base64Audio = request.getReader().readLine();
    byte[] decodedAudio = Base64.getDecoder().decode(base64Audio);
    int langIndex = Integer.parseInt(request.getParameter("langIndex"));
    int langIndexMax = AllowedLanguageCodes.getTranslationLanguageList().size() - 1;

    if (langIndex > langIndexMax || langIndex < 0) {
      response.setHeader("error", "The language sent is not currently supported.");
      response.sendError(500);
      return;
    }

    String languageCode = AllowedLanguageCodes.getSpeechCodeForIndex(langIndex);
    SpeechClient speechClient = SpeechClient.create();
    RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
    RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding).setAudioChannelCount(2)
        .setLanguageCode(languageCode).setEnableAutomaticPunctuation(true).build();
    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(decodedAudio)).build();
    RecognizeResponse apiResponse = speechClient.recognize(config, audio);

    if (apiResponse.getResultsList().isEmpty() || apiResponse.getResultsList().get(0).getAlternativesList().isEmpty()) {
      response.setHeader("error", "The audio you recorded couldn't be understood, please try again.");
      response.sendError(500);
      return;
    }

    String apiResponseTranscript = apiResponse.getResultsList().get(0).getAlternativesList().get(0).getTranscript();
    response.setContentType("application/json;");
    Gson gson = new Gson();
    response.getWriter().print(gson.toJson(apiResponseTranscript));
  }
}
