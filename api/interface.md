
# yutak

IM system

<!--- If we have only one group/collection, then no need for the "ungrouped" heading -->



## Endpoints

* [channel](#channel)
    1. [create](#1-create)
    1. [info](#2-info)
    1. [delete](#3-delete)
    1. [subscriber/add](#4-subscriberadd)
    1. [subscriber/del](#5-subscriberdel)
    1. [block/add](#6-blockadd)
    1. [block/set](#7-blockset)
    1. [block/remove](#8-blockremove)
    1. [white/add](#9-whiteadd)
    1. [white/set](#10-whiteset)
    1. [white/remove](#11-whiteremove)
    1. [white/list](#12-whitelist)
* [user](#user)
    1. [token](#1-token)
    1. [deviceQuit](#2-devicequit)
    1. [addSystemUid](#3-addsystemuid)
    1. [delSystemUid](#4-delsystemuid)
    1. [onlineStatus](#5-onlinestatus)
* [message](#message)
    1. [send](#1-send)
    1. [sync](#2-sync)
    1. [syncAck](#3-syncack)
    1. [stream/start](#4-streamstart)
    1. [stream/end](#5-streamend)
* [system](#system)
    1. [block/list](#1-blocklist)
    1. [block/remove](#2-blockremove)
    1. [block/add](#3-blockadd)
* [monitor](#monitor)
    1. [conn](#1-conn)
    1. [channel](#2-channel)
    1. [message](#3-message)

--------



## channel

Abstract channel design, including groups, individuals, communities, channels, data channels, and systems. etc



### 1. create



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/create
```



***Body:***

```js        
{
    "channelInfo" : {
        "channelId" : "1234",
        "channelType" : 2,
        "ban" : 0,
        "large" : 1,
        "disband" : 0
    },
    "subscribers" : [
        "1234",
        "1234"
    ]
}
```



### 2. info



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/info
```



***Body:***

```js        
{
        "channelId" : "1234",
        "channelType" : 2,
        "ban" : 0,
        "large" : 1,
        "disband" : 0
}
```



### 3. delete



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/delete
```



***Body:***

```js        
{
    "channelId" : "",
    "channelType" : 1
}
```



### 4. subscriber/add



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/subscriber/add
```



***Body:***

```js        
{
    "channelId" : "1234",
    "subscribers" : [
        "1234",
        "fhsb",
        "fga8syfar"
    ],
    "temp" : 1,
    "channelType" : 2,
    "reset" : 0
}
```



### 5. subscriber/del



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/subscriber/del
```



***Body:***

```js        
{
    "channelId" : "1234",
    "subscribers" : [
        "1233"
    ],
    "temp" : 1,
    "channelType" :1
}
```



### 6. block/add



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/block/add
```



***Body:***

```js        
{
    "channelId" : "1234",
    "channelType" : 2,
    "uids" : [
        "2131"
    ]
}
```



### 7. block/set



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/block/set
```



***Body:***

```js        
{
    "channelId" : "1234",
    "channelType" : 2,
    "uids" : [
        "what can i say"
    ]
}
```



### 8. block/remove



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/block/remove
```



***Body:***

```js        
{
    "channelId" : "1234",
    "channelType" : 2,
    "uids" : [
        "what can i say"
    ]
}
```



### 9. white/add



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/white/add
```



***Body:***

```js        
{
    "channelId" : "1234",
    "channelType" : 2,
    "uids" : [
        "21311",
        "aefwerf",
        "awdwed"
    ]
}
```



### 10. white/set



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/white/set
```



***Body:***

```js        
{
    "channelId" : "1234",
    "channelType" : 1,
    "uids" : [
        "2131"
    ]
}
```



### 11. white/remove



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/channel/white/remove
```



***Body:***

```js        
{
    "channelId" : "1234",
    "channelType" : 1,
    "uids" : [
        "2131"
    ]
}
```



### 12. white/list



***Endpoint:***

```bash
Method: GET
Type: 
URL: http://localhost:10001/channel/white/list
```



***Query params:***

| Key | Value | Description |
| --- | ------|-------------|
| channelId | 1234 |  |



## user

user part



### 1. token



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/user/token
```



***Body:***

```js        
{
    "uid" : "" ,
    "token" : "",
    "deviceFlag" : 1,
    "deviceLevel" : 0
}
```



### 2. deviceQuit



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/user/deviceQuit
```



***Body:***

```js        
{
    "uid" : "23",
    "deviceFlag" : 0
}
```



### 3. addSystemUid



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/user/addSystemUid
```



***Body:***

```js        
{
    "uids" : [
        "1234",
        "1233"
    ]
}
```



### 4. delSystemUid



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/user/delSystemUid
```



***Body:***

```js        
{
    "uids" : [
        "1234"
    ]
}
```



### 5. onlineStatus



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/user/onlineStatus
```



***Body:***

```js        
{
    "uids": [
        "123"
    ]
}
```



## message

message part



### 1. send



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/message/send
```



***Body:***

```js        
{
    "header":{
        "noPersist":0,
        "redDot":0,
        "syncOnce":0
    },
    "clientMsgNo":null,
    "streamNo":null,
    "fromUID":"23",
    "channelID":null,
    "channelType":0,
    "expire":0,
    "payload":"MTIzMTIfawergweasrgh",
    "subscribers":[
        "0",
        "1",
        "2"
    ]
}
```



### 2. sync



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/message/sync
```



***Body:***

```js        
{
    "uid" : "123",
    "messageSeq" : 123,
    "limit" : 1
}
```



### 3. syncAck



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/message/syncAck
```



***Body:***

```js        
{
    "uid" : "",
    "lastMessageSeq" : 12
}
```



### 4. stream/start



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/message/stream/start
```



***Body:***

```js        
{
    "header" : {
        "noPersist" : true,
        "redDot" : true,
        "syncOnce" : false
    },
    "clientMsgNo" : "12",
    "fromUID" : "13",
    "channelID" : "12",
    "channelType" : 0,
    "payload" : "23r2343"
}
```



### 5. stream/end



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/message/stream/end
```



***Body:***

```js        
{
    "channelId" : "123",
    "channelType" : 1,
    "streamNo" : "!@3"
}
```



## system

system part



### 1. block/list



***Endpoint:***

```bash
Method: GET
Type: 
URL: http://localhost:10001/system/block/list
```



### 2. block/remove



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/system/block/remove
```



***Body:***

```js        
{
    "ips" :[
        "127.0.0.3"
    ]
}
```



### 3. block/add



***Endpoint:***

```bash
Method: POST
Type: RAW
URL: http://localhost:10001/system/block/add
```



***Body:***

```js        
{
    "ips" : [
        "127.0.0.3",
        "127.0.0.2",
        "127.0.0.4",
        "127.0.0.6"
    ]
}
```



## monitor

monitor part



### 1. conn



***Endpoint:***

```bash
Method: GET
Type: 
URL: http://localhost:10001/monitor/conn
```



### 2. channel



***Endpoint:***

```bash
Method: GET
Type: 
URL: http://localhost:10001/monitor/channel
```



### 3. message



***Endpoint:***

```bash
Method: GET
Type: 
URL: 
```



---
[Back to top](#yutak)

>Generated at 2024-07-14 18:32:06 by [docgen](https://github.com/thedevsaddam/docgen)
