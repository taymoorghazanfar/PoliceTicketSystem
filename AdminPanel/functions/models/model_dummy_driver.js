let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.get_dummy_driver = function (query) {

    return admin.firestore()
        .collection("dummy_drivers")
        .where("licenseNumber", "==", query)
        .get()
        .then(snapshot => {

            if (!snapshot.empty) {

                return {
                    code: 200,
                    message: "Dummy driver found",
                    result: snapshot.docs[0].data()
                }
            }

            return admin.firestore()
                .collection("dummy_drivers")
                .where("plateNumber", "==", query)
                .get()
                .then(snapshot => {

                    if (!snapshot.empty) {

                        return {
                            code: 200,
                            message: "Dummy driver found",
                            result: snapshot.docs[0].data()
                        }
                    }

                    return {
                        code: 400,
                        message: "No dummy driver found",
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

model.add_dummy_driver = function (license_number, plate_number) {

    let dummy_driver = {

        licenseNumber: license_number,
        plateNumber: plate_number
    }

    return admin.firestore()
        .collection("dummy_drivers")
        .doc(license_number)
        .set(dummy_driver)
        .then(() => {

            return {
                code: 200,
                message: "Dummy driver added successfully",
                result: dummy_driver
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.get_all_dummy_drivers = function () {

    return admin.firestore()
        .collection("dummy_drivers")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No dummy drivers exist"
                }
            }

            let dummy_drivers = [];

            snapshot.forEach(doc => {

                let dummy_driver = doc.data();
                dummy_drivers.push(dummy_driver);
            });

            return {
                code: 200,
                message: "Dummy drivers found",
                result: dummy_drivers
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.update_dummy_driver = function (license_number, key, value) {

    // if license_number is updated, delete the user, recreate using the new license number
    if (key === "licenseNumber") {

        return admin.firestore()
            .collection("dummy_drivers")
            .doc(license_number)
            .get()
            .then(snapshot => {

                let dummy_driver = snapshot.data();

                // delete the existing dummy driver
                return admin.firestore()
                    .collection("dummy_drivers")
                    .doc(license_number)
                    .delete()
                    .then(() => {

                        dummy_driver["licenseNumber"] = value;

                        // push the dummy driver to the database
                        return admin.firestore()
                            .collection("dummy_drivers")
                            .doc(value)
                            .set(dummy_driver)
                            .then(() => {

                                return {
                                    code: 200,
                                    message: "License number updated successfully",
                                    result: dummy_driver
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
                    })
            })
            .catch(e => {

                throw new functions.https.HttpsError("internal",
                    e.message);
            })
    } else {

        return admin.firestore()
            .collection("dummy_drivers")
            .doc(license_number)
            .update(key, value)
            .then(() => {

                return {
                    code: 200,
                    message: "Plate number updated successfully",
                }

            })
            .catch(e => {

                throw new functions.https.HttpsError("internal",
                    e.message);
            })
    }
}

model.update_dummy_driver_full = function (license_number, updated_license_number, updated_plate_number) {

    // delete the previous dummy user
    return admin.firestore()
        .collection("dummy_drivers")
        .doc(license_number)
        .delete()
        .then(() => {

            // add the updated dummy driver
            let updated_dummy_driver = {
                licenseNumber: updated_license_number,
                plateNumber: updated_plate_number
            };

            return admin.firestore()
                .collection("dummy_drivers")
                .doc(updated_license_number)
                .set(updated_dummy_driver)
                .then(() => {

                    return {
                        code: 200,
                        message: "Dummy driver is updated successfully",
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
        })
}


model.delete_dummy_driver = function (license_number) {

    return admin.firestore()
        .collection("dummy_drivers")
        .doc(license_number)
        .delete()
        .then(() => {

            return {
                code: 200,
                message: `Dummy driver with license number: ${license_number} has been deleted`
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}
module.exports = model;