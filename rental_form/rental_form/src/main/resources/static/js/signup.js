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

$(document).ready(function () {
    document.getElementById('signup_btn').onclick = function (e) {
      if(validateForm()){

        let person = {
          firstName : document.getElementById('name_signup').value,
          lastName : document.getElementById('surname_signup').value,
          username : document.getElementById('uname_signup').value,
          password : document.getElementById('psw_signup').value,
          address : document.getElementById('address_signup').value,
          email : document.getElementById('mail_signup').value,
          phoneNumber : document.getElementById('pn_signup').value,
          occupation: document.getElementById('occupation_signup').value,
          dateOfBirth : new Date(document.getElementById('dob_signup').value)
        };

        document.getElementById('name_signup').value = '';
        document.getElementById('surname_signup').value = '';
        document.getElementById('uname_signup').value = '';
        document.getElementById('psw_signup').value = '';
        document.getElementById('address_signup').value = '';
        document.getElementById('mail_signup').value = '';
        document.getElementById('pn_signup').value = '';
        document.getElementById('dob_signup').value = '';

        $.ajax({
          url : "http://localhost:5050/signup",
          data : JSON.stringify(person),//{obj: person},
          //dataType : "json",
          type : "POST",
          //chace : false,
          contentType: 'application/json',
          
          success: function (data) {
            alert("Signup success")
            location.href = "../templates/home.html"
          },
          error: function(xhr) {
            alert(xhr.responseText)
          }
        })
      }
    }
  });