let num_ = 1
let pag = 1;
let st = "dateOfReview";
let d = "desc";
let text = ""

function assignNotification(value){
  let supportRequest
  
  if(value.hasOwnProperty("report")){
    supportRequest = {
      report: value.report,
      username: sessionStorage.getItem("userLog"),
      applicant: value.applicant,
      requestCategory: value.requestCategory
    }
  }
  else{
    supportRequest = {
      ticket: value.ticket,
      username: sessionStorage.getItem("userLog"),
      applicant: value.applicant,
      requestCategory: value.requestCategory
    }
  }

  $.ajax({
    url : "http://localhost:5050/admin/assign_open_notification",
    data : JSON.stringify(supportRequest),
    dataType : "json",
    type : "POST",
    contentType: 'application/json',
    success: function (data) {
      getOpen();  
      generate_ticket();
      generate_report();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function getOpen(){
  document.getElementById("ticket_list").innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/admin/get_all_open",
    data : {page : pag},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-card w3-container";

        var userRev = document.createElement("p");
        userRev.textContent = value.applicant;
        userRev.style.fontWeight="bold";

        var h5 = document.createElement("p");
        h5.textContent = value.requestCategory;
        h5.style.fontWeight = "bold";
        h5.style.textAlign= "center";

        var h5_rating = document.createElement("p");
        if(value.hasOwnProperty("report"))
          h5_rating.textContent = value.report.title;
        else
          h5_rating.textContent = value.ticket.title;
        h5_rating.style.fontWeight = "bold";
        h5_rating.style.textAlign= "center";

        var h6 = document.createElement("h6");
        if(value.hasOwnProperty("report"))
          h6.textContent = value.report.dateOfReport.substring(0,10);
        else
          h6.textContent = value.ticket.ticketDate.substring(0,10);
        h6.className = "w3-text-teal";
        h6.style.textAlign= "center";

        var p = document.createElement("h5");
        if(value.hasOwnProperty("report"))
          p.textContent = value.report.description;
        else
          p.textContent = value.ticket.description;

        if(value.hasOwnProperty("ticket")){
          var category = document.createElement("h5");
          category.textContent = value.ticket.category;
          div.append(category);
        }

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
      
        var btn = document.createElement("button");
        btn.textContent = "ASSIGN NOTIFICATION";
        btn.onclick = function(){
          sessionStorage.setItem("status",value.status);
          sessionStorage.setItem("title",value.title);
          sessionStorage.setItem("description",value.description);
          sessionStorage.setItem("category",value.category);
          sessionStorage.setItem("date",value.ticketDate);
          sessionStorage.setItem("id",value.ticketId);
          assignNotification(value);
        };

        /*var remove_btn = document.createElement("button");
        remove_btn.textContent = "Remove Ticket";
        remove_btn.onclick = function(){
          removeTicket(value);
        };*/

        div.append(p);
        div.append(h6);
        div.append(userRev);
        div.append(h5);
        div.append(p);
        div.append(h5_rating);
        div.append(btn);
        div.append(document.createElement("hr"));
        /*div.append(remove_btn);
        div.append(document.createElement("hr"));
        */
        document.getElementById("ticket_list").append(div);
      });
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function removeTicket(value){
  let com;
  if(sessionStorage.getItem("isAdmin") == "false")
    com = "DELETE";
  else
    com = "CLOSE";


  let request = {
    updateInitiator: sessionStorage.getItem("userLog"),
    command: com,
    referenceTicket: value
  }
  $.ajax({
    url : "http://localhost:5050/updateNotification",
    data : JSON.stringify(request),
    dataType : "json",
    type : "PATCH",
    contentType: 'application/json',
    success: function (data) {
      
      generate_ticket();
      if(sessionStorage.getItem("isAdmin") == "true"){
        generate_report();
        getOpen();  
      }
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function sendResponse(value){
  let request = {
    updateInitiator: sessionStorage.getItem("userLog"),
    command: "REPLY",
    referenceTicket: value,
    response: document.getElementById("risposta").value
  }
  $.ajax({
    url : "http://localhost:5050/updateNotification",
    data : JSON.stringify(request),
    dataType : "json",
    type : "PATCH",
    contentType: 'application/json',
    success: function (data) {
      getOpen();  
      generate_ticket();
      generate_report();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function sendResponseReport(value){
  let request = {
    updateInitiator: sessionStorage.getItem("userLog"),
    command: "REPLY",
    referenceReport: value,
    response: document.getElementById("risposta").value
  }
  $.ajax({
    url : "http://localhost:5050/updateNotification",
    data : JSON.stringify(request),
    dataType : "json",
    type : "PATCH",
    contentType: 'application/json',
    success: function (data) {
      getOpen();  
      generate_ticket();
      generate_report();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function generate_ticket(){
  document.getElementById('ticket_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/get_ticket",
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
        h6.textContent = value.ticketDate.substring(0,10);
        h6.className = "w3-text-teal";
        h6.style.textAlign= "center";

        var p = document.createElement("h5");
        p.textContent = value.description;
        p.style.textAlign= "left";

        div.append(p);
        div.append(h6);
        div.append(userRev);
        div.append(h5);
        div.append(p);
        div.append(h5_rating);
        
        if(value.hasOwnProperty("response")){
          var response = document.createElement("h5");
          response.textContent = value.response;
          response.style.textAlign= "center";
          div.append(response);
        }

        
        if(value.status != "CLOSED"){
          var btn = document.createElement("button");
          if(sessionStorage.getItem("isAdmin") == "false"){
            btn.textContent = "Modify Ticket";
            btn.onclick = function(){
              sessionStorage.setItem("status",value.status);
              sessionStorage.setItem("title",value.title);
              sessionStorage.setItem("description",value.description);
              sessionStorage.setItem("category",value.category);
              sessionStorage.setItem("date",value.ticketDate);
              sessionStorage.setItem("id",value.ticketId);
              location.href = "../../templates/ticketForm.html";
            };
            div.append(document.createElement("hr"));
          div.append(btn);
          }
          else if(!value.hasOwnProperty("response")){
            let risposta = document.createElement('input');
            risposta.id = "risposta";
            btn.textContent = "Answer Ticket";
            btn.onclick = function(){
              sendResponse(value);  
            };
            div.append(risposta);
            div.append(document.createElement("hr"));
          div.append(btn);
          }

          var remove_btn = document.createElement("button")
          if(sessionStorage.getItem("isAdmin") == "false"){
            remove_btn.textContent = "Remove Ticket";
            remove_btn.onclick = function(){
              removeTicket(value);
            };
          }
          else{
            remove_btn.textContent = "Close Ticket";
            remove_btn.onclick = function(){
              removeTicket(value);
            };
          }
          
          
          div.append(document.createElement("hr"));
          div.append(remove_btn);
          div.append(document.createElement("hr"));
        }
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
  if(sessionStorage.getItem("isAdmin") == "false")
    generate_ticket()
  else{
    getOpen();  
    generate_ticket();
    generate_report();
  }
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

function generate_report(){
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
        userRev.textContent = value.businessActivityName;
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
        }

        if(value.status != "CLOSED"){
          var btn = document.createElement("button");
          var remove_btn = document.createElement("button");

          if(!value.hasOwnProperty("response")){
            let risposta = document.createElement('input');
            risposta.id = "risposta";
            btn.textContent = "Answer report";
            btn.onclick = function(){
              sendResponseReport(value);
            };

            div.append(risposta);
            div.append(document.createElement("hr"));
            div.append(btn);
          }
          remove_btn.textContent = "Close report";
          remove_btn.onclick = function(){
            removeTicket(value);
          };
          div.append(document.createElement("hr"));
          div.append(remove_btn);
        }
        
        document.getElementById("ticket_list").append(div);
      });
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

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

document.getElementById("business_input").onkeyup = function(){
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
  text = document.getElementById("business_input").value;
  generate_ticket();
}
const processChange = debounce(() => saveInput());