// let button;
let login_form;
let error_text;
let spinner;
let main_content;

window.onload = function () {

    main_content = document.getElementById("content");
    spinner = document.getElementById("spinner");

    toggle_loading(true);

    error_text = document.getElementById("error_text");
    error_text.style.display = "none";

    login_form = document.getElementById("login_form");
    login_form
        .addEventListener("submit", e => {
            e.preventDefault()

            let email = login_form["email"].value;
            let password = login_form["password"].value;
            error_text.style.display = "none";

            signIn(email, password);
        })
}

function signIn(email, password) {

    toggle_loading(true);

    firebase.auth().signInWithEmailAndPassword(email, password)
        .then(user => {

            console.log('logged in', user);
            toggle_loading(false);
            // window.location.href = "html/dashboard.html";
        })
        .catch(error => {

            toggle_loading(false);
            error_text.style.display = "";
            error_text.innerHTML = "Invalid email or password"
        });
}

function toggle_loading(show_loading) {

    if (show_loading) {

        main_content.style.display = "none"
        spinner.style.display = "";

    } else {

        main_content.style.display = "table-cell"
        spinner.style.display = "none";
    }
}

// auth listener
firebase.auth().onAuthStateChanged(user => {

    if (user) {

        // toggle_loading(false);
        window.location.href = "dashboard.html";

    } else {

        toggle_loading(false);
        console.log("not logged in");
    }
});