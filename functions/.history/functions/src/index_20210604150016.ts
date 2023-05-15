import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript

admin.initializeApp();

export const onProductStockUpdate = 
functions.firestore.document('Products/{productId}')
.onUpdate((change, context) => {
  const after = change.after.data() // We are getting the changed document and we are using data method to turn it into a JS object.
  const previous = change.before.data();

  let user = firebase.currentUser;
  uid = user.uid;

  const isInUserFavorites = await getUserData('{userId}', after.id);

  const payload = {
    data: {
      name: String(after.name),
      imgUrl: String(after.iconUrl)
    }
  }

  if (after.stock == previous.stock) {
    return
  }

  if (previous.stock != 0) { 
    return
  }

  // If the new stock is lower than 1.
  if (after.stock < 1) {
    return
  }

  if (isInUserFavorites == true) {
    let response = await admin.messaging().sendToTopic("test", payload)
    console.log(response)
  }
})


export const sendNotificationFavoriteUpdated = 
functions.firestore.document('products/{uid}')
.onCreate(async (snapshot, context) => {
    try {
      console.log('Starting sendNotification Function');
      const doc = snapshot.data();
      console.log(doc.content);

      const nickname = await getUserData(, doc.id);
      // Do something with the nickname value
      return true;
    } catch (error) {
      // ...
    }
  });

async function getUserData(userId: string, productId: string) {
  try {
    const snapshot = await admin.firestore()
        .collection('favorites')
        .where("userId", "==", userId)
        .where("productId", "==", productId)
        .get();

    if (snapshot.exists) {
       const userData = snapshot.data();
       return userData.nickname;
    } else {
      //Throw an error
    }
  } catch (error) {
    // I would suggest you throw an error
    console.log('Error getting User Information:', error);
    return `NOT FOUND: ${error}`;
  }
}