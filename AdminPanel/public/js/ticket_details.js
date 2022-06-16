let text_ticket_id;
let table_details;
let table_violator;
let table_issuer;
let table_violations;

let div_loading;
let section_details;
let section_issued_to;
let section_issued_by;
let section_violations;

let ticket;

window.onload = function () {

    init();
}

function init() {

    div_loading = document.getElementById("div_loading");
    section_details = document.getElementById("section_details");
    section_issued_to = document.getElementById("section_issued_to");
    section_issued_by = document.getElementById("section_issued_by");
    section_violations = document.getElementById("section_violations");
    text_ticket_id = document.getElementById("text_ticket_id");
    table_details = document.getElementById("table_details");
    table_violator = document.getElementById("table_violator");
    table_issuer = document.getElementById("table_issuer");
    table_violations = document.getElementById("table_violations");

    get_ticket();
}

function get_ticket() {

    toggle_loading(true);
    let id = window.location.href;
    id = id.slice(id.length - 4);

    let cf_get_ticket = firebase.functions().httpsCallable("ticket-get_ticket");
    let data = {id};

    cf_get_ticket(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                alert(response["message"]);
                return;
            }

            if (response["code"] === 200) {

                ticket = response["result"];

                set_ticket_details(ticket);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function set_ticket_details(ticket) {

    // set ticket id
    text_ticket_id.innerHTML = `Ticket: <b>${ticket["id"]}</b>`;

    // setup violator table
    let driver = ticket["violator"];

    let table_violator_data =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>Name</th>\n" +
        "        <th>License Number</th>\n" +
        "        <th>Plate Number</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        " </thead>";

    table_violator_data +=
        `<tr>
            <td id="td_nm_${driver["licenseNumber"]}">${driver["name"]}</td>
            <td id="td_ln_${driver["licenseNumber"]}">${driver["licenseNumber"]}</td>
            <td id="td_pn_${driver["licenseNumber"]}">${driver["plateNumber"]}</td>
            <td>
                 <button id="bt_sh_${driver["licenseNumber"]}"
                 class="btn btn-info"
                  onclick="show_driver(this.id)">Show Details</button>
            </td>
        </tr>`;

    table_violator.innerHTML = table_violator_data;

    // setup issuer table
    let policeman = ticket["issuer"];

    let table_issuer_data = " <thead>\n" +
        "    <tr>\n" +
        "        <th>Name</th>\n" +
        "        <th>Badge Number</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    table_issuer_data +=
        `<tr>
             <td id="td_nm_${policeman["badgeNumber"]}">${policeman["name"]}</td>
             <td id="td_bn_${policeman["badgeNumber"]}">${policeman["badgeNumber"]}</td>
             <td>
                 <button id="bt_sh_${policeman["badgeNumber"]}"
                        class="btn btn-info"
                         onclick="show_policeman(this.id)">Show Details</button>
             </td>
        </tr>`

    table_issuer.innerHTML = table_issuer_data;

    // setup violations table
    let violations = ticket["penalties"];

    let table_violations_data =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>Code</th>\n" +
        "        <th>Title</th>\n" +
        "        <th>Penalty</th>\n" +
        "    </tr>\n" +
        " </thead>";

    let total_fine = 0;

    violations.forEach(violation => {

        total_fine += violation["amount"];

        let table_row =
            `<tr>
                 <td id="td_id_${violation["id"]}">${violation["id"]}</td>
                 <td id="td_tl_${violation["title"]}">${violation["title"]}</td>
                 <td style="color: #ff1b15;" id="td_py_${violation["amount"]}">G ${violation["amount"]}</td>
            </tr>`

        table_violations_data += table_row;
    });

    table_violations.innerHTML = table_violations_data;

    // setup ticket details table
    let table_details_data =
        " <thead>\n" +
        "    <tr>\n" +
        "        <th>Issue Date</th>\n" +
        "        <th>Due Date</th>\n" +
        "        <th>Status</th>\n" +
        "        <th>Total Fine</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    table_details_data +=
        `<tr>
            <td id="td_di_${ticket["id"]}">${ticket["dateIssued"]}</td>
            <td id="td_dd_${ticket["id"]}">${ticket["dateDue"]}</td>
            <td id="td_st_${ticket["id"]}" style="color: ${ticket["is_payed"] === true ?
            '#31a800' : '#ff1b15'}">${ticket["isPayed"] === true ? "PAID" : "UNPAID"}</td>
            <td style="color: #ff1b15;" id="td_fn_${ticket["id"]}">G ${total_fine}</td>
        </tr>`;

    table_details.innerHTML = table_details_data;

    toggle_loading(false);
}

function show_driver(license_number) {

    license_number = license_number.slice(6);
    window.location.href = `driver_details.html?${license_number}`;
}

function show_policeman(badge_number) {

    badge_number = badge_number.slice(6);
    window.location.href = `policeman_details.html?${badge_number}`;
}

function toggle_loading(show_loading) {

    if (show_loading) {

        section_details.style.display = "none";
        section_issued_to.style.display = "none";
        section_issued_by.style.display = "none";
        section_violations.style.display = "none";
        div_loading.style.display = "flex";

    } else {

        section_details.style.display = "";
        section_issued_to.style.display = "";
        section_issued_by.style.display = "";
        section_violations.style.display = "";
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