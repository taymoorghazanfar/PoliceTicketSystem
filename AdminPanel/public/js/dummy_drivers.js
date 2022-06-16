let form_add_driver;
let form_search_driver;
let table_all_dummy_drivers;
let div_content;
let div_loading;
let text_heading;
let text_empty;
let text_not_found;
let section_add;
let section_search;
let section_data;

let all_dummy_drivers = [];

window.onload = function () {

    init();
}

function init() {

    div_content = document.getElementById("div_content");
    div_loading = document.getElementById("div_loading");
    text_heading = document.getElementById("text_heading");
    text_empty = document.getElementById("text_empty");
    text_not_found = document.getElementById("text_not_found");
    section_add = document.getElementById("section_add");
    section_search = document.getElementById("section_search");
    section_data = document.getElementById("section_data");

    document.getElementById("search_bar").addEventListener("focus", e => {

        text_not_found.style.display = "none";
    });

    toggle_loading(true);

    form_add_driver = document.getElementById("form_add_driver");
    form_search_driver = document.getElementById("form_search_driver");
    table_all_dummy_drivers = document.getElementById("table_all_dummy_drivers");

    form_add_driver.addEventListener("submit", event => {

        event.preventDefault();

        let license_number = form_add_driver["license_number"].value;
        let plate_number = form_add_driver["plate_number"].value;

        form_add_driver.reset();
        add_dummy_driver(license_number, plate_number);
    });

    form_search_driver.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_driver["query"].value;

        search_dummy_driver(query);
    });

    get_all_dummy_drivers();
}

