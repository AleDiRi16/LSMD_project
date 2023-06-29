let pag_rev = 1;
let st_rev = "dateOfReview";
let d_rev = "desc";
let inputSearchText_rev = "";

function reportReview(value){
  let t = {
    description : "I have a problem with this review",
    category: "HELP",
    title: "REVIEW PROBLEM",
    review: value
  }

  let request = {
    username : sessionStorage.getItem("userLog"),
    requestCategory: "REPORT",
    report : t
  }
      

  $.ajax({
    url : "http://localhost:5050/sendNotification",
    data : JSON.stringify(request),
    //dataType : "json",
    type : "POST",
    contentType: 'application/json',
    success: function (data) {
        alert("Notification has been sent");
    },
    error: function(xhr) {
        alert(xhr.responseText)
    }
  })
}

function generate_review(){
  document.getElementById("list").innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/business/getReviews",
    data : {businessActivityName: sessionStorage.getItem("business_name"), page : pag_rev, sortTarget : st_rev, direction : d_rev, searchText: inputSearchText_rev},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){

        var div = document.createElement("div");
        div.className = "w3-margin w3-card w3-container";

        var username = document.createElement("h5");
        username.textContent = value.username;

        var h5 = document.createElement("h5");
        h5.textContent = value.subject;
        h5.style.fontWeight = "bold";
        //h5.style.textAlign = "center";

        var h5_rating = document.createElement("h5");
        h5_rating.textContent = value.rating;
        h5_rating.style.fontWeight = "bold";
        h5_rating.style.textAlign = "right";

        var h6 = document.createElement("h6");
        h6.textContent = value.dateOfReview.substring(0,10);
        h6.className = "w3-text-teal";
        //h6.style.textAlign = "center";

        var p = document.createElement("p");
        p.textContent = value.review;

        var reportBtn = document.createElement("button");
        reportBtn.textContent = "REPORT REVIEW";
        reportBtn.onclick = function(){
          reportReview(value);
        }
      
        
        div.append(h5_rating);
        div.append(username);
        div.append(h5);
        div.append(h6);
        div.append(p);
        div.append(reportBtn);

        document.getElementById("list").append(div);
      });
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

document.getElementById('next').onclick = function () {
  page_number++;
  generate_car();
};

document.getElementById('previous').onclick = function () {
  page_number--;
  generate_car();
};

document.getElementById('reservation').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reservations.html";
};

document.getElementById('vehicles').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_vehicles.html";
};

$(document).ready(function () {
  $.ajax({
    url : "http://localhost:5050/userProfile",
    data : {username : sessionStorage.getItem("userLog")},
    //dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      JSON.stringify(data)
      document.getElementById("business_name").innerText = data.username;
      document.getElementById("business_address").innerText = data.city;
      document.getElementById("business_mail").innerText = data.address;
      document.getElementById("business_pn").innerText = data.phoneNumber;
      sessionStorage.setItem("business_name", data.username);
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
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
      alert("Logout complete");
      location.href = "../../templates/home.html";
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
};