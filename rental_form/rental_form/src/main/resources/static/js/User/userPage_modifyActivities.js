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
  datepicker();
  document.getElementById('start').value = sessionStorage.getItem("start").substring(8,10)+"/"+sessionStorage.getItem("start").substring(5,7)+"/"+sessionStorage.getItem("start").substring(0,4);
  document.getElementById('end').value = sessionStorage.getItem("end").substring(8,10)+"/"+sessionStorage.getItem("end").substring(5,7)+"/"+sessionStorage.getItem("end").substring(0,4);
  document.getElementById('startTime').value = sessionStorage.getItem("start").substring(11,16);
  document.getElementById('endTime').value = sessionStorage.getItem("end").substring(11,16);
  
  document.getElementById('btn').onclick = function (e) {
    if(validateForm()){
      
      startDate = document.getElementById("start").value;
      endDate = document.getElementById("end").value;
      clock_start = document.getElementById("startTime").value;
      clock_end = document.getElementById("endTime").value;
      
      var startYear = startDate.substring(6,10);
      var startMonth = parseInt(startDate.substring(3,5)) - 1;
      var startDay = parseInt(startDate.substring(0,2));

      var startHour = parseInt(clock_start.substring(0,2)) + 2;
      var startMin = parseInt(clock_start.substring(3,5));

      var endYear = endDate.substring(6,10);
      var endMonth = parseInt(endDate.substring(3,5)) - 1;
      var endDay = parseInt(endDate.substring(0,2));

      var endHour = parseInt(clock_end.substring(0,2)) + 2;
      var endMin = parseInt(clock_end.substring(3,5));

      console.log(startDay);
      console.log(startMonth);
      console.log(startYear);
      console.log(startHour);
      console.log(startMin);

      let prova1 = new Date(startYear, startMonth , startDay, startHour, startMin);
      let prova2 = new Date(endYear, endMonth, endDay, endHour, endMin);

      if(prova1 < new Date()){
        alert("Invalid Date");
        return;
      }

        let res = {
          businessActivity: sessionStorage.getItem("business"),
          user: sessionStorage.getItem("userLog"),
          category: sessionStorage.getItem("category"),
          endDate: sessionStorage.getItem("end"),
          price: sessionStorage.getItem("price"),
          startDate: sessionStorage.getItem("start"),
          vehicle: sessionStorage.getItem("vehicle"),
          id: sessionStorage.getItem("id"),
          identifier: sessionStorage.getItem("identifier")
        };
  
        let  reservationUser= {
          user: sessionStorage.getItem("userLog"),
          reservation: res,
          startDate: prova1,
          endDate: prova2
        };
        $.ajax({
          url : "http://localhost:5050/updateReservation",
          data : JSON.stringify(reservationUser),
          type : "POST",
          contentType: 'application/json',
          
          success: function (data) {
            alert("Reservation update correctly");
            location.href = "./userPage_main.html"; 
          },
          error: function(xhr) {
            alert(xhr.responseText)
          }
        })
      //}
    }
  }
});

function datepicker() {
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