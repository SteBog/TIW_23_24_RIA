(function () {
	document.getElementById("create-group-btn").addEventListener("click", (e) => {
		let modal = document.createElement("div");
		modal.id = "modal-window";
		modal.className = "modal";
		modal.style.display = "block";
		
		let content = document.createElement("div");
		content.className = "modal-content";
		group_form_modal(content);
		
		modal.appendChild(content);
		document.getElementById("pageContainer").appendChild(modal);
	});
	
	window.onclick = function(event) {
		let modal = document.getElementById("modal-window");
		if (event.target == modal) {
		    modal.remove();
		}
	}
	
	let min_part, max_par;
	function group_form_modal(modal) {
		let form = document.createElement("form");
		form.appendChild(create_input("text", "nome", "Nome"));
		form.appendChild(create_input("text", "descrizione", "Descrizione"));
		let giorni = create_input("number", "giorni", "Giorni");
		form.appendChild(giorni);
		let minPart = create_input("number", "minPartecipanti", "Min Partecipanti");
		form.appendChild(minPart);
		let maxPart = create_input("number", "maxPartecipanti", "Max Partecipanti")
		form.appendChild(maxPart);
	
		giorni.children[1].min = 0;
		//	listener min value max
		
		minPart.addEventListener("change", (e) => {
			min_part = parseInt(minPart.children[1].value);
			maxPart.querySelector("input").setAttribute("min", parseInt(e.target.value));
		});
		
		maxPart.addEventListener("change", (e) => {
			max_par = parseInt(maxPart.children[1].value);
		});
		
		let error_span = document.createElement("span");
		error_span.className = "errore";
		
		form.appendChild(error_span);
		
		let submit = document.createElement("input");
		submit.type = "button";
		submit.className = "creazione";
		submit.value = "AVANTI";
		
		submit.addEventListener("click", (e) => {
			if (form.reportValidity()) {
				var req = new XMLHttpRequest();
			    req.onreadystatechange = () => {
			        if (req.readyState == XMLHttpRequest.DONE) {
			            var message = req.responseText;
			            switch (req.status) {
			                case 200:
								create_anagrafica();
			                    break;
			                default:
			                    error_span.textContent = message;
			                    break;
			            }
			        }
			    }
			    req.open("POST", "CreateGroup");
			    req.send(new FormData(form));
			}
		});
		
		form.appendChild(submit);
		
		modal.appendChild(form);
	}
	
	function create_input(type, name, value) {
		let div = document.createElement("div");
		div.className = "formGroup";
		
		let label = document.createElement("label");
		label.htmlFor = name;
		label.textContent = value + ":";
		
		div.appendChild(label);
		
		let input_form = document.createElement("input");
		input_form.type = type;
		input_form.id = name;
		input_form.placeholder = name;
		input_form.name = name;
		input_form.required = true;
		
		div.appendChild(input_form);
		
		return div;
	}
	
	let tentativi = 0;
	function create_anagrafica() {
		let modal_window = document.getElementById("modal-window");
		let modal_content = modal_window.firstChild;
		modal_content.firstChild.remove();
		
		let form = document.createElement("form");
		modal_content.appendChild(form);
		
		let ul = document.createElement("ul");
		form.appendChild(ul);
		
		get_user_list(ul);
		
		let submit = document.createElement("input");
		submit.type = "button";
		submit.value = "INVIA";
		
		submit.addEventListener("click", (e) => {
			let users = form.querySelectorAll("ul input");
			let checkedCounter = 0;
			
			for (let i = 0; i < users.length; i++)
				if (users[i].checked) checkedCounter++; 
			
			if (checkedCounter < min_part) {
				alert("Numero di invitati insufficiente");
				
				tentativi++;
				if (tentativi >= 3) {
					document.getElementById("modal-window").remove();
					tentativi = 0;
				}
			} else if (checkedCounter > max_par) {
				alert("Numero di invitati eccessivo");
				
				tentativi++;
				if (tentativi >= 3) {
					document.getElementById("modal-window").remove();
					tentativi = 0;
				}
			} else {
				save_creation(form);
			}
		});
		
		form.appendChild(submit);
	}
	
	
	function save_creation(form) {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var data = req.responseText;
	            switch (req.status) {
	                case 200:
						alert("Gruppo creato correttamente");
						document.getElementById("modal-window").remove();
						let container = document.getElementById("pageContainer").querySelectorAll("div");
						container[2].remove();
						container[1].remove();
						
						downloadGroupData();
						break;
					case 408:
						alert("Numero di tentativi massimo superato");
						document.getElementById("modal-window").remove();
	                default:
	                    console.log("Si è verificato un errore");
						break;
	            }
	        }
	    }
		req.open("POST", "SaveCreation");
		req.send(new FormData(form));
	}
	
	function get_user_list(ul) {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var data = req.responseText;
	            switch (req.status) {
	                case 200:
	                    data = JSON.parse(data);
						data.forEach((item) => {
							let li = document.createElement("li");
							let label = document.createElement("label");
							li.appendChild(label);
							
							let checkbox = document.createElement("input");
							checkbox.type = "checkbox";
							checkbox.value = item.username;
							checkbox.name = "selectedUsers";
							
							label.appendChild(checkbox);
							let span = document.createElement("span");
							span.textContent = item.nome + " " + item.cognome;
							label.appendChild(span);
							
							ul.appendChild(li);
						});
						break;
	                default:
	                    console.log("Si è verificato un errore");
						break;
	            }
	        }
	    }
	    req.open("GET", "GetUserList");
	    req.send();
	}
})();