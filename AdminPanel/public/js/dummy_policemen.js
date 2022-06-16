let form_add_policemen;
let form_search_policemen;
let table_all_dummy_policemen;
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

    document.getElementById("search_bar").addEventListener("focus", e => {

        text_not_found.style.display = "none";
    });

    toggle_loading(true);

    form_add_policemen = document.getElementById("form_add_policemen");
    form_search_policemen = document.getElementById("form_search_policemen");
    table_all_dummy_policemen = document.getElementById("table_all_dummy_policemen");

    form_add_policemen.addEventListener("submit", event => {

        event.preventDefault();

        let badge_number = form_add_policemen["badge_number"].value;

        form_add_policemen.reset();
        add_dummy_policemen(badge_number);
    });

    form_search_policemen.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_policemen["query"].value;

        search_dummy_policemen(query);
    });

    get_all_dummy_policemen();
}

function add_dummy_policemen(badge_number) {

    toggle_loading(true);
    let cf_search_dummy_policemen = firebase.functions().httpsCallable("dummy_policemen-get_dummy_policemen");
    let data = {query: badge_number}

    cf_search_dummy_policemen(data)
        .then(response => {

            response = response.data;

            if (response.code === 200) {

                Swal.fire({
                    title: "Badge number is already in use!",
                    confirmButtonColor: '#0c237e',
                    icon: 'warning'
                });
                toggle_loading(false);
                return;
            }

            if (response.code === 400) {

                let cf_add_policemen = firebase.functions().httpsCallable("dummy_policemen-add_dummy_policemen");
                let new_dummy_policemen = {badge_number};

                cf_add_policemen(new_dummy_policemen)
                    .then(response => {

                        response = response.data;

                        if (response["code"] === 200) {

                            Swal.fire({
                                title: "Policeman added successfully",
                                confirmButtonColor: '#0c237e',
                                icon: 'success'
                            });
                            get_all_dummy_policemen();
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

function get_all_dummy_policemen() {

    toggle_loading(true);
    let cf_get_all_dummy_policemen = firebase.functions().httpsCallable("dummy_policemen-get_all_dummy_policemen");

    cf_get_all_dummy_policemen()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                show_empty();
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let all_dummy_policemen = response["result"];

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th>Badge Number</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                all_dummy_policemen.forEach(dummy_policemen => {

                    let badge_number = dummy_policemen["badgeNumber"];

                    let table_row = `<tr>
                                        <td id="td_bn_${badge_number}">${badge_number}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${badge_number}"
                                                  class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_policemen_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${badge_number}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_dummy_policemen(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${badge_number}" style="display: none">
                                    
                                        <td><input id="ip_bn_${badge_number}"
                                         placeholder="Enter badge number" 
                                          class="form-control"
                                         style="font-size: 12px"
                                         value="${badge_number}" 
                                         required></td>
                                         
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${badge_number}" 
                                                  class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_dummy_policemen(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${badge_number}" 
                                                   class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_policemen_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

                    table += table_row;
                });

                table_all_dummy_policemen.innerHTML = table;
                document.getElementById("text_count").innerHTML = ` All Dummy Policemen (${all_dummy_policemen.length})`
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_dummy_policemen(query) {

    toggle_loading(true);
    let cf_search_dummy_policemen = firebase.functions().httpsCallable("dummy_policemen-get_dummy_policemen");
    let data = {query}

    cf_search_dummy_policemen(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let dummy_policemen = response["result"];

                document.getElementById("search_bar").value = "";
                show_dummy_policeman(dummy_policemen);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function clear_dummy_policeman() {

    document.getElementById("table_search").innerHTML = "";
    document.getElementById("search_bar").value = "";
}

function show_dummy_policeman(dummy_policeman) {

    document.getElementById("table_search").innerHTML = "";

    let table = " <thead>\n" +
        "    <tr>\n" +
        "        <th>Badge Number</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    let badge_number = dummy_policeman["badgeNumber"];

    let table_row = `<tr>
                                        <td id="td_bn_${badge_number}">${badge_number}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${badge_number}"
                                                  class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_policemen_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${badge_number}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_dummy_policemen(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${badge_number}" style="display: none">
                                    
                                        <td><input id="ip_bn_${badge_number}"
                                         placeholder="Enter badge number" 
                                          class="form-control"
                                         style="font-size: 12px"
                                         value="${badge_number}" 
                                         required></td>
                                         
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${badge_number}" 
                                                  class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_dummy_policemen(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${badge_number}" 
                                                   class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_policemen_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

    table += table_row;
    document.getElementById("table_search").innerHTML = table;
}

function update_dummy_policemen(badge_number) {

    badge_number = badge_number.slice(6);

    let updated_badge_number = document.getElementById(`ip_bn_${badge_number}`).value;

    let current_badge_number = document.getElementById(`td_bn_${badge_number}`).innerHTML;

    // todo: check empty
    if (updated_badge_number.length === 0) {

        alert("Required field is empty");
        return;
    }

    //todo: check if nothing is updated
    if (updated_badge_number === current_badge_number) {

        alert("No value is updated");
        return;
    }


    // check if value is duplicate
    toggle_loading(true);
    let cf_search_dummy_policemen = firebase.functions().httpsCallable("dummy_policemen-get_dummy_policemen");
    let data = {query: updated_badge_number};

    cf_search_dummy_policemen(data)
        .then(response => {

            response = response.data;

            if (response.code === 200) {

                Swal.fire({
                    title: "Badge number must be unique",
                    confirmButtonColor: '#0c237e',
                    icon: 'warning'
                });
                toggle_loading(false);
                return;
            }

            if (response.code === 400) {

                // update the badge number
                let cf_update_dummy_policemen = firebase.functions().httpsCallable("dummy_policemen-update_dummy_policemen");
                let data = {badge_number, updated_badge_number}

                cf_update_dummy_policemen(data)
                    .then(response => {

                        response = response.data;

                        if (response.code === 200) {

                            Swal.fire({
                                title: "Policeman updated successfully",
                                confirmButtonColor: '#0c237e',
                                icon: 'success'
                            });
                            get_all_dummy_policemen();
                            return;
                        }

                        toggle_loading(false);
                        alert("An error occurred");
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

function delete_dummy_policemen(badge_number) {

    badge_number = badge_number.slice(6);

    if (confirm(`Delete dummy user with badge no.: ${badge_number} ?`) === true) {

        toggle_loading(true);
        let cf_delete_dummy_policemen = firebase.functions().httpsCallable("dummy_policemen-delete_dummy_policemen");
        let data = {badge_number}

        cf_delete_dummy_policemen(data)
            .then(response => {

                response = response.data;

                if (response["code"] === 200) {

                    Swal.fire({
                        title: 'Driver Deleted Successfully!',
                        confirmButtonColor: '#0c237e',
                        icon: 'success'
                    });
                    get_all_dummy_policemen();
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

function show_edit_policemen_form(badge_number) {

    badge_number = badge_number.slice(6);

    document.getElementById(`tr_${badge_number}`).style.display = "";
}

function hide_edit_policemen_form(badge_number) {

    badge_number = badge_number.slice(6);

    document.getElementById(`tr_${badge_number}`).style["display"] = "none";
}

function toggle_loading(show_loading) {

    if (show_loading) {

        clear_dummy_policeman();
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