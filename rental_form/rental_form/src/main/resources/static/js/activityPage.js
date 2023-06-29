let page_number = 1;
let st = "brand";
let d = "asc";
let inputSearchText= "";

let page_number_rev = 1;
let st_rev = "name";
let d_rev = "asc";
let inputSearchText_rev= "";

var startDate, endDate;

function validateForm() {
  var isValid = true;
  var inputs = document.getElementsByTagName("input");
  for (var i = 0; i < inputs.length; i++) {
    if (inputs[i].hasAttribute("required") && inputs[i].value == "") {
      isValid = false;
      inputs[i].classList.add("invalid");
    } else {
      inputs[i].classList.remove("invalid");
    }
  }
  return isValid;
}

function addReservation(value, input, clock_start, clock_end){

  //if(validateForm()){
  var startYear = startDate.substring(6,10);
  var startMonth = parseInt(startDate.substring(3,5)) - 1;
  var startDay = parseInt(startDate.substring(0,2));

  var startHour = parseInt(clock_start.value.substring(0,2)) + 2;
  var startMin = parseInt(clock_start.value.substring(3,5));

  var endYear = endDate.substring(6,10);
  var endMonth = parseInt(endDate.substring(3,5)) - 1;
  var endDay = parseInt(endDate.substring(0,2));

  var endHour = parseInt(clock_end.value.substring(0,2)) + 2;
  var endMin = parseInt(clock_end.value.substring(3,5));

  let prova1 = new Date(startYear, startMonth , startDay, startHour, startMin);
  let prova2 = new Date(endYear, endMonth, endDay, endHour, endMin);

  if(prova1 < new Date()){
    alert("Invalid Date");
    return;
  }

  let renting_reservation = {
    businessActivity : sessionStorage.getItem("business_name"), 
    user : sessionStorage.getItem("userLog"),

    vehicle : value.name,
    startDate : prova1,
    endDate : prova2,
    price : value.price,
    category : value.category,
    identifier: value.vehicleIdentifier,
    id: value.id
  }

  let reservationUser = {
    username : sessionStorage.getItem("userLog"),
    //activity : sessionStorage.getItem("business_name"),
    rentingReservation : renting_reservation
  };

  $.ajax({
    url : "http://localhost:5050/addReservation",
    data : JSON.stringify(reservationUser),
    type : "POST",
    contentType: 'application/json',
    success: function (data) {
      alert("Correct rent");
    },
    error: function(xhr) {
      alert("VEHICLE NOT AVAILABLE ON THESE DATES,CHOOSE OTHERS");
    }
  })

}

