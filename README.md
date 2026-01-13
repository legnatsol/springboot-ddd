# SpringBoot DDD Library

## Installation

Gradle:
```gradle
implementation 'io.github.legnatsol:springboot-ddd:1.0.0'
```

Maven:
```xml
<dependency>
    <groupId>io.github.legnatsol</groupId>
    <artifactId>springboot-ddd</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Quick Start

### Define Aggregate Root
```java
public class Order extends BaseAggregateRoot<OrderId> {
    private OrderId id;
    // ...
}
```

### Define Value Object
```java
Money price = Money.of(100.0, Currency.getInstance("CNY"));
```

## Module Description
- `domain` - DDD core abstract
- `dto` - Data Transfer Object
- `event` - Domain Event
- `repository` - Repository abstract
- `model` - Common value object