let pag_rev = 1;
let st_rev = "dateOfReview";
let d_rev = "desc";
let inputSearchText_rev = "";

function removeReview(value){

  let reviewToRemove = {
    username: sessionStorage.getItem("userLog"),
    review: value
  }

  $.ajax({
    url : "http://localhost:5050/removeReview",
    data : JSON.stringify(reviewToRemove),
    dataType : "json",
    type : "DELETE",
    contentType: 'application/json',
    success: function (data) {
      generate_review();
    },
    error: function(xhr) {
      generate_review();
    }
  })
}

function generate_review(){
  document.getElementById('reviews_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/getReviews",
    data : {username : sessionStorage.getItem("userLog"), page : pag_rev, sortTarget : st_rev, direction : d_rev, searchText:inputSearchText_rev},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-container";

        var userRev = document.createElement("p");
        userRev.textContent = value.businessActivity;
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

        var btn = document.createElement("button");
        btn.textContent = "Modify review»";
        btn.onclick = function(){
          sessionStorage.setItem("subject",value.subject);
          sessionStorage.setItem("rating",value.rating);
          sessionStorage.setItem("review",value.review);
          sessionStorage.setItem("date",value.dateOfReview);
          location.href = "../../templates/User/userPage_activities.html";
        };

        var remove_btn = document.createElement("button");
        remove_btn.textContent = "Remove review»";
        remove_btn.onclick = function(){
          removeReview(value);
        };
      
        div.append(userRev);
        div.append(h5);
        div.append(h5_rating);
        div.append(h6);
        div.append(p);
        div.append(btn);
        div.append(document.createElement("hr"));
        div.append(remove_btn);
        document.getElementById("reviews_list").append(div);
      });
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

$(document).ready(function () {
  generate_review();
});

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

//responsive input field
document.getElementById("rev_input").onkeyup = function(){
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
  inputSearchText_rev = document.getElementById("rev_input").value;
  generate_review();
}
const processChange = debounce(() => saveInput());


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

document.getElementById('next').onclick = function () {
  pag_rev++;
  generate_review();
};

document.getElementById('previous').onclick = function () {
  pag_rev--;
  generate_review();
};

document.getElementById('logout').onclick = function () {
  $.ajax({
    url : "http://localhost:5050/logout",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      sessionStorage.clear();
      alert("Logout complete");
      location.href = "../../templates/home.html";
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
};