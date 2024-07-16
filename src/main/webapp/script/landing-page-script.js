{
	let login_container, register_container;
	let go_to_reg, go_to_login;
	
	login_container = document.getElementById("login-container");
	register_container = document.getElementById("registration-container");
	
	go_to_login = document.getElementById("login-here");
	go_to_reg = document.getElementById("register-here");
				
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") != null) {
			window.location.href = "home.html";
		} else {
			login_container.hidden = false;
			register_container.hidden = true; 
		}
	});
	
	function change_container(origin, destination) {
		origin.hidden = true;
		destination.hidden = false;
	}
	
	go_to_reg.addEventListener("click", () => {
		change_container(login_container, register_container);
	});
	
	go_to_login.addEventListener("click", () => {
		change_container(register_container, login_container);
	});
}