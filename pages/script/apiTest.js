
const util = require('util');
const getRequest = require("./getRequest.js");
const getAuthor = require("./getAuthor.js");
const getBook = require("./getBook.js")

function dummy_function(x, y) { 
	
	if(true){
		if(x == 1) list_of_books.push(y); else list_of_authors.push(y);

		if(list_of_books.length + list_of_authors.length == num_of_requests){
			console.log('\n################################################################################\nBook Requests:')
			list_of_books.forEach(z => {
				console.log('--------------------------------------------------------------------------------')
				console.log(util.inspect(z, false, null, true /* enable colors */ )); 
			});
			console.log('\n################################################################################\nAuthor Requests:')
			list_of_authors.forEach(z => {
				console.log('--------------------------------------------------------------------------------')
				console.log(util.inspect(z, false, null, true /* enable colors */ )); 
			});
		}
	}
}

let limit = 5, num_of_requests = 0;
let list_of_books = [], list_of_authors = [];

if(true){
	getBook('key', '/works/OL17489309W')			.then( (results) => { dummy_function(1,	results) } );
	getBook('title', 'the lord of the rings', limit).then( (results) => { dummy_function(1, results) } );
	getBook('isbn', '9780439700900', limit)			.then( (results) => { dummy_function(1, results) } );
	num_of_requests += 3;
}

if(true){
	getAuthor('name', 'Robert Jordan', limit)		.then( (results) => { dummy_function(2,	results) } );
	getAuthor('key', '/authors/OL18319A', limit)	.then( (results) => { dummy_function(2, results) } );
	num_of_requests += 2;
}
