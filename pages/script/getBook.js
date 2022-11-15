
// DO NOT CALL THIS FUNCTION: call getBook() instead
async function getBookHelper(_type, _value, _limit) {
    const api_url_base = 'http://www.openlibrary.org'
    var request_url
    let escaped_value = encodeURIComponent(_value).replaceAll('%20','+').replaceAll('%2F','/');
    limit_exists = _limit !== null && _limit > 1;
    if(limit_exists) escaped_limit = encodeURIComponent(_limit).replaceAll('%20','+')

    switch(_type){
        case 'key': request_url = api_url_base + escaped_value + '.json'; break;
        case 'title': request_url = api_url_base + '/search.json?title=' + escaped_value + (limit_exists ? '&limit=' + escaped_limit : ''); break;
        case 'isbn': request_url = api_url_base + '/isbn/' + escaped_value + '.json'; break;
        default: return []
    }
   
    // Make API call
    const response = await fetch(request_url);
    console.log(request_url)
 
    // to JSON 
    const data = await response.json();

    // redirect if necessary
    if(typeof data.type !== 'undefined' && data.type.key === '/type/redirect'){
        return getBook('key', data.results.location)
    }
 
 // TODO finish this part
 switch(_type){

    case 'isbn':
    temp = [{
        'key': data.key,
        'title': data.title,
        'authors': data.authors
    }];
    console.log(temp);
    console.log(data.authors);
    return temp;


    case 'title':

    var temp;
    var temp_list = [];
    const docs = data.docs;

    for(let i = 0; i < docs.length; i++){
        let key = docs[i].key;
        if(typeof key === 'undefined') continue;

        var title = null;
        var author_keys = null;
        var author_names = null;

        try{
            title = docs[i].title
            author_keys = docs[i].author_key
            author_names = docs[i].author_name
        }catch(error){
          console.error(error);
        } finally{

            temp_list.push({
                'key': key,
                'title': title,
                'author_keys': author_keys,
                'author_names': author_names
            })
        }
    }
    console.log(temp_list)
    return temp_list;

    default:
        console.log('invalid _type: ' + _type)
        return []
 }
}

/*
_type should be 'key', 'title', or 'isbn'
_value holds that specific value
_limit is optional

An example call: getBook('title', 'the lord of the rings', 10)
*/
async function getBook(_type, _value, _limit){
    return { "books": getBookHelper(_type, _value, _limit) }
}

export {
     getBook 
}; 