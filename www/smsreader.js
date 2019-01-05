function fetchSms(action, filter_array, since) {
    let _filter_array = [];
    let _since = 0;
    if (filter_array && filter_array.length) {
        _filter_array = filter_array;
    }
    if (since) {
        _since = +(new Date(since));
    }
    return new Promise((resolve, reject) => {
        cordova.exec(resolve, reject, "SMSReader", action, [_since, _filter_array]);
    });
}

module.exports = {
    getAllSMS: function (since) {
        return fetchSms("all", [], since);
    },
    filterSenders: function (senderids, since) {
        return fetchSms("filtersenders", senderids, since);
    },
    filterBody: function (searchtexts, since) {
        return fetchSms("filterbody", searchtexts, since);
    }
};
