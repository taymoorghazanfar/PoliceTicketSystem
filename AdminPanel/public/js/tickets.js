let form_search_ticket;
let table_all_tickets;
let div_content;
let div_loading;
let text_heading;
let text_empty;
let text_not_found;
let section_search;
let section_data;

let all_tickets = [];

window.onload = function () {

    init();
}

function init() {

    div_content = document.getElementById("div_content");
    div_loading = document.getElementById("div_loading");
    text_heading = document.getElementById("text_heading");
    text_empty = document.getElementById("text_empty");
    text_not_found = document.getElementById("text_not_found");
    section_search = document.getElementById("section_search");
    section_data = document.getElementById("section_data");

    document.getElementById("search_bar").addEventListener("focus", e => {

        text_not_found.style.display = "none";
    });

    toggle_loading(true);

    form_search_ticket = document.getElementById("form_search_ticket");
    table_all_tickets = document.getElementById("table_all_tickets");

    form_search_ticket.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_ticket["query"].value;

        search_ticket(query);
    });

    get_all_tickets();
}

function get_all_tickets() {

    toggle_loading(true);
    let cf_get_all_tickets = firebase.functions().httpsCallable("ticket-get_all_tickets");

    cf_get_all_tickets()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                show_empty();
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                all_tickets = response["result"];
                let sorted_tickets = [];

                all_tickets.forEach(ticket => {

                    let date_string = ticket["dateIssued"];
                    let date = date_string.split("-");
                    ticket["dateIssued"] = `${date[2]}-${date[1]}-${date[0]}`;

                    sorted_tickets.push(ticket);
                });

                // sort tickets by date
                sorted_tickets.sort((a, b) => Date.parse(b["dateIssued"]) - Date.parse(a["dateIssued"]));

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th>Ticket ID</th>\n" +
                    "        <th>Issue Date</th>\n" +
                    "        <th>Status</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                sorted_tickets.forEach(ticket => {

                    let id = `${ticket["id"]}`;
                    let issue_date = `${ticket["dateIssued"]}`;
                    let is_payed = `${ticket["isPayed"]}`;

                    let date = issue_date.split("-");
                    issue_date = `${date[2]}-${date[1]}-${date[0]}`;

                    let table_row = `<tr>
                                        <td id="td_id_${id}">${id}</td>
                                        <td id="td_di_${id}">${issue_date}</td>
                                        <td style="color: ${is_payed === true ? '#31a800' : '#ff1b15'}" id="td_st_${id}">${is_payed === true ? 'PAID' : 'UNPAID'}</td>
                                        <td>
                                             <button class="btn btn-info" id="bt_sh_${id}"
                                                 onclick="show_ticket(this.id)">Show Details</button>
                                        </td>
                                    </tr>`

                    table += table_row;
                });

                table_all_tickets.innerHTML = table;
                document.getElementById("text_count").innerHTML = `All Tickets (${all_tickets.length})`
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_ticket(query) {

    toggle_loading(true);
    let cf_search_ticket = firebase.functions().httpsCallable("ticket-get_ticket");
    let data = {id: query}

    cf_search_ticket(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let ticket = response["result"];

                document.getElementById("search_bar").value = "";
                show_ticket("sr_tk_" + ticket["id"]);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function show_ticket(id) {

    id = id.slice(6);
    window.location.href = `ticket_details.html?${id}`;
}

function toggle_loading(show_loading) {

    if (show_loading) {

        section_search.style.display = "none"
        section_data.style.display = "none"
        div_loading.style.display = "flex";

    } else {

        section_data.style.display = ""
        section_search.style.display = ""
        div_loading.style.display = "none";
    }
}

function show_empty() {

    section_search.style.display = "none";
    section_data.style.display = "none";
    text_empty.style.display = "";
}

function signOut() {

    firebase.auth().signOut()
        .then(() => {

            window.location.href = "index.html";
        });

    return true;
}

// auth listener
firebase.auth().onAuthStateChanged(user => {

    if (user) {

        // toggle_loading(false);
        console.log("logged in");

    } else {

        window.location.href = "index.html";
    }
});