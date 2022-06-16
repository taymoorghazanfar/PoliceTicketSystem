let form_add_collection_center;
let form_search_collection_center;
let table_all_collection_centers;
let div_content;
let div_loading;
let text_heading;
let text_empty;
let text_not_found;
let section_add;
let section_search;
let section_data;

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

    text_heading.innerHTML = "Collection Centers"

    document.getElementById("search_bar").addEventListener("focus", e => {

        text_not_found.style.display = "none";
    });

    toggle_loading(true);

    form_add_collection_center = document.getElementById("form_add_collection_center");
    form_search_collection_center = document.getElementById("form_search_collection_center");
    table_all_collection_centers = document.getElementById("table_all_collection_centers");

    form_add_collection_center.addEventListener("submit", event => {

        event.preventDefault();

        let name = form_add_collection_center["name"].value;
        let phone = form_add_collection_center["phone"].value;
        let lat = form_add_collection_center["lat"].value;
        let lng = form_add_collection_center["lng"].value;

        form_add_collection_center.reset();
        add_collection_center(name, phone, lat, lng);
    });

    form_search_collection_center.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_collection_center["query"].value;

        search_collection_center(query);
    });

    get_all_collection_centers();
}

function add_collection_center(name, phone, lat, lng) {

    toggle_loading(true);
    let cf_add_collection_center = firebase.functions().httpsCallable("collection_center-add_collection_center");
    let new_collection_center = {name, phone, lat, lng};

    cf_add_collection_center(new_collection_center)
        .then(response => {

            response = response.data;

            if (response["code"] === 200) {

                Swal.fire({
                    title: 'Collection center added successfully!',
                    confirmButtonColor: '#0c237e',
                    icon: 'success'
                });
                get_all_collection_centers();
            }
        })
        .catch(e => {

            toggle_loading(false);
            alert(e.message);
        })
}

