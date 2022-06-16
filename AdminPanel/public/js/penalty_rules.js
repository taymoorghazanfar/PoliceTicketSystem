let form_add_penalty_rule;
let form_search_penalty_rule;
let table_all_penalty_rules;
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

    form_add_penalty_rule = document.getElementById("form_add_penalty_rule");
    form_search_penalty_rule = document.getElementById("form_search_penalty_rule");
    table_all_penalty_rules = document.getElementById("table_all_penalty_rules");

    form_add_penalty_rule.addEventListener("submit", event => {

        event.preventDefault();

        let title = form_add_penalty_rule["title"].value;
        let description = form_add_penalty_rule["description"].value;
        let amount = form_add_penalty_rule["amount"].value;

        form_add_penalty_rule.reset();
        add_penalty_rule(title, description, amount);
    });

    form_search_penalty_rule.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_penalty_rule["query"].value;

        search_penalty_rule(query);
    });

    get_all_penalty_rules();
}

function add_penalty_rule(title, description, amount) {

    toggle_loading(true);
    let cf_add_penalty_rule = firebase.functions().httpsCallable("penalty_rule-add_penalty_rule");
    let new_penalty_rule = {title, description, amount};

    cf_add_penalty_rule(new_penalty_rule)
        .then(response => {

            response = response.data;

            if (response["code"] === 200) {

                Swal.fire({
                    title: 'Penalty rule added successfully!',
                    confirmButtonColor: '#0c237e',
                    icon: 'success'
                });
                get_all_penalty_rules();
            }
        })
        .catch(e => {

            toggle_loading(false);
            alert(e.message);
        })
}

