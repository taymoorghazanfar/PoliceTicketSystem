let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.verify_license = function (licenseNumber) {

    // check if license number exist in dummy database
    return admin.firestore()
        .collection("dummy_drivers")
        .where("licenseNumber", "==", licenseNumber)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "License number is invalid"
                }
            }

            // check if license is already registered
            return admin.firestore()
                .collection("drivers")
                .where("licenseNumber", "==", licenseNumber)
                .get()
                .then(snapshot => {

                    if (!snapshot.empty) {

                        return {
                            code: 400,
                            message: "Provided license number is already registered"
                        }
                    }

                    return {
                        code: 200,
                        message: "License is verified"
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

model.signup = function (licenseNumber, plateNumber, licenseExpiry, name, email, password) {

    // check if plate number already exist and matches with the license
    return admin.firestore()
        .collection("dummy_drivers")
        .where("licenseNumber", "==", licenseNumber)
        .where("plateNumber", "==", plateNumber)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "Plate number is invalid. Enter the plate number associated with your license"
                }
            }

            // check if email already exist
            return admin.firestore()
                .collection("drivers")
                .where("email", "==", email)
                .get()
                .then(snapshot => {

                    if (!snapshot.empty) {

                        return {
                            code: 400,
                            message: "Provided email is already registered"
                        }
                    }

                    // add the new driver
                    let new_driver = {

                        licenseNumber,
                        plateNumber,
                        licenseExpiry,
                        name,
                        email,
                        password,
                        tickets: []
                    };

                    return admin.firestore()
                        .collection("drivers")
                        .doc(licenseNumber)
                        .set(new_driver)
                        .then(() => {

                            return {
                                code: 200,
                                message: "Registered successfully",
                                driver: new_driver
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

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.login = function (email, password) {

    return admin.firestore()
        .collection("drivers")
        .where("email", "==", email)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "Email is invalid"
                }
            }

            let driver = snapshot.docs[0].data();

            if (driver["password"] !== password) {

                return {
                    code: 400,
                    message: "Password is incorrect"
                }
            }

            return {

                code: 200,
                message: "Driver found",
                driver: snapshot.docs[0].data()
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.get_driver = function (email) {

    return admin.firestore()
        .collection("drivers")
        .where("email", "==", email)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No driver found with provided email"
                }
            }

            return {
                code: 200,
                message: "Driver found",
                driver: snapshot.docs[0].data()
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.get_driver_by_license_number = function (license_number) {

    return admin.firestore()
        .collection("drivers")
        .where("licenseNumber", "==", license_number)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No driver found with provided license number"
                }
            }

            return {
                code: 200,
                message: "Driver found",
                driver: snapshot.docs[0].data()
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.get_driver_by_plate_number = function (plate_number) {

    return admin.firestore()
        .collection("drivers")
        .where("plateNumber", "==", plate_number)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No driver found with provided plate number"
                }
            }

            return {
                code: 200,
                message: "Driver found",
                driver: snapshot.docs[0].data()
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.get_all_drivers = function () {

    return admin.firestore()
        .collection("drivers")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No drivers exist"
                }
            }

            let drivers = [];

            snapshot.forEach(doc => {

                let driver = doc.data();
                drivers.push(driver);
            });

            return {
                code: 200,
                message: "Drivers found",
                result: drivers
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.query_driver = function (query) {

    return admin.firestore()
        .collection("drivers")
        .where("licenseNumber", "==", query)
        .get()
        .then(snapshot => {

            if (!snapshot.empty) {

                return {
                    code: 200,
                    message: "Driver found",
                    result: snapshot.docs[0].data()
                }
            }

            return admin.firestore()
                .collection("drivers")
                .where("plateNumber", "==", query)
                .get()
                .then(snapshot => {

                    if (!snapshot.empty) {

                        return {
                            code: 200,
                            message: "Driver found",
                            result: snapshot.docs[0].data()
                        }
                    }

                    return {
                        code: 400,
                        message: "No driver found"
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

module.exports = model;