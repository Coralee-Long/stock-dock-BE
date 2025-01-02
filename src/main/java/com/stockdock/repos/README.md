### **Database Design Plan for StockDock**

---

### **1. MongoDB Collections**

#### **1.1. `current_stocks`**
- **Purpose**: Store the latest stock data for quick access.
- **Schema**:
  ```json
  {
      "symbol": "AAPL",          // Unique identifier for the stock
      "currency": "USD",         // Currency of the prices
      "latestQuote": {           // Latest quote fetched from Alpaca
          "ap": 150.6,           // Ask Price
          "as": 100,             // Ask Size
          "ax": "XNAS",          // Ask Exchange
          "bp": 150.4,           // Bid Price
          "bs": 120,             // Bid Size
          "bx": "XNAS",          // Bid Exchange
          "c": ["T"],            // Conditions
          "t": "2025-01-02T15:00:00Z", // Timestamp
          "z": "C"               // Tape
      }
  }
  ```
- **Operations**:
    - Overwrite `latestQuote` with new data for the stock upon every fetch.
    - Provides data for dashboards and quick views.

---

#### **1.2. `historical_stock_data`**
- **Purpose**: Track the price history of stocks for trend analysis and graphing.
- **Schema**:
  ```json
  {
      "symbol": "AAPL",          // Unique identifier for the stock
      "currency": "USD",         // Currency of the prices
      "quotes": [                // Array of historical quotes
          {
              "ap": 150.6,       // Ask Price
              "as": 100,         // Ask Size
              "ax": "XNAS",      // Ask Exchange
              "bp": 150.4,       // Bid Price
              "bs": 120,         // Bid Size
              "bx": "XNAS",      // Bid Exchange
              "c": ["T"],        // Conditions
              "t": "2025-01-02T15:00:00Z", // Timestamp
              "z": "C"           // Tape
          },
          {
              "ap": 149.8,       // Ask Price
              "as": 90,          // Ask Size
              "ax": "XNAS",
              "bp": 149.6,
              "bs": 110,
              "bx": "XNAS",
              "c": ["T"],
              "t": "2025-01-01T15:00:00Z",
              "z": "C"
          }
      ]
  }
  ```
- **Operations**:
    - Append new `StockQuote` objects to the `quotes` array.
    - Ensure no duplicates by checking the `t` (timestamp) field.

---

### **2. Workflow**
1. **Fetch Data**:
    - Call Alpaca API’s `getAllQuotes` to retrieve a `StockQuotesResponse`.

2. **Update `current_stocks`**:
    - For each stock, overwrite the `latestQuote` field with the new `StockQuote`.

3. **Update `historical_stock_data`**:
    - Append the new `StockQuote` to the `quotes` array for each stock.
    - Check for duplicate timestamps before appending.

---

### **3. Advantages**
- **Separation of Concerns**:
    - `current_stocks`: Optimized for real-time dashboard data.
    - `historical_stock_data`: Designed for analytics and graphing.

- **Scalability**:
    - Historical data can be expanded to support minute/hourly/daily aggregations.

- **Simplicity**:
    - Clear distinction between current and historical data.

---
