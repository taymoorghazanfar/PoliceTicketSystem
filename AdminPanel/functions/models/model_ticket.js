let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.get_ticket = function (id) {

    return admin.firestore()
        .collection("tickets")
        .where("id", "==", id)
        .get()
        .then(snapshot => {

            if (!snapshot.empty) {

                return {
                    code: 200,
                    message: "ticket found",
                    result: snapshot.docs[0].data()
                }
            }

            return {
                code: 400,
                message: "No ticket found"
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.get_all_tickets = function () {

    return admin.firestore()
        .collection("tickets")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No tickets exist"
                }
            }

            let tickets = [];

            snapshot.forEach(doc => {

                let ticket = doc.data();
                tickets.push(ticket);
            });

            return {
                code: 200,
                message: "tickets found",
                result: tickets
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

module.exports = model;