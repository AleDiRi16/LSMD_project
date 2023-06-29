let page_number = 1;  //page number used in business browsing
let st = "name";      //sortTarget used in ajax call
let d = "asc";        //direction used in ajax call
let ban = ""          //businessActivityName used in ajax call

function deleteBusiness(value){
  $.ajax({
    url : "http://localhost:5050/admin/remove_business",
    data : value.name,
    dataType : "json",
    type : "DELETE",
    contentType: 'application/json',
    success: function (data) {
      alert("BUSINESS BANNED")
      generate_business();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}


//business list generator using jquery call
function generate_business(){
  document.getElementById('list').innerHTML = "";
  $.ajax({
    url : "http://localhost:5050/get_business_activity",
    data : {businessActivityName: ban, page : page_number, sortTarget : st, direction : d},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var board = document.createElement('div');
        board.className = "w3-row";
    
        var div0 = document.createElement('div');
        div0.className = "w3-col m6 w3-padding-large w3-hide-small";
    
        var img = document.createElement("img");
        img.src="../static/img/business/" + value.image;
        img.style="width:60%;max-height:350px;";
        div0.appendChild(img);
    
        var div = document.createElement('div');
        div.className = "w3-col m6 w3-padding-large";
        var h3 = document.createElement("h3");
        var b0 = document.createElement("b");
        b0.textContent = value.name;
        h3.appendChild(b0);
        var h5 = document.createElement("h5");
        h5.textContent=value.city;
    
        div.appendChild(h3);
        div.appendChild(h5);
    
        var div2 = document.createElement('div');
        div2.className = "w3-container";
        var p = document.createElement('p');
        p.textContent= value.description;
        div2.appendChild(p);
        var div3 = document.createElement('div');
        div3.className = "w3-row";
    
        var div4 = document.createElement('div');
        div4.className = "w3-col m8 s12";
        var btn = document.createElement('button');
        btn.className="w3-button w3-padding-large w3-white w3-border";
        btn.id='business';
        btn.onclick = function () {
          sessionStorage.setItem("business_name",value.name);
          sessionStorage.setItem("business_address",value.address);
          sessionStorage.setItem("business_city",value.city);
          sessionStorage.setItem("business_pn",value.phoneNumber);
          location.href = "../templates/activityPage.html"; 
          //location.href = "../templates/BusinessActivity/businessPage_vehicles.html"; //togliere dopo debug
        };
        var b1 = document.createElement("b");
        b1.textContent="AVAILABLE VEHICLES»";
        btn.appendChild(b1);
    
        div4.appendChild(btn);
        div3.appendChild(div4);
    
        var div5 = document.createElement('div');
        div5.className = "w3-col m4 w3-hide-small";
        
        var span0 = document.createElement('span');
        span0.className="w3-padding-large w3-right";
    
        var b = document.createElement('b');
        b.textContent="Rating  ";
    
        span0.appendChild(b);
    
        var span = document.createElement('span');
        span.textContent= value.rating;
        span.className="w3-tag";

        span0.appendChild(span);
        div5.appendChild(span0);
        div3.appendChild(div5);
        div2.appendChild(div3);
        div.appendChild(div2);
        board.appendChild(div);
        board.appendChild(div0);

        if(sessionStorage.getItem("isAdmin") == "true"){
          var adminBtn = document.createElement('button');
          adminBtn.className="w3-button w3-padding-large w3-white w3-border";
          adminBtn.textContent = "DELETE BUSINESS ACTIVITY: " + value.name;
          adminBtn.style.fontWeight = "bold";
          adminBtn.onclick = function () {
            deleteBusiness(value);
          };
          board.appendChild(adminBtn);
        }
    
        
        document.getElementById('list').appendChild(board);
      });
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

//select list handler
function change_param(){
  if(document.getElementById('order').value == 1){
    st = "name";
    d = "asc";
    page_number = 1;
  }
  else if(document.getElementById('order').value == 2){
    st = "name";
    d = "desc";
    page_number = 1;
  }
  else if(document.getElementById('order').value == 3){
    st = "rating";
    d = "desc";
    page_number = 1;
  }
  else{
    st = "rating";
    d = "asc";
    page_number = 1;
  }
  generate_business();
};

//first home page load
$(document).ready(function () {
  generate_business();
});

//next arrow button
document.getElementById('next').onclick = function () {
  page_number++;
  generate_business();
};

//previous arrow button
document.getElementById('previous').onclick = function () {
  page_number--;
  generate_business();
};

document.getElementById('logout').onclick = function () {
  if(sessionStorage.getItem("userLog")){
    $.ajax({
      url : "http://localhost:5050/logout",
      type : "GET",
      contentType: 'application/json',
      success: function (data) {
        alert("Logout complete");
        sessionStorage.clear();
        location.href = "./home.html";
      },
      error: function(xhr) {
        alert(xhr.responseText)
      }
    })
  }
};

//responsive input field
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
  ban = document.getElementById("business_input").value;
  generate_business();
}
const processChange = debounce(() => saveInput());
