let table_details;
let table_paid_tickets;
let table_unpaid_tickets;

let text_paid_tickets;
let text_unpaid_tickets;
let text_license_number;
let text_no_paid_tickets;
let text_no_unpaid_tickets;

let div_loading;
let section_details;
let section_tickets_paid;
let section_tickets_unpaid;

let driver;

window.onload = function () {

    init();
}

function init() {

    div_loading = document.getElementById("div_loading");
    section_details = document.getElementById("section_details");
    section_tickets_paid = document.getElementById("section_tickets_paid");
    section_tickets_unpaid = document.getElementById("section_tickets_unpaid");

    text_license_number = document.getElementById("text_license_number");
    text_paid_tickets = document.getElementById("text_tickets_paid");
    text_unpaid_tickets = document.getElementById("text_tickets_unpaid");
    text_no_paid_tickets = document.getElementById("text_no_paid_tickets");
    text_no_unpaid_tickets = document.getElementById("text_no_unpaid_tickets");

    table_details = document.getElementById("table_details");
    table_paid_tickets = document.getElementById("table_paid_tickets");
    table_unpaid_tickets = document.getElementById("table_unpaid_tickets");
    text_paid_tickets = document.getElementById("text_tickets_paid");
    text_unpaid_tickets = document.getElementById("text_tickets_unpaid");

    get_driver();
}

function get_driver() {

    toggle_loading(true);
    let license_number = window.location.href;
    license_number = license_number.slice(license_number.length - 13);

    let cf_get_driver = firebase.functions().httpsCallable("driver-get_driver_by_license_number");
    let data = {license_number};

    cf_get_driver(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                alert(response["message"]);
                return;
            }

            if (response["code"] === 200) {

                driver = response["driver"];
                set_driver_details(driver);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function set_driver_details(driver) {

    text_license_number.innerHTML = `Driver: <b>${driver["licenseNumber"]}</b>`;

    // setup driver details table
    let table_details_data =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>Plate Number</th>\n" +
        "        <th>License Expiry</th>\n" +
        "        <th>Name</th>\n" +
        "        <th>Email</th>\n" +
        "        <th>Total Tickets</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    table_details_data +=
        `<tr>
            <td id="td_pn_${driver["licenseNumber"]}">${driver["plateNumber"]}</td>
            <td id="td_le_${driver["licenseNumber"]}">${driver["licenseExpiry"]}</td>
            <td id="td_nm_${driver["licenseNumber"]}">${driver["name"]}</td>
            <td id="td_em_${driver["licenseNumber"]}">${driver["email"]}</td>
            <td id="td_em_${driver["licenseNumber"]}">${driver["tickets"].length}</td>
        </tr>`;

    table_details.innerHTML = table_details_data;

    // setup violations table
    let tickets = driver["tickets"];
    let paid_tickets_count = 0;
    let unpaid_tickets_count = 0;

    let table_ticket_header =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>ID</th>\n" +
        "        <th>Issue Date</th>\n" +
        "        <th>Due Date</th>\n" +
        "        <th>Violations</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        " </thead>";

    let table_paid_tickets_data = table_ticket_header;
    let table_unpaid_tickets_data = table_ticket_header;

    tickets.forEach(ticket => {

        let table_row =
            `<tr>
                 <td id="td_id_${ticket["id"]}">${ticket["id"]}</td>
                 <td id="td_di_${ticket["id"]}">${ticket["dateIssued"]}</td>
                 <td id="td_dd_${ticket["id"]}">${ticket["dateDue"]}</td>
                 <td id="td_dd_${ticket["id"]}">${ticket["penalties"].length}</td>
                 <td>
                     <button id="bt_sh_${ticket["id"]}"
                             class="btn btn-info"
                             onclick="show_ticket(this.id)">Show Details</button>
                 </td>
            </tr>`

        if (ticket["isPayed"] === true) {

            paid_tickets_count++
            table_paid_tickets_data += table_row;

        } else {

            unpaid_tickets_count++;
            table_unpaid_tickets_data += table_row;
        }
    });

    table_paid_tickets.innerHTML = table_paid_tickets_data;
    table_unpaid_tickets.innerHTML = table_unpaid_tickets_data;

    text_paid_tickets.innerHTML = `Paid Tickets: <b>${paid_tickets_count}</b>`;
    text_unpaid_tickets.innerHTML = `Unpaid Tickets: <b>${unpaid_tickets_count}</b>`;

    if (paid_tickets_count === 0) {

        table_paid_tickets.style.display = "none";
        text_no_paid_tickets.style.display = "";
    }

    if (unpaid_tickets_count === 0) {

        table_unpaid_tickets.style.display = "none";
        text_no_unpaid_tickets.style.display = "";
    }

    toggle_loading(false);
}

function show_ticket(ticket_number) {

    ticket_number = ticket_number.slice(6);
    window.location.href = `ticket_details.html?${ticket_number}`;
}

function toggle_loading(show_loading) {

    if (show_loading) {

        section_details.style.display = "none";
        section_tickets_paid.style.display = "none";
        section_tickets_unpaid.style.display = "none";
        div_loading.style.display = "flex";

    } else {

        section_details.style.display = "";
        section_tickets_paid.style.display = "";
        section_tickets_unpaid.style.display = "";
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