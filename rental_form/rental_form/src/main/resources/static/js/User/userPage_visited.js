let page_number_follower = 1;
let d_follower = "ASC";
let st_follower = "";
let d_followed  = "ASC";
let st_followed = "";
let page_number_followed = 1;

let num_rev = 1
let pag_rev = 1;
let st_rev = "dateOfReview";
let d_rev = "desc";
let inputSearchText_rev = "";

function unfollowUser(user){
  $.ajax({
    url : "http://localhost:5050/unfollowUser",
    data : {username: user},
    type : "POST",
    success: function (data) {
      alert("unfollow");
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
      alert("follow");
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function banUser(){
  $.ajax({
    url : "http://localhost:5050/admin/remove_user",
    data : sessionStorage.getItem("visited"),
    type : "DELETE",
    dataType : "json",
    contentType: 'application/json',
    success: function (data) {
      alert("USER BANNED");
      //location.href = "../../templates/User/userPage_main.html";
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function generate_review(){
  document.getElementById('reviews_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/getReviews",
    data : {username : sessionStorage.getItem("visited"), page : pag_rev, sortTarget : st_rev, direction : d_rev, searchText:inputSearchText_rev},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-container";

        var userRev = document.createElement("p");
        userRev.textContent = value.username;
        userRev.style.fontWeight="bold";

        var h5 = document.createElement("h5");
        h5.textContent = value.subject;
        h5.style.fontWeight = "bold";
        h5.style.textAlign= "center";

        var h5_rating = document.createElement("h5");
        h5_rating.textContent = value.rating;
        h5_rating.style.fontWeight = "bold";
        h5_rating.style.textAlign= "center";

        var h6 = document.createElement("h6");
        h6.textContent = value.dateOfReview.substring(0,10);
        h6.className = "w3-text-teal";
        h6.style.textAlign= "center";

        var p = document.createElement("p");
        p.textContent = value.review;
      
        div.append(userRev);
        div.append(h5);
        div.append(h5_rating);
        div.append(h6);
        div.append(p);
        document.getElementById("reviews_list").append(div);
      });
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
    data : {username : sessionStorage.getItem("visited"), page : page_number_follower, direction: d_follower, searchText: st_follower},
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

        li.appendChild(img);
        li.appendChild(span);
        li.appendChild(span0);
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
    data : {username : sessionStorage.getItem("visited"), page : page_number_followed, direction: d_followed, searchText: st_followed},
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

        li.appendChild(img);
        li.appendChild(span);
        li.appendChild(span0);
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
      data : {username : sessionStorage.getItem("visited")},
      type : "GET",
      contentType: 'application/json',
      success: function (data) {
        JSON.stringify(data)
        document.getElementById("firstName").innerText = data.firstName;
        document.getElementById("work").innerText = data.occupation;
        document.getElementById("address").innerText = JSON.stringify(data.address).replace(/(\r\n|\n|\r| |"|-)/gm,"");
        document.getElementById("email").innerText = data.email;
        document.getElementById("phone").innerText = data.phoneNumber.replace(/(\r\n|\n|\r| )/gm, "");

        if(sessionStorage.getItem("isAdmin") == "true"){
          var adminBtn = document.createElement('button');
          adminBtn.className="w3-button w3-padding-large w3-white w3-border";
          adminBtn.textContent = "BAN USER: "
          adminBtn.style.fontWeight = "bold";
          adminBtn.onclick = function () {
            banUser();
          };
          document.getElementById("nav").appendChild(adminBtn);
        }

        var followBtn = document.createElement('button');
        followBtn.className="w3-button w3-padding-large w3-white w3-border";
        followBtn.textContent = "FOLLOW"
        followBtn.style.fontWeight = "bold";
        followBtn.onclick = function () {
          followUser(sessionStorage.getItem("visited"));
          generate_followed();
          generate_follower();
        };
        document.getElementById("nav").appendChild(followBtn);

        var unfollowBtn = document.createElement('button');
        unfollowBtn.className="w3-button w3-padding-large w3-white w3-border";
        unfollowBtn.textContent = "UNFOLLOW "
        unfollowBtn.style.fontWeight = "bold";
        unfollowBtn.onclick = function () {
          unfollowUser(sessionStorage.getItem("visited"));
          generate_followed();
          generate_follower();
        };
        document.getElementById("nav").appendChild(unfollowBtn);

      },
      error: function(xhr) {
        alert(xhr.responseText)
      }
  })
  generate_followed()
  generate_follower();
  generate_review()
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



function change_param_rev(){
  if(document.getElementById('rev_order').value == 1){
    st_rev = "dateOfReview";
    d_rev = "desc";
  }
  else if(document.getElementById('rev_order').value == 2){
    st_rev = "dateOfReview";
    d_rev = "asc";
  }
  else if(document.getElementById('rev_order').value == 3){
    st_rev = "rating";
    d_rev = "asc";
  }
  else{
    st_rev = "rating";
    d_rev = "desc";
  }
  generate_review();
};

document.getElementById("rev_input").onkeyup = function(){
  processChange3()
}

function debounce(func, timeout = 700){
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => { func.apply(this, args); }, timeout);
  };
}
function saveInput3(){
  inputSearchText_rev = document.getElementById("rev_input").value;
  generate_review();
}
const processChange3 = debounce(() => saveInput3());

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

document.getElementById('logout').onclick = function () {
  $.ajax({
    url : "http://localhost:5050/logout",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      sessionStorage.clear();
      location.href = "../../templates/home.html";
      alert("Logout complete");
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
};

document.getElementById('lente').onclick = function () {
  sessionStorage.setItem("visited", document.getElementById("searchUser").value);
  location.href = "../../templates/User/userPage_visited.html";
};