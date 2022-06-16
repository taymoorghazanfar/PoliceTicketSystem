let functions = require("firebase-functions"),
    model = require("../models/model_policeman");

exports.signup = functions.https.onCall((data) => {

    let badgeNumber = data["badgeNumber"];
    let name = data["name"];
    let email = data["email"];
    let password = data["password"];

    return model.signup(badgeNumber, name, email, password);
});

exports.login = functions.https.onCall((data) => {

    let email = data["email"];
    let password = data["password"];

    return model.login(email, password);
});

exports.get_policeman = functions.https.onCall((data) => {

    let email = data["email"];

    return model.get_policeman(email);
});

exports.issue_ticket = functions.https.onCall((data) => {

    let ticket = data["ticket"];

    return model.issue_ticket(ticket);
});

exports.get_all_policemen = functions.https.onCall((data) => {

    return model.get_all_policemen();
});

exports.query_policeman = functions.https.onCall((data) => {

    let query = data["query"];

    return model.query_policeman(query);
});
