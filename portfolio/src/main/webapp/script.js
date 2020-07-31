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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function navbar_set_active_item() {
  var current_webpage = location.pathname.substring(1); //Don't count the leading /
  if (current_webpage.length < 1) //Don't need to set an active item if on index
    return;
  if (current_webpage[0] == 'a')
    document.getElementById("about").classList.add("active");
  else if (current_webpage[0] == 'r')
    document.getElementById("resume").classList.add("active");
  else if (current_webpage[0] == 's')
    document.getElementById("social").classList.add("active");
  else if (current_webpage[0] == 'c')
    document.getElementById("comments").classList.add("active");
}

function redirectToPath(givenPath) {
  window.open(givenPath, "_blank");
}

function getRandomIntegerWithinInterval(left, right) {
  if (left > right)
    [left, right] = [right, left];
  return Math.floor(Math.random() * (right - left + 1)) + left;
}

function showImage() {
  var image = new Image();
  image.src = 'resources/pets';
  image.style.maxHeight = "40vh";
  image.style.maxWidth = "auto";

  var randomNumber = getRandomIntegerWithinInterval(1, 6);
  image.src += randomNumber + '.JPG';

  document.getElementById("image-container").innerHTML = ''; // Remove image if existing
  document.getElementById("image-container").appendChild(image);
}

function fetchComments() {
  var nr = document.getElementById("number_comments").value;
  var lang = document.getElementById("lang_code").value;
  document.getElementsByClassName("comment-section")[0].innerHTML = '';
  fetch('/comments?how_many=' + nr + "&lang=" + lang).then(response => response.json()).then(messages => {
    messages.forEach(message => {
      makeElement(message.nickname, message.comment);
    })
  }).then(loadAuthInformation());
}

function makeElement(nickname, message) {
  var newComment = document.createElement("li");
  newComment.classList.add("comment");

  var newDiv = document.createElement("div");
  newDiv.classList.add("comment-info");
  newDiv.innerText = nickname;

  var newP = document.createElement("p");
  newP.innerText = message;

  newComment.appendChild(newDiv);
  newComment.appendChild(newP);

  document.getElementsByClassName("comment-section")[0].appendChild(newComment);
}

function deleteComments() {
  fetch("/delete-comment", { method: 'POST' }).then(response => fetchComments());
}

function loadAuthInformation() {
  fetch('/auth').then(response => response.json()).then(handler => {
    if (handler.isLoggedIn) {
      document.getElementById("hide-id").style.display = "";

      var LogoutButton = document.createElement("button");
      LogoutButton.innerText = "Logout";
      LogoutButton.classList.add("btn");
      LogoutButton.classList.add("btn-danger");
      LogoutButton.onclick = function () { window.open(handler.authLink, "_self"); };

      document.getElementById("comments-link").innerHTML = "";
      document.getElementById("comments-link").appendChild(LogoutButton);
      loadNickname();
    }
    else {
      document.getElementById("hide-id").style.display = "none";

      var LoginButton = document.createElement("button");
      LoginButton.innerText = "Login to see the contents";
      LoginButton.classList.add("btn");
      LoginButton.classList.add("btn-success");
      LoginButton.onclick = function () { window.open(handler.authLink, "_self"); };

      document.getElementById("comments-link").innerHTML = "";
      document.getElementById("comments-link").appendChild(LoginButton);
      return false;
    }
  });
}

function loadNickname() {
  fetch("/user-info").then(response => response.json()).then(user => {
    var nickname = user.nickname;
    var email = user.email;
    var id = user.id;
    if (nickname.length < 1) {
      document.getElementById("nickname-container").style.display = "";
    }
    else {
      document.getElementById("hide-unlogged").style.display = "";
      document.getElementById("nickname-display").style.display = "";

      var elem = document.createElement("p");
      elem.innerText = "Your nickname is currently: " + nickname;

      var anchor = document.createElement("a");
      anchor.setAttribute("href", "#");
      anchor.setAttribute("onclick", "showElementByID('nickname-container'); hideElementByID('hide-unlogged')");
      anchor.innerHTML = "here";

      document.getElementById("nickname-display").innerHTML = "If you want to change your nickname click ";
      document.getElementById("nickname-display").appendChild(anchor);
      document.getElementById("nickname-display").appendChild(elem);
    }

  })
}

function showElementByID(ID) {
  document.getElementById(ID).style.display = "";
}

function hideElementByID(ID) {
  document.getElementById(ID).style.display = "none";
}
