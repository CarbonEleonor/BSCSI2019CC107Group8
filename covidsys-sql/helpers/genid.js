module.exports.generateID = () => {
    return "Cy" + require('crypto').randomBytes(7).toString('hex');
}
module.exports.generateCode = () => {
    return Math.floor(100000 + Math.random() * 900000).toString();
}