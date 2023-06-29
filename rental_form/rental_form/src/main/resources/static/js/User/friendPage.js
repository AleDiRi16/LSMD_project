let page_number_follower = 1;
let d_follower = "ASC";
let st_follower = "";
let d_followed  = "ASC";
let st_followed = "";
let page_number_followed = 1;

function unfollowUser(user){
  $.ajax({
    url : "http://localhost:5050/unfollowUser",
    data : {username: user},
    type : "POST",
    success: function (data) {
      generate_follower();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function followUser(user){
  $.ajax({
    url : "http://localhost:5050/followUser",
    data : {username: user},
    type : "POST",
    success: function (data) {
      generate_follower()
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function generate_follower(){
  document.getElementById('follower_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/getFollowerList",
    data : {username : sessionStorage.getItem("userLog"), page : page_number_follower, direction: d_follower, searchText: st_follower},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      for(let x=0; x<data.length;x++) {
        var li = document.createElement("li");
        li.className="w3-padding-32";
        var img = document.createElement("img");
        img.className="w3-left w3-margin-right";
        img.src = "../../static/img/index.png";
        img.width="50";
        img.onclick = function(){
          sessionStorage.setItem("visited", data[x]);
          location.href = "../../templates/User/userPage_visited.html";
        }

        var span = document.createElement("span");
        span.className="w3-large";


        var span0 = document.createElement("span");
        span0.textContent=data[x];

        var btn = document.createElement('button');
        btn.className="w3-button w3-padding-large w3-white w3-border";
        btn.id='business';
        var b1 = document.createElement("b");
        b1.textContent="UNFOLLOW»";
        btn.appendChild(b1);
        btn.style.cssFloat='right';
        btn.onclick = function(){
          unfollowUser(data[x]);
        }

        li.appendChild(img);
        li.appendChild(span);
        li.appendChild(span0);
        li.appendChild(btn);
        document.getElementById('follower_list').appendChild(li);
      }
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function generate_followed(){
  document.getElementById('list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/getFollowedList",
    data : {username : sessionStorage.getItem("userLog"), page : page_number_followed, direction: d_followed, searchText: st_followed},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      for(let x=0; x<data.length;x++) {
        var li = document.createElement("li");
        li.className="w3-padding-32";
        var img = document.createElement("img");
        img.className="w3-left w3-margin-right";
        img.src = "../../static/img/index.png";
        img.width="50";
        img.onclick = function(){
          sessionStorage.setItem("visited", data[x]);
          location.href = "../../templates/User/userPage_visited.html";
        }

        var span = document.createElement("span");
        span.className="w3-large";

        var span0 = document.createElement("span");
        span0.textContent=data[x];

        var btn = document.createElement('button');
        btn.className="w3-button w3-padding-large w3-white w3-border";
        btn.id='business';
        var b1 = document.createElement("b");
        b1.textContent="FOLLOW»";
        btn.appendChild(b1);
        btn.style.cssFloat='right';
        btn.onclick = function(){
          followUser(data[x]);
        }

        li.appendChild(img);
        li.appendChild(span);
        li.appendChild(span0);
        li.appendChild(btn);
        document.getElementById('list').appendChild(li);
      }
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

$(document).ready(function () {

  $.ajax({
    url : "http://localhost:5050/userProfile",
    data : {username : sessionStorage.getItem("userLog")},
    //dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      JSON.stringify(data)
      document.getElementById("firstName").innerText = data.firstName;
      document.getElementById("work").innerText = data.occupation;
      document.getElementById("address").innerText = JSON.stringify(data.address).replace(/(\r\n|\n|\r| |"|-)/gm,"");
      document.getElementById("email").innerText = data.email;
      document.getElementById("phone").innerText = data.phoneNumber.replace(/(\r\n|\n|\r| )/gm, "");
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })

  generate_follower();
  generate_followed();
});


document.getElementById('home').onclick = function () {
  location.href = "../../templates/home.html";
};

document.getElementById('1').onclick = function () {
  sessionStorage.setItem("analytics","1");
  location.href = "../../templates/User/userPage_analytics.html";
};

document.getElementById('2').onclick = function () {
  sessionStorage.setItem("analytics","2");
  location.href = "../../templates/User/userPage_analytics.html";
};

document.getElementById('3').onclick = function () {
  sessionStorage.setItem("analytics","3");
  location.href = "../../templates/User/userPage_analytics.html";
};

document.getElementById('friend').onclick = function () {
  location.href = "../../templates/User/friendPage.html";
};

document.getElementById('profile').onclick = function () {
  location.href = "../../templates/User/userPage_main.html";
};

//responsive input field
document.getElementById("followed_input").onkeyup = function(){
  processChange()
}

function debounce(func, timeout = 700){
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => { func.apply(this, args); }, timeout);
  };
}
function saveInput(){
  st_followed = document.getElementById("followed_input").value;
  generate_followed();
}
const processChange = debounce(() => saveInput());

//responsive input field
document.getElementById("follower_input").onkeyup = function(){
  processChange2()
}

function saveInput2(){
  st_follower = document.getElementById("follower_input").value;
  generate_follower();
}
const processChange2 = debounce(() => saveInput2());

function change_param(){
  if(document.getElementById('followed_order').value == 1){
    d_followed = "asc";
    page_number_followed = 1;
  }
  else{
    d_followed = "desc";
    page_number_followed = 1;
  }
  generate_followed();
};

function change_param2(){
  if(document.getElementById('follower_order').value == 1){
    d_follower = "asc";
    page_number_follower = 1;
  }
  else{
    d_follower = "desc";
    page_number_follower = 1;
  }
  generate_follower();
};

document.getElementById('next').onclick = function () {
  page_number_followed++;
  generate_followed();
};

//previous arrow button
document.getElementById('previous').onclick = function () {
  page_number_followed--;
  generate_followed();
};

document.getElementById('next_follower').onclick = function () {
  page_number_follower++;
  generate_follower();
};

//previous arrow button
document.getElementById('previous_follower').onclick = function () {
  page_number_follower--;
  generate_follower();
};

document.getElementById('lente').onclick = function () {
  sessionStorage.setItem("visited", document.getElementById("searchUser").value);
  location.href = "../../templates/User/userPage_visited.html";
};

document.getElementById('logout').onclick = function () {
  $.ajax({
    url : "http://localhost:5050/logout",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      alert("Logout complete");
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
};