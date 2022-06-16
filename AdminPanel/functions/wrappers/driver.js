let functions = require("firebase-functions"),
    model = require("../models/model_driver");

exports.verify_license = functions.https.onCall((data) => {

    let licenseNumber = data["licenseNumber"];

    return model.verify_license(licenseNumber);
});

exports.signup = functions.https.onCall((data) => {

    let licenseNumber = data["licenseNumber"];
    let plateNumber = data["plateNumber"];
    let licenseExpiry = data["licenseExpiry"];
    let name = data["name"];
    let email = data["email"];
    let password = data["password"];

    return model.signup(licenseNumber, plateNumber, licenseExpiry, name, email, password);
});

exports.login = functions.https.onCall((data) => {

    let email = data["email"];
    let password = data["password"];

    return model.login(email, password);
});

exports.get_driver = functions.https.onCall((data) => {

    let email = data["email"];

    return model.get_driver(email);
});

exports.get_driver_by_license_number = functions.https.onCall((data) => {

    let license_number = data["license_number"];

    return model.get_driver_by_license_number(license_number);
});

exports.get_driver_by_plate_number = functions.https.onCall((data) => {

    let plate_number = data["plate_number"];

    return model.get_driver_by_plate_number(plate_number)
});

exports.get_all_drivers = functions.https.onCall(() => {

    return model.get_all_drivers();
});

exports.query_driver = functions.https.onCall((data) => {

    let query = data["query"];

    return model.query_driver(query);
});
