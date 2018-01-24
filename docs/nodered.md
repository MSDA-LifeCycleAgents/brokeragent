```json
[
    {
        "id": "8de2e9aa.868438",
        "type": "tcp in",
        "z": "2da70128.34aa7e",
        "name": "JadeListener",
        "server": "server",
        "host": "localhost",
        "port": "1738",
        "datamode": "single",
        "datatype": "utf8",
        "newline": ":",
        "topic": "",
        "base64": false,
        "x": 214.00001525878906,
        "y": 579.2381591796875,
        "wires": [
            [
                "504e96a2.a9f8a8"
            ]
        ]
    },
    {
        "id": "504e96a2.a9f8a8",
        "type": "function",
        "z": "2da70128.34aa7e",
        "name": "CommunicationMessageSplitter",
        "func": "function getDateTime() {\n\n    var date = new Date();\n\n    var hour = date.getHours();\n    hour = (hour < 10 ? \"0\" : \"\") + hour;\n\n    var min  = date.getMinutes();\n    min = (min < 10 ? \"0\" : \"\") + min;\n\n    var sec  = date.getSeconds();\n    sec = (sec < 10 ? \"0\" : \"\") + sec;\n\n    var year = date.getFullYear();\n\n    var month = date.getMonth() + 1;\n    month = (month < 10 ? \"0\" : \"\") + month;\n\n    var day  = date.getDate();\n    day = (day < 10 ? \"0\" : \"\") + day;\n\n    return day + \"/\" + month + \"/\" + year + \"-\" + hour + \":\" + min + \":\" + sec;\n\n}\n\n\nvar findSender =  msg.payload.match(/:sender.+/);\nvar findReceiver = msg.payload.match(/:receiver.+/);\nvar findContent = msg.payload.match(/:content.+/);\n\nvar splitSender = findSender[0].split(/:sender/);\nvar splitReceiver = findReceiver[0].split(/:receiver/);\nvar splitContent = findContent[0].split(/:content/);\n\nvar sender = splitSender[1].match(/agent-identifier\\s+:name\\s+[^\\s]+/g);\nvar receiver = splitReceiver[1].match(/agent-identifier\\s+:name\\s+[^\\s]+/g);\n\nvar content = splitContent[1];\n\nvar senderSplit = sender[0].split(/:name\\s+/);\nvar receiverSplit = receiver[0].split(/:name\\s+/);\n\nmsg.payload = {\"time\":getDateTime(), \"sender\":senderSplit[1],\"receiver\": receiverSplit[1], \"content\": content};\n\nreturn msg;",
        "outputs": "1",
        "noerr": 0,
        "x": 592.4286117553711,
        "y": 607.2381286621094,
        "wires": [
            [
                "36afb639.98ea6a",
                "965e02e6.60993"
            ]
        ]
    },
    {
        "id": "5f12da2e.c27464",
        "type": "inject",
        "z": "2da70128.34aa7e",
        "name": "",
        "topic": "",
        "payload": "<?xml version=\"1.0\"?> <envelope>     <params index=\"1\">         <intended-receiver><agent-identifier><name> ams@192.168.0.151:1600/JADE</name><addresses></addresses></agent-identifier></intended-receiver>         <to><agent-identifier><name> ams@192.168.0.151:1600/JADE at 1600</name><addresses></addresses></agent-identifier></to>         <from><agent-identifier><name>node-red</name><addresses><url>http://127.0.0.1:1880/</url></addresses></agent-identifier></from>         <acl-representation>fipa.acl.rep.string.std</acl-representation>         <payload-encoding>US-ASCII</payload-encoding>         <payload-length>611</payload-length>         <date>20071202T002816000</date>     </params> </envelope> (request :sender(agent-identifier :name node-red :addresses(sequence tcp://127.0.0.1:9032)) :receiver (set (agent-identifier :name ams@192.168.0.151:1600/JADE :addresses (sequence http://192.168.0.151:7779/acc tcp://192.168.0.151:6789))) :content \"((action       (agent-identifier         :name ams@192.168.0.151:1600/JADE         :addresses           (sequence http://192.168.0.151:7778/acc tcp://192.168.0.151:6789))       (search (ams-agent-description)         (search-constraints           :max-results -1))))\" :language fipa-sl0 :ontology fipa-agent-management :protocol fipa-request :conversation-id 17)",
        "payloadType": "str",
        "repeat": "60",
        "crontab": "",
        "once": true,
        "x": 302.8571363176618,
        "y": 162.95240020751953,
        "wires": [
            [
                "a85473dd.70bee"
            ]
        ]
    },
    {
        "id": "dd39056b.e68ac8",
        "type": "ui_template",
        "z": "2da70128.34aa7e",
        "group": "7b0fe93a.352658",
        "name": "TableWithCommunication",
        "order": 2,
        "width": "20",
        "height": "6",
        "format": "<style>\n#wrap {\n    max-width: 300px;\n    white-space: nowrap; \n    width: 12em; \n    overflow: ellipsis;\n    text-overflow: clipped; \n}\n</style>\n<table style=\"width:100%\">\n  <tr>\n    <th>Time</th>\n    <th>Sender</th>\n    <th>Receiver</th> \n    <th>Content</th>\n  </tr>\n   <tr ng-repeat=\"obj in msg.payload\">\n    <td>{{ obj.time }}</td>\n    <td>{{ obj.sender }}</td>\n    <td>{{ obj.receiver }}</td>\n    <td id=\"wrap\">{{ obj.content }}</td>\n  </tr>\n</table>",
        "storeOutMessages": true,
        "fwdInMessages": true,
        "templateScope": "local",
        "x": 1239.5716857910156,
        "y": 573.0954055786133,
        "wires": [
            []
        ]
    },
    {
        "id": "1d442c9e.bf61e3",
        "type": "tcp in",
        "z": "2da70128.34aa7e",
        "name": "",
        "server": "server",
        "host": "",
        "port": "9032",
        "datamode": "stream",
        "datatype": "utf8",
        "newline": "",
        "topic": "",
        "base64": false,
        "x": 303,
        "y": 282.2381258010864,
        "wires": [
            [
                "f3fabfe.4d7844"
            ]
        ]
    },
    {
        "id": "b1c1ec7e.3c778",
        "type": "debug",
        "z": "2da70128.34aa7e",
        "name": "Status",
        "active": false,
        "console": "true",
        "complete": "payload",
        "x": 895.5833206176758,
        "y": 349.15479850769043,
        "wires": []
    },
    {
        "id": "a85473dd.70bee",
        "type": "tcp out",
        "z": "2da70128.34aa7e",
        "host": "192.168.0.151",
        "port": "6789",
        "beserver": "client",
        "base64": false,
        "end": false,
        "name": "",
        "x": 706.3333282470703,
        "y": 160.57146549224854,
        "wires": []
    },
    {
        "id": "f3fabfe.4d7844",
        "type": "function",
        "z": "2da70128.34aa7e",
        "name": "StatusMessageSplitter",
        "func": "var splitString = msg.payload.split('<content>');\nvar splitAgain = splitString[1].split('-1');\n\nvar findAgent = splitAgain[1].match(/agent-identifier\\s+:name\\s+[^\\s]+/g);\nvar findState = splitAgain[1].match(/:state\\s+\\w+/g);\nvar arrayLength = findAgent.length;\nvar newArray = [];\nfor (var i = 0; i < arrayLength; i++) {\n   var splitName = findAgent[i].split(/:name\\s+/);\n   var splitState = findState[i].split(/:state\\s+/);\n    newArray[i] = {\"name\":splitName[1],\"state\": splitState[1]};\n}\nmsg.payload = newArray;\n\nreturn msg;",
        "outputs": "1",
        "noerr": 0,
        "x": 557.75,
        "y": 282.5000419616699,
        "wires": [
            [
                "b1c1ec7e.3c778",
                "e700877c.288a98"
            ]
        ]
    },
    {
        "id": "e700877c.288a98",
        "type": "ui_template",
        "z": "2da70128.34aa7e",
        "group": "d9f4ff5a.fec57",
        "name": "TableWithStatus",
        "order": 1,
        "width": "8",
        "height": "4",
        "format": "<table>\n  <tr>\n    <th>Name</th>\n    <th>State</th> \n  </tr>\n  <tr ng-repeat=\"obj in msg.payload\">\n    <td>{{ obj.name }}</td>\n    <td>{{ obj.state }}</td>\n  </tr>\n</table>",
        "storeOutMessages": true,
        "fwdInMessages": true,
        "templateScope": "local",
        "x": 953.8945274353027,
        "y": 283.7578582763672,
        "wires": [
            []
        ]
    },
    {
        "id": "20477d4a.a3e902",
        "type": "debug",
        "z": "2da70128.34aa7e",
        "name": "Communication",
        "active": true,
        "console": "true",
        "complete": "payload",
        "x": 938.0195770263672,
        "y": 451.0039119720459,
        "wires": []
    },
    {
        "id": "965e02e6.60993",
        "type": "file",
        "z": "2da70128.34aa7e",
        "name": "WriteToFile",
        "filename": "C:\\\\Users\\\\Hans\\\\Desktop\\\\jadeCommunication.json",
        "appendNewline": true,
        "createDir": false,
        "overwriteFile": "false",
        "x": 685.3945388793945,
        "y": 904.8281383514404,
        "wires": []
    },
    {
        "id": "b11862cb.fc3f6",
        "type": "file in",
        "z": "2da70128.34aa7e",
        "name": "ReadFromFile",
        "filename": "C:\\\\Users\\\\Hans\\\\Desktop\\\\jadeCommunication.json",
        "format": "lines",
        "chunk": false,
        "sendError": false,
        "x": 959.8907928466797,
        "y": 745.8478336334229,
        "wires": [
            [
                "f815fde5.00dfd"
            ]
        ]
    },
    {
        "id": "36afb639.98ea6a",
        "type": "delay",
        "z": "2da70128.34aa7e",
        "name": "",
        "pauseType": "delay",
        "timeout": "1",
        "timeoutUnits": "seconds",
        "rate": "1",
        "nbRateUnits": "1",
        "rateUnits": "second",
        "randomFirst": "1",
        "randomLast": "5",
        "randomUnits": "seconds",
        "drop": false,
        "x": 783.898494720459,
        "y": 745.9454154968262,
        "wires": [
            [
                "b11862cb.fc3f6"
            ]
        ]
    },
    {
        "id": "d43c19aa.79b2b8",
        "type": "join",
        "z": "2da70128.34aa7e",
        "name": "",
        "mode": "custom",
        "build": "merged",
        "property": "payload",
        "propertyType": "msg",
        "key": "topic",
        "joiner": "\\n",
        "joinerType": "str",
        "accumulate": false,
        "timeout": "",
        "count": "",
        "x": 1287.8984375,
        "y": 798.7734336853027,
        "wires": [
            [
                "51379bfe.388e94"
            ]
        ]
    },
    {
        "id": "f815fde5.00dfd",
        "type": "split",
        "z": "2da70128.34aa7e",
        "name": "",
        "splt": "\\n",
        "spltType": "str",
        "arraySplt": 1,
        "arraySpltType": "len",
        "stream": false,
        "addname": "",
        "x": 903.8945274353027,
        "y": 906.792965888977,
        "wires": [
            [
                "2c4a8a7a.bac906"
            ]
        ]
    },
    {
        "id": "2c4a8a7a.bac906",
        "type": "json",
        "z": "2da70128.34aa7e",
        "name": "",
        "pretty": true,
        "x": 1106.88671875,
        "y": 875.7031230926514,
        "wires": [
            [
                "d43c19aa.79b2b8"
            ]
        ]
    },
    {
        "id": "51379bfe.388e94",
        "type": "join",
        "z": "2da70128.34aa7e",
        "name": "",
        "mode": "custom",
        "build": "array",
        "property": "payload",
        "propertyType": "msg",
        "key": "topic",
        "joiner": "\\n",
        "joinerType": "str",
        "accumulate": false,
        "timeout": "",
        "count": "",
        "x": 971.8984375,
        "y": 576.7695407867432,
        "wires": [
            [
                "dd39056b.e68ac8"
            ]
        ]
    },
    {
        "id": "7b0fe93a.352658",
        "type": "ui_group",
        "z": "",
        "name": "Communications",
        "tab": "894b23d1.d8a9f",
        "order": 2,
        "disp": true,
        "width": "20"
    },
    {
        "id": "d9f4ff5a.fec57",
        "type": "ui_group",
        "z": "",
        "name": "Status",
        "tab": "894b23d1.d8a9f",
        "disp": true,
        "width": "8"
    },
    {
        "id": "894b23d1.d8a9f",
        "type": "ui_tab",
        "z": "",
        "name": "AgentDashboard",
        "icon": "dashboard"
    }
]
```
