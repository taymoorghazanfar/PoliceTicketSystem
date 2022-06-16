let functions = require("firebase-functions"),
    model = require("../models/dashboard_model");

exports.get_dashboard_data = functions.https.onCall(() => {

    return model.get_dashboard_data();
});