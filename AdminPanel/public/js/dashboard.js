let div_loading;
let section_upper_row;
let section_lower_row;
let section_first_chart;
let section_second_chart;

let text_total_drivers;
let text_total_policemen;
let text_total_collection_centers;
let text_total_penalty_rules;
let text_total_paid_tickets;
let text_total_unpaid_tickets;
let text_total_revenue;
let text_today;

let data;

window.onload = function () {

    init();
    get_data();
}

function init() {

    div_loading = document.getElementById("div_loading");
    section_upper_row = document.getElementById("section_upper_row");
    section_lower_row = document.getElementById("section_lower_row");
    section_first_chart = document.getElementById("section_first_chart");
    section_second_chart = document.getElementById("section_second_chart");

    text_total_drivers = document.getElementById("text_total_drivers");
    text_total_policemen = document.getElementById("text_total_policemen");
    text_total_collection_centers = document.getElementById("text_total_collection_centers");
    text_total_penalty_rules = document.getElementById("text_total_penalty_rules");
    text_total_paid_tickets = document.getElementById("text_total_paid_tickets");
    text_total_unpaid_tickets = document.getElementById("text_total_unpaid_tickets");
    text_total_revenue = document.getElementById("text_total_revenue");
    text_today = document.getElementById("text_today");
}

function get_data() {

    toggle_loading(true);
    let cf_get_dashboard_data = firebase.functions().httpsCallable("dashboard-get_dashboard_data");

    cf_get_dashboard_data()
        .then(response => {

            response = response.data;

            if (response["code"] === 200) {

                data = response["data"];

                init_texts();
                init_first_chart();
                init_second_chart();
                toggle_loading(false);
            }
        })
        .catch(e => {

            alert(e.message);
        });
}

function init_texts() {

    text_total_drivers.innerHTML = `# Drivers: <b>${data["total_drivers"]}</b>`;
    text_total_policemen.innerHTML = `# Policemen: <b>${data["total_policemen"]}</b>`;
    text_total_collection_centers.innerHTML = `# Collection Centers: <b>${data["total_collection_centers"]}</b>`;
    text_total_penalty_rules.innerHTML = `# Penalty Rules: <b>${data["total_penalty_rules"]}</b>`;
    text_total_paid_tickets.innerHTML = `# Paid Tickets: <b>${data["total_paid_tickets"]}</b>`;
    text_total_unpaid_tickets.innerHTML = `# Unpaid Tickets: <b>${data["total_unpaid_tickets"]}</b>`;
    text_total_revenue.innerHTML = `# Revenue: <b>${data["total_revenue"]} G</b>`;
    text_today.innerHTML = `Today's Tickets: <b>${get_date()}</b>`;
}

function init_first_chart() {

    const ctxL = document.getElementById("lineChart").getContext('2d');
    const myLineChart = new Chart(ctxL, {
        type: 'line',
        data: {
            labels: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            datasets: [{
                label: "Issued Tickets",
                data: data["tickets_timeline"]["issued"],
                backgroundColor: [
                    'rgb(93,133,255, .2)',
                ],
                borderColor: [
                    'rgb(39,24,165, .7)',
                ],
                borderWidth: 2
            },
                {
                    label: "Paid Tickets",
                    data: data["tickets_timeline"]["paid"],
                    backgroundColor: [
                        'rgb(106,176,98, .2)',
                    ],
                    borderColor: [
                        'rgb(42,115,12, .7)',
                    ],
                    borderWidth: 2
                },
                {
                    label: "Unpaid Tickets",
                    data: data["tickets_timeline"]["unpaid"],
                    backgroundColor: [
                        'rgb(171,106,106, .2)',
                    ],
                    borderColor: [
                        'rgb(155,16,16, .7)',
                    ],
                    borderWidth: 2
                }
            ]
        },
        options: {
            responsive: true
        }
    });
}

function init_second_chart() {

    const ctxB = document.getElementById("barChart").getContext('2d');
    const myBarChart = new Chart(ctxB, {
        type: 'bar',
        data: {
            labels: ["Issued Tickets", "Unpaid Tickets", "Paid Tickets"],
            datasets: [{
                label: "Today's chart of tickets",
                data: data["today_tickets"],
                backgroundColor: [
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                ],
                borderColor: [
                    'rgba(153, 102, 255, 1)',
                    'rgba(255,99,132,1)',
                    'rgba(75, 192, 192, 1)',
                ],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });
}

function get_date() {

    let myDate;
    myDate = new Date(new Date().getTime());
    let dateString = myDate.toLocaleDateString();

    let arr = dateString.split("/");

    let day = arr[1];
    let month = arr[0];
    let year = arr[2];

    if (day.length === 1) {

        day = "0" + day;
    }

    if (month.length === 1) {

        month = "0" + month;
    }

    return day + "-" + month + "-" + year;
}

function goto_revenue(){

    Swal.fire({

        title: "To be developed in future",
        confirmButtonColor: '#0c237e',
        icon: 'info'
    });
}

function toggle_loading(show_loading) {

    if (show_loading) {

        section_upper_row.style.display = "none";
        section_lower_row.style.display = "none";
        section_first_chart.style.display = "none";
        section_second_chart.style.display = "none";
        div_loading.style.display = "flex";

    } else {

        section_upper_row.style.display = "";
        section_lower_row.style.display = "";
        section_first_chart.style.display = "";
        section_second_chart.style.display = "";
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