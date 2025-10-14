# Spring Modulith Event-Driven Application

A demonstration of Spring Modulith framework implementing event-driven architecture with reliable event processing and resume capabilities.

## 📋 Overview

This project showcases a modular e-commerce system built with Spring Modulith, featuring:

- **Modular Architecture**: Clean separation of concerns with inventory and order modules
- **Event-Driven Communication**: Asynchronous communication between modules via Spring events
- **Event Resume Capabilities**: Reliable event processing with automatic retry and resume on application restart
- **Database Persistence**: PostgreSQL for production, H2 for testing
- **Message Broker**: RabbitMQ for external event publishing
- **Comprehensive Testing**: Unit tests, integration tests, and modulith structure validation

## 🏗️ Architecture

### Modules

1. **Order Module** (`com.kamruddin.modulith.order`)
   - Order entity and repository
   - Order service with business logic
   - REST API for order management
   - Publishes `OrderPlacedEvent` when orders are created

2. **Inventory Module** (`com.kamruddin.modulith.inventory`)
   - Product entity and repository
   - Product service for inventory management
   - REST API for product operations
   - Listens to `OrderPlacedEvent` to update stock levels

3. **Core Module** (`com.kamruddin.modulith`)
   - Main application class
   - Event publication service for managing incomplete publications
   - Shared configuration and utilities

### Event Flow

```
Order Creation → OrderPlacedEvent → Inventory Update
     ↓              ↓                      ↓
OrderService → EventPublication → InventoryEventListener
```

## 🚀 Features

### Event-Driven Architecture
- Asynchronous communication between modules
- Loose coupling between order and inventory domains
- Event externalization to RabbitMQ for cross-service communication
- Configurable event exchange via Spring Modulith's `@Externalized` annotation

### Event Resume Capabilities
- **Event Publication Tracking**: All events are tracked in database with completion status
- **Incomplete Publication Handling**: Failed events are automatically retried
- **Resume on Restart**: Outstanding events are republished when application restarts
- **Transactional Safety**: Event listeners use transactions with proper rollback handling

### Data Validation
- Bean validation with Jakarta Validation API
- Custom validation constraints for business rules
- Input sanitization and error handling

### Database Support
- PostgreSQL for production environment
- H2 in-memory database for testing
- Flyway-style schema initialization
- Sample data loading

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Architecture**: Spring Modulith 1.4.1
- **Database**: PostgreSQL (production), H2 (testing)
- **Message Broker**: RabbitMQ 4.1.4
- **Build Tool**: Maven
- **Container**: Docker & Docker Compose
- **Testing**: JUnit 5, Spring Boot Test, ArchUnit

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (for full setup)

## 🚀 Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd spring-modulith-poc/modulith
mvn clean compile
```

### 2. Start Infrastructure

```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432
- RabbitMQ on port 5672 (with management UI on 15672)

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

### 4. Run Tests

```bash
mvn test
```

## 📚 API Endpoints

### Products

```http
GET    /api/products      # List all products
GET    /api/products/{id} # Get product by ID
POST   /api/products      # Create new product
PUT    /api/products/{id} # Update product
DELETE /api/products/{id} # Delete product
```

### Orders

```http
GET    /api/orders        # List all orders
GET    /api/orders/{id}   # Get order by ID
POST   /api/orders        # Create new order
PUT    /api/orders/{id}   # Update order
DELETE /api/orders/{id}   # Delete order
```

### Sample API Usage

Create a product:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Mouse",
    "description": "High-precision gaming mouse",
    "price": 79.99,
    "stockQuantity": 25
  }'
```

Create an order:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

## 🗄️ Database Schema

### Tables

- **products**: Product catalog with pricing and stock information
- **orders**: Customer orders with references to products
- **event_publication**: Spring Modulith event publication tracking (auto-created)

### Sample Data

The application loads sample products on startup:
- Laptop ($999.99, 50 units)
- Mouse ($25.99, 100 units)
- Keyboard ($79.99, 75 units)
- Monitor ($349.99, 30 units)

## ⚙️ Configuration

### Application Properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase?timezone=UTC
spring.datasource.username=myuser
spring.datasource.password=secret

# Spring Modulith Events
spring.modulith.events.jdbc.schema-initialization.enabled=true
spring.modulith.events.externalization.enabled=true
spring.modulith.events.republish-outstanding-events-on-restart=true

# AMQP Configuration for RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=myuser
spring.rabbitmq.password=secret
spring.rabbitmq.virtual-host=/
```

