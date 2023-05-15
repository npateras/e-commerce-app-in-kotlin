import * as functions from "firebase-functions";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


export.sendNotificationFavoriteUpdated = 
functions.firestore.document('products/{uid}').onUpdate((event) => {
    let productId = event.after.id

    let title = event.after.get('title')
    let content = event.after.get('content')
    var message = {
        notification: {
            title: title,
            body: content,
        },
    };

    let response = await admin.messaging()
});
