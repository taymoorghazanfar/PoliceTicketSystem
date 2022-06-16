let functions = require("firebase-functions"),
    model = require("../models/model_dummy_policemen");

exports.get_dummy_policemen = functions.https.onCall((data) => {

    let query = data["query"];

    return model.get_dummy_policemen(query);
});

exports.add_dummy_policemen = functions.https.onCall((data) => {

    let badge_number = data["badge_number"];

    return model.add_dummy_policemen(badge_number);
});

exports.get_all_dummy_policemen = functions.https.onCall(() => {

    return model.get_all_dummy_policemen();
});

exports.update_dummy_policemen = functions.https.onCall((data) => {

    let badge_number = data["badge_number"];
    let updated_badge_number = data["updated_badge_number"];

    return model.update_dummy_policemen(badge_number, updated_badge_number);
});


exports.delete_dummy_policemen = functions.https.onCall((data) => {

    let badge_number = data["badge_number"];

    return model.delete_dummy_policemen(badge_number);
});