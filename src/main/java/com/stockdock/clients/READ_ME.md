### **Client in Spring Boot: Purpose and Role**

A **client** in your backend is a dedicated class that manages communication with an external service, such as Alpaca's API. It centralizes all API interaction logic, ensuring clean and maintainable code.

---

### **What a Client Does**
1. **Sends Requests**: Builds and sends HTTP requests (e.g., GET, POST) to the external service.
2. **Handles Authentication**: Adds API keys or tokens to requests for secure communication.
3. **Receives Responses**: Processes the data returned from the external API.
4. **Encapsulates Logic**: Keeps API-related code in one place for better reusability and separation of concerns.

> Equivalent to using libraries like `axios` or `fetch` in JavaScript for modular API communication.

---

### **Why Set Up an Alpaca Client?**
The Alpaca client will:
- Fetch data (e.g., account info, stock quotes) from Alpaca.
- Centralize API logic, making it reusable across your backend.
- Serve as the foundation for all Alpaca-related interactions.

---

### **High-Level Backend Structure**
1. **Controller**:
    - Exposes endpoints for the frontend (e.g., `/api/stocks/quotes/{symbol}`).
    - Delegates work to services or clients.

   ```java
   @RestController
   @RequestMapping("/api/stocks")
   public class StockController {

       private final AlpacaClient alpacaClient;

       public StockController(AlpacaClient alpacaClient) {
           this.alpacaClient = alpacaClient;
       }

       @GetMapping("/quotes/{symbol}")
       public StockQuotesResponse getQuote(@PathVariable String symbol) {
           return alpacaClient.getQuote(symbol);
       }
   }
   ```

2. **Service**:
    - Contains business logic (e.g., deciding which stocks to fetch or which rules to apply).

3. **Client**:
    - Handles outbound API communication (e.g., sending requests to Alpaca).

   ```java
   @Service
   public class AlpacaClient {

       private final RestClient restClient;
       private final String apiKey;
       private final String apiSecret;
       private final String baseUrl;

       public AlpacaClient(
           @Value("${alpaca.api.key}") String apiKey,
           @Value("${alpaca.api.secret}") String apiSecret,
           @Value("${alpaca.api.base.url}") String baseUrl
       ) {
           this.restClient = RestClient.builder().build();
           this.apiKey = apiKey;
           this.apiSecret = apiSecret;
           this.baseUrl = baseUrl;
       }

       public StockQuotesResponse getQuote(String symbol) {
           URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
               .path("/v2/stocks/{symbol}/quotes")
               .buildAndExpand(symbol)
               .toUri();

           return restClient.get()
               .uri(uri)
               .headers(headers -> {
                   headers.set("APCA-API-KEY-ID", apiKey);
                   headers.set("APCA-API-SECRET-KEY", apiSecret);
               })
               .retrieve()
               .body(StockQuotesResponse.class);
       }
   }
   ```

---

### **Understanding RestClient**
1. **Builder Pattern**:
    - Use `RestClient.builder()` to configure and build your HTTP client.
    - This is the modern alternative to `RestTemplate` in Spring Boot.

2. **Key Elements**:
    - **Base URL**: Set once to avoid repetition (`https://paper-api.alpaca.markets`).
    - **Headers**: Add API keys to every request.
    - **Requests**: Build and send HTTP requests (e.g., GET, POST).

---

### **`getAccountDetails()` Endpoint**
This method interacts with Alpaca's `/v2/account` endpoint, commonly used to:
1. **Test Connectivity**: Ensure API keys and communication are correctly set up.
2. **Fetch Account Information**: Retrieve details like cash balance, account status, and buying power for dashboard display.

---

### **Summary Table: Controller vs. Client**

| **Controller**                    | **Client**                          |
|-----------------------------------|-------------------------------------|
| Handles **incoming HTTP requests**. | Handles **outbound HTTP requests**. |
| Exposes endpoints for the frontend. | Communicates with external APIs.    |
| Returns data to the frontend.      | Fetches data from APIs for the backend. |
| Part of the **presentation layer**. | Part of the **service layer**.       |

---

### **When to Add `getAccountDetails()`**
1. **Useful for a Dashboard**: Displays account details like cash balance and portfolio value.
2. **Optional for Stock Trading**: You can skip it if your app focuses only on fetching stock data or executing trades.

---
