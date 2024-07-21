/**
 * 
 */
{
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") != null) {
			document.getElementById("welcome-username").textContent = "Nice to see you again " + sessionStorage.getItem("username");
			
			downloadGroupData();
		} else {
			location.href = "index.html";
		}
	});
	
	function downloadGroupData() {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var message;
	            switch (req.status) {
	                case 200:
						message = JSON.parse(req.responseText);
	                    createGroupsContainer("I tuoi Gruppi", message["adminGroups"], true);
	                    createGroupsContainer("I Gruppi di cui fai parte", message["groupsWithUser"], false);
						break;
	                default:
						console.log(message);
	                    alert("Si è verificato un errore");
	                    break;
	            }
	        }
	    }
	    req.open("GET", "GetGroupsData");
	    req.send();
	}
	
	function createGroupsContainer(title, data, create_trash) {
		var div_container = document.createElement("div");
		
		var header = document.createElement("h3");
		header.textContent = title;
		div_container.appendChild(header);
		
		var table = document.createElement("table");
		table.classList.add("tabellaProg");
		
		createTableHeader(table);
		
		createTableBody(table, data, create_trash);
		
		div_container.appendChild(table);
		document.getElementById("pageContainer").appendChild(div_container);
	}
	
	function createTableHeader(table) {
		var thead = document.createElement("thead");
		var row = document.createElement("tr");
		var name_cell, duration_cell, details_cell;
		
		name_cell = document.createElement("td");
		name_cell.textContent = "Nome";
		
		duration_cell = document.createElement("td");
		duration_cell.textContent = "Durata";
		
		details_cell = document.createElement("td");
		details_cell.textContent = "Dettagli";
		
		row.appendChild(name_cell);
		row.appendChild(duration_cell);
		row.appendChild(details_cell);
		
		thead.appendChild(row);
		
		table.appendChild(thead);
	}
	
	function createTableBody(table, data, create_trash) {
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		data.forEach((item, index) => {
			var row = document.createElement("tr");
			var name_cell, duration_cell, details_cell;
			
			name_cell = document.createElement("td");
			name_cell.textContent = item["nome"];
			
			duration_cell = document.createElement("td");
			duration_cell.textContent = item["durata"];
			
			details_cell = document.createElement("td");
			details_cell.textContent = "Details";
			details_cell.addEventListener("click", () => {
				show_details(item["id"], create_trash);
			});
			
			row.appendChild(name_cell);
			row.appendChild(duration_cell);
			row.appendChild(details_cell);
			
			tbody.appendChild(row);
		});
	}
	
	function show_details(group_id, create_trash) {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var message;
	            switch (req.status) {
	                case 200:
						message = JSON.parse(req.responseText);
						group_details_container(message, create_trash);
	                    break;
	                default:
						message = req.responseText;
						console.log(message);
	                    alert("Si è verificato un errore");
	                    break;
	            }
	        }
	    }
	    req.open("GET", "GroupDetails?groupId=" + group_id);
	    req.send();
	}
	
	function group_details_container(data, create_trash) {
		document.getElementById("pageContainer").querySelectorAll("div").forEach((item) => {
			if (item.id != "menu") item.hidden = true;
		});
		
		var tabellaProg = document.createElement("table");
		tabellaProg.className = "tabellaProg";
		tabellaProg.id = "tabellaProg";
		
		add_home_to_nav(create_trash);
		
		group_details_container_header(tabellaProg);
		
		group_details_container_body(tabellaProg, data);
		
		if (create_trash) add_trash();
	}
	
	function add_trash() {
		var trash = document.createElement("img");
		trash.src = "images/trash.png";
		trash.id = "trash_icon";
		
		trash.addEventListener("dragover", (e) => {
			dragOver(e);
		});
		trash.addEventListener("dragleave", dragLeave);
		trash.addEventListener("drop", (e) => {
			drop(e);
		});
		document.getElementById("pageContainer").appendChild(trash);
	}
	
	function add_home_to_nav(create_trash) {
		var nav = document.getElementById("nav");
		var span = document.createElement("span");
		span.textContent = "Home";
		span.id = "go-to-home-span";
		span.addEventListener("click", (e) => {
			document.getElementById("pageContainer").removeChild(document.getElementById("tabellaProg"));
			if(create_trash) document.getElementById("pageContainer").removeChild(document.getElementById("trash_icon"));
			document.getElementById("nav").removeChild(document.getElementById("go-to-home-span"));
			document.getElementById("header-details").open = false;
			
			document.getElementById("pageContainer").querySelectorAll("div").forEach((item) => {
				if (item.id != "menu") item.hidden = false;
			});
		});
		nav.appendChild(span);
	}
	
	function group_details_container_header(table) {
		var thead = document.createElement("thead");
		var trow = document.createElement("tr");
		
		var th = document.createElement("th");
		th.textContent = "Nome";
		trow.appendChild(th);
	
		th = document.createElement("th");
		th.textContent = "Descrizione";
		trow.appendChild(th);
		
		th = document.createElement("th");
		th.textContent = "Giorni Rimasti";
		trow.appendChild(th);
		
		th = document.createElement("th");
		th.textContent = "Admin Username";
		trow.appendChild(th);
		
		th = document.createElement("th");
		th.textContent = "Numero massimo partecipanti";
		trow.appendChild(th);
		
		th = document.createElement("th");
		th.textContent = "Numero minimo partecipanti";
		trow.appendChild(th);
		
		th = document.createElement("th");
		th.textContent = "Partecipanti";
		trow.appendChild(th);
		
		thead.appendChild(trow);
		
		table.appendChild(thead);
		
		document.getElementById("pageContainer").appendChild(table);
	}
	
	function group_details_container_body(table, data) {
		var tbody = document.createElement("tbody");
		var trow = document.createElement("tr");
		const group = data["group"];
		const partecipants = data["partecipants"];
		
		// Creiamo la cella per 'nome'
	    const td_nome = document.createElement('td');
	    td_nome.textContent = group.nome;
		td_nome.id = "details_group_name";
	    trow.appendChild(td_nome);

	    // Creiamo la cella per 'descrizione'
	    const td_descrizione = document.createElement('td');
	    td_descrizione.textContent = group.descrizione;
	    trow.appendChild(td_descrizione);

	    // Creiamo la cella per 'durata'
	    const td_durata = document.createElement('td');
	    td_durata.textContent = group.durata;
	    trow.appendChild(td_durata);

	    // Creiamo la cella per 'admin'
	    const td_admin = document.createElement('td');
	    td_admin.textContent = group.admin;
	    trow.appendChild(td_admin);

	    // Creiamo la cella per 'maxPartecipanti'
	    const td_maxPartecipanti = document.createElement('td');
	    td_maxPartecipanti.textContent = group.maxPartecipanti;
	    trow.appendChild(td_maxPartecipanti);

	    // Creiamo la cella per 'minPartecipanti'
	    const td_minPartecipanti = document.createElement('td');
	    td_minPartecipanti.textContent = group.minPartecipanti;
	    trow.appendChild(td_minPartecipanti);
		
		const td_partecipanti = document.createElement("td");
		td_partecipanti.id = "partecipants_list";
		partecipants.forEach((item) => {
			let div_partecipant = document.createElement("span");
			
			let name = document.createElement("span");
			name.textContent = item[0];
			let lastname = document.createElement("span");
			lastname.textContent = item[1];
			
			div_partecipant.appendChild(name);
			div_partecipant.appendChild(lastname);
			
			div_partecipant.draggable = true;
			div_partecipant.addEventListener("dragstart", (e) => {
				dragStart(e);
			});
			
			td_partecipanti.appendChild(div_partecipant);
		});
		trow.appendChild(td_partecipanti);
		
		table.appendChild(trow);
	}
	
	
	/*
		Drag and Drop handling
	*/
	
	let name_dragged, lastname_dragged, partecipant;
	function dragStart(event) {
		partecipant = event.target;
		name_dragged = partecipant.children[0].textContent;
		lastname_dragged = partecipant.children[1].textContent;
	}
	
	function dragOver(event) {
		event.preventDefault();
	}
	
	function dragLeave() {
		name_dragged = null;
		lastname_dragged = null;
	}
	
	function drop(event) {
		event.preventDefault();
		delete_user(name_dragged, lastname_dragged);
		name_dragged = null;
		lastname_dragged = null;
	}
	
	function delete_user(userName, userLastName) {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var message;
	            switch (req.status) {
	                case 200:
						partecipant.remove();
						console.log(req.responseText);
						alert("Eliminato con successo");
	                    break;
	                default:
						message = req.responseText;
						console.log(message);
	                    alert("Si è verificato un errore");
	                    break;
	            }
	        }
	    }
		let group_name = document.getElementById("details_group_name").textContent;
	    req.open("GET", "DeletePartecipant?groupName=" + group_name + "&partecipantName=" + userName + "&partecipantLastName=" + userLastName);
	    req.send();
	}
}