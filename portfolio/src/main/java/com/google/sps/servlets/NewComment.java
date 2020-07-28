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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    if (!userService.isUserLoggedIn()) {
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"Please login first.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    if (!Captcha) {
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"Please verify that you are a human\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    String uid = userService.getCurrentUser().getUserId();
    Query usersQuery = new Query("Users").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, uid));
    PreparedQuery result = datastore.prepare(usersQuery);
    Entity givenEntity = result.asSingleEntity();
    String nickname = (String) givenEntity.getProperty("nickname");
    String entered_message = getParameter(request, "message");

    if (nickname.isEmpty()) { //Don't let an user without a nickname to post a comment.
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"You must set your nickname first.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    if (entered_message.isEmpty()) {
      response.setContentType("text/html");
      response.getWriter()
          .print("<script>alert(\"The entered message must contain at least one character.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    Entity taskEntity = new Entity("Comment");
    taskEntity.setProperty("id", uid); //store the user's ID to deal with nickname changes
    taskEntity.setProperty("message", entered_message);
    taskEntity.setProperty("timestamp", System.currentTimeMillis());

    datastore.put(taskEntity);

    response.sendRedirect("/comments.html");
  }

  private String getParameter(HttpServletRequest request, String param_name) {
    if (request.getParameter(param_name).isEmpty())
      return "";
    return request.getParameter(param_name);
  }
}
