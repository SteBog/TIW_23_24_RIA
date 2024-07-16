
function register(form) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = () => {
		if (req.readyState == XMLHttpRequest.DONE) {
			var message = req.responseText;
			switch (req.status) {
				case 200:
					sessionStorage.setItem('username', message);
					window.location.replace("home.html");
					break;
				default:
					document.getElementById("reg-error-message").textContent = message;
					break;
			}
		}
	}
	req.open("POST", "Register");
	req.send(new FormData(form));
}

(function () {
	document.getElementById("registration-button").addEventListener("click", (e) => {
		let form = e.target.closest("form");
		if (form.reportValidity()) {
			register(form);
		}
	});
})();