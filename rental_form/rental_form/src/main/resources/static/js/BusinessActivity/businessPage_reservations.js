let num = 1
let pag = 1;
let st = "endDate";
let d = "desc";
let inputSearchText = "";

function generate_reservation(){
  document.getElementById('list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/business/get_renting_reservation",
    data : {username: sessionStorage.getItem("userLog"), page : pag, sortTarget : st, direction : d, searchText: inputSearchText},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-card w3-container";

        var username = document.createElement("h5");
        username.textContent = value.user;

        var h5 = document.createElement("h5");
        h5.textContent = "Vehicle: " + value.vehicle;
        h5.style.fontWeight = "bold";

        var h5_rating = document.createElement("h5");
        h5_rating.textContent = "Earnings: " + value.price;

        var h6_start = document.createElement("h6");
        h6_start.textContent = value.startDate.substring(0,10) +" "+ value.startDate.substring(11,16) + "  --  " + value.endDate.substring(0,10) +" "+ value.endDate.substring(11,16);;
        h6_start.className = "w3-text-teal";

        div.append(username);
        div.append(h5);
        div.append(h5_rating);
        div.append(h6_start);


        document.getElementById("list").append(div);
      });
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function change_param(){
  if(document.getElementById('order').value == 1){
    st = "vehicle";
    d = "asc";
  }
  else if(document.getElementById('order').value == 2){
    st = "vehicle";
    d = "desc";
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

document.getElementById('next').onclick = function () {
  page_number++;
  generate_reservation();
};

document.getElementById('previous').onclick = function () {
  page_number--;
  generate_reservation();
};

document.getElementById('vehicles').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_vehicles.html";
};

document.getElementById('reviews').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reviews.html";
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
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
  generate_reservation();
});

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

document.getElementById('next').onclick = function () {
  pag++;
  generate_reservation();
};

document.getElementById('previous').onclick = function () {
  pag--;
  generate_reservation();
};