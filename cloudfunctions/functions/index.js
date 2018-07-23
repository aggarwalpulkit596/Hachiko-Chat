/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

// [START all]
// [START import]
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendRequest = functions.database.ref('/notifications/{user_id}/{notification_id}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const user_id = context.params.user_id;
  		const notification_id = context.params.notification_id;
      console.log('We have a notification from :', notification_id);

      /*
   * 'fromUser' query retreives the ID of the user who sent the notification
   */

  const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('You have new notification from  : ', from_user_id);

    /*
     * The we run two queries at a time using Firebase 'Promise'.
     * One to get the name of the user who sent the notification
     * another one to get the devicetoken to the device we want to send notification to
     */

    const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
    const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

    return Promise.all([userQuery, deviceToken]).then(result => {

      const userName = result[0].val();
      const token_id = result[1].val();

      /*
       * We are creating a 'payload' to create a notification to be sent.
       */

      const payload = {
        notification: {
          title : "New Friend Request",
          body: `${userName} has sent you request`,
          icon: "default",
          click_action : "com.example.pulkit.chatapp1_TARGET_NOTIFICATION"
        },
        data : {
          from_user_id : from_user_id,
          userName : userName

        }
      };

      /*
       * Then using admin.messaging() we are sending the payload notification to the token_id of
       * the device we retreived.
       */

      return admin.messaging().sendToDevice(token_id, payload).then(response => {

        console.log('This was the notification Feature');

        return null;

      });

    });

  });

    });

    exports.sendMessage = functions.database.ref('/messages/{user_id}/{friend_id}/{message_id}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const user_id = context.params.user_id;
  		const message_id = context.params.message_id;
  		const friend_id = context.params.friend_id;
  		const messageData = snapshot.val();

      console.log('We have a notification from :', friend_id);

      /*
   * 'fromUser' query retreives the ID of the user who sent the notification
   */

  const fromUser = admin.database().ref(`/messages/${user_id}/${friend_id}/${message_id}`).once('value');

  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('You have new notification from  : ', from_user_id);

    /*
     * The we run two queries at a time using Firebase 'Promise'.
     * One to get the name of the user who sent the notification
     * another one to get the devicetoken to the device we want to send notification to
     */

    const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
    const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

    return Promise.all([userQuery, deviceToken]).then(result => {

      const userName = result[0].val();
      const token_id = result[1].val();

      /*
       * We are creating a 'payload' to create a notification to be sent.
       */
       if(messageData.from !== user_id){
      const payload = {
        notification: {
          title : "New Message",
          body: `${userName} has sent you a message`,
          icon: "default",
          click_action : "com.example.pulkit.chatapp1_TARGET_NOTIFICATION1"
        },
        data : {
          from_user_id : from_user_id,
          userName : userName
        }
      };
            return admin.messaging().sendToDevice(token_id, payload).then(response => {

        console.log('This was the notification Feature');

        return null;

      });

  }else
  return null;

      /*
       * Then using admin.messaging() we are sending the payload notification to the token_id of
       * the device we retreived.
       */



    });

  });

    });


  exports.removeUserFromDatabase = functions.auth.user()
    .onDelete((user)=> {
  // Get the uid of the deleted user.
    var uid = user.uid;

  console.log('This was the notification Feature',uid);
  // Remove the user from your Realtime Database's /users node.
  return admin.database().ref("Users/" + uid).remove();
});

function listAllUsers(nextPageToken) {
  // List batch of users, 10 at a time.
  admin.auth().listUsers(10, nextPageToken)
    .then(function(listUsersResult) {
      listUsersResult.users.forEach(function(userRecord) {
          
        //console.log("user", userRecord.toJSON());
        admin.auth().deleteUser(userRecord.uid)
            .then(function() {
                console.log("Successfully deleted user");
                  return null;
            })
            .catch(function(error) {
                console.log("Error deleting user:", error);
            });

      });
      if (listUsersResult.pageToken) {
        // List next batch of users.
          //Wait because timeout
          setTimeout(listAllUsers(listUsersResult.pageToken), 2000);
                  
      }
      return null;
    })
    .catch(function(error) {
      console.log("Error listing users:", error);
    });
}

exports.clean = functions.https.onRequest((req, res) => {
    listAllUsers();
});
// [END import]