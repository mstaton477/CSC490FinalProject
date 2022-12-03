// Imports
const express = require('express');
const session = require('express-session');
const mysql = require('mysql');
const bcrypt = require('bcrypt');
const path = require('path');
const LocalStrategy = require('passport-local');
const passport = require('passport');

//javascript files import 

const getBook = require('./pages/script/getBook');
const getAuthor = require('./pages/script/getAuthor');
const db = require('./database');
const { result } = require('lodash');


const store = new session.MemoryStore();
const app = express();



app.set('view engine', 'ejs');
app.engine('.html', require('ejs').renderFile);
app.set('views', path.join(__dirname, "/views"));




//getting homepage
app.use(express.static(__dirname + '/pages'));

app.use(session({
    key: "cats",
    secret: "cats",
    resave: false,
    saveUninitialized: false
}));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' + '/views/home.html')
})

//getting signup page and inserting new users into the database, then redirect to login 
//bycrypt is what we are using the hash the password to make it more secure
app.post('/views/signin', async (req, res) => {
    inputData = {
        Username: req.body.Username,
        Name: req.body.Name,
        Email: req.body.Email,
        Password: await bcrypt.hash(req.body.Password, 10)
    }

    var sql = 'SELECT * FROM user WHERE Username =?';
    await db.query(sql, [inputData.Username], async (err, data) => {
        if (err) throw err;
        if (data.length != 0) {
            console.log(inputData.Username + " already exists");
            return res.send(`${Username} already exists`);
        } else {
            var sql = 'INSERT INTO user SET ?';
            await db.query(sql, inputData, (err, data) => {
                if (err) throw (err)
                console.log("You have sucessfully Registered your account");
                console.log(data.insertId);
                return res.redirect('./login.html');
            })

        }
    })
});

//grabbing user details from the database to log people in 
// then will redirect to the user specific dashboard 
app.post('/login', async (req, res) => {
    const Username = req.body.username;
    const Password = req.body.password;



    const sqlSearch = "SELECT * FROM user where Username = ?;";
    const search_query = mysql.format(sqlSearch, [Username]);


    await db.query(search_query, async (err, result) => {
        if (err) throw err;
        if (result.length == 0) {
            console.log("User doesn't exsit");
            res.sendStatus(404);
        } else {
            const hashedPassword = result[0].Password;
            req.session.loggedinUser = true;
            req.session.Username = Username;
            if (await bcrypt.compare(Password, hashedPassword)) {
                console.log('Login Successful');
                // res.send(`${Username} is logged in `);   
            } else {
                console.log("Password Incorrect");
                res.send("Password Incorrect ");
            }
            res.redirect('./dashboard');

        }
    })
})
//user specific dashboard 
//will hold the users book lists, clubs, link to book search
app.get('/dashboard', async function (req, res) {
    if (req.session.loggedinUser) {
        const Username = req.session.Username;
        const booklistSQL = 'SELECT * FROM `book list` WHERE Username = ? ';
        const bookList_query = mysql.format(booklistSQL, [Username]);

        function getBookLists() {
            return new Promise((reslove, reject) =>{
                try{
                    db.query(bookList_query, function (err, bookListResults){
                        if (err) return reject(err); 
                        return reslove(bookListResults); 
                    }); 
                }catch(e){
                    reject(e); 
                }
            })
            }

        let bookListResults = await getBookLists();
        
        const clublistSQL = 'SELECT * FROM `clubs` WHERE Usernames = ? ';
        const clubList_query = mysql.format(clublistSQL, [Username]);

        function getclubLists() {
            return new Promise((reslove, reject) =>{
                try{
                    db.query(clubList_query, function (err, clubListResults){
                        if (err) return reject(err); 
                        return reslove(clubListResults); 
                    }); 
                }catch(e){
                    reject(e); 
                }
            })
            }

        let clubListResults = await getclubLists();
        
        res.render("../pages/views/dashboard.ejs", {
            Username: Username,
            bookListResults: bookListResults, 
            clubListResults: clubListResults
        })

    } else {
        res.redirect('./login');
    }


});

// log out function 
app.get('/logout', function (req, res) {
    req.session.destroy();
    res.redirect('/login');
});


//search page 
app.post('/search', async function (req, res) {
    searchtxt = req.body.Answer;
    console.log(req.body.Answer);
    if (req.body.titlesearch) {

        var results = await getBook('title', searchtxt, 20);
        var searchChoice = req.body.titlesearch;

        res.render("../pages/views/search-results.ejs",
            {
                searchChoice: searchChoice,
                data: results
            })
    }

    if (req.body.authorsearch) {

        var results = await getAuthor('name', searchtxt, 20);
        var searchChoice = req.body.authorsearch;

        console.log(results);

        res.render("../pages/views/search-results.ejs",
            {
                searchChoice: searchChoice,
                data: results
            })
    }

    if (req.body.isbnsearch) {

        var results = await getBook('isbn', searchtxt,);
        var searchChoice = req.body.isbnsearch;

        console.log(results);

        res.render("../pages/views/search-results.ejs",
            {
                searchChoice: searchChoice,
                data: results
            })

    }


}
)

app.get('/views/clublist', async function(req, res){
    const clublistSQL = 'SELECT * FROM `clubs` ';
    const clubList_query = mysql.format(clublistSQL);

        // var clubListResults = await db.query(clubList_query, function(err, clubListResults){
        //     if (err) throw err; 
        //     return (clubListResults); 
        // }); 
            
        // res.render("../pages/views/clublist.ejs", {
        //     clubListResults: clubListResults
        // })

        await db.query(clubList_query, function(err, clubListResults){
            if (err) throw err; 
            res.render("../pages/views/clublist.ejs", {
                clubListResults: clubListResults
            })
        })
})

app.listen(process.env.PORT || 8080, function () {
    console.log('Node app is running on port 8080');
});