function generate_review(){
  document.getElementById('reviews_list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/business/getReviews",
    data : {businessActivityName: sessionStorage.getItem("business_name"), page : page_number_rev, sortTarget : st_rev, direction : d_rev, searchText : inputSearchText_rev},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var div = document.createElement("div");
        div.className = "w3-margin w3-card w3-container";

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

function generate_car(){
  document.getElementById('list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/business/all_business_vehicle",
    data : {businessName: sessionStorage.getItem("business_name"), page : page_number, sortTarget : st, direction : d, searchText: inputSearchText},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var li = document.createElement("div");
        li.className="w3-padding w3-card w3-row";

        var span = document.createElement("b");
        span.style.fontSize = "26px";
        span.textContent= value.brand + " " + value.name;
        span.style.textAlign= "center";
        li.appendChild(span);

        var div_right = document.createElement('div');
        div_right.className = "w3-col w3-padding-large w3-hide-small";
        var img = document.createElement("img");
        img.src="../static/img/vehicle/"+value.image;
        img.className="w3-right";
        img.style="width:40%; height: 160px";
        div_right.appendChild(img);

        var div_left = document.createElement('div');
        div_left.className = "w3-left-align";

        var b2 = document.createElement("b");
        b2.textContent= "Year: " + value.year;

        var b3 = document.createElement("b");
        b3.textContent= "Price per day: " + value.price;

        var b4 = document.createElement("b");

        if(value.automaticTrasmission == undefined)
          b4.textContent= "Automatic vehicle: " + "False";  
        else
          b4.textContent= "Automatic vehicle: " + value.automaticTrasmission;

        div_left.appendChild(b2);
        div_left.appendChild(document.createElement("br"));
        div_left.appendChild(b3);
        div_left.appendChild(document.createElement("br"));
        div_left.appendChild(b4);
        
        var input = document.createElement("input");
        input.type = "text";
        input.style.textAlign = "center";
        input.placeholder = "Insert date";
        input.className = "datepicker";
        input.required = true;

        var clock_start = document.createElement('input');
        clock_start.name = "time";
        clock_start.placeholder = "Set starting time";
        clock_start.style.textAlign = "center";
        clock_start.required = true;

        var clock_end = document.createElement('input');
        clock_end.name = "time";
        clock_end.placeholder = "Set finish time";
        clock_end.style.textAlign = "center";
        clock_end.required = true;
        

        li.appendChild(div_right);
        li.appendChild(img);
        li.appendChild(div_left);

        li.append(input);
        li.appendChild(clock_start);
        li.appendChild(clock_end);

        
        if(sessionStorage.getItem("userLog")){
          var btn = document.createElement('button');
          btn.className="w3-button w3-padding-large w3-white w3-border";
          btn.onclick = function(){
            addReservation(value, input, clock_start, clock_end);
          };
          var b1 = document.createElement("b");
        b1.textContent="BOOK»";
        btn.appendChild(b1);
        btn.style.cssFloat='right';
          li.appendChild(btn);
        }
        
        document.getElementById('list').appendChild(li);
        
      });
      datepicker();
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

document.getElementById('next_rev').onclick = function () {
  page_number_rev++;
  generate_review();
};

document.getElementById('previous_rev').onclick = function () {
  page_number_rev--;
  generate_review();
};

$(document).ready(function () {
  document.getElementById("business_name").innerText = sessionStorage.getItem("business_name");
  document.getElementById("business_address").innerText = sessionStorage.getItem("business_city");
  document.getElementById("business_mail").innerText = sessionStorage.getItem("business_address");
  document.getElementById("business_pn").innerText = sessionStorage.getItem("business_pn");
  generate_car();
  generate_review();
});

document.getElementById('home').onclick = function () {
        location.href = "../templates/home.html";
};

document.getElementById('profile').onclick = function () {
  if(sessionStorage.getItem("userLog"))
    location.href = "../templates/User/userpage_main.html";
};

function change_param_car(){
  if(document.getElementById('car_order').value == 1){
    st = "brand";
    d = "asc";
  }
  else if(document.getElementById('car_order').value == 2){
    st = "brand";
    d = "desc";
  }
  else if(document.getElementById('car_order').value == 3){
    st = "category";
    d = "asc";
  }
  else{
    st = "category";
    d = "desc";
  }
  generate_car();
};

function change_param_rev(){
  if(document.getElementById('rev_order').value == 1){
    st_rev = "dateOfReview";
    d_rev = "asc";
  }
  else if(document.getElementById('rev_order').value == 2){
    st_rev = "dateOfReview";
    d_rev = "desc";
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
document.getElementById("car_input").onkeyup = function(){
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
  inputSearchText = document.getElementById("car_input").value;
  generate_car();
}
const processChange = debounce(() => saveInput());

document.getElementById("rev_input").onkeyup = function(){
  processChangeRev()
}
function saveInputRev(){
  inputSearchText_rev = document.getElementById("rev_input").value;
  generate_review();
}
const processChangeRev = debounce(() => saveInputRev());

function datepicker() {
  $(".datepicker").datepicker({
    dateFormat: 'dd-mm-yy',
    constrainInput: false,
    onSelect: function(selectedDate) {
      if (!startDate || endDate) {
        startDate = selectedDate;//selectedDate.substring(3,5) + "/" + selectedDate.substring(0,2) + "/" +  selectedDate.substring(6,10);
        endDate = null;
      } else if (startDate && !endDate) {
        if (selectedDate > startDate) {
          endDate = selectedDate;//selectedDate.substring(3,5) + "/" + selectedDate.substring(0,2) + "/" +  selectedDate.substring(6,10);
        } else {
          startDate = startDate;
          endDate = selectedDate;//selectedDate.substring(3,5) + "/" + selectedDate.substring(0,2) + "/" +  selectedDate.substring(6,10);
        }
      } else {
        startDate = selectedDate;//selectedDate.substring(3,5) + "/" + selectedDate.substring(0,2) + "/" +  selectedDate.substring(6,10);
        endDate = null;
      }
      $(this).val(startDate + " - " + (endDate ? endDate : ""));
    }
  });

  $("input[name=time]").clockpicker({       
    placement: 'bottom',
    align: 'left',
    autoclose: true,
    default: 'now',
    donetext: "Select",
    init: function() { 
        console.log("colorpicker initiated");
    },
    beforeShow: function() {
        console.log("before show");
    },
    afterShow: function() {
        console.log("after show");
    },
    beforeHide: function() {
        console.log("before hide");
    },
    afterHide: function() {
        console.log("after hide");
    },
    beforeHourSelect: function() {
        console.log("before hour selected");
    },
    afterHourSelect: function() {
        console.log("after hour selected");
    },
    beforeDone: function() {
        console.log("before done");
    },
    afterDone: function() {
        console.log("after done");
    }
  });
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