var exec = require('cordova/exec');

module.exports.sendValue = function (arg0, success, error) {
    exec(success, error, 'NativeConnector', 'sendValue', [arg0]);
};