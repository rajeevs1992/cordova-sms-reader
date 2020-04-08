function ensurePermission(permissions) {
    return new Promise((resolve, reject) => {
        cordova.exec(resolve, reject, "SMSReader", "permission", [permissions]);
    });
}

function fetchSms(action, foldertype, since, searchtexts, senderids, permissions) {
    let _foldertype = '';
    let _senderids = [];
    let _searchtexts = [];
    let _since = 0;
    if (foldertype && foldertype.length) {
        _foldertype = foldertype
    }
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
                cordova.exec(resolve, reject, "SMSReader", action, [_foldertype, _since, _searchtexts, _senderids]);
            });
        }, (err) => {
            return Promise.reject(err);
        });
}
function fetchSmsInbox(action, since, searchtexts, senderids, permissions) {
    return fetchSms(action, 'inbox', since, searchtexts, senderids, permissions);
}

module.exports = {
    /**
     * @deprecated Replaced by getSmsInbox
     */
    getAllSMS: function (since) {
        return this.getSmsInbox(since);
    },
    filterSenders: function (senderids, since) {
        return fetchSmsInbox("filtersenders", since, null, senderids, ['read']);
    },
    filterBody: function (searchtexts, since) {
        return fetchSmsInbox("filterbody", since, searchtexts, null, ['read']);
    },
    filterBodyOrSenders: function (searchtexts, senderids, since) {
        return fetchSmsInbox("filterbodyorsenders", since, searchtexts, senderids, ['read']);
    },
    getSmsFromFolder: function (folderType, since) {
        return fetchSms("all", folderType, since, null, null, ['read']);
    },
    getSmsAll: function(since) {
        return this.getSmsFromFolder('', since);
    },
    getSmsInbox: function(since) {
        return this.getSmsFromFolder('inbox', since);
    },
    getSmsSent: function (since) {
        return this.getSmsFromFolder('sent', since);
    }
};
