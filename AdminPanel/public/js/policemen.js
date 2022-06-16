let form_search_policeman;
let table_all_policemen;
let div_content;
let div_loading;
let text_heading;
let text_empty;
let text_not_found;
let section_search;
let section_data;

let all_policemen = [];

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

    form_search_policeman = document.getElementById("form_search_policeman");
    table_all_policemen = document.getElementById("table_all_policemen");

    form_search_policeman.addEventListener("submit", event => {

        event.preventDefault();

        let query = form_search_policeman["query"].value;

        search_policeman(query);
    });

    get_all_policemen();
}

function get_all_policemen() {

    toggle_loading(true);
    let cf_get_all_policemen = firebase.functions().httpsCallable("policeman-get_all_policemen");

    cf_get_all_policemen()
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                show_empty();
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                all_policemen = response["result"];

                let table = " <thead>\n" +
                    "    <tr>\n" +
                    "        <th>Name</th>\n" +
                    "        <th>Badge Number</th>\n" +
                    "        <th>Options</th>\n" +
                    "    </tr>\n" +
                    "    </thead>";

                all_policemen.forEach(policeman => {

                    let name = `${policeman["name"]}`;
                    let badge_number = `${policeman["badgeNumber"]}`;

                    let table_row = `<tr>
                                        <td id="td_nm_${badge_number}">${name}</td>
                                        <td id="td_bn_${badge_number}">${badge_number}</td>
                                        <td>
                                             <button class="btn btn-info" id="bt_sh_${badge_number}"
                                                 onclick="show_policeman(this.id)">Show Details</button>
                                        </td>
                                    </tr>`

                    table += table_row;
                });

                table_all_policemen.innerHTML = table;
                document.getElementById("text_count").innerHTML = `All Policemen (${all_policemen.length})`
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function search_policeman(query) {

    toggle_loading(true);
    let cf_search_policeman = firebase.functions().httpsCallable("policeman-query_policeman");
    let data = {query}

    cf_search_policeman(data)
        .then(response => {

            response = response.data;

            if (response["code"] === 400) {

                text_not_found.style.display = "";
                toggle_loading(false);
                return;
            }

            if (response["code"] === 200) {

                let policeman = response["result"];

                document.getElementById("search_bar").value = "";
                show_policeman("sr_pm_" + policeman["badgeNumber"]);
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function show_policeman(badge_number) {

    badge_number = badge_number.slice(6);
    window.location.href = `policeman_details.html?${badge_number}`;

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