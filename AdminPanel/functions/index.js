let admin = require("firebase-admin");
let firebase_config = {
    apiKey: "AIzaSyBr9SyF_jmcdaPGg7zp7NsJIpZ3xX-Y0ko",
    authDomain: "police-ticket-4cdcc.firebaseapp.com",
    databaseURL: "https://police-ticket-4cdcc-default-rtdb.firebaseio.com",
    projectId: "police-ticket-4cdcc",
    storageBucket: "police-ticket-4cdcc.appspot.com",
    messagingSenderId: "27138017368",
    appId: "1:27138017368:web:3be464c20afffa3bdb622d",
    measurementId: "G-7Z8NZHEXSL"
};

const vision = require("@google-cloud/vision");
const client = new vision.ImageAnnotatorClient();
const functions = require('firebase-functions');

admin.initializeApp(firebase_config);

exports.dummy_driver = require('./wrappers/dummy_driver');
exports.dummy_policemen = require('./wrappers/dummy_policemen');
exports.penalty_rule = require('./wrappers/penalty_rule');
exports.collection_center = require('./wrappers/collection_center');
exports.policeman = require('./wrappers/policeman');
exports.driver = require('./wrappers/driver');
exports.ticket = require('./wrappers/ticket');
exports.dashboard = require('./wrappers/dashboard');
exports.annotateImage = functions.https.onCall(async (data) => {

    try {
        return await client.annotateImage(JSON.parse(data));
    } catch (e) {
        throw new functions.https.HttpsError("internal", e.message, e.details);
    }
});