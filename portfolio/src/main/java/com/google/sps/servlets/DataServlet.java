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

import com.google.sps.classes.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns some example content. TODO: modify this file to handle
 * comments data
 */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  private List<Comment> arr = new ArrayList<>();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Comment newComment = new Comment(getParameter(request, "message"), getParameter(request, "name"));
    arr.add(newComment);
    response.sendRedirect("/comments.html");
  }

  private String getParameter(HttpServletRequest request, String param_name) {
    if (request.getParameter(param_name).length() < 1)
      return "Default value"; // To be modified in the future to show a warning
    return request.getParameter(param_name);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    Gson gson = new Gson();
    String json = gson.toJson(arr);
    response.getWriter().println(json);
  }
}
