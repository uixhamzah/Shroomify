const functions = require("firebase-functions");
const admin = require("firebase-admin");
const express = require("express");
const cors = require("cors");
const app = express();
// admin.initializeApp();
//Set Service Account to enable using Firebase features 
var serviceAccount = require("./permissions.json");
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://molten-muse-352811-default-rtdb.asia-southeast1.firebasedatabase.app"
});
const db = admin.firestore();
app.use( cors( {origin:true}));




//Routes
app.get('/hello',(req, res) => {
    return res.status(200).send('Hello World!');
});


//Create
app.post('/api/create', async(req, res) => {
    try {
        await db.collection('mushrooms').doc('/' + req.body.id + '/')
        .create({
            name: req.body.name,
            description: req.body.description
        })

        return res.status(200).send();

    } catch (error) {
        console.log(error);
        return res.status(500).send(error);
    }
});


//Read
//Read specific mushrooms
app.get('/api/read/:id', async(req, res) => {
    try {
        const document = db.collection('mushrooms').doc(req.params.id);
        let product = await document.get();
        let response = product.data();

        return res.status(200).send(response);

    } catch (error) {
        console.log(error);
        return res.status(500).send(error);
    }
});

//Read all mushrooms
app.get('/api/read', async(req, res) => {
    try {
        let query = db.collection('mushrooms');
        let response = [];

        await query.get().then(querySnapshot => {
            let docs = querySnapshot.docs; //query result

            for (let doc of docs)
            {
                const selectedItem = {
                    id : doc.id,
                    name: doc.data().name,
                    description: doc.data().description
                };
                response.push(selectedItem);
            }

            return response; //each then should return a value
        })
        return res.status(200).send(response);

    } catch (error) {
        console.log(error);
        return res.status(500).send(error);
    }
});


//Update
app.put('/api/update/:id', async(req, res) => {
    try {
        const document = db.collection('mushrooms').doc(req.params.id);

        await document.update({
            name: req.body.name,
            description: req.body.description
        })

        return res.status(200).send();

    } catch (error) {
        console.log(error);
        return res.status(500).send(error);
    }
});


//Delete
app.delete('/api/delete/:id', async(req, res) => {
    try {
        const document = db.collection('mushrooms').doc(req.params.id);
        await document.delete();

        return res.status(200).send();

    } catch (error) {
        console.log(error);
        return res.status(500).send(error);
    }
});


//Export API to Firebase Cloud Functions
exports.app = functions.https.onRequest(app);












// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
