let page_number = 1;
let st = "brand";
let d = "asc";
let inputSearchText= "";

function send_car(){

  let vehicle = {
    automaticTrasmission : document.getElementById("automaticTrasmission").value, 
    brand : document.getElementById("brand").value,
    name : document.getElementById("name").value,
    year : document.getElementById("year").value,
    price : document.getElementById("price").value,
    category : document.getElementById("category").value
  }

  let request = {
    businessUsername : sessionStorage.getItem("userLog"),
    vehicleToInsert : vehicle
  };

  $.ajax({
    url : "http://localhost:5050/business/add_vehicle",
    data : JSON.stringify(request),
    type : "POST",
    contentType: 'application/json',
    success: function (data) {
      generate_car();
    },
    error: function(xhr) {
      alert("Error")
    }
  })
}

function removeVehicle(value){
  let request = {
    command : "DELETE",
    businessUsername : sessionStorage.getItem("userLog"),
    vehicleToUpdate : value
  };

  $.ajax({
    url : "http://localhost:5050/business/remove_vehicle",
    data : JSON.stringify(request),
    type : "DELETE",
    contentType: 'application/json',
    success: function (data) {
      generate_car();
    },
    error: function(xhr) {
      alert(xhr.responseText)
    }
  })
}

function modifyVehicle(value, automaticTrasmission, year, price, brand, name, category){
  let request = {
    command : "UPDATE",
    businessUsername : sessionStorage.getItem("userLog"),
    vehicleToUpdate : value,
    brand : brand.value,
    automaticTrasmission : automaticTrasmission.value,
    year : year.value,
    price : price.value,
    name : name.value,
    category : category.value
  };

  $.ajax({
    url : "http://localhost:5050/business/update_vehicle",
    data : JSON.stringify(request),
    type : "PATCH",
    contentType: 'application/json',
    success: function (data) {
      generate_car();
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
    data : {businessName: "Maiami rent", page : page_number, sortTarget : st, direction : d, searchText: inputSearchText},
    dataType : "json",
    type : "GET",
    contentType: 'application/json',
    success: function (data) {
      $.each( data, function(key,value){
        var li = document.createElement("div");
        li.className="w3-padding-32 w3-col w3-card";
        var img = document.createElement("img");
        img.className="w3-left w3-margin-right";
        img.src="../../static/img/vehicle/" + value.image;
        img.width="100";

        var categoryText = document.createElement("h1");
        categoryText.className="w3-large";
        categoryText.textContent= "Category:";
        categoryText.style.fontWeight = "bold";

        var category = document.createElement("input");
        category.className="w3-large";
        category.value= value.category;
        category.readOnly = true;
        category.style.border = "none";
        category.style.textAlign ="center";
        
        var brandText = document.createElement("h1");
        brandText.className="w3-large";
        brandText.textContent= "Brand:";
        brandText.style.fontStyle;
        brandText.style.fontWeight = "bold";

        var brand = document.createElement("input");
        brand.className="w3-large";
        brand.value= value.brand;
        brand.readOnly = true;
        brand.style.border = "none";
        brand.style.textAlign ="center";

        var nameText = document.createElement("h1");
        nameText.className="w3-large";
        nameText.textContent= "Name:";
        nameText.style.fontWeight = "bold";

        var name = document.createElement("input");
        name.className="w3-large";
        name.value= value.name;
        name.readOnly = true;
        name.style.border = "none";
        name.style.textAlign ="center";

        var yearText = document.createElement("h1");
        yearText.className="w3-large";
        yearText.textContent= "Year:";
        yearText.style.fontWeight = "bold";

        var year = document.createElement("input");
        year.className="w3-large";
        year.value= value.year;
        year.readOnly = true;
        year.style.border = "none";
        year.style.textAlign ="center";

        var priceText = document.createElement("h1");
        priceText.className="w3-large";
        priceText.textContent= "Price:";
        priceText.style.fontWeight = "bold";

        var price = document.createElement("input");
        price.className="w3-large";
        price.value= value.price;
        price.readOnly = true;
        price.style.border = "none";
        price.style.textAlign ="center";

        var automaticTrasmissionText = document.createElement("h1");
        automaticTrasmissionText.className="w3-large";
        automaticTrasmissionText.textContent= "AutomaticTrasmission:";
        automaticTrasmissionText.style.fontWeight = "bold";

        var automaticTrasmission = document.createElement("input");
        automaticTrasmission.className="w3-large";
        if(value.automaticTrasmission == undefined)
          automaticTrasmission.value= "False";  
        else
          automaticTrasmission.value= value.automaticTrasmission;
        automaticTrasmission.readOnly = true;
        automaticTrasmission.style.border = "none";
        automaticTrasmission.style.textAlign ="center";

        var btn = document.createElement('button');
        btn.className="w3-button w3-padding-large w3-white w3-border";
        btn.id='business_remove';
        var b1 = document.createElement("b");
        b1.textContent="Remove";
        btn.appendChild(b1);
        btn.style.cssFloat='right';
        btn.onclick = function(){
          removeVehicle(value);
        }

        

        var btn3 = document.createElement('button');
        btn3.className="w3-button w3-padding-large w3-white w3-border";
        btn3.id='business_update';
        var b3 = document.createElement("b");
        b3.textContent="Save update";
        btn3.appendChild(b3);
        btn3.style.cssFloat='right';
        btn3.disabled = true;
        btn3.onclick = function(){
          modifyVehicle(value, automaticTrasmission, year, price, brand, name, category);
        }

        var btn2 = document.createElement('button');
        btn2.className="w3-button w3-padding-large w3-white w3-border";
        btn2.id='business_update';
        var b2 = document.createElement("b");
        b2.textContent="Update";
        btn2.appendChild(b2);
        btn2.style.cssFloat='right';
        btn2.onclick = function(){
          automaticTrasmission.readOnly = false;
          year.readOnly = false;
          price.readOnly = false;
          brand.readOnly = false;
          name.readOnly = false;
          category.readOnly = false;
          btn3.disabled = false;
          btn2.disabled = true;
          btn.disabled = true;
        }

        li.appendChild(img);
        li.appendChild(document.createElement('hr'));
        li.appendChild(document.createElement('hr'));
        li.appendChild(document.createElement('hr'));
        
        li.appendChild(categoryText);
        li.appendChild(category);
        li.appendChild(brandText);
        li.appendChild(brand);
        li.appendChild(nameText);
        li.appendChild(name);
        li.appendChild(yearText);
        li.appendChild(year);
        li.appendChild(priceText);
        li.appendChild(price);
        li.appendChild(automaticTrasmissionText);
        li.appendChild(automaticTrasmission);

        li.appendChild(document.createElement('hr'));
        li.appendChild(btn);
        li.appendChild(btn2);
        li.appendChild(btn3);
        document.getElementById('list').appendChild(li);
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

document.getElementById('submit_veichle').onclick = function () {
  send_car();
};

document.getElementById('reservation').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reservations.html";
};

document.getElementById('reviews').onclick = function () {
  location.href = "../../templates/BusinessActivity/businessPage_reviews.html";
};

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
  generate_car();
});

document.getElementById('next').onclick = function () {
  page_number++;
  generate_car();
};

document.getElementById('previous').onclick = function () {
  page_number--;
  generate_car();
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

document.getElementById('next').onclick = function () {
  page_number++;
  generate_car();
};

document.getElementById('previous').onclick = function () {
  page_number--;
  generate_car();
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

document.getElementById('1').onclick = function () {
  sessionStorage.setItem("analytics","1");
  location.href = "../../templates/BusinessActivity/businessPage_analytics.html";
};

document.getElementById('2').onclick = function () {
  sessionStorage.setItem("analytics","2");
  location.href = "../../templates/BusinessActivity/businessPage_analytics.html";
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