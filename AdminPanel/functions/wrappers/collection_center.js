let functions = require("firebase-functions"),
    model = require("../models/model_collection_center");

exports.get_collection_center = functions.https.onCall((data) => {

    let query = data["query"];

    return model.get_collection_center(query);
});

exports.add_collection_center = functions.https.onCall((data) => {

    let name = data["name"];
    let phone = data["phone"];
    let lat = data["lat"];
    let lng = data["lng"];

    return model.add_collection_center(name, phone, lat, lng);
});

exports.get_all_collection_centers = functions.https.onCall(() => {

    return model.get_all_collection_centers();
});

exports.update_collection_center = functions.https.onCall((data) => {

    let id = data["id"];
    let name = data["name"];
    let phone = data["phone"];
    let lat = data["lat"];
    let lng = data["lng"];

    return model.update_collection_center(id, name, phone, lat, lng);
});


exports.delete_collection_center = functions.https.onCall((data) => {

    let id = data["id"];

    return model.delete_collection_center(id)
});