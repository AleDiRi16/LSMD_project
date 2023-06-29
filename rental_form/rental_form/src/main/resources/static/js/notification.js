function sendNotification(){

    let t = {
        description : document.getElementById("description").value,
        category: document.getElementById("category").value,
        title: document.getElementById("title").value
    }
    
    let request
    if(sessionStorage.getItem("userLog").includes("@Business")){
        request = {
            username : sessionStorage.getItem("userLog"),
            requestCategory: "REPORT",
            report : t
        }
    } 
    else{
        request = {
            username : sessionStorage.getItem("userLog"),
            requestCategory: "TICKET",
            ticket : t
        } 
    }
        

    $.ajax({
        url : "http://localhost:5050/sendNotification",
        data : JSON.stringify(request),
        //dataType : "json",
        type : "POST",
        contentType: 'application/json',
        success: function (data) {
            alert("Notification has been sent");
        },
        error: function(xhr) {
            alert(xhr.responseText)
        }
    })
}

document.getElementById('notificationBtn').onclick = function(){
    sendNotification();
}

document.getElementById('ticketList').onclick = function(){
    if(sessionStorage.getItem("userLog").includes("@Business"))
        location.href = "../../templates/BusinessActivity/businessPage_ticket.html"
    else
        location.href = "../../templates/User/userPage_ticket.html"
}
