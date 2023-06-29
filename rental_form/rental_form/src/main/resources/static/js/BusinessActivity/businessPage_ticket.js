let num_ = 1
let pag = 1;
let st = "dateOfReview";
let d = "desc";
let text = ""

function removeTicket(value){
  let request = {
    updateInitiator: sessionStorage.getItem("userLog"),
    command: "DELETE",
    referenceReport: value
  }
  $.ajax({
    url : "http://localhost:5050/updateNotification",
    data : JSON.stringify(request),
    dataType : "json",
    type : "PATCH",
    contentType: 'application/json',
    success: function (data) {
      alert("Report removed correctly");
      generate_ticket();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function generate_ticket(){
  document.getElementById('ticket_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/get_report",
    data : {username : sessionStorage.getItem("userLog"), page : pag, sortTarget : st, direction : d, searchText: text},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-card w3-container";

        var userRev = document.createElement("p");
        userRev.textContent = value.category;
        userRev.style.fontWeight="bold";

        var h5 = document.createElement("p");
        h5.textContent = value.title;
        h5.style.fontWeight = "bold";
        h5.style.textAlign= "center";

        var h5_rating = document.createElement("p");
        h5_rating.textContent = value.status;
        h5_rating.style.fontWeight = "bold";
        h5_rating.style.textAlign= "center";

        var h6 = document.createElement("h6");
        h6.textContent = value.dateOfReport.substring(0,10);
        h6.className = "w3-text-teal";
        h6.style.textAlign= "center";

        var p = document.createElement("h5");
        p.textContent = value.description;

        div.append(p);
        div.append(h6);
        div.append(userRev);
        div.append(h5);
        div.append(p);

        if(value.hasOwnProperty("review")){
          var dateOfReview = document.createElement("p");
          dateOfReview.textContent = value.review.dateOfReview.substring(0,10);
          dateOfReview.style.fontWeight="bold";

          var username = document.createElement("p");
          username.textContent = value.review.username;
          username.style.fontWeight = "bold";
          username.style.textAlign= "center";

          var subject = document.createElement("p");
          subject.textContent = value.review.subject;
          subject.style.fontWeight = "bold";
          subject.style.textAlign= "center";

          var review = document.createElement("h6");
          review.textContent = value.review.review;
          dateOfReview.className = "w3-text-teal";
          review.style.textAlign= "center";

          var rating = document.createElement("h5");
          rating.textContent = value.review.rating;

          div.append(dateOfReview);
          div.append(username);
          div.append(subject);
          div.append(review);
          div.append(rating);
        }

        div.append(h5_rating);

        if(value.hasOwnProperty("response")){
          var response = document.createElement("h5");
          response.textContent = value.response;
          response.style.textAlign= "center";
          div.append(response);
        }else{
      
          if(value.status != "CLOSED"){
            var btn = document.createElement("button");
            btn.textContent = "Modify report";
            btn.onclick = function(){
              sessionStorage.setItem("status",value.status);
              sessionStorage.setItem("title",value.title);
              sessionStorage.setItem("description",value.description);
              sessionStorage.setItem("date",value.dateOfReport);
              sessionStorage.setItem("id",value.reportId);
              location.href = "../../templates/ticketForm.html";
            };
            div.append(btn);
          div.append(document.createElement("hr"));
          }
        }
        var remove_btn = document.createElement("button");
        remove_btn.textContent = "Remove report";
        remove_btn.onclick = function(){
          removeTicket(value);
        };

        
        
        div.append(remove_btn);
        div.append(document.createElement("hr"));
        
        document.getElementById("ticket_list").append(div);
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
    //dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      JSON.stringify(data)
      document.getElementById("business_name").innerText = data.username;
      document.getElementById("business_address").innerText = data.city;
      document.getElementById("business_mail").innerText = data.address;
      document.getElementById("business_pn").innerText = data.phoneNumber;
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
  generate_ticket()
});


document.getElementById('reservation').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reservations.html";
};

document.getElementById('reviews').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reviews.html";
};

document.getElementById('vehicles').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_vehicles.html";
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