// Imports
const express = require('express');
const cookieParser = require("cookie-parser");
const morgan = require('morgan'); 
const sessions = require('express-session');
const http = require('http');
const mysql = require('mysql');
var bodyParser = require('body-parser');
const { eachDayOfInterval } = require('date-fns');
const { response } = require('express');
const app = express();


app.use(sessions({
    secret: 'secret',
    saveUninitialized:true,
    cookie: { maxAge: 1000 * 60 * 60 * 24 }, // 24 hours
    resave: false
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


app.use(express.static(__dirname + '/pages'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})

app.get('/signin', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/signin.html')
})


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

app.get('/login', function(req, res){
    res.sendFile(__dirname + '/pages' +'/views/login.html')
})

app.post('/login', function(req, res){
    let Username = req.body.Username; 
    let Password = req.body.Password; 

    if(Username && Password){
        db.query('SELECT * FROM user WHERE Username =? AND Password =?', [Username, Password], function(err, results, fields){
            if(err){throw err} 
            if(results.length > 0){
                req.session.loggedin = true; 
                req.session.Username = Username; 
                res.redirect('views/dashboard.html');
            }else{
                res.send("Incorrect Username or Password");
            }
            res.end(); 
        });
    }else{
        res.send('Please Enter Username and Password');
        res.end(); 
    }
});

app.get('/dashboard', function(req, res, next){
    if(req.session.loggedinUser){
        res.render('dashboard',{Username: req.session.Username})
    }else{
        res.sendFile(__dirname + '/pages' +'/views/login.html')
    }
});

app.get('/logout', function(req, res){
    req.session.destroy(); 
    res.redirect('/login');
});



app.listen(3000, function () {
    console.log('Node app is running on port 3000');
});

