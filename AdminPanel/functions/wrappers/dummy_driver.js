let functions = require("firebase-functions"),
    model = require("../models/model_dummy_driver");

exports.get_dummy_driver = functions.https.onCall((data) => {

    let query = data["query"];

    return model.get_dummy_driver(query);
});

exports.add_dummy_driver = functions.https.onCall((data) => {

    let license_number = data["license_number"];
    let plate_number = data["plate_number"];

    return model.add_dummy_driver(license_number, plate_number);
});

exports.get_all_dummy_drivers = functions.https.onCall(() => {

    return model.get_all_dummy_drivers();
});

exports.update_dummy_driver = functions.https.onCall((data) => {

    let license_number = data["license_number"];
    let key = data["key"];
    let value = data["value"];

    return model.update_dummy_driver(license_number, key, value);
});

exports.update_dummy_driver_full = functions.https.onCall((data) => {

    let license_number = data["license_number"];
    let updated_license_number = data["updated_license_number"];
    let updated_plate_number = data["updated_plate_number"];

    return model.update_dummy_driver_full(license_number, updated_license_number, updated_plate_number);
});

exports.delete_dummy_driver = functions.https.onCall((data) => {

    let license_number = data["license_number"];

    return model.delete_dummy_driver(license_number);
});