import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript

admin.initializeApp();

export const onProductStockUpdate = functions
    .firestore.document("favorites/{favoriteID}")
    .onUpdate(async (change) => {
      // We are getting the changed document and we are using data method
      // to turn it into a JS object.
      const after = change.after.data();
      const previous = change.before.data();

      const productID = after.id;
      const productName = after.name;
      const productImg = after.imgUrl;
      const userId = after.userId;

      const listRegTokens = [];

      // const [usersSnapshot] = await Promise.all([usersRef.get()]);

      // Validation checks
      // If new stock is the same as previous one.
      if (after.stock == previous.stock) {
        return;
      }

      // If previous stock wasn't zero that means we don't need to notify
      // the user since the product wasn't out of stock.
      if (previous.stock != 0) {
        return;
      }

      // If the new stock is lower than 1 (Changed to 0 most likely).
      if (after.stock < 1) {
        return;
      }

      // admin.firestore().collection("favorites")
      // .where("branch.productId", "==", productID).get()
      //     .then((querySnapshot) => {
      //         const users = [] 
      //         querySnapshot.forEach((doc) => { 
      //           users.push(doc.get("userId"))
      //         });

      //         users.forEach()
      //         return admin.firestore().collection('student_public').where('branch.id', '==', documentId).get();
      //     })
      //     .then((querySnapshot) => {
      //       querySnapshot.forEach((doc) => { 
      //           batch.update(doc.ref, {branch: {id: documentId, name: after.name}}); 
      //       });
      //       return batch.commit() 
      //    })
      //    .catch(err => { console.log('error===>', err); });

      // const isInUserFavorites = await getUserData('test', after.id);

      const usersRef = await admin.firestore()
          .collection("users")
          .where("id", "==", userId)
          .get()
            .then((querySnapshot) => {
              querySnapshot.forEach((doc) => { 
                listRegTokens.push(doc.get("registrationTokens"))
              });
              console.log(listRegTokens);
              return listRegTokens;
            })
        .catch(err => { console.log("error===>", err); });

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
        },
      };

      return admin.messaging().sendToDevice(listRegTokens, payload).then(response => {
          const listStillRegTokens = listRegTokens

          response.results.forEach((result, index) => {
              const error = result.error;
              if (error) {
                  const failedRegToken = listRegTokens[index];
                  console.log("error===>", failedRegToken, error);
                  if (error.code === "messaging/invalid-registration-token" 
                      || error.code == "messaging/registration-token-not-registered") {
                          const failedIndex = listStillRegTokens.indexOf(failedRegToken)
                          if (failedRegToken > -1) {
                              listStillRegTokens.splice(failedRegToken, 1)
                          }
                      }
              }
          })
          return admin.firestore().doc("users/" + userId).update({
              listRegTokens: listStillRegTokens
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
