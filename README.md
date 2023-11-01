# Introduction

This module exposes a single endpoint for searching flights withing the bundled flights.csv file:

```
    POST /farplace/flightsExists { 
        "origArp": "TLV",
        "destArp": "BER", 
        "date": "2022-04-01", 
        "flightNum: "1"
    }
```

Changes in the bundled file are reflected dynamically via periodic reload mechanism.