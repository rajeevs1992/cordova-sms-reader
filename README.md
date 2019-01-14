# SMS reader for Cordova applications

A simple to use, minimal frills SMS reader for cordova applications.

To keep things simple, only inbox is searched.

## Installing

From cordova plugin registry

    cordova plugin add cordova-sms-reader

From plugin repository

    cordova plugin add https://github.com/rajeevs1992/cordova-sms-reader.git

## Supported platforms

- Android

## API Reference

- [smsreader](#smsreader)
  - [.getAllSMS(since)](#getallsms)
  - [.filterSenders(senderids, since)](#filtersenders)
  - [.filterBody(searchtexts, since)](#filterbody)
  - [.filterBodyOrSenders(searchtexts, senderids, since)](#filterBodyOrSenders)
  - [.SMS](#sms) : `Object`
  

## smsreader

<a id="smsreader"></a>

### getAllSMS(since)

<a id="getallsms"></a>
Fetch all SMS since a specified date. The `since` parameter is optional.

```js
smsreader.getAllSMS()
    .then((sms)=>{
        // Fetches all SMS.
    },
    (err)=>{
        console.error(err);
    });

smsreader.getAllSMS('2019-01-01')
    .then((sms)=>{
        // Fetches all SMS received after 2019-01-01
    },
    (err)=>{
        console.error(err);
    });

smsreader.getAllSMS(new Date(2019, 0, 1))
    .then((sms)=>{
        // Fetches all SMS received after 2019-01-01
    },
    (err)=>{
        console.error(err);
    });
```

### filterSenders(senderids, since)

<a id="filtersenders"></a>
Fetch all SMS since date, from a list of sender addresses. The `since` parameter is optional.

> The senderid is case sensitive.

```js
smsreader.filterSenders(['123456','01402368'])
    .then((sms)=>{
        // Fetches all SMS from 123456 OR 01402368.
    },
    (err)=>{
        console.error(err);
    });

smsreader.filterSenders(['123456','01402368'], '2019-01-01')
    .then((sms)=>{
        // Fetches all SMS recieved from 123456 OR 01402368 AND receieved after 2019-01-01.
    },
    (err)=>{
        console.error(err);
    });
```

### filterBody(searchtexts, since)

<a id="filterbody"></a>
Fetch all SMS since date, filtered by search texts. SMS is returned if **ANY** of the search string is present in the body. The `since` parameter is optional.

> The search text is case insensitive.

```js
smsreader.filterBody(['hello','alice'])
    .then((sms)=>{
        // Fetches all SMS, with body containing words 'hello' OR 'alice'.
    },
    (err)=>{
        console.error(err);
    });

smsreader.filterBody(['hello','alice'], '2019-01-01')
    .then((sms)=>{
        // Fetches all SMS, with body containing words 'hello' OR 'alice' AND receieved after 2019-01-01.
    },
    (err)=>{
        console.error(err);
    });
```


### filterBodyOrSenders(searchtexts, since)

<a id="filterBodyOrSenders"></a>
Fetch all SMS since date, filtered by search texts or sender ids. SMS is returned if **ANY** of the search string is present in the body **OR** is from any of the senderids. The `since` parameter is optional.

> The search text is case insensitive.

```js
smsreader.filterBodyOrSenders(['hello','alice'], ['12456','147589'])
    .then((sms)=>{
        // Fetches all SMS, with body containing words ('hello' OR 'alice') OR (address in ['12456','147589'])
    },
    (err)=>{
        console.error(err);
    });

smsreader.filterBodyOrSenders(['hello','alice'], ['12456','147589'], '2019-01-01')
    .then((sms)=>{
        // Fetches all SMS, with body containing words ('hello' OR 'alice') OR (address in ['12456','147589']) AND receieved after 2019-01-01.
    },
    (err)=>{
        console.error(err);
    });
```

### SMS

<a id="sms"></a>
The API functions return a `Promise` resolving to an array of `SMS` objects, which have the following properties.

- `id` : `number`, A unique id for the SMS, assigned by the android messenger.
- `address` : `string`, The sender address.
- `body` : `string`, The SMS body.
- `date` : `number`, A timestamp of the received date.
- `read` : `boolean`, A flag denoting if SMS is read or not.

#### Sample response

```json
[
    {
        "id" : 3,
        "address" : "123456",
        "body" : "Hello world",
        "date" : 1546681106290,
        "read" : true
    },
    {
        "id" : 4,
        "address" : "87956",
        "body" : "Hello SMS",
        "date" : 1546681106292,
        "read" : false
    }
]
```
