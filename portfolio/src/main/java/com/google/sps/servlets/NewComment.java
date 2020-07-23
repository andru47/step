package com.google.sps.servlets;

import com.google.sps.classes.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/new-comment")
public class NewComment extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String CaptchaResponse = request.getParameter("g-recaptcha-response");
    boolean Captcha = CaptchaVerifier.Verifier(CaptchaResponse);
    if (!Captcha) {
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"Please verify that you are a human\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }
    String entered_name = getParameter(request, "name");
    String entered_message = getParameter(request, "message");

    if (entered_name.length() == 0){
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"The entered name must contain at least one character.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    if (entered_message.length() == 0){
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"The entered message must contain at least one character.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    Entity taskEntity = new Entity("Comment");
    taskEntity.setProperty("name", entered_name);
    taskEntity.setProperty("message", entered_message);
    taskEntity.setProperty("timestamp", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    response.sendRedirect("/comments.html");
  }

  private String getParameter(HttpServletRequest request, String param_name) {
    if (request.getParameter(param_name).length() < 1)
      return "";
    return request.getParameter(param_name);
  }
}
