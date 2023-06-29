let generateUrl;
let pag = 1;
let d = "desc";

if(sessionStorage.getItem("analytics") == "1"){
  generateUrl = "http://localhost:5050/getMostRentedVehicle"; 
}else if(sessionStorage.getItem("analytics") == 2){ //fatta
  generateUrl = "http://localhost:5050/getRankingBusiness";
}else if(sessionStorage.getItem("analytics") == 3){
  generateUrl = "http://localhost:5050/get_most_followed_user";
}else if(sessionStorage.getItem("analytics") == 4){//fatta
  generateUrl = "http://localhost:5050/getMostActiveUsers";
}else if(sessionStorage.getItem("analytics") == 5){//fatta
  generateUrl = "http://localhost:5050/getRankingHistoricVehicles";
}else{//fatta
  generateUrl = "http://localhost:5050/get_recommended_business_activity";
}

function generate_analytics(){
  document.getElementById("list").innerHTML="";

  if(sessionStorage.getItem("analytics") == "1"){

    document.getElementById("list").innerHTML="";
    var nome = document.createElement("input");
    nome.placeholder="Insert business name";
    nome.textContent = "";
    nome.id = "name";
    var eta = document.createElement("input");
    eta.id = "age";
    eta.textContent = "";
    eta.placeholder="Insert age";
    var send = document.createElement("button");
    send.textContent = "SEND";

    var query = document.createElement("p");
    query.textContent = "Most rented vehicle brand by age group";
    document.getElementById("list").append(query);
    send.onclick = function(){
      document.getElementById("list").innerHTML="";
      
      document.getElementById("list").appendChild(query);
      document.getElementById("list").appendChild(nome);
      document.getElementById("list").appendChild(eta);
      document.getElementById("list").appendChild(send);
      $.ajax({
        url : generateUrl,
        data: {name: document.getElementById("name").value, age:document.getElementById("age").value},
        type : "GET",
        contentType: 'application/json',
        success: function (data) {
          
          
          $.each( data, function(key,value){
            var div = document.createElement("div");
            div.className = "w3-margin w3-card w3-container";
    
            var city = document.createElement("p");
            city.textContent = value.city;
            city.style.fontWeight="bold";
    
            var h5 = document.createElement("h5");
            h5.textContent = value.brand;
            h5.style.textAlign= "center";
  
            var h5_cat = document.createElement("h5");
            h5_cat.textContent = value.category;
            h5_cat.style.textAlign= "center";
    
            var h5_rating = document.createElement("h5");
            h5_rating.textContent = value.count;
            h5_rating.style.textAlign= "center";
          
            div.append(city);
            div.append(h5);
            div.append(h5_cat);
            div.append(h5_rating);
            document.getElementById("list").append(div);
          });
        },
        error: function(xhr) {
          alert(xhr.responseText)
        }
      })
    }
    document.getElementById("list").appendChild(nome);
    document.getElementById("list").appendChild(eta);
    document.getElementById("list").appendChild(send);
    
  }
  
  else if(sessionStorage.getItem("analytics") == "2"){
    $.ajax({
      url : generateUrl,
      type : "GET",
      contentType: 'application/json',
      success: function (data) {

        var query = document.createElement("p");
        query.textContent = "Top activities ranked by average rating for each city";
        query.style.fontWeight="bold";
        document.getElementById("list").append(query);
        
        $.each( data, function(key,value){
          var div = document.createElement("div");
          div.className = "w3-margin w3-card w3-container";
  
          var city = document.createElement("p");
          city.textContent = value.city;
          city.style.fontWeight="bold";
  
          var h5 = document.createElement("h5");
          h5.textContent = value.name;
          h5.style.textAlign= "center";
  
          var h5_rating = document.createElement("h5");
          h5_rating.textContent = value.rating;
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

  else if(sessionStorage.getItem("analytics") == "3"){
    $.ajax({
      url : generateUrl,
      data: {username: sessionStorage.getItem("userLog"), page: pag, direction: d},
      type : "GET",
      contentType: 'application/json',
      success: function (data) {

        var query = document.createElement("p");
        query.textContent = "Most followed user among followed ones";
        query.style.fontWeight="bold";
        document.getElementById("list").append(query);
        
        $.each( data, function(key,value){
          var div = document.createElement("div");
          div.className = "w3-margin w3-card w3-container";
  
          var h5 = document.createElement("h5");
          h5.textContent = value.username;
          h5.style.textAlign= "center";

          var h5_rating = document.createElement("h5");
          h5_rating.textContent = value.follower;
          h5_rating.style.textAlign= "center";

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

  else if(sessionStorage.getItem("analytics") == "4"){
    $.ajax({
      url : generateUrl,
      data: {username: sessionStorage.getItem("userLog")},
      type : "GET",
      contentType: 'application/json',
      success: function (data) {

        var query = document.createElement("p");
        query.textContent = "Most active users among followed ones";
        query.style.fontWeight="bold";
        document.getElementById("list").append(query);
        
        $.each( data, function(key,value){
          var div = document.createElement("div");
          div.className = "w3-margin w3-card w3-container";
  
          var h5 = document.createElement("h5");
          h5.textContent = value.username;
          h5.style.textAlign= "center";

          div.append(h5);
          document.getElementById("list").append(div);
        });
      },
      error: function(xhr) {
        alert(xhr.responseText)
      }
    })
  }

  else if(sessionStorage.getItem("analytics") == "5"){
    $.ajax({
      url : generateUrl,
      data: {year:2000},
      type : "GET",
      contentType: 'application/json',
      success: function (data) {

        var query = document.createElement("p");
        query.textContent = "Activity ranking by last year earnings for historic vehicles";
        query.style.fontWeight="bold";
        document.getElementById("list").append(query);
        
        $.each( data, function(key,value){
          var div = document.createElement("div");
          div.className = "w3-margin w3-card w3-container";
  
          var h5 = document.createElement("h5");
          h5.textContent = value.name;
          h5.style.textAlign= "center";

          var h5_rating = document.createElement("h5");
          h5_rating.textContent = value.count + "$";
          h5_rating.style.textAlign= "center";

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
  else{
    $.ajax({
      url : generateUrl,
      data: {username: sessionStorage.getItem("userLog"), page: pag, direction: d},
      type : "GET",
      contentType: 'application/json',
      success: function (data) {

        var query = document.createElement("p");
        query.textContent = "Show recommended rental activity based on the rating of your followers";
        query.style.fontWeight="bold";
        document.getElementById("list").append(query);
        
        $.each( data, function(key,value){
          var div = document.createElement("div");
          div.className = "w3-margin w3-card w3-container";
  
          var h5 = document.createElement("h5");
          h5.textContent = value.name;
          h5.style.textAlign= "center";

          var h5_rating = document.createElement("h5");
          h5_rating.textContent = value.rating;
          h5_rating.style.textAlign= "center";

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
  generate_analytics();
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