let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.get_dummy_policemen = function (query) {

    return admin.firestore()
        .collection("dummy_policemen")
        .where("badgeNumber", "==", query)
        .get()
        .then(snapshot => {

            if (!snapshot.empty) {

                return {
                    code: 200,
                    message: "Dummy policemen found",
                    result: snapshot.docs[0].data()
                }
            }

            return {
                code: 400,
                message: "No dummy policemen found"
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.add_dummy_policemen = function (badge_number) {

    let dummy_policemen = {

        badgeNumber: badge_number
    }

    return admin.firestore()
        .collection("dummy_policemen")
        .doc(badge_number)
        .set(dummy_policemen)
        .then(() => {

            return {
                code: 200,
                message: "Dummy policemen added successfully",
                result: dummy_policemen
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.get_all_dummy_policemen = function () {

    return admin.firestore()
        .collection("dummy_policemen")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No dummy policemen exist"
                }
            }

            let dummy_policemen = [];

            snapshot.forEach(doc => {

                let dummy_policeman = doc.data();
                dummy_policemen.push(dummy_policeman);
            });

            return {
                code: 200,
                message: "Dummy policemen found",
                result: dummy_policemen
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.update_dummy_policemen = function (badge_number, updated_badge_number) {

    return admin.firestore()
        .collection("dummy_policemen")
        .doc(badge_number)
        .delete()
        .then(() => {

            let updated_dummy_policemen = {badgeNumber: updated_badge_number};

            return admin.firestore()
                .collection("dummy_policemen")
                .doc(updated_badge_number)
                .set(updated_dummy_policemen)
                .then(() => {

                    return {
                        code: 200,
                        message: "Dummy policemen updated successfully",
                    }

                })
                .catch(e => {

                    throw new functions.https.HttpsError("internal",
                        e.message);
                })
        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.delete_dummy_policemen = function (badge_number) {

    return admin.firestore()
        .collection("dummy_policemen")
        .doc(badge_number)
        .delete()
        .then(() => {

            return {
                code: 200,
                message: "Dummy policemen with badge no.: " + badge_number + " has been deleted"
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}
module.exports = model;