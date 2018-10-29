
var exec = require('cordova/exec');

module.exports.sendValue = function (arg0, success, error) {
    exec(success, error, 'NativeConnector', 'sendValue', [arg0]);
};

module.exports.openKeycardPort = function (arg, success, error ) {
    exec(success, error, 'NativeConnector', 'openKeycardPort', [arg]);
};

module.exports.closeKeycardPort = function ( success, error) {
    exec(success, error, 'NativeConnector', 'closeKeycardPort', []);
};

module.exports.openGPIOPort = function ( success, error) {
    exec(success, error, 'NativeConnector', 'openGPIOPort', []);
};

module.exports.closeGPIOPort = function ( success, error) {
    exec(success, error, 'NativeConnector', 'closeGPIOPort', []);
};

module.exports.getPortList = function ( success, error) {
    exec(success, error, 'NativeConnector', 'getPortList', []);
};
