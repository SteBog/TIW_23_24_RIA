(function () {
	document.getElementById("logout-btn").addEventListener('click', (e) => {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            switch (req.status) {
	                case 200:
	                    sessionStorage.removeItem("username");
	                    window.location.replace("index.html");
	                    break;
	                default:
						alert("Si è verificato un errore");
	                    break;
	            }
	        }
	    }
	    req.open("GET", "Logout");
	    req.send();
	});
})();