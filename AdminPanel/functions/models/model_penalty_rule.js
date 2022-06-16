let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.get_penalty_rule = function (query) {

    return admin.firestore()
        .collection("penalty_rules")
        .where("id", "==", query)
        .get()
        .then(snapshot => {

            if (!snapshot.empty) {

                return {
                    code: 200,
                    message: "Penalty rule found",
                    result: snapshot.docs[0].data()
                }
            }

            return {
                code: 400,
                message: "No penalty rule found"
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.add_penalty_rule = function (title, description, amount) {

    let id = admin.firestore().collection("penalty_rules").doc().id.slice(0, 4);

    let penalty_rule = {

        id,
        title,
        description,
        amount,
        dateCreated: get_date(false)
    }

    return admin.firestore()
        .collection("penalty_rules")
        .doc(id)
        .set(penalty_rule)
        .then(() => {

            return {
                code: 200,
                message: "Penalty rule added successfully",
                result: penalty_rule
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.get_all_penalty_rules = function () {

    return admin.firestore()
        .collection("penalty_rules")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No penalty rules exist"
                }
            }

            let penalty_rules = [];

            snapshot.forEach(doc => {

                let penalty_rule = doc.data();
                penalty_rules.push(penalty_rule);
            });

            return {
                code: 200,
                message: "Penalty rules found",
                result: penalty_rules
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.update_penalty_rule = function (id, title, description, amount) {


    return admin.firestore()
        .collection("penalty_rules")
        .doc(id)
        .update({title, description, amount})
        .then(() => {

            return {
                code: 200,
                message: "Penalty rule updated successfully",
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.delete_penalty_rule = function (id) {

    return admin.firestore()
        .collection("penalty_rules")
        .doc(id)
        .delete()
        .then(() => {

            return {
                code: 200,
                message: "Penalty rule with id: " + id + " has been deleted"
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

function get_date(get_due_date) {

    let myDate;

    if (get_due_date) {

        myDate = new Date(new Date().getTime() + (5 * 24 * 60 * 60 * 1000));

    } else {

        myDate = new Date(new Date().getTime());
    }

    let dateString = myDate.toLocaleDateString();

    let arr = dateString.split("/");

    let day = arr[1];
    let month = arr[0];
    let year = arr[2];

    if (day.length === 1) {

        day = "0" + day;
    }

    if (month.length === 1) {

        month = "0" + month;
    }

    return day + "-" + month + "-" + year;
}

module.exports = model;