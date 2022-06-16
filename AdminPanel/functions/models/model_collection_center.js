let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.get_collection_center = function (query) {

    return admin.firestore()
        .collection("collection_centers")
        .where("id", "==", query)
        .get()
        .then(snapshot => {

            if (!snapshot.empty) {

                return {
                    code: 200,
                    message: "Collection center found",
                    result: snapshot.docs[0].data()
                }
            }

            return admin.firestore()
                .collection("collection_centers")
                .where("name", "==", query)
                .get()
                .then(snapshot => {

                    if (!snapshot.empty) {

                        return {
                            code: 200,
                            message: "Collection center found",
                            result: snapshot.docs[0].data()
                        }
                    }

                    return {
                        code: 400,
                        message: "No Collection center found"
                    }

                })
                .catch(e => {

                    throw new functions.https.HttpsError("internal",
                        e.message);
                });

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.add_collection_center = function (name, phone, lat, lng) {

    let id = admin.firestore().collection("collection_centers").doc().id.slice(0, 4);

    let collection_center = {

        id,
        name,
        phone,
        lat,
        lng,
        dateCreated: get_date(false)
    }

    return admin.firestore()
        .collection("collection_centers")
        .doc(id)
        .set(collection_center)
        .then(() => {

            return {
                code: 200,
                message: "Collection center added successfully",
                result: collection_center
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.get_all_collection_centers = function () {

    return admin.firestore()
        .collection("collection_centers")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No Collection centers exist"
                }
            }

            let collection_centers = [];

            snapshot.forEach(doc => {

                let collection_center = doc.data();
                collection_centers.push(collection_center);
            });

            return {
                code: 200,
                message: "Collection centers found",
                result: collection_centers
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.update_collection_center = function (id, name, phone, lat, lng) {

    return admin.firestore()
        .collection("collection_centers")
        .doc(id)
        .update({name, phone, lat, lng})
        .then(() => {

            return {
                code: 200,
                message: "Collection center updated successfully",
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.delete_collection_center = function (id) {

    return admin.firestore()
        .collection("collection_centers")
        .doc(id)
        .delete()
        .then(() => {

            return {
                code: 200,
                message: "Collection center with id: " + id + " has been deleted"
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