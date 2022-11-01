// Imports
const express = require('express');
const cookieParser = require("cookie-parser");
const morgan = require('morgan'); 
const sessions = require('express-session');
const http = require('http');
const mysql = require('mysql');
var bodyParser = require('body-parser');
const { eachDayOfInterval } = require('date-fns');
const { response, query } = require('express');
const { co } = require('co');
const app = express();


app.use(sessions({
    secret: 'secret',
    saveUninitialized:true,
    resave: true 
}));

app.use(express.json());
app.use(express.urlencoded({extended:true}));

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

//getting homepage
app.use(express.static(__dirname + '/pages'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})


app.post('/views/signin', function(req, res, next) {
    inputData = {
        Username: req.body.Username,
        Name: req.body.Name, 
        Email: req.body.Email,
        Password: req.body.Password
    }

    var sql = 'SELECT * FROM user WHERE Username =?';
    db.query(sql, [inputData.Username], function(err, data, fields){
        if(err) throw err;
        if(data.length > 1){
            console.log(inputData.Username + " already exists"); 
        }else {
            var sql = 'INSERT INTO user SET ?';
            db.query(sql, inputData, function(err, data) {
                if(err) throw err; 
            });
            console.log("You have sucessfully Registered your account"); 
        }
    })
});

app.post('/login', function(req,res){
    var Username = req.body.usernameField; 
    var Password = req.body.passwordField; 

    var sql = 'SELECT * FROM user WHERE Username =? AND Password =?';
    db.query(sql, [Username, Password], function(err, data, fields){
        if(err) throw err; 
        if(data.length > 0){
            req.session.logginedUser = true; 
            req.session.Username = Username; 
            res.redirect('/dashboard');
        }else{
            res.render('login-form', {alertMsg:"Your Username or password is wrong"});
        }
    })
})

app.get('/dashboard', function(req, res, next) {
    if(req.session.logginedUser){
        res.render('dashboard', {Username:req.session.Username})
    }else{
        res.redirect('/login');
    }
});

app.get('/logout', function(req, res){
    req.session.destroy(); 
    res.redirect('/login');
});

app.listen(3000, function () {
    console.log('Node app is running on port 3000');
});

