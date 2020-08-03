// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.classes.AllowedLanguageCodes;
import com.google.sps.classes.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  private List<Comment> arr = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    Query commentsQuery = new Query("Comment").addSort("timestamp");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    PreparedQuery results = datastore.prepare(commentsQuery);
    List<Comment> arr = new ArrayList<>();
    int how_many = Integer.parseInt(request.getParameter("how_many"));
    int langIndex = Integer.parseInt(request.getParameter("langIndex"));
    int langIndexMax = AllowedLanguageCodes.getTranslationLanguageList().size() - 1;

    if (langIndex > langIndexMax || langIndex < 0){
      response.setHeader("error", "The language sent is not currently supported.");
      response.sendError(500);
      return;
    }

    String langCode = AllowedLanguageCodes.getTranslationCodeForIndex(langIndex);
    
    for (Entity it : results.asIterable()) {
      String message = (String) it.getProperty("message");
      String uid = (String) it.getProperty("id");
      long timestamp = (long) it.getProperty("timestamp");
      long id = (long) it.getKey().getId();

      Translate translate = (Translate) TranslateOptions.getDefaultInstance().getService();
      Translation translation = translate.translate(message, Translate.TranslateOption.targetLanguage(langCode));
      String translatedMessage = translation.getTranslatedText();

      Query nicknameQuery = new Query("Users").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, uid));
      PreparedQuery result = datastore.prepare(nicknameQuery); // Get the nickname of the user who wrote the current comment
      Entity givenEntity = result.asSingleEntity();

      String name = (String) givenEntity.getProperty("nickname");
      arr.add(new Comment(translatedMessage, name, timestamp, id));
      --how_many;
      
      if (how_many == 0) // If how_many goes below 0, all the comments will be loaded in the list and displayed
        break;           // on the html page. So, setting a value <=0 in the POST request will display all the comments
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");

    response.getWriter().println(gson.toJson(arr));
  }
}
