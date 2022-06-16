let functions = require("firebase-functions"),
    model = require("../models/model_ticket");

exports.get_ticket = functions.https.onCall((data) => {

    let id = data["id"];

    return model.get_ticket(id);
});

exports.get_all_tickets = functions.https.onCall(() => {

    return model.get_all_tickets();
});