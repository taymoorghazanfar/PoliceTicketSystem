let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.signup = function (badgeNumber, name, email, password) {

    // check if badge number exist in the dummy database
    return admin.firestore()
        .collection("policemen")
        .where("badgeNumber", "==", badgeNumber)
        .get()
        .then(snapshot => {

            // if no policemen exist in the dummy database
            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "Provided badge number is invalid"
                }
            }

            // check existing policemen database
            return admin.firestore()
                .collection("policemen")
                .where("badgeNumber", "==", badgeNumber)
                .get()
                .then(snapshot => {

                    // if policemen exist in the database
                    if (!snapshot.empty) {

                        return {
                            code: 400,
                            message: "Provided badge number is already registered"
                        }
                    }

                    // check if email is unique
                    return admin.firestore()
                        .collection("policemen")
                        .where("email", "==", email)
                        .get()
                        .then(snapshot => {

                            // if policemen exist in the database with that email
                            if (!snapshot.empty) {

                                return {
                                    code: 400,
                                    message: "Provided email is already registered"
                                }
                            }

                            // add the new policeman to the database
                            let new_policeman = {

                                badgeNumber,
                                name,
                                email,
                                password,
                                ticketsIssued: []
                            };

                            return admin.firestore()
                                .collection("policemen")
                                .doc(badgeNumber)
                                .set(new_policeman)
                                .then(() => {

                                    return {
                                        code: 200,
                                        message: "Registered successfully",
                                        policeman: new_policeman
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

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.login = function (email, password) {

    return admin.firestore()
        .collection("policemen")
        .where("email", "==", email)
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "Email is invalid"
                }
            }

            let policeman = snapshot.docs[0].data();

            if (policeman["password"] !== password) {

                return {
                    code: 400,
                    message: "Password is incorrect"
                }
            }

            return {
                code: 200,
                message: "Policeman found",
                policeman: snapshot.docs[0].data()
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.get_policeman = function (email) {

    return admin.firestore()
        .collection("policemen")
        .where("email", "==", email)
        .get()
        .then(snapshot => {

            // if no policemen exist in the dummy database
            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No policeman found with provided email"
                }
            }

            return {
                code: 200,
                message: "Policeman found",
                policeman: snapshot.docs[0].data()
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        });
}

model.issue_ticket = function (ticket) {

    let id = admin.firestore().collection("tickets").doc().id.slice(0, 4);
    let dateIssued = get_date(false);
    let dateDue = get_date(true)

    ticket["id"] = id;
    ticket["dateIssued"] = dateIssued;
    ticket["dateDue"] = dateDue;

    return admin.firestore()
        .collection("tickets")
        .doc(id)
        .set(ticket)
        .then(() => {

            let policeman_id = ticket["issuer"]["badgeNumber"];

            return admin.firestore()
                .collection("policemen")
                .doc(policeman_id)
                .update({ticketsIssued: admin.firestore.FieldValue.arrayUnion(ticket)})
                .then(() => {

                    let driver_id = ticket["violator"]["licenseNumber"];

                    return admin.firestore()
                        .collection("drivers")
                        .doc(driver_id)
                        .update({tickets: admin.firestore.FieldValue.arrayUnion(ticket)})
                        .then(() => {

                            return {

                                code: 200,
                                ticketId: id,
                                dateIssued,
                                dateDue
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

model.get_all_policemen = function () {

    return admin.firestore()
        .collection("policemen")
        .get()
        .then(snapshot => {

            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No policemen exist"
                }
            }

            let policemen = [];

            snapshot.forEach(doc => {

                let policeman = doc.data();
                policemen.push(policeman);
            });

            return {
                code: 200,
                message: "Policemen found",
                result: policemen
            }

        })
        .catch(e => {

            throw new functions.https.HttpsError("internal",
                e.message);
        })
}

model.query_policeman = function (query) {

    return admin.firestore()
        .collection("policemen")
        .where("badgeNumber", "==", query)
        .get()
        .then(snapshot => {

            // if no policemen exist in the dummy database
            if (snapshot.empty) {

                return {
                    code: 400,
                    message: "No policeman found"
                }
            }

            return {
                code: 200,
                message: "Policeman found",
                result: snapshot.docs[0].data()
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