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
    document.getElementById('subject').value = sessionStorage.getItem("title");
    document.getElementById('review').value = sessionStorage.getItem("description");
    document.getElementById('btn').onclick = function (e) {
      if(validateForm()){
        let request;


        if(sessionStorage.getItem("userLog").includes("@Business")){
          let report = {
            status: sessionStorage.getItem("status"),
            description: sessionStorage.getItem("description"),
            title: sessionStorage.getItem("title"),
            dateOfReport: sessionStorage.getItem("date"),
            reportId: sessionStorage.getItem("id")
          }

          request = {
              updateInitiator: sessionStorage.getItem("userLog"),
              command: "UPDATE",
              description: document.getElementById("review").value,
              title: document.getElementById("subject").value,
              referenceReport: report
          }
        }
        else{
          let ticket = {
              status: sessionStorage.getItem("status"),
              description: sessionStorage.getItem("description"),
              category: sessionStorage.getItem("category"),
              title: sessionStorage.getItem("title"),
              ticketDate: sessionStorage.getItem("date"),
              ticketId: sessionStorage.getItem("id")
          }

          request = {
              updateInitiator: sessionStorage.getItem("userLog"),
              command: "UPDATE",
              description: document.getElementById("review").value,
              title: document.getElementById("subject").value,
              referenceTicket: ticket
          }
        }

        
        $.ajax({
            url : "http://localhost:5050/updateNotification",
            data : JSON.stringify(request),
            dataType : "json",
            type : "PATCH",
            contentType: 'application/json',
            success: function (data) {
              if(sessionStorage.getItem("userLog").includes("@Business"))
                location.href = "../templates/BusinessActivity/businessPage_ticket.html";
              else
                location.href = "./User/userPage_ticket.html";
            },
            error: function(xhr) {
                alert(xhr.responseText)
            }
        })
      }
    }
})