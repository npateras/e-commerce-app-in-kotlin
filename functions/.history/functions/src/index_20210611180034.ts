import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import { response } from "express";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript

admin.initializeApp();

export const onProductStockUpdate = functions
    .firestore.document("Products/{productId}")
    .onUpdate((change, context) => {
      // We are getting the changed document and we are using data method
      // to turn it into a JS object.
      const after = change.after.data();
      const previous = change.before.data();
    
      const productID = after.id;
      const productName = after.name;
      const productImg = after.iconUrl;
      const productPrice = after.price;


      // const isInUserFavorites = await getUserData('test', after.id);

      const payload = {
        notification: {
          title: "Product Stock Update!",
          body: productName + " is now available in stock!",
          image: productImg,
          clickAction: String("ProductDetailsActivity"),
        },
        data: {
          PRODUCT_ID: productID,
          PRODUCT_NAME: productName,
          PRODUCT_IMAGE: productImg,
          PRODUCT_PRICE: productPrice,
        },
      };

      if (after.stock == previous.stock) {
        return;
      }

      if (previous.stock != 0) {
        return;
      }

      // If the new stock is lower than 1.
      if (after.stock < 1) {
        return;
      }

      return admin.messaging().sendToDevice("test", payload).then(response => {
          const stillRegisteredTokens = registrationTokens

          response.results.forEach((result, index) => {
              const error = result.error;
              if (error)
          })
      })
    });

// async function getUserData(userId: string, productId: string) {
//   try {
//     const snapshot = await admin.firestore()
//         .collection('favorites')
//         .where("userId", "==", userId)
//         .where("productId", "==", productId)
//         .get();

//     if (snapshot != null) {
//        const userData = snapshot.data()
//        return userData.nickname
//     } else {
//       //Throw an error
//     }
//   } catch (error) {
//     // I would suggest you throw an error
//     console.log('Error getting User Information:', error)
//     return `NOT FOUND: ${error}`
//   }
// }