### Environment Variables

For Docker Compose setup:
- `POSTGRES_DB=mydatabase`
- `POSTGRES_USER=myuser`
- `POSTGRES_PASSWORD=secret`
- `RABBITMQ_DEFAULT_USER=myuser`
- `RABBITMQ_DEFAULT_PASS=secret`

## 🧪 Testing

### Test Categories

1. **Unit Tests**: Service layer testing with mocked dependencies
2. **Integration Tests**: Full application context testing
3. **Modulith Structure Tests**: Architecture validation using ArchUnit

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=EventPublicationServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Configuration

- Uses H2 in-memory database for fast test execution
- Event publication schema is automatically created for tests
- RabbitMQ test containers for integration testing

## 🔄 Event Resume Functionality

### How It Works

1. **Event Publication**: When an order is placed, an `OrderPlacedEvent` is published
2. **Tracking**: Spring Modulith tracks the publication in the `event_publication` table
3. **RabbitMQ Externalization**: Events are externalized to RabbitMQ using the configured exchange
4. **Processing**: The inventory listener processes the event and updates stock
5. **Completion**: Successful processing marks the publication as completed
6. **Failure Handling**: If processing fails, the publication remains incomplete
7. **Resume**: On application restart, incomplete publications are automatically retried

### Manual Event Management

The `EventPublicationService` provides programmatic control:

```java
@Autowired
private EventPublicationService eventService;

// Resubmit all incomplete publications
eventService.resubmitIncompletePublications();

// Resubmit only failed publications
eventService.resubmitFailedPublications();

// Resubmit publications older than 1 hour
eventService.resubmitIncompletePublicationsOlderThan(Duration.ofHours(1));
```

## � RabbitMQ Integration

### Configuration

The application uses a dedicated configuration class to set up RabbitMQ for event externalization:

```java
@Configuration
public class RabbitMQConfig {
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Exchange exchange() {
        // Create a Topic exchange with the application name
        return new TopicExchange(applicationName, true, false);
    }
    
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
```

This creates a durable Topic exchange named `modulith` (from application name) to which all externalized events are published.

### Event Externalization

Events are marked for externalization using the `@Externalized` annotation:

```java
@Externalized
public class OrderPlacedEvent implements Serializable {
    // Event properties
}
```

## �📊 Monitoring

### H2 Console (Development)

Access the H2 database console at: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: *(empty)*

### RabbitMQ Management

Access RabbitMQ management UI at: `http://localhost:15672`

- **Username**: `myuser`
- **Password**: `secret`

Key RabbitMQ components to monitor:
- **Exchanges**: Look for the `modulith` exchange created automatically
- **Queues**: Monitor message processing and consumption
- **Connections**: Verify application connectivity

## 🐳 Docker Support

### Development Setup

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Build

```bash
# Build JAR
mvn clean package -DskipTests

# Build Docker image
mvn spring-boot:build-image

# Run with Docker
docker run --network host spring-modulith-app:latest
```

## 📚 Additional Resources

- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/3.5.6/reference/)
- [Spring Data JDBC](https://docs.spring.io/spring-boot/3.5.6/reference/data/sql.html#data.sql.jdbc)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)

## 🆘 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure PostgreSQL is running: `docker-compose ps`
   - Check connection settings in `application.properties`

2. **Event Publication Table Missing**
   - Enable schema initialization: `spring.modulith.events.jdbc.schema-initialization.enabled=true`

3. **RabbitMQ Connection Issues**
   - Verify RabbitMQ is running: `docker-compose logs rabbitmq`
   - Check credentials in `compose.yaml`
   - Confirm exchange creation in RabbitMQ management UI

4. **Event Externalization Failures**
   - Check that the RabbitMQ exchange matches the one used by Spring Modulith
   - Verify the `@Externalized` annotation is correctly applied to event classes

4. **Port Conflicts**
   - PostgreSQL uses port 5432
   - RabbitMQ uses ports 5672 (AMQP) and 15672 (management)
   - Application uses port 8080

### Debug Mode

Run with debug logging:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

**Built with ❤️ using Spring Modulith**