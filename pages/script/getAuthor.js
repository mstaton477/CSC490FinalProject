// LEAVE COMMENTED WHEN DONE
var base_path = './'

if(typeof base_path === 'undefined') base_path = './pages/script/'
const getRequest = require(base_path + "getRequest.js")

async function getAuthor(type_, value_, limit_, timeout_){
    if (timeout_ === null) timeout_ = 10000;
    data = await getRequest(bookHelper,  __timeout, __type, __value, __limit);
    return { "authors": data};
}