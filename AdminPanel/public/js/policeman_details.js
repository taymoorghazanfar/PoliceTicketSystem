let table_details;
let table_tickets_count;
let table_tickets;

let text_all_tickets;
let text_badge_number;

let div_loading;
let section_details;
let section_total_tickets;
let section_tickets;

let policeman;

window.onload = function () {

    init();
}

function init() {

    div_loading = document.getElementById("div_loading");
    section_details = document.getElementById("section_details");
    section_total_tickets = document.getElementById("section_total_tickets");
    section_tickets = document.getElementById("section_all_tickets");

    text_badge_number = document.getElementById("text_badge_number");
    text_all_tickets = document.getElementById("text_tickets");

    table_details = document.getElementById("table_details");
    table_tickets_count = document.getElementById("table_tickets_count");
    table_tickets = document.getElementById("table_tickets");

    get_policeman();
}

function get_policeman() {

    toggle_loading(true);
    let badge_number = window.location.href;
    badge_number = badge_number.slice(badge_number.length - 6);

    let cf_get_policeman = firebase.functions().httpsCallable("policeman-query_policeman");
    let data = {query: badge_number};

    cf_get_policeman(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                alert(response["message"]);
                return;
            }

            if (response["code"] === 200) {

                policeman = response["result"];

                set_policeman_details(policeman);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function set_policeman_details(policeman) {

    text_badge_number.innerHTML = `Policeman: <b>${policeman["badgeNumber"]}</b>`;

    // setup policeman details table
    let table_details_data =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>Badge Number</th>\n" +
        "        <th>Name</th>\n" +
        "        <th>Email</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    table_details_data +=
        `<tr>
            <td id="td_bn_${policeman["badgeNumber"]}">${policeman["badgeNumber"]}</td>
            <td id="td_nm_${policeman["badgeNumber"]}">${policeman["name"]}</td>
            <td id="td_em_${policeman["badgeNumber"]}">${policeman["email"]}</td>
        </tr>`;

    table_details.innerHTML = table_details_data;

    // setup tickets table
    let tickets = policeman["ticketsIssued"];
    let paid_tickets_count = 0;
    let unpaid_tickets_count = 0;

    let table_ticket_header =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>ID</th>\n" +
        "        <th>Issue Date</th>\n" +
        "        <th>Due Date</th>\n" +
        "        <th>Status</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        " </thead>";

    let table_tickets_data = table_ticket_header;

    tickets.forEach(ticket => {

        let table_row =
            `<tr>
                 <td id="td_id_${ticket["id"]}">${ticket["id"]}</td>
                 <td id="td_di_${ticket["id"]}">${ticket["dateIssued"]}</td>
                 <td id="td_dd_${ticket["id"]}">${ticket["dateDue"]}</td>
                 <td style="color: ${ticket["isPayed"] === true ?
                '#31a800' : '#ff1b15'}" id="td_st_${ticket["id"]}">
                ${ticket["isPayed"] === true ? "PAID" : "UNPAID"}</td>
                 <td>
                     <button id="bt_sh_${ticket["id"]}"
                             class="btn btn-info"
                             onclick="show_ticket(this.id)">Show Details</button>
                 </td>
            </tr>`

        ticket["isPayed"] === true ? paid_tickets_count++ : unpaid_tickets_count++;
        table_tickets_data += table_row;
    });

    table_tickets.innerHTML = table_tickets_data;

    // setup tickets count table
    let table_count_data =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>Total Tickets Issued</th>\n" +
        "        <th>Paid Tickets</th>\n" +
        "        <th>Unpaid Tickets</th>\n" +
        "    </tr>\n" +
        " </thead>";

    table_count_data +=
        `<tr>
             <td id="td_tt_${policeman["badgeNumber"]}">${paid_tickets_count + unpaid_tickets_count}</td>
             <td id="td_pt_${policeman["badgeNumber"]}">${paid_tickets_count}</td>
             <td id="td_ut_${policeman["badgeNumber"]}">${unpaid_tickets_count}</td>
         </tr>`

    table_tickets_count.innerHTML = table_count_data;

    text_all_tickets.innerHTML = `All Issued Tickets: <b>${policeman["ticketsIssued"].length}</b>`;
    toggle_loading(false);
}

function show_ticket(ticket_number) {

    ticket_number = ticket_number.slice(6);
    window.location.href = `ticket_details.html?${ticket_number}`;
}

function toggle_loading(show_loading) {

    if (show_loading) {

        section_details.style.display = "none";
        section_total_tickets.style.display = "none";
        section_tickets.style.display = "none";
        div_loading.style.display = "flex";

    } else {

        section_details.style.display = "";
        section_total_tickets.style.display = "";
        section_tickets.style.display = "";
        div_loading.style.display = "none";
    }
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