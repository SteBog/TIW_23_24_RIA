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
	
	function createGroupsContainer(title, data) {
		var div_container = document.createElement("div");
		div_container.id = "group_list_container";
		
		var header = document.createElement("h3");
		header.textContent = title;
		div_container.appendChild(header);
		
		var table = document.createElement("table");
		table.classList.add("tabellaProg");
		
		createTableHeader(table);
		
		createTableBody(table, data);
		
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
	
	function createTableBody(table, data) {
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
				show_details(item["id"]);
			});
			
			row.appendChild(name_cell);
			row.appendChild(duration_cell);
			row.appendChild(details_cell);
			
			tbody.appendChild(row);
		});
	}
	
	function downloadGroupData() {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var message = JSON.parse(req.responseText);
	            switch (req.status) {
	                case 200:
	                    createGroupsContainer("I tuoi Gruppi", message["adminGroups"]);
	                    break;
	                default:
	                    alert("Si è verificato un errore");
	                    break;
	            }
	        }
	    }
	    req.open("POST", "GetGroupsData");
	    req.send();
	}
	
	function show_details(group_id) {
		var req = new XMLHttpRequest();
	    req.onreadystatechange = () => {
	        if (req.readyState == XMLHttpRequest.DONE) {
	            var message;
	            switch (req.status) {
	                case 200:
						message = JSON.parse(req.responseText);
						group_details_container(message);
						console.log(message);
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
	
	function group_details_container(data) {
		document.getElementById("group_list_container").hidden = true;
		
		var tabellaProg = document.createElement("table");
		tabellaProg.className = "tabellaProg";
		tabellaProg.id = "tabellaProg";
		
		group_details_container_header(tabellaProg);
		
		group_details_container_body(tabellaProg, data);
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
		partecipants.forEach((item) => {
			let span = document.createElement("span");
			span.textContent = item;
			td_partecipanti.appendChild(span);
		});
		trow.appendChild(td_partecipanti);
		
		table.appendChild(trow);
	}
}