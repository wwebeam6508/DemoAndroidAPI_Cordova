cordova.define("cordova-plugin-nativeconnector.NativeConnector", function(require, exports, module) {
var exec = require('cordova/exec');

module.exports.sendValue = function (arg0, success, error) {
    exec(success, error, 'NativeConnector', 'sendValue', [arg0]);
};

module.exports.openKeycardPort = function ( success, error) {
    exec(success, error, 'NativeConnector', 'openKeycardPort', []);
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

});
