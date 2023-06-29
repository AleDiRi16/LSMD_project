let generateUrl;
let pag = 1;
let d = "desc";

if(sessionStorage.getItem("analytics") == "1"){
  generateUrl = "http://localhost:5050/getMostRentedVehicle"; 
}else if(sessionStorage.getItem("analytics") == 2){
  generateUrl = "http://localhost:5050/getRankingBusiness";
}

function generate_analytics(){
  document.getElementById("list").innerHTML="";

  if(sessionStorage.getItem("analytics") == 1){
    let req = {
      command: "VEHICLE_BY_WORKER",
      businessUsername: sessionStorage.getItem("userLog"),
      startDate: new Date(),
      endDate: new Date()
    }

    request = JSON.stringify(req);
    $.ajax({
      url : "http://localhost:5050/business/analytics",
      data: request,
      dataType: "json",
      type : "POST",
      contentType: 'application/json',
      success: function (data) {

        var query = document.createElement("p");
        query.textContent = "Most used vehicle for user work typology";
        query.style.fontWeight="bold";
        document.getElementById("list").append(query);
        
        $.each( data, function(key,value){
          var div = document.createElement("div");
          div.className = "w3-margin w3-card w3-container";
  
          var city = document.createElement("p");
          city.textContent = value._id.occupation;
          city.style.fontWeight="bold";
  
          var h5 = document.createElement("h5");
          h5.textContent = value.vehicle;
          h5.style.textAlign= "center";
  
          var h5_rating = document.createElement("h5");
          h5_rating.textContent = value.count;
          h5_rating.style.textAlign= "center";
        
          div.append(city);
          div.append(h5);
          div.append(h5_rating);
          document.getElementById("list").append(div);
        });
      },
      error: function(xhr) {
        alert(xhr.responseText)
      }
    })
  }
}

function generate_analytics2(){
  let req = {
    command: "VEHICLE_BY_DATE",
    businessUsername: sessionStorage.getItem("userLog"),
    startDate: new Date(document.getElementById("start").value),
    endDate: new Date(document.getElementById("end").value)
  }

  request = JSON.stringify(req);
  $.ajax({
    url : "http://localhost:5050/business/analytics",
    data: request,
    dataType: "json",
    type : "POST",
    contentType: 'application/json',
    success: function (data) {

      
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-card w3-container";

        var city = document.createElement("p");
        city.textContent = value._id.vehicle;
        city.style.fontWeight="bold";

        var h5_rating = document.createElement("h5");
        h5_rating.textContent = value.count;
        h5_rating.style.textAlign= "center";
      
        div.append(city);
        div.append(h5_rating);
        document.getElementById("list").append(div);
      });
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
    dataType : "json",
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
  sessionStorage.setItem("business_name", document.getElementById("business_name").value);
  generate_analytics();
  if(sessionStorage.getItem("analytics") == 2){
    var query = document.createElement("p");
    query.textContent = "For each vehicle, total number of rentals in a range of N years";
    query.style.fontWeight="bold";
    document.getElementById("list").append(query);
  }
});


document.getElementById('1').onclick = function () {
  sessionStorage.setItem("analytics","1");
  location.href = "./businessPage_analytics.html";
};

document.getElementById('2').onclick = function () {
  sessionStorage.setItem("analytics","2");
  location.href = "./businessPage_analytics.html";
};

document.getElementById('reservation').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reservations.html";
};

document.getElementById('reviews').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reviews.html";
};

document.getElementById('next').onclick = function () {
  pag++;
  generate_analytics();
};

document.getElementById('previous').onclick = function () {
  pag--;
  generate_analytics();
};

function change_param(){
  if(document.getElementById('order').value == 1){
    d = "desc";
  }
  else if(document.getElementById('order').value == 2){
    d = "asc";
  }
  generate_analytics();
};

document.getElementById('submit').onclick = function () {
  generate_analytics2();
};

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