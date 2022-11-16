
async function getRequest(_f, _timeout, ..._args) {
    await new Promise(resolve => setTimeout(resolve, _timeout));
    return _f(..._args);
}

module.exports = getRequest;