function add_dummy_driver(license_number, plate_number) {

    toggle_loading(true);
    let cf_search_dummy_driver = firebase.functions().httpsCallable("dummy_driver-get_dummy_driver");
    let data = {query: license_number}

    cf_search_dummy_driver(data)
        .then(response => {

            response = response.data;

            if (response.code === 200) {

                Swal.fire({
                    title: 'License number is already in use!',
                    confirmButtonColor: '#0c237e',
                    icon: 'warning'
                });
                toggle_loading(false);
                return;
            }

            if (response.code === 400) {

                let data = {query: plate_number}

                cf_search_dummy_driver(data)
                    .then(response => {

                        response = response.data;

                        if (response["code"] === 200) {

                            Swal.fire({
                                title: 'Plate number is already in use!',
                                confirmButtonColor: '#0c237e',
                                icon: 'warning'
                            });
                            toggle_loading(false);
                            return;
                        }

                        if (response["code"] === 400) {

                            console.log(response["message"]);

                            let cf_add_user = firebase.functions().httpsCallable("dummy_driver-add_dummy_driver");
                            let new_dummy_driver = {license_number, plate_number};

                            cf_add_user(new_dummy_driver)
                                .then(response => {

                                    response = response.data;

                                    if (response["code"] === 200) {

                                        Swal.fire({
                                            title: 'Driver added successfully!',
                                            confirmButtonColor: '#0c237e',
                                            icon: 'success'
                                        });
                                        get_all_dummy_drivers();
                                    }
                                })
                                .catch(e => {

                                    alert(e.message);
                                })
                        }
                    })
                    .catch(e => {

                        alert(e.message);
                    });
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function get_all_dummy_drivers() {

    toggle_loading(true);
    let cf_get_all_dummy_drivers = firebase.functions().httpsCallable("dummy_driver-get_all_dummy_drivers");

    cf_get_all_dummy_drivers()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                show_empty();
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                all_dummy_drivers = response["result"];

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th>License Number</th>\n" +
                    "        <th>Plate Number</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                all_dummy_drivers.forEach(dummy_driver => {

                    let license_number = `${dummy_driver["licenseNumber"]}`
                    let plate_number = `${dummy_driver["plateNumber"]}`

                    let table_row = `<tr>
                                        <td id="td_ln_${license_number}">${license_number}</td>
                                        <td id="td_pn_${license_number}">${plate_number}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${license_number}"
                                                class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_driver_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${license_number}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_dummy_driver(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${license_number}" style="display: none">
                                    
                                        <td><input id="ip_ln_${license_number}"
                                         placeholder="Enter license number" 
                                         value="${license_number}" 
                                          class="form-control"
                                         style="font-size: 12px"
                                         required></td>
                                         
                                        <td><input id="ip_pn_${license_number}" 
                                        placeholder="Enter plate number" 
                                         class="form-control"
                                         style="font-size: 12px"
                                        value="${plate_number}" 
                                        required></td>
                                        
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${license_number}" 
                                                 class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_dummy_driver(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${license_number}" 
                                                 class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_driver_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

                    table += table_row;
                });

                table_all_dummy_drivers.innerHTML = table;
                document.getElementById("text_count").innerHTML = ` All Dummy Drivers (${all_dummy_drivers.length})`
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_dummy_driver(query) {

    toggle_loading(true);
    let cf_search_dummy_driver = firebase.functions().httpsCallable("dummy_driver-get_dummy_driver");
    let data = {query}

    cf_search_dummy_driver(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let dummy_driver = response["result"];

                document.getElementById("search_bar").value = "";
                show_dummy_driver(dummy_driver);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}


function clear_dummy_driver() {

    document.getElementById("table_search").innerHTML = "";
    document.getElementById("search_bar").value = "";
}

function show_dummy_driver(dummy_driver) {

    document.getElementById("table_search").innerHTML = "";

    let table = " <thead>\n" +
        "    <tr>\n" +
        "        <th>License Number</th>\n" +
        "        <th>Plate Number</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    let license_number = `${dummy_driver["licenseNumber"]}`
    let plate_number = `${dummy_driver["plateNumber"]}`

    let table_row = `<tr>
                                        <td id="td_ln_${license_number}">${license_number}</td>
                                        <td id="td_pn_${license_number}">${plate_number}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${license_number}"
                                                class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_driver_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${license_number}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_dummy_driver(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${license_number}" style="display: none">
                                    
                                        <td><input id="ip_ln_${license_number}"
                                         placeholder="Enter license number" 
                                         value="${license_number}" 
                                          class="form-control"
                                         style="font-size: 12px"
                                         required></td>
                                         
                                        <td><input id="ip_pn_${license_number}" 
                                        placeholder="Enter plate number" 
                                         class="form-control"
                                         style="font-size: 12px"
                                        value="${plate_number}" 
                                        required></td>
                                        
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${license_number}" 
                                                 class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_dummy_driver(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${license_number}" 
                                                 class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="hide_edit_driver_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

    table += table_row;
    document.getElementById("table_search").innerHTML = table;
}

function update_dummy_driver(license_number) {

    license_number = license_number.slice(6);

    let updated_license_number = document.getElementById(`ip_ln_${license_number}`).value;
    let updated_plate_number = document.getElementById(`ip_pn_${license_number}`).value;

    let current_license_number = document.getElementById(`td_ln_${license_number}`).innerHTML;
    let current_plate_number = document.getElementById(`td_pn_${license_number}`).innerHTML;

    // todo: check empty
    if (updated_license_number.length === 0 || updated_plate_number.length === 0) {

        alert("Required fields are empty");
        return;
    }

    //todo: check if nothing is updated
    if (updated_license_number === current_license_number && updated_plate_number === current_plate_number) {

        alert("No values are updated");
        return;
    }

    let updateMode;
    let LICENSE_UPDATED = 0;
    let PLATE_UPDATED = 1;
    let BOTH_UPDATED = 2;

    // if only license number is updated
    if (updated_license_number !== current_license_number && updated_plate_number === current_plate_number) {

        updateMode = LICENSE_UPDATED;
    }

    // if only plate number is updated
    else if (updated_license_number === current_license_number && updated_plate_number !== current_plate_number) {

        updateMode = PLATE_UPDATED;
    }

    // if both are updated
    else if (updated_license_number !== current_license_number && updated_plate_number !== current_plate_number) {

        updateMode = BOTH_UPDATED;
    }

    // update only specific value
    if (updateMode === LICENSE_UPDATED || updateMode === PLATE_UPDATED) {

        toggle_loading(true);
        // check if value is duplicate
        let cf_search_dummy_driver = firebase.functions().httpsCallable("dummy_driver-get_dummy_driver");
        let data = {query: updateMode === LICENSE_UPDATED ? updated_license_number : updated_plate_number}

        cf_search_dummy_driver(data)
            .then(response => {

                response = response.data;

                if (response.code === 200) {

                    Swal.fire({
                        title: updateMode === LICENSE_UPDATED ?
                            "License number must be unique" : "Plate number must be unique",
                        confirmButtonColor: '#0c237e',
                        icon: 'warning'
                    });
                    toggle_loading(false);
                    return;
                }

                if (response.code === 400) {

                    toggle_loading(true);
                    let cf_update_dummy_driver = firebase.functions().httpsCallable("dummy_driver-update_dummy_driver");
                    let data = {
                        license_number,
                        key: updateMode === LICENSE_UPDATED ? "licenseNumber" : "plateNumber",
                        value: updateMode === LICENSE_UPDATED ? updated_license_number : updated_plate_number
                    }

                    cf_update_dummy_driver(data)
                        .then(response => {

                            response = response.data;

                            if (response["code"] === 200) {

                                Swal.fire({
                                    title: "Driver updated successfully",
                                    confirmButtonColor: '#0c237e',
                                    icon: 'success'
                                });
                                get_all_dummy_drivers();
                            }
                        })
                        .catch(e => {

                            alert(e.message);
                        });
                }
            })
            .catch(e => {

                alert(e.message);
            });
        return;
    }

    toggle_loading(true);
    // update both the plate and license
    let cf_update_dummy_driver = firebase.functions().httpsCallable("dummy_driver-update_dummy_driver_full");
    let data = {license_number, updated_license_number, updated_plate_number}

    cf_update_dummy_driver(data)
        .then(response => {

            response = response.data;

            if (response.code === 200) {

                Swal.fire({
                    title: "Driver updated successfully",
                    confirmButtonColor: '#0c237e',
                    icon: 'success'
                });
                get_all_dummy_drivers();
                return;
            }

            alert("An error occurred");
            toggle_loading(false);
        })
        .catch(e => {

            alert(e.message);
        });
}

function delete_dummy_driver(license_number) {

    license_number = license_number.slice(6);

    if (confirm(`Delete dummy user with license number: ${license_number} ?`) === true) {

        toggle_loading(true);
        let cf_delete_dummy_driver = firebase.functions().httpsCallable("dummy_driver-delete_dummy_driver");
        let data = {license_number}

        cf_delete_dummy_driver(data)
            .then(response => {

                response = response.data;

                if (response["code"] === 200) {

                    Swal.fire({

                        title: "Driver deleted successfully",
                        confirmButtonColor: '#0c237e',
                        icon: 'success'
                    });
                    get_all_dummy_drivers();
                    return;
                }

                alert("An error occurred");
                toggle_loading(false);
            })
            .catch(e => {

                alert(e.message);
            })
    }
}

function show_edit_driver_form(license_number) {

    license_number = license_number.slice(6);

    document.getElementById(`tr_${license_number}`).style.display = "";
}

function hide_edit_driver_form(license_number) {

    license_number = license_number.slice(6);

    document.getElementById(`tr_${license_number}`).style["display"] = "none";
}

function toggle_loading(show_loading) {

    if (show_loading) {

        clear_dummy_driver();
        section_add.style.display = "none"
        section_search.style.display = "none"
        section_data.style.display = "none"
        div_loading.style.display = "flex";

    } else {

        section_search.style.display = ""
        section_add.style.display = ""
        section_data.style.display = ""
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