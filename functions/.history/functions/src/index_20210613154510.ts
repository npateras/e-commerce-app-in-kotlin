import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import { response } from "express";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript

admin.initializeApp();

// Constants
const FIELD_REGISTRATION_TOKENS = "registrationTokens"
const COLLECTION_USERS = "users"
const COLLECTION_FAVORITES = "favorites"
const FIELD_ID = "id"

export const onFavoriteStockUpdate = functions
    .firestore.document(COLLECTION_FAVORITES + "/{favoriteID}")
    .onUpdate((change) => {
      // We are getting the changed document and we are using data method
      // to turn it into a JS object.
      const after = change.after.data();
      const previous = change.before.data();

      // AFTER variables
      const newProductID = after.id;
      const newProductName = after.name;
      const newProductImg = after.imgUrl;
      const newUserId = after.userId;
      const newStock = after.stock;

      // PREVIOUS variables
      const oldStock = previous.stock;

      // Initialize empty array for tokens
      const listRegTokens:string[] = [];

      // const [usersSnapshot] = await Promise.all([usersRef.get()]);

      // Validation checks
      // If new stock is the same as previous one.
      if (newStock == oldStock) {
        return;
      }

      // If previous stock wasn't zero that means we don't need to notify
      // the user since the product wasn't out of stock.
      if (oldStock != 0) {
        return;
      }

      // If the new stock is lower than 1 (Changed to 0 most likely).
      if (newStock < 1) {
        return;
      }

      const payload = {
        notification: {
          title: "Product Stock Update!",
          body: newProductName + " is now available in stock!",
          image: newProductImg,
          clickAction: String("ProductDetailsActivity"),
        },
        data: {
          PRODUCT_ID: newProductID,
          PRODUCT_NAME: newProductName,
          PRODUCT_IMAGE: newProductImg,
        },
      };

      admin.firestore()
          .collection(COLLECTION_USERS)
          .where(FIELD_ID, "==", newUserId)
          .get()
          .then((results) => {
            results.forEach((doc) => {
              listRegTokens.push(doc.get(FIELD_REGISTRATION_TOKENS));
              // Logging
              console.log("User Tokens:" + doc.get(FIELD_REGISTRATION_TOKENS));
            });
            return listRegTokens;
          })
          .then(() => {
            return admin.messaging()
            .sendToDevice(listRegTokens, payload)
            .then((response) => {
              const listStillRegTokens = listRegTokens;

              response.results.forEach((result, index) => {
                const error = result.error;
                if (error) {
                  const failedRegToken = listRegTokens[index];
                  // Error Logging
                  console.error("Error===>", failedRegToken, error);

                  if (error.code === "messaging/invalid-registration-token" ||
                        error.code ==
                        "messaging/registration-token-not-registered") {
                    // Failed reg token index.
                    const failedIndex =
                    listStillRegTokens.indexOf(failedRegToken);
                    // If failed index exists.
                    if (failedIndex > -1) {
                      // If we find a failed token, we must remove it from the
                      // database.
                      listStillRegTokens.splice(parseInt(failedRegToken), 1);
                    }
                  }
                }
              });
            })
            .then(() => {
              // Update by removing the old reg tokens.
              return admin.firestore()
              .collection(COLLECTION_USERS)
              .doc(newUserId)
              .update({
                listRegTokens: listStillRegTokens,
              });
            });
          })
          .then(() => {
            response.status(200).send("Query operations completed successfully!")
          })
          .catch((error) => {
            // Error Logging
            console.error("Error===>", error);
          });
    });