function get_all_collection_centers() {

    toggle_loading(true);
    let cf_get_all_collection_centers = firebase.functions().httpsCallable("collection_center-get_all_collection_centers");

    cf_get_all_collection_centers()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                show_empty();
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let all_collection_centers = response["result"];

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th style='width: 50px'>ID</th>\n" +
                    "        <th>Name</th>\n" +
                    "        <th>Phone Number</th>\n" +
                    "        <th>Latitude</th>\n" +
                    "        <th>Longitude</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                all_collection_centers.forEach(collection_center => {

                    let id = collection_center["id"];
                    let name = collection_center["name"];
                    let phone = collection_center["phone"];
                    let lat = collection_center["lat"];
                    let lng = collection_center["lng"];

                    let table_row = `<tr>
                                        <td id="td_id_${id}">${id}</td>
                                        <td id="td_nm_${id}">${name}</td>
                                        <td id="td_ph_${id}">${phone}</td>
                                        <td id="td_lt_${id}">${lat}</td>
                                        <td id="td_ln_${id}">${lng}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${id}"
                                                 class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_collection_center_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${id}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_collection_center(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${id}" style="display: none">
                                    
                                        <td>${id}</td>
                                         
                                         <td><input id="ip_nm_${id}"
                                         placeholder="Enter center name" 
                                         class="form-control"
                                         style="font-size: 12px"
                                         value="${name}" 
                                         required></td>
                                         
                                         <td><input id="ip_ph_${id}"
                                         placeholder="Enter contact number" 
                                         class="form-control"
                                         type="number"
                                         style="font-size: 12px"
                                         value="${phone}" 
                                         required></td>
                                         
                                         <td><input id="ip_lt_${id}"
                                         placeholder="Enter latitude" 
                                         value="${lat}" 
                                         type="number"
                                         style="font-size: 12px"
                                         class="form-control"
                                         max="-0.1"
                                         step="any"
                                         required></td>
                                         
                                         <td><input id="ip_ln_${id}"
                                         placeholder="Enter longitude" 
                                         value="${lng}"
                                         style="font-size: 12px"
                                         class="form-control"
                                         type="number"
                                         max="-0.1"
                                         step="any"
                                         required></td>
                                         
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${id}" 
                                                class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_collection_center(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${id}" 
                                                class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_collection_center_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

                    table += table_row;
                });

                table_all_collection_centers.innerHTML = table;
                document.getElementById("text_count").innerHTML = `All Collection Centers (${all_collection_centers.length})`
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_collection_center(query) {

    toggle_loading(true);
    let cf_search_collection_center = firebase.functions().httpsCallable("collection_center-get_collection_center");
    let data = {query}

    cf_search_collection_center(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let collection_center = response["result"];

                document.getElementById("search_bar").value = "";
                show_collection_center(collection_center);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function clear_collection_center() {

    document.getElementById("table_search").innerHTML = "";
    document.getElementById("search_bar").value = "";
}

function show_collection_center(collection_center) {

    document.getElementById("table_search").innerHTML = "";

    let table = " <thead>\n" +
        "    <tr>\n" +
        "        <th style='width: 50px'>ID</th>\n" +
        "        <th>Name</th>\n" +
        "        <th>Phone Number</th>\n" +
        "        <th>Latitude</th>\n" +
        "        <th>Longitude</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    let id = collection_center["id"];
    let name = collection_center["name"];
    let phone = collection_center["phone"];
    let lat = collection_center["lat"];
    let lng = collection_center["lng"];

    let table_row = `<tr>
                                        <td id="td_id_${id}">${id}</td>
                                        <td id="td_nm_${id}">${name}</td>
                                        <td id="td_ph_${id}">${phone}</td>
                                        <td id="td_lt_${id}">${lat}</td>
                                        <td id="td_ln_${id}">${lng}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${id}"
                                                 class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_collection_center_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${id}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_collection_center(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${id}" style="display: none">
                                    
                                        <td>${id}</td>
                                         
                                         <td><input id="ip_nm_${id}"
                                         placeholder="Enter center name" 
                                         class="form-control"
                                         style="font-size: 12px"
                                         value="${name}" 
                                         required></td>
                                         
                                         <td><input id="ip_ph_${id}"
                                         placeholder="Enter contact number" 
                                         class="form-control"
                                         type="number"
                                         style="font-size: 12px"
                                         value="${phone}" 
                                         required></td>
                                         
                                         <td><input id="ip_lt_${id}"
                                         placeholder="Enter latitude" 
                                         value="${lat}" 
                                         type="number"
                                         style="font-size: 12px"
                                         class="form-control"
                                         max="-0.1"
                                         step="any"
                                         required></td>
                                         
                                         <td><input id="ip_ln_${id}"
                                         placeholder="Enter longitude" 
                                         value="${lng}"
                                         style="font-size: 12px"
                                         class="form-control"
                                         type="number"
                                         max="-0.1"
                                         step="any"
                                         required></td>
                                         
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${id}" 
                                                class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_collection_center(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${id}" 
                                                class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_collection_center_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

    table += table_row;
    document.getElementById("table_search").innerHTML = table;
}

function update_collection_center(id) {

    id = id.slice(6);

    let updated_name = document.getElementById(`ip_nm_${id}`).value;
    let updated_phone = document.getElementById(`ip_ph_${id}`).value;
    let updated_lat = document.getElementById(`ip_lt_${id}`).value;
    let updated_lng = document.getElementById(`ip_ln_${id}`).value;

    let current_name = document.getElementById(`td_nm_${id}`).innerHTML;
    let current_phone = document.getElementById(`td_ph_${id}`).innerHTML;
    let current_lat = document.getElementById(`td_lt_${id}`).innerHTML;
    let current_lng = document.getElementById(`td_ln_${id}`).innerHTML;

    // check empty
    if (updated_name.length === 0
        || updated_phone.length === 0
        || updated_lat.length === 0
        || updated_lng.length === 0) {

        alert("Required fields are empty");
        return;
    }

    // check if nothing is updated
    if (updated_name === current_name
        && updated_phone === current_phone
        && updated_lat === current_lat
        && updated_lng === current_lng) {

        alert("No value is updated");
        return;
    }

    toggle_loading(true);
    // update the collection center
    let cf_update_collection_center = firebase.functions().httpsCallable("collection_center-update_collection_center");
    let data = {id, name: updated_name, phone: updated_phone, lat: updated_lat, lng: updated_lng}

    cf_update_collection_center(data)
        .then(response => {

            response = response.data;

            if (response.code === 200) {

                Swal.fire({
                    title: 'Collection center updated successfully!',
                    confirmButtonColor: '#0c237e',
                    icon: 'success'
                });
                get_all_collection_centers();
                return;
            }

            alert("An error occurred");
            toggle_loading(false);
        })
        .catch(e => {

            alert(e.message);
        });
}

function delete_collection_center(id) {

    id = id.slice(6);

    if (confirm(`Delete collection center with id: ${id} ?`) === true) {

        toggle_loading(true);
        // delete the collection center
        let cf_delete_collection_center = firebase.functions().httpsCallable("collection_center-delete_collection_center");
        let data = {id}

        cf_delete_collection_center(data)
            .then(response => {

                response = response.data;

                if (response["code"] === 200) {

                    Swal.fire({
                        title: 'Collection center deleted Successfully!',
                        confirmButtonColor: '#0c237e',
                        icon: 'success'
                    });
                    get_all_collection_centers();
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

function show_edit_collection_center_form(id) {

    id = id.slice(6);

    document.getElementById(`tr_${id}`).style.display = "";
}

function hide_edit_collection_center_form(id) {

    id = id.slice(6);

    document.getElementById(`tr_${id}`).style["display"] = "none";
}

function toggle_loading(show_loading) {

    if (show_loading) {

        clear_collection_center();
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