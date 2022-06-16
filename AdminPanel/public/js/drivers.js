let form_search_driver;
let table_all_drivers;
let div_content;
let div_loading;
let text_heading;
let text_empty;
let text_not_found;
let section_search;
let section_data;

let all_drivers = [];

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

    form_search_driver = document.getElementById("form_search_driver");
    table_all_drivers = document.getElementById("table_all_drivers");

    form_search_driver.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_driver["query"].value;

        search_driver(query);
    });

    get_all_drivers();
}

function get_all_drivers() {

    toggle_loading(true);
    let cf_get_all_drivers = firebase.functions().httpsCallable("driver-get_all_drivers");

    cf_get_all_drivers()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                toggle_loading(false);
                show_empty();
                return;
            }

            if (response["code"] === 200) {

                all_drivers = response["result"];

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th>Name</th>\n" +
                    "        <th>License Number</th>\n" +
                    "        <th>Plate Number</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                all_drivers.forEach(driver => {

                    let name = `${driver["name"]}`;
                    let license_number = `${driver["licenseNumber"]}`;
                    let plate_number = `${driver["plateNumber"]}`;

                    let table_row = `<tr>
                                        <td id="td_nm_${license_number}">${name}</td>
                                        <td id="td_ln_${license_number}">${license_number}</td>
                                        <td id="td_pn_${license_number}">${plate_number}</td>
                                        <td>
                                             <button class="btn btn-info" id="bt_sh_${license_number}"
                                                 onclick="show_driver(this.id)">Show Details</button>
                                        </td>
                                    </tr>`

                    table += table_row;
                });

                table_all_drivers.innerHTML = table;
                document.getElementById("text_count").innerHTML = `All Drivers (${all_drivers.length})`;
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_driver(query) {

    toggle_loading(true);
    let cf_search_driver = firebase.functions().httpsCallable("driver-query_driver");
    let data = {query}

    cf_search_driver(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let driver = response["result"];

                document.getElementById("search_bar").value = "";
                show_driver("sr_dr_" + driver["licenseNumber"]);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function show_driver(license_number) {

    license_number = license_number.slice(6);
    window.location.href = `driver_details.html?${license_number}`;
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