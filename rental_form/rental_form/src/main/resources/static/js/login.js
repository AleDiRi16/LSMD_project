$(document).ready(function () {
  document.getElementById('login_btn').onclick = function (e) {
    let uname = document.getElementById('uname_login').value
    let psw = document.getElementById('psw_login').value

    let requestUser = {
      username : uname,
      password : psw
    };

    $.ajax({
      url : "http://localhost:5050/login",
      data : JSON.stringify(requestUser),
      type : "POST",
      dataType: "json",
      contentType: 'application/json',
      success: function (data) {
        sessionStorage.setItem("userLog",uname);
        sessionStorage.setItem("isAdmin",data.isAdmin);
        if(uname.includes("@Business"))
          location.href = "../templates/BusinessActivity/businessPage_vehicles.html"
        else
          location.href = "../templates/home.html"
      },
      error: function(xhr) {
        alert(xhr.responseText)
      }
    })
  }
});