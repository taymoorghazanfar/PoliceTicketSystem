let functions = require('firebase-functions'),
    admin = require("firebase-admin");

let model = {};

model.get_dashboard_data = function () {

    let data = {};
    let jan = {paid: 0, unpaid: 0, issued: 0};
    let feb = {paid: 0, unpaid: 0, issued: 0};
    let mar = {paid: 0, unpaid: 0, issued: 0};
    let apr = {paid: 0, unpaid: 0, issued: 0};
    let may = {paid: 0, unpaid: 0, issued: 0};
    let jun = {paid: 0, unpaid: 0, issued: 0};
    let jul = {paid: 0, unpaid: 0, issued: 0};
    let aug = {paid: 0, unpaid: 0, issued: 0};
    let sep = {paid: 0, unpaid: 0, issued: 0};
    let oct = {paid: 0, unpaid: 0, issued: 0};
    let nov = {paid: 0, unpaid: 0, issued: 0};
    let dec = {paid: 0, unpaid: 0, issued: 0};
    let yearly_data = {

        "01": jan,
        "02": feb,
        "03": mar,
        "04": apr,
        "05": may,
        "06": jun,
        "07": jul,
        "08": aug,
        "09": sep,
        "10": oct,
        "11": nov,
        "12": dec
    };
    let today = {paid: 0, unpaid: 0, issued: 0};

    // total drivers
    return admin.firestore()
        .collection("drivers")
        .get()
        .then(snapshot => {

            data["total_drivers"] = snapshot.size;

            // total policemen
            return admin.firestore()
                .collection("policemen")
                .get()
                .then(snapshot => {

                    data["total_policemen"] = snapshot.size;

                    // total collection centers
                    return admin.firestore()
                        .collection("collection_centers")
                        .get()
                        .then(snapshot => {

                            data["total_collection_centers"] = snapshot.size;

                            // total penalty rules
                            return admin.firestore()
                                .collection("penalty_rules")
                                .get()
                                .then(snapshot => {

                                    data["total_penalty_rules"] = snapshot.size;

                                    // total tickets
                                    return admin.firestore()
                                        .collection("tickets")
                                        .get()
                                        .then(snapshot => {

                                            let total_paid_tickets = 0;
                                            let total_unpaid_tickets = 0;
                                            let total_revenue = 0;

                                            snapshot.forEach(doc => {

                                                let ticket = doc.data();
                                                let today_date = get_date(false);

                                                if (ticket["isPayed"]) {

                                                    if (ticket["dateIssued"] === today_date) {

                                                        today["issued"]++;
                                                        today["paid"]++;
                                                    }

                                                    ticket["penalties"].forEach(penalty => {

                                                        total_revenue += penalty["amount"];
                                                    });

                                                    total_paid_tickets++;

                                                } else {

                                                    if (ticket["dateIssued"] === today_date) {

                                                        today["issued"]++;
                                                        today["unpaid"]++;
                                                    }

                                                    total_unpaid_tickets++;
                                                }

                                                // yearly
                                                let month = ticket["dateIssued"].split("-")[1];
                                                let year = ticket["dateIssued"].split("-")[2];

                                                if (year === today_date.split("-")[2]) {

                                                    switch (month) {

                                                        case "01":
                                                        case "02":
                                                        case "03":
                                                        case "04":
                                                        case "05":
                                                        case "06":
                                                        case "07":
                                                        case "08":
                                                        case "09":
                                                        case "10":
                                                        case "11":
                                                        case "12":
                                                            ticket["isPayed"] ? yearly_data[month]["paid"]++ : yearly_data[month]["unpaid"]++;
                                                            yearly_data[month]["issued"]++;
                                                            break;
                                                    }
                                                }
                                            });

                                            data["total_paid_tickets"] = total_paid_tickets;
                                            data["total_unpaid_tickets"] = total_unpaid_tickets;
                                            data["total_revenue"] = total_revenue;
                                            data["today_tickets"] = [today["issued"], today["unpaid"], today["paid"]];

                                            data["tickets_timeline"] = {
                                                "issued": [
                                                    yearly_data["01"]["issued"],
                                                    yearly_data["02"]["issued"],
                                                    yearly_data["03"]["issued"],
                                                    yearly_data["04"]["issued"],
                                                    yearly_data["05"]["issued"],
                                                    yearly_data["06"]["issued"],
                                                    yearly_data["07"]["issued"],
                                                    yearly_data["08"]["issued"],
                                                    yearly_data["09"]["issued"],
                                                    yearly_data["10"]["issued"],
                                                    yearly_data["11"]["issued"],
                                                    yearly_data["12"]["issued"]
                                                ],
                                                "paid": [
                                                    yearly_data["01"]["paid"],
                                                    yearly_data["02"]["paid"],
                                                    yearly_data["03"]["paid"],
                                                    yearly_data["04"]["paid"],
                                                    yearly_data["05"]["paid"],
                                                    yearly_data["06"]["paid"],
                                                    yearly_data["07"]["paid"],
                                                    yearly_data["08"]["paid"],
                                                    yearly_data["09"]["paid"],
                                                    yearly_data["10"]["paid"],
                                                    yearly_data["11"]["paid"],
                                                    yearly_data["12"]["paid"]
                                                ],
                                                "unpaid": [
                                                    yearly_data["01"]["unpaid"],
                                                    yearly_data["02"]["unpaid"],
                                                    yearly_data["03"]["unpaid"],
                                                    yearly_data["04"]["unpaid"],
                                                    yearly_data["05"]["unpaid"],
                                                    yearly_data["06"]["unpaid"],
                                                    yearly_data["07"]["unpaid"],
                                                    yearly_data["08"]["unpaid"],
                                                    yearly_data["09"]["unpaid"],
                                                    yearly_data["10"]["unpaid"],
                                                    yearly_data["11"]["unpaid"],
                                                    yearly_data["12"]["unpaid"]
                                                ]
                                            };

                                            return {

                                                code: 200,
                                                data: data
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