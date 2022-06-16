let functions = require("firebase-functions"),
    model = require("../models/model_penalty_rule");

exports.get_penalty_rule = functions.https.onCall((data) => {

    let query = data["query"];

    return model.get_penalty_rule(query);
});

exports.add_penalty_rule = functions.https.onCall((data) => {

    let title = data["title"];
    let description = data["description"];
    let amount = data["amount"];

    return model.add_penalty_rule(title, description, amount);
});

exports.get_all_penalty_rules = functions.https.onCall(() => {

    return model.get_all_penalty_rules();
});

exports.update_penalty_rule = functions.https.onCall((data) => {

    let id = data["id"];
    let title = data["title"];
    let description = data["description"];
    let amount = data["amount"];

    return model.update_penalty_rule(id, title, description, amount);
});


exports.delete_penalty_rule = functions.https.onCall((data) => {

    let id = data["id"];

    return model.delete_penalty_rule(id)
});