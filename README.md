# Introduction

This module exposes a single endpoint for searching flights withing the bundled flights.csv file:

```
    POST /farplace/flightsExists { 
        "origArp": "TLV",
        "destArp": "BER", 
        "date": "2022-04-01", 
        "flightNum: "1020"
    }
```

Changes in the bundled file are reflected dynamically via periodic reload mechanism.

Test from terminal by running 
```
curl -H "Content-Type: application/json" -d '{ "origArp": "TLV", "destArp": "BER", "date": "2022-04-01", "flightNum": "1021"}' localhost:8082/fareplace/flightExists
```