package com.google.sps.servlets;

import com.google.sps.classes.Comment;
import com.google.sps.classes.AuthData;

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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    response.setContentType("application/json;");
    Gson gson = new Gson();
    if (userService.isUserLoggedIn())
    {
      String logoutUrl = userService.createLogoutURL("/comments.html");
      response.getWriter().println(gson.toJson(new AuthData(true, logoutUrl)));
    }
    else{
      String loginUrl = userService.createLoginURL("/comments.html");
      response.getWriter().println(gson.toJson(new AuthData(false, loginUrl)));
    }
  }
}