function get_all_penalty_rules() {

    toggle_loading(true);
    let cf_get_all_penalty_rules = firebase.functions().httpsCallable("penalty_rule-get_all_penalty_rules");

    cf_get_all_penalty_rules()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                show_empty();
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let all_penalty_rules = response["result"];

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th style='width: 50px'>ID</th>\n" +
                    "        <th style='width: 250px'>Title</th>\n" +
                    "        <th style='width: 100px'>Amount</th>\n" +
                    "        <th style='width: 500px'>Description</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                all_penalty_rules.forEach(penalty_rule => {

                    let id = penalty_rule["id"];
                    let title = penalty_rule["title"];
                    let description = penalty_rule["description"];
                    let amount = penalty_rule["amount"];

                    let table_row = `<tr>
                                        <td id="td_id_${id}">${id}</td>
                                        <td id="td_tl_${id}">${title}</td>
                                        <td id="td_am_${id}">${amount}</td>
                                        <td id="td_ds_${id}">${description}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${id}"
                                                class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_penalty_rule_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${id}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_penalty_rule(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${id}" style="display: none">
                                    
                                        <td>${id}</td>
                                         
                                         <td><input id="ip_tl_${id}"
                                         placeholder="Enter title" 
                                         value="${title}" 
                                         class="form-control"
                                         style="font-size: 12px"
                                         required></td>
                                         
                                         <td><input id="ip_am_${id}"
                                         placeholder="Enter penalty amount" 
                                         type="number"
                                         class="form-control"
                                         style="font-size: 12px"
                                         value="${amount}" 
                                         required></td>
                                         
                                         <td><input id="ip_ds_${id}"
                                         placeholder="Enter description" 
                                         value="${description}" 
                                         class="form-control"
                                         style="font-size: 12px"
                                         required></td>
                                         
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${id}" 
                                                class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_penalty_rule(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${id}" 
                                                 class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_penalty_rule_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

                    table += table_row;
                });

                table_all_penalty_rules.innerHTML = table;
                document.getElementById("text_count").innerHTML = `All Penalty Rules (${all_penalty_rules.length})`
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_penalty_rule(query) {

    toggle_loading(true);
    let cf_search_penalty_rule = firebase.functions().httpsCallable("penalty_rule-get_penalty_rule");
    let data = {query}

    cf_search_penalty_rule(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let penalty_rule = response["result"];

                document.getElementById("search_bar").value = "";
                show_penalty_rule(penalty_rule);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function clear_penalty_rule() {

    document.getElementById("table_search").innerHTML = "";
    document.getElementById("search_bar").value = "";
}

function show_penalty_rule(penalty_rule) {

    document.getElementById("table_search").innerHTML = "";

    let id = penalty_rule["id"];
    let title = penalty_rule["title"];
    let description = penalty_rule["description"];
    let amount = penalty_rule["amount"];

    let table = " <thead>\n" +
        "    <tr>\n" +
        "        <th>ID</th>\n" +
        "        <th>Title</th>\n" +
        "        <th>Amount</th>\n" +
        "        <th>Description</th>\n" +
        "        <th>Options</th>\n" +
        "    </tr>\n" +
        "    </thead>";

    let table_row = `<tr>
                                        <td id="td_id_${id}">${id}</td>
                                        <td id="td_tl_${id}">${title}</td>
                                        <td id="td_am_${id}">${amount}</td>
                                        <td id="td_ds_${id}">${description}</td>
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_ed_${id}"
                                                class="btn btn-primary"
                                                 style="font-size: 12px"
                                                 onclick="show_edit_penalty_rule_form(this.id)">Edit</button>
                                                 
                                                <button id="bt_dl_${id}"
                                                class="btn btn-danger"
                                                style="font-size: 12px"
                                                onclick="delete_penalty_rule(this.id)">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr id="tr_${id}" style="display: none">
                                    
                                        <td>{id}</td>
                                         
                                         <td><input id="ip_tl_${id}"
                                         placeholder="Enter title" 
                                         value="${title}" 
                                         class="form-control"
                                         style="font-size: 12px"
                                         required></td>
                                         
                                         <td><input id="ip_am_${id}"
                                         placeholder="Enter penalty amount" 
                                         type="number"
                                         class="form-control"
                                         style="font-size: 12px"
                                         value="${amount}" 
                                         required></td>
                                         
                                         <td><input id="ip_ds_${id}"
                                         placeholder="Enter description" 
                                         value="${description}" 
                                         class="form-control"
                                         style="font-size: 12px"
                                         required></td>
                                         
                                        <td>
                                            <div style="display: inline">
                                                <button id="bt_up_${id}" 
                                                class="btn btn-success"
                                                style="font-size: 12px"
                                                onclick="update_penalty_rule(this.id)">Update</button>
                                                
                                                <button id="bt_cl_${id}" 
                                                 class="btn btn-secondary"
                                                style="font-size: 12px"
                                                onclick="hide_edit_penalty_rule_form(this.id)">Cancel</button>
                                            </div>
                                    </tr>`

    table += table_row;
    document.getElementById("table_search").innerHTML = table;
}

function update_penalty_rule(id) {

    id = id.slice(6);

    let updated_title = document.getElementById(`ip_tl_${id}`).value;
    let updated_description = document.getElementById(`ip_ds_${id}`).value;
    let updated_amount = document.getElementById(`ip_am_${id}`).value;

    let current_title = document.getElementById(`td_tl_${id}`).innerHTML;
    let current_description = document.getElementById(`td_ds_${id}`).innerHTML;
    let current_amount = document.getElementById(`td_am_${id}`).innerHTML;

    // check empty
    if (updated_title.length === 0
        || updated_description.length === 0
        || updated_amount.length === 0) {

        alert("Required fields are empty");
        return;
    }

    // check if nothing is updated
    if (updated_title === current_title
        && updated_description === current_description
        && updated_amount === current_amount) {

        alert("No value is updated");
        return;
    }

    toggle_loading(true);
    // update the penalty rule
    let cf_update_penalty_rule = firebase.functions().httpsCallable("penalty_rule-update_penalty_rule");
    let data = {id, title: updated_title, description: updated_description, amount: updated_amount}

    cf_update_penalty_rule(data)
        .then(response => {

            response = response.data;

            if (response.code === 200) {

                Swal.fire({
                    title: 'Penalty rule updated successfully!',
                    confirmButtonColor: '#0c237e',
                    icon: 'success'
                });
                get_all_penalty_rules();
                return;
            }

            alert("An error occurred");
            toggle_loading(false);
        })
        .catch(e => {

            alert(e.message);
        });
}

function delete_penalty_rule(id) {

    id = id.slice(6);

    if (confirm(`Delete penalty rule with id: ${id} ?`) === true) {

        toggle_loading(true);
        // delete the penalty rule
        let cf_delete_penalty_rule = firebase.functions().httpsCallable("penalty_rule-delete_penalty_rule");
        let data = {id}

        cf_delete_penalty_rule(data)
            .then(response => {

                response = response.data;

                if (response["code"] === 200) {

                    Swal.fire({
                        title: 'Penalty rule deleted successfully!',
                        confirmButtonColor: '#0c237e',
                        icon: 'success'
                    });
                    get_all_penalty_rules();
                    return;
                }

                alert("An error occurred");
            })
            .catch(e => {

                alert(e.message);
            })
    }
}

function show_edit_penalty_rule_form(id) {

    id = id.slice(6);

    document.getElementById(`tr_${id}`).style.display = "";
}

function hide_edit_penalty_rule_form(id) {

    id = id.slice(6);

    document.getElementById(`tr_${id}`).style["display"] = "none";
}

function toggle_loading(show_loading) {

    if (show_loading) {

        clear_penalty_rule();
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