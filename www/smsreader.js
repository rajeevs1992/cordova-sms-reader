function ensurePermission(permissions) {
    return new Promise((resolve, reject) => {
        cordova.exec(resolve, reject, "SMSReader", "permission", [permissions]);
    });
}

function fetchSms(action, since, searchtexts, senderids, permissions) {
    let _senderids = [];
    let _searchtexts = [];
    let _since = 0;
    if (senderids && senderids.length) {
        _senderids = senderids;
    }
    if (searchtexts && searchtexts.length) {
        _searchtexts = searchtexts;
    }
    if (since) {
        _since = +(new Date(since));
    }
    return ensurePermission(permissions)
        .then((success) => {
            return new Promise((resolve, reject) => {
                cordova.exec(resolve, reject, "SMSReader", action, [_since, _searchtexts, _senderids]);
            });
        }, (err) => {
            return Promise.reject(err);
        });
}

module.exports = {
    getAllSMS: function (since) {
        return fetchSms("all", since, null, null, ['read']);
    },
    filterSenders: function (senderids, since) {
        return fetchSms("filtersenders", since, null, senderids, ['read']);
    },
    filterBody: function (searchtexts, since) {
        return fetchSms("filterbody", since, searchtexts, null, ['read']);
    },
    filterBodyOrSenders: function (searchtexts, senderids, since) {
        return fetchSms("filterbodyorsenders", since, searchtexts, senderids, ['read']);
    }
};
