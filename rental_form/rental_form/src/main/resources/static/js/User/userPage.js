let pag = 1;
let st = "endDate";
let d = "desc";
let inputSearchText = "";

function removeActivities(value){  
  let res = {
    username: sessionStorage.getItem("userLog"),
    rentingReservation: value
  } 
  $.ajax({
    url : "http://localhost:5050/removeReservation",
    data : JSON.stringify(res),
    dataType : "json",
    type : "POST",
    contentType: 'application/json',
    success: function (data) {
      generate_reservation();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
  generate_reservation()
}

function generate_reservation(){
  document.getElementById('activities_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/getReservations",
    data : {username : sessionStorage.getItem("userLog"), Page : pag, sortTarget : st, direction : d, searchText : inputSearchText},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var li = document.createElement("li");
        li.className="w3-padding-32";

        var h5 = document.createElement("h5");
        h5.textContent = value.businessActivity;
        h5.className = "w3-opacity";

        var h6 = document.createElement("h6");
        h6.textContent = value.startDate.substring(0,10) +" "+ value.startDate.substring(11,16) + "  --  " + value.endDate.substring(0,10) +" "+ value.endDate.substring(11,16);
        h6.className = "w3-text-teal";

        var p = document.createElement("p");
        p.textContent = value.vehicle;

        var price = document.createElement("p");
        price.textContent = "Total amount: " + value.price;

        var reviewBtn = document.createElement("button");
        reviewBtn.className = "w3-button w3-padding-large w3-white w3-border";
        reviewBtn.textContent = "Write a review»";
        reviewBtn.onclick = function(){
          sessionStorage.setItem("subject","");
          sessionStorage.setItem("rating","");
          sessionStorage.setItem("review","");
          sessionStorage.setItem("business",value.businessActivity);
          location.href = "../../templates/User/userPage_activities.html";
        };
        
        li.append(h5);
        li.append(p);
        li.append(price);
        li.append(h6);
        
        li.append(document.createElement("hr"));
        li.appendChild(reviewBtn);

        if(new Date(value.startDate) > new Date()){
          var updateBtn = document.createElement("button");
          updateBtn.className = "w3-button w3-padding-large w3-white w3-border";
          updateBtn.textContent = "Modify»";
          updateBtn.onclick = function(){
            sessionStorage.setItem("start",value.startDate);
            sessionStorage.setItem("end",value.endDate);
            sessionStorage.setItem("business",value.businessActivity);
            sessionStorage.setItem("category",value.category);
            sessionStorage.setItem("vehicle",value.vehicle);
            sessionStorage.setItem("price",value.price);
            sessionStorage.setItem("identifier",value.identifier);
            sessionStorage.setItem("id",value.id);

            location.href = "../../templates/User/userPage_modifyActivity.html";
          };
          li.appendChild(updateBtn);  
        }

        var removeBtn = document.createElement("button");
        removeBtn.className = "w3-button w3-padding-large w3-white w3-border";
        removeBtn.textContent = "Remove activity»";
        removeBtn.onclick = function(){
          removeActivities(value);
        };
        li.appendChild(removeBtn);

        document.getElementById("activities_list").append(li);
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
  generate_reservation()
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

document.getElementById('4').onclick = function () {
  sessionStorage.setItem("analytics","4");
  location.href = "../../templates/User/userPage_analytics.html";
};

document.getElementById('5').onclick = function () {
  sessionStorage.setItem("analytics","5");
  location.href = "../../templates/User/userPage_analytics.html";
};

document.getElementById('6').onclick = function () {
  sessionStorage.setItem("analytics","6");
  location.href = "../../templates/User/userPage_analytics.html";
};

document.getElementById('friend').onclick = function () {
  location.href = "../../templates/User/friendPage.html";
};

document.getElementById('profile').onclick = function () {
  location.href = "../../templates/User/userPage_main.html";
};

function change_param(){
  if(document.getElementById('order').value == 1){
    st = "endDate";
    d = "desc";
  }
  else if(document.getElementById('order').value == 2){
    st = "endDate";
    d = "asc";
  }
  else if(document.getElementById('order').value == 3){
    st = "price";
    d = "asc";
  }
  else{
    st = "price";
    d = "desc";
  }
  generate_reservation();
};

//responsive input field
document.getElementById("business_input").onkeyup = function(){
  processChangeRes()
}

function debounce(func, timeout = 700){
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => { func.apply(this, args); }, timeout);
  };
}
function saveInputRes(){
  inputSearchText = document.getElementById("business_input").value;
  generate_reservation();
}
const processChangeRes = debounce(() => saveInputRes());

document.getElementById('act_next').onclick = function () {
  pag++;
  generate_reservation();
};

document.getElementById('act_previous').onclick = function () {
  pag--;
  generate_reservation();
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
      sessionStorage.clear();
      location.href = "../../templates/home.html";
      alert("Logout complete");
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
};