
const util = require('util');
const getRequest = require("./getRequest.js");
const getAuthor = require("./getAuthor.js");
const getBook = require("./getBook.js")

let type = 'title', searchtxt = 'song of ice and fire', limit = 5;
function dummy_function(x) { console.log(util.inspect(x, false, null, true /* enable colors */)) }

getBook(type, searchtxt, limit)
    .then(results => dummy_function(results))