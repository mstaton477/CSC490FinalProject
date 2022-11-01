// Imports
const express = require('express');
const cookieParser = require("cookie-parser");
const sessions = require('express-session');
const http = require('http');
const mysql = require('mysql');
var bodyParser = require('body-parser');
const { eachDayOfInterval } = require('date-fns');
const app = express();

// let encodeURL = parseUrl.urlencoded({extended: false});

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use(sessions({
    secret: "thisismysecrctekey",
    saveUninitialized:true,
    cookie: { maxAge: 1000 * 60 * 60 * 24 }, // 24 hours
    resave: false
}));

app.use(cookieParser());

const db = mysql.createConnection({
    host: "us-cdbr-east-06.cleardb.net", 
    user: "bab87ea7d060c5", 
    password: "c593381b", 
    database: "heroku_209a0a2d6441663"
});

db.connect((err) => {
    if(err) {throw err;}
    console.log("DB connection OK")
});


app.use(express.static(__dirname + '/pages'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})

app.get('/signin', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/signin.html')
})


// app.post('/views/signin', (req, res) => {
//     const Username = req.body.Username; 
//     const Name = req.body.Name; 
//     const Email = req.body.Email; 
//     const Password = req.body.Password;


//     const sql = `INSERT INTO user (Username, Name, Email, Password) VALUES ('${Username}', '${Name}', '${Email}', '${Password}')`;
//     db.query(sql, function(err, result) {
//         if(err) throw err; 
//         console.log('record inserted');
//         res.redirect('success.html'); 
//     })
// })

app.post('/views/signin', function(req, res, next) {
    inputData = {
        Username: req.body.Username,
        Name: req.body.Name, 
        Email: req.body.Email,
        Password: req.body.Password
    }

    var sql = 'SELECT * FROM user WHERE Email =?';
    db.query(sql, [inputData.Email], function(err, data, fields){
        if(err) throw err
        if(data.length > 1){
            var msg = inputData.Email + " already exists"; 
        }else {
            var sql = 'INSERT INTO user SET ?';
            db.query(sql, inputData, function(err, data) {
                if(err) throw err; 
            });
            var msg = "You have sucessfully Registered your account"; 
        }
    
    })
});

app.listen(3000, function () {
    console.log('Node app is running on port 3000');
});

