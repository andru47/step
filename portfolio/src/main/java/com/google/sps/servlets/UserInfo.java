package com.google.sps.servlets;

import com.google.sps.classes.Comment;
import com.google.sps.classes.AuthData;
import com.google.sps.classes.User;

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
import javax.swing.RepaintManager;

@WebServlet("/user-info")
public class UserInfo extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    
    if (!userService.isUserLoggedIn()) { //Make sure that user is logged in to avoid Error 500
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"Please login first.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    String id = userService.getCurrentUser().getUserId();
    Gson gson = new Gson();

    Query userQuery = new Query("Users").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery result = datastore.prepare(userQuery);
    Entity givenEntity = result.asSingleEntity();

    if (givenEntity == null){
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(new User("", "", ""))); //Return an empty user to indicate in JS that they need to add a nickname first
      return;
    }

    User user = new User((String) givenEntity.getProperty("email"), 
                         (String) givenEntity.getProperty("nickname"),
                         (String) givenEntity.getProperty("id"));

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(user));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) { //Make sure that user is logged in to avoid Error 500
      response.setContentType("text/html");
      response.getWriter().print("<script>alert(\"Please login first.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }
    
    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();
    String nickname = request.getParameter("nickname");

    if (nickname.isEmpty()) {
      response.setContentType("text/html");
      response.getWriter()
          .print("<script>alert(\"The entered nickname must contain at least one character.\")</script>");
      RequestDispatcher dispatcher = request.getRequestDispatcher("/comments.html");
      dispatcher.include(request, response);
      return;
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("Users", id);

    entity.setProperty("id", id);
    entity.setProperty("email", email);
    entity.setProperty("nickname", nickname);

    datastore.put(entity);
    response.sendRedirect("/comments.html");
  }

}
