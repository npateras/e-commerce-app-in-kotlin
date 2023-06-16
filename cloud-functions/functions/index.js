const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.productStockUpdate = functions.firestore.document('Products/{product}').onUpdate(async(snap, context) => {
    console.log('Push notification event triggered');

    // We are getting the changed document and we are using data method
    // to turn it into a JS object.
    const newProduct = snap.after.data();
    const oldProduct = snap.before.data();

    const pID = newProduct.id;
    const pName = newProduct.name;
    const pImg = newProduct.iconUrl;

    // Validation checks
    // If new stock is the same as previous one.
    if (newProduct.stock == oldProduct.stock) {
      return false;
    }

    // If previous stock wasn't zero that means we don't need to notify
    // the user since the product wasn't out of stock.
    if (oldProduct.stock != 0) {
      return false;
    }

    // If the new stock is lower than 1 (Changed to 0 most likely).
    if (newProduct.stock < 1) {
      return false;
    }

    const payload = {
        notification: {
            title: `${pName}: Stock Updated!`,
            body: `${pName} is now available in stock 😍`
        },
        data: {
            title: `${pName}: Stock Updated!`,
            message: `${pName} is now available in stock 😍`,
            product_id: pID,
            image: String(pImg)
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    await admin.messaging().sendToTopic(pID, payload, options);
});