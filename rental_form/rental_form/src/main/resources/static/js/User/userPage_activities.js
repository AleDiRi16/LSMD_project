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
  document.getElementById('subject').value = sessionStorage.getItem("subject");
  document.getElementById('review').value = sessionStorage.getItem("review");
  document.getElementById('rating').value = sessionStorage.getItem("rating");
  document.getElementById('btn').onclick = function (e) {
    if(validateForm()){
      if(sessionStorage.getItem("review") == ""){
        let rev = {
          subject : document.getElementById('subject').value,
          review : document.getElementById('review').value,
          rating : document.getElementById('rating').value,
          dateOfReview: new Date(),
          businessActivity: sessionStorage.getItem("business")
        };
  
        let reviewUser = {
          username: sessionStorage.getItem("userLog"),
          review: rev
        };
        $.ajax({
          url : "http://localhost:5050/addReview",
          data : JSON.stringify(reviewUser),
          type : "POST",
          contentType: 'application/json',
          
          success: function (data) {
            location.href = "./userPage_main.html"; 
          },
          error: function(xhr) {
            alert(xhr.responseText)
          }
        })
      }
      else{
        let rev = {
          subject : sessionStorage.getItem("subject"),
          review : sessionStorage.getItem("review"),
          rating : sessionStorage.getItem("rating"),
          dateOfReview: sessionStorage.getItem("date"),
          businessActivity: sessionStorage.getItem("business")
        };
  
        let reviewUser = {
          username: sessionStorage.getItem("userLog"),
          subject : document.getElementById('subject').value,
          review : document.getElementById('review').value,
          rating : document.getElementById('rating').value,
          referenceReview : rev
        };
        $.ajax({
          url : "http://localhost:5050/updateReview",
          data : JSON.stringify(reviewUser),
          type : "PATCH",
          contentType: 'application/json',
          
          success: function (data) {
            location.href = "./userPage_main.html"; 
          },
          error: function(xhr) {
            alert(xhr.responseText)
            location.href = "./userPage_main.html"; 
          }
        })
      }
      document.getElementById('subject').value = '';
      document.getElementById('review').value = '';
      document.getElementById('rating').value = '';
    }
  }
});