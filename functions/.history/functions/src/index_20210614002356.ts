import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import {response} from "express";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript

admin.initializeApp();

// #region Constants
const FIELD_REGISTRATION_TOKENS = "registrationTokens";
const COLLECTION_USERS = "users";
const COLLECTION_FAVORITES = "favorites";
// const FIELD_ID = "id";
const TAG_ERROR = "Error===>";
const ERROR_CODE_1 = "messaging/invalid-registration-token";
const ERROR_CODE_2 = "messaging/registration-token-not-registered";
// #endregion

export const onFavoriteStockUpdate = functions
    .firestore.document(COLLECTION_FAVORITES + "/{favoriteID}")
    .onUpdate((change) => {
      // We are getting the changed document and we are using data method
      // to turn it into a JS object.
      const after = change.after.data();
      const previous = change.before.data();

      // AFTER variables
      const newProductID = after.productId;
      const newProductName = after.name;
      const newProductImg = after.imgUrl;
      const newUserId = after.userId;
      const newStock = after.stock;

      // PREVIOUS variables
      const oldStock = previous.stock;

      // Validation checks
      // If new stock is the same as previous one.
      if (newStock == oldStock) {
        return false;
      }

      // If previous stock wasn't zero that means we don't need to notify
      // the user since the product wasn't out of stock.
      if (oldStock != 0) {
        return false;
      }

      // If the new stock is lower than 1 (Changed to 0 most likely).
      if (newStock < 1) {
        return false;
      }

      const payload = {
        data: {
          PRODUCT_ID: newProductID,
          PRODUCT_NAME: newProductName,
          PRODUCT_IMG_URL: newProductImg,
        },
      };

      // Retrieve reg tokens of the user.
      return admin.firestore()
          .collection(COLLECTION_USERS)
          .doc(newUserId)
          .get()
          .then((results) => {
            const listRegTokens = results.get(FIELD_REGISTRATION_TOKENS);

            // Logging
            console.log("User Tokens:" + listRegTokens);

            return listRegTokens;
          })
          .then((resultTokens) => {
            admin.messaging()
                .sendToDevice(resultTokens, payload)
                .then((response) => {
                  const listStillRegTokens = resultTokens;

                  response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                      const failedRegToken = resultTokens[index];
                      // Error Logging
                      console.error(TAG_ERROR, failedRegToken, error);

                      if (error.code === ERROR_CODE_1 ||
                        error.code == ERROR_CODE_2) {
                        // Failed reg token index.
                        const failedIndex = listStillRegTokens
                            .indexOf(failedRegToken);

                        // If failed index exists.
                        if (failedIndex > -1) {
                          // If we find a failed token, we must remove it from
                          // the database.
                          listStillRegTokens
                              .splice(parseInt(failedRegToken), 1);
                        }
                      }
                    }
                  });

                  return listStillRegTokens;
                })
                .then((results) => {
                  // Update by removing the old reg tokens.
                  return admin.firestore()
                      .collection(COLLECTION_USERS)
                      .doc(newUserId)
                      .update({
                        registrationTokens: results,
                      });
                });
          })
          .then(() => {
            response.status(200)
                .send("Query operations completed successfully!");
          })
          .catch((error) => {
            // Error Logging
            console.error(TAG_ERROR, error);
          });
    });

