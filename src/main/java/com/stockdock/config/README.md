## Config Package

### Purpose
This package contains configuration classes for the application. Configuration centralizes setup for reusable components, making the codebase more maintainable and scalable.

### Classes
- **RestClientConfig**: Configures the `RestClient` bean for making HTTP calls to external APIs.

### Notes
- The `@Configuration` annotation marks the class as a source of bean definitions.
- The `RestClient` bean can be injected into any service or controller that needs to make API calls.

### Example Usage
Inject `RestClient` into a service:
```java
@Service
public class ExampleService {
    private final RestClient restClient;

    public ExampleService(RestClient restClient) {
        this.restClient = restClient;
    }
}
```