
# 📐 Quantity Measurement App

> A progressive journey through software design principles — from basic equality comparisons to advanced arithmetic operations with selective support.

---

## 🏗 Architecture

```
📂 IMeasurable (interface)
    ├── getConversionFactor()
    ├── convertToBaseUnit()
    ├── convertFromBaseUnit()
    ├── getUnitName()
    ├── supportsArithmetic()         [default: true]
    └── validateOperationSupport()   [default: no-op]
         │
         ├── 📂 LengthUnit (enum)
         │    ├── FEET
         │    ├── INCHES
         │    ├── YARDS
         │    └── CENTIMETERS
         │
         ├── 📂 WeightUnit (enum)
         │    ├── KILOGRAM
         │    ├── GRAM
         │    └── POUND
         │
         ├── 📂 VolumeUnit (enum)
         │    ├── LITRE
         │    ├── MILLILITRE
         │    └── GALLON
         │
         └── 📂 TemperatureUnit (enum)   [arithmetic disabled]
              ├── CELSIUS
              ├── FAHRENHEIT
              └── KELVIN

📂 SupportsArithmetic (functional interface)
    └── boolean isSupported()

📂 Quantity<U extends IMeasurable> (generic class)
    ├── value: double
    ├── unit: U
    ├── equals()
    ├── convertTo()
    ├── add() / add(other, targetUnit)
    ├── subtract() / subtract(other, targetUnit)
    ├── divide()
    └── ArithmeticOperation (private enum)
         ├── ADD
         ├── SUBTRACT
         └── DIVIDE

📂 QuantityMeasurementApp
    ├── demonstrateEquality<U>()
    ├── demonstrateComparison<U>()
    ├── demonstrateConversion<U>()
    ├── demonstrateAddition<U>()
    ├── demonstrateSubtraction<U>()
    ├── demonstrateDivision<U>()
    └── demonstrateTemperature()
```

---

## UC1: Basic Feet Equality — The Foundation

### What we did
- Created a simple `Feet` class to represent measurements
- Implemented basic equality comparison: *"Is 1 foot equal to 1 foot?"*

### What we learned
- **Value objects** — Objects that represent a concept by their value, not identity
- **Overriding `equals()`** — How to customize equality comparison in Java
- **Test-Driven Development (TDD)** — Writing tests first, then implementation

### Key concept

```java
Feet f1 = new Feet(1.0);
Feet f2 = new Feet(1.0);
f1.equals(f2); // true - same value
```

---

## UC2: Cross-Unit Comparison (Feet + Inches)

### What we did
- Extended equality to compare different units: *"Is 1 foot equal to 12 inches?"*
- Introduced conversion logic to compare apples to apples

### What we learned
- **Normalization** — Converting different representations to a common base
- **Conversion factors** — Mathematical relationships between units (1 foot = 12 inches)
- **Base unit concept** — Choosing one unit as the reference (base)

### Problem solved

```java
Length feet   = new Length(1.0,  FEET);
Length inches = new Length(12.0, INCHES);
feet.equals(inches); // true - equivalent values
```

---

## UC3: Generic Length Class with DRY Principle

### What we did
- Replaced separate `Feet` and `Inches` classes with a generic `Length` class
- Added `LengthUnit` enum to represent different units

### What we learned
- **DRY (Don't Repeat Yourself)** — Eliminate code duplication
- **Enums** — Type-safe way to represent fixed sets of constants
- **Composition** — Combining value + unit instead of separate classes
- **Single class, multiple units** — More scalable than one class per unit

### Design evolution

```
Before: Feet class, Inches class, Yards class...
After:  Length class + LengthUnit enum
```

---

## UC4: Adding More Units (Yards + Centimeters)

### What we did
- Added `YARDS` and `CENTIMETERS` to the `LengthUnit` enum
- Made sure all units work seamlessly with existing code

### What we learned
- **Open-Closed Principle (OCP)** — Open for extension, closed for modification
- **Scalability** — Adding units is now easy — just add enum constants
- **Consistency** — All units follow the same pattern

### Scalability demonstration

```java
// Adding a new unit is just one line!
enum LengthUnit {
    FEET(12.0),
    INCHES(1.0),
    YARDS(36.0),
    CENTIMETERS(0.393701) // New unit added easily
}
```

---

## UC5: Unit Conversion Operations

### What we did
- Added `convert()` and `convertTo()` methods
- Implemented actual unit conversion, not just comparison

### What we learned
- **Static utility methods** — `Length.convert(value, from, to)` for conversions
- **Immutability** — Operations return new objects instead of modifying existing ones
- **Precision handling** — Rounding to 2 decimal places to manage floating-point errors

### Usage

```java
Length feet   = new Length(1.0, FEET);
Length inches = feet.convertTo(INCHES); // Returns new Length(12.0, INCHES)
```

---

## UC6: Addition — Same and Different Units

### What we did
- Implemented addition of quantities in same or different units
- Result inherits the unit of the first operand

### What we learned
- **Method overloading** — Multiple versions of `add()` method
- **Unit normalization** — Convert to base unit, add, convert back
- **Operator design** — Choosing sensible defaults (result in first operand's unit)

### Example

```java
Length l1 = new Length(1.0,  FEET);
Length l2 = new Length(12.0, INCHES);
Length result = l1.add(l2); // Returns 2.0 FEET
```

---

## UC7: Addition with Explicit Target Unit

### What we did
- Added `add(other, targetUnit)` method
- User specifies desired result unit

### What we learned
- **API flexibility** — Giving users control over output format
- **Method overloading patterns** — Convenience method + explicit method
- **Default parameters** — Using overloading to simulate default arguments

### User control

```java
l1.add(l2, YARDS);  // Result in yards
l1.add(l2, INCHES); // Result in inches
```

---

## UC8: Standalone Enum with Conversion Responsibility

### What we did
- Extracted `LengthUnit` from nested enum to standalone enum
- Moved conversion logic **into** the enum itself
- Changed base unit from feet to inches

### What we learned
- **Separation of Concerns** — Enum handles conversions, class handles operations
- **Delegation** — `Length` delegates to `LengthUnit` for conversions
- **Enum as behavior carrier** — Enums can have methods, not just constants
- **Refactoring without breaking** — All tests still pass after refactoring

### Architecture improvement

```java
public enum LengthUnit {
    FEET(12.0);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }
}
```

---

## UC9: Multi-Category Support (Weight Measurements)

### What we did
- Created `WeightUnit` enum (`KILOGRAM`, `GRAM`, `POUND`)
- Created `Weight` class mirroring the `Length` pattern
- Added demonstration methods for weight operations

### What we learned
- **Pattern replication** — Following established patterns for new features
- **Category separation** — Weight and Length are incompatible (can't compare)
- **Type safety** — `instanceof` checks prevent cross-category comparisons

### Problem introduced

> Code duplication: `Length` and `Weight` classes are nearly identical. `WeightUnit` and `LengthUnit` have duplicate structure. `QuantityMeasurementApp` has duplicate methods. Not scalable: adding Volume or Temperature means more duplication.

---

## UC10: Generic Architecture — The Breakthrough

### What we did
- Created `IMeasurable` interface — a contract for all unit types
- Created generic `Quantity<U extends IMeasurable>` class
- Made both `LengthUnit` and `WeightUnit` implement `IMeasurable`
- Simplified `QuantityMeasurementApp` to use generic methods

### What we learned

#### 1. Generic Programming

```java
Quantity<LengthUnit> length = new Quantity<>(1.0, FEET);
Quantity<WeightUnit> weight = new Quantity<>(1.0, KILOGRAM);
```

- One class works with **any** unit type
- Compile-time type safety
- No code duplication

#### 2. Interface-Based Design

```java
public interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}
```

- Defines a contract all units must follow
- Enables polymorphism
- Allows treating different unit types uniformly

#### 3. Bounded Type Parameters

```java
public class Quantity<U extends IMeasurable>
```

- `U` can be **any** type that implements `IMeasurable`
- Compiler enforces this constraint
- Type safety without sacrificing flexibility

#### 4. DRY Principle Mastered

| Before UC10 | After UC10 |
|-------------|------------|
| Duplicate `Length` and `Weight` classes | Single `Quantity` class for **all** categories |
| ~200 lines of duplicated code | ~200 lines eliminated |

#### 5. Single Responsibility Principle

```java
// Before: 10 methods (5 for length, 5 for weight)
demonstrateLengthEquality()
demonstrateWeightEquality()
demonstrateLengthConversion()
demonstrateWeightConversion()
// ...

// After: 5 generic methods
demonstrateEquality<U>()    // Works for ALL types
demonstrateConversion<U>()  // Works for ALL types
// ...
```

#### 6. Open-Closed Principle

The system is **open** for adding new categories and **closed** for modification.

**Adding a new category (e.g., Volume):**

```java
// 1. Create enum implementing IMeasurable
public enum VolumeUnit implements IMeasurable {
    LITER(1.0),
    MILLILITER(0.001),
    GALLON(3.78541);
    // ... implement interface methods
}

// 2. Use it immediately — NO OTHER CHANGES NEEDED!
Quantity<VolumeUnit> vol = new Quantity<>(1.0, LITER);
vol.equals(new Quantity<>(1000.0, MILLILITER)); // Works!
```

#### 7. Liskov Substitution Principle

- Any `IMeasurable` implementation can be used with `Quantity<U>`
- No special cases needed
- Substitutable without breaking functionality

#### 8. Type Erasure Handling

```java
// Generic type info is erased at runtime, so we check manually
if (this.unit.getClass() != that.unit.getClass())
    return false; // Prevents comparing length to weight
```

#### 9. Polymorphism

```java
IMeasurable unit = LengthUnit.FEET;   // Polymorphic reference
unit.convertToBaseUnit(1.0);           // Works!

unit = WeightUnit.KILOGRAM;            // Different type
unit.convertToBaseUnit(1.0);           // Still works!
```

---

## UC11: Volume Measurements — Testing Generic Architecture

### What we did
- Created `VolumeUnit` enum (`LITRE`, `MILLILITRE`, `GALLON`)
- Implemented volume-to-volume conversions
- Added volume addition operations
- Applied precision rounding (2 decimal places)

### What we learned
- **Architecture validation** — UC10's generic design works perfectly for new categories
- **Precision management** — Floating-point arithmetic requires rounding strategies
- **Zero-modification extension** — Added entire category without changing existing code

### Key implementation

```java
public enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    // Rounding prevents floating-point errors
    @Override
    public double convertToBaseUnit(double value) {
        double result = value * conversionFactor;
        return Math.round(result * 100.0) / 100.0;
    }
}
```

### Problem solved

```java
Quantity<VolumeUnit> gallon = new Quantity<>(1.0,    GALLON);
Quantity<VolumeUnit> litre  = new Quantity<>(3.78,   LITRE);
gallon.equals(litre); // true - equivalent after rounding

Quantity<VolumeUnit> sum = gallon.add(litre); // 2.0 GALLON
```

---

## UC12: Subtraction and Division — Expanding Arithmetic Operations

### What we did
- Implemented `subtract()` method with same/explicit target unit
- Implemented `divide()` method returning a ratio
- Added comprehensive validation for all arithmetic operations
- Centralized validation logic to avoid duplication

### What we learned
- **Consistent API design** — Subtraction mirrors addition's dual-method pattern
- **Division semantics** — Returns a scalar (`double`), not a `Quantity`
- **Validation patterns** — Consistent error handling across operations
- **Edge case handling** — Division by zero protection with epsilon comparison

### Key implementations

#### Subtraction

```java
Quantity<LengthUnit> l1 = new Quantity<>(5.0, FEET);
Quantity<LengthUnit> l2 = new Quantity<>(3.0, FEET);
Quantity<LengthUnit> diff = l1.subtract(l2);             // 2.0 FEET

// With explicit target unit
Quantity<LengthUnit> diffInches = l1.subtract(l2, INCHES); // 24.0 INCHES
```

#### Division

```java
Quantity<LengthUnit> l1 = new Quantity<>(6.0, FEET);
Quantity<LengthUnit> l2 = new Quantity<>(3.0, FEET);
double ratio = l1.divide(l2); // 2.0 (dimensionless)
```

### Validation strategy

```java
private void validateArithmeticOperands(
    Quantity<U> other,
    U targetUnit,
    boolean targetUnitRequired
) {
    // Null check
    // Category compatibility check
    // Finite value check
    // Target unit check (conditional)
}
```

---

## UC13: Centralized Arithmetic Logic — DRY at Operation Level

### What we did
- Created `ArithmeticOperation` enum encapsulating operation logic
- Refactored `add()`, `subtract()`, `divide()` to use centralized helpers
- Eliminated code duplication across arithmetic methods
- Introduced functional programming with `DoubleBinaryOperator`

### What we learned
- **Strategy Pattern** — Enum-based strategy for different operations
- **Lambda expressions** — Using lambdas for operation logic
- **Template Method Pattern** — Shared validation + operation + conversion flow
- **DRY at algorithm level** — Don't repeat the "convert → operate → convert back" pattern
- **Code reduction** — 50% fewer lines of code with improved maintainability

### Architecture breakthrough

#### `ArithmeticOperation` Enum

```java
private enum ArithmeticOperation {
    ADD     ((a, b) -> a + b),
    SUBTRACT((a, b) -> a - b),
    DIVIDE  ((a, b) -> {
        if (Math.abs(b) < EPSILON) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    });

    private final DoubleBinaryOperator operation;

    ArithmeticOperation(DoubleBinaryOperator operation) {
        this.operation = operation;
    }

    public double compute(double a, double b) {
        return operation.applyAsDouble(a, b);
    }
}
```

#### Centralized Helper

```java
private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
    double thisBase  = unit.convertToBaseUnit(this.value);
    double otherBase = other.unit.convertToBaseUnit(other.value);
    return operation.compute(thisBase, otherBase);
}

// Usage:
double result = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
```

### Benefits achieved

1. **Single source of truth** — Operation logic defined once
2. **Easy extension** — Adding multiply/modulo means adding one enum constant
3. **Consistent behavior** — All operations use the same validation flow
4. **Testability** — Can test operations independently
5. **Functional programming** — Leveraging Java's functional capabilities

---

## UC14: Temperature Measurements — Selective Arithmetic Support

### What we did
- Created `TemperatureUnit` enum (`CELSIUS`, `FAHRENHEIT`, `KELVIN`)
- Implemented **non-linear** conversions (temperature has offset, not just scaling)
- Created `SupportsArithmetic` functional interface
- Extended `IMeasurable` with arithmetic support checking
- Made arithmetic operations validate unit support

> **Key insight:** Temperature doesn't support addition/subtraction of absolute values.

### What we learned

#### 1. Non-Linear Conversions

```java
public enum TemperatureUnit implements IMeasurable {
    CELSIUS    (c -> c),
    FAHRENHEIT (f -> (f - 32) * 5 / 9),   // Offset + scaling
    KELVIN     (k -> k - 273.15);

    private final Function<Double, Double> conversionToBase;

    @Override
    public double convertToBaseUnit(double value) {
        return conversionToBase.apply(value);
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        switch (this) {
            case FAHRENHEIT: return (baseValue * 9 / 5) + 32;
            case KELVIN:     return baseValue + 273.15;
            default:         return baseValue;
        }
    }
}
```

**Why non-linear matters:**

| Type | Example | Result |
|------|---------|--------|
| Linear | 1 foot + 1 foot | = 2 feet ✅ |
| Non-linear | 10°C + 10°C | ≠ 20°C ❌ (meaningless) |

#### 2. Functional Interface for Arithmetic Support

```java
@FunctionalInterface
public interface SupportsArithmetic {
    boolean isSupported();
}
```

#### 3. Enhanced `IMeasurable` Interface

```java
public interface IMeasurable {
    // Existing methods...

    // New methods with defaults:
    default SupportsArithmetic supportsArithmetic() {
        return () -> true;  // Default: supports arithmetic
    }

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default void validateOperationSupport(String operation) {
        // Default: no-op (operations allowed)
    }
}
```

#### 4. Temperature Overrides

```java
public enum TemperatureUnit implements IMeasurable {
    // ...

    private final SupportsArithmetic supportsArithmetic = () -> false;

    @Override
    public boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
            "Temperature does not support operation: " + operation +
            " on absolute values.");
    }
}
```

#### 5. Updated `Quantity` Methods

```java
public Quantity<U> add(Quantity<U> other) {
    if (other == null) {
        throw new IllegalArgumentException("Quantity cannot be null");
    }

    // Check if this unit type supports arithmetic
    this.unit.validateOperationSupport("ADD");
    other.unit.validateOperationSupport("ADD");

    return add(other, this.unit);
}
```

### Why this matters — The Temperature Problem

```java
// ✅ These make sense:
Quantity<TemperatureUnit> temp1 = new Quantity<>(0.0,  CELSIUS);
Quantity<TemperatureUnit> temp2 = new Quantity<>(32.0, FAHRENHEIT);
temp1.equals(temp2);       // true  - comparison works!
temp1.convertTo(KELVIN);   // 273.15 KELVIN - conversion works!

// ❌ These DON'T make sense:
temp1.add(temp2);          // Throws UnsupportedOperationException
temp1.subtract(temp2);     // Throws UnsupportedOperationException
temp1.divide(temp2);       // Throws UnsupportedOperationException
```

**Physical reasoning:**

- **Temperature** represents a point on a scale (absolute value)
- **Temperature difference** is a different concept (interval/delta)
- Adding 20°C + 30°C = 50°C is **physically meaningless**
- But 30°C − 20°C = 10°C *difference* makes sense (but returns a different type)

### Design patterns applied

| Pattern | Application |
|---------|-------------|
| Template Method | Default implementations with override points |
| Strategy Pattern | `SupportsArithmetic` strategy per unit type |
| Fail-Fast | Validate before executing any operation |
| Liskov Substitution | `TemperatureUnit` still substitutable; throws only on invalid ops |
| Functional Programming | Lambda-based conversion functions |

### Architectural flexibility

```java
// Pressure: supports all arithmetic — no overrides needed
public enum PressureUnit implements IMeasurable {
    // Uses all defaults
}

// Dates: support subtraction but not addition
public enum DateUnit implements IMeasurable {
    private final SupportsArithmetic supportsArithmetic = () -> false;

    @Override
    public void validateOperationSupport(String operation) {
        if (operation.equals("SUBTRACT")) {
            return; // Allow subtraction
        }
        throw new UnsupportedOperationException(
            operation + " not supported for dates");
    }
}
```

---
## UC16: Database Integration with JDBC for Quantity Measurement Persistence

### What we did
Extended the N-Tier architecture from UC15 by replacing the in-memory `QuantityMeasurementCacheRepository` with a **JDBC-based database repository** for long-term persistent storage. Also introduced a professional **Maven project structure** with proper package organization by layer.

### What we learned
- **JDBC (Java Database Connectivity)** — Low-level API for connecting and executing SQL queries on relational databases
- **Connection Pooling** — Reusing database connections efficiently to reduce overhead and improve performance
- **Parameterized SQL Queries** — Using `PreparedStatement` with `?` placeholders to prevent SQL injection
- **Maven Project Structure** — Standard directory layout (`src/main/java`, `src/test/java`, `src/main/resources`)
- **Configuration Management** — Loading environment-specific settings from `application.properties`
- **Custom Exception Hierarchy** — `DatabaseException` extending `QuantityMeasurementException` for DB-specific errors
- **Transaction Management** — Grouping operations as atomic units with rollback on failure
- **Resource Management** — Proper closing of `ResultSet`, `Statement`, `Connection` using try-with-resources
- **H2 In-Memory Database** — Lightweight database for isolated unit and integration testing
- **SLF4J Logging** — Replacing `System.out.println` with structured logging using Logback

### Project Structure

```
src/
├── main/java/com/app/quantitymeasurement/
│   ├── QuantityMeasurementApp.java
│   ├── controller/
│   ├── service/
│   ├── repository/
│   │   ├── IQuantityMeasurementRepository.java
│   │   ├── QuantityMeasurementCacheRepository.java
│   │   └── QuantityMeasurementDatabaseRepository.java  ← New
│   ├── entity/
│   ├── exception/
│   │   └── DatabaseException.java                      ← New
│   └── util/
│       ├── ApplicationConfig.java                      ← New
│       └── ConnectionPool.java                         ← New
├── main/resources/
│   ├── application.properties
│   └── db/schema.sql
└── test/java/com/app/quantitymeasurement/
    ├── repository/
    ├── service/
    ├── controller/
    └── integrationTests/
```

### Key Classes

| Class | Purpose |
|---|---|
| `ApplicationConfig` | Loads DB config from `application.properties` |
| `ConnectionPool` | Manages reusable pool of JDBC connections |
| `DatabaseException` | Custom exception for database-specific errors |
| `QuantityMeasurementDatabaseRepository` | JDBC implementation of `IQuantityMeasurementRepository` |

### Problem solved

```java
// Before UC16 — lost data on restart
IQuantityMeasurementRepository repo = new QuantityMeasurementCacheRepository();
// Data gone when app shuts down ❌

// After UC16 — persistent storage
IQuantityMeasurementRepository repo = new QuantityMeasurementDatabaseRepository();
// Data survives restarts, queryable, scalable ✅

// Switch via config — no code change needed!
repository.type=database   // in application.properties
```

### New Repository Methods

| Method | Purpose |
|---|---|
| `save(entity)` | Persists entity to database |
| `getAllMeasurements()` | Retrieves all stored records |
| `getMeasurementsByOperation()` | Filters by operation type |
| `getMeasurementsByType()` | Filters by measurement category |
| `getTotalCount()` | Returns total number of records |
| `deleteAll()` | Removes all records (useful for testing) |
| `getPoolStatistics()` | Returns connection pool status |

### Maven Commands

```bash
mvn clean compile     # Build the project
mvn clean test        # Run all tests
mvn exec:java         # Run the application
mvn clean package     # Create executable JAR
```

### Advantages over UC15

- Data **survives application restarts** — no more lost history
- **SQL queries** enable filtering, aggregation, and reporting
- **Connection pooling** improves performance under load
- **H2 database** enables fast, isolated, reproducible tests
- **SQL injection prevented** via parameterized queries
- Easy to **swap to MySQL/PostgreSQL** — just change `application.properties`
- All UC1–UC15 tests **still pass** — behavior unchanged

---
## UC17: Spring Framework Integration - REST Services and JPA

### What we did
Transformed the standalone Quantity Measurement Application into a **Spring Boot REST service** by replacing manual JDBC with Spring Data JPA, exposing functionality through RESTful HTTP endpoints, and adding centralized exception handling, API documentation, and integration testing.

### What we learned
- **Spring Boot Auto-Configuration** — Automatic bean registration and embedded Tomcat server with minimal setup
- **Spring Data JPA** — Replacing manual JDBC with declarative query methods by extending `JpaRepository`
- **REST Controllers** — Using `@RestController`, `@GetMapping`, `@PostMapping` to expose HTTP endpoints
- **Dependency Injection** — Spring container managing beans via `@Service`, `@Repository`, `@Autowired`
- **@Transactional** — Declarative transaction management with automatic rollback on exceptions
- **Global Exception Handling** — Centralized error responses using `@ControllerAdvice` and `@ExceptionHandler`
- **DTO Pattern** — `QuantityDTO`, `QuantityMeasurementDTO`, `QuantityInputDTO` for clean API communication
- **Bean Validation** — Input validation using `@NotNull`, `@NotEmpty`, `@Pattern`, `@AssertTrue`
- **Swagger/OpenAPI** — Auto-generated interactive API documentation using `@Operation`, `@Tag`
- **MockMvc Testing** — Testing REST endpoints using `@WebMvcTest` and `@MockBean` without full context
- **Spring Boot Actuator** — Built-in health checks and metrics at `/actuator/health`
- **Spring Security Basics** — Security configuration foundation for future authentication
- **SLF4J + Logback** — Structured logging replacing `System.out.println`
- **Maven Surefire Report** — Generating rich HTML test reports

### Architecture

```
HTTP Request
     │
@RestController (QuantityMeasurementController)
     │  @PostMapping / @GetMapping
     ▼
@Service (QuantityMeasurementServiceImpl)
     │  Business logic + @Transactional
     ▼
@Repository (QuantityMeasurementRepository)
     │  extends JpaRepository<QuantityMeasurementEntity, Long>
     ▼
H2 / MySQL Database (via Spring Data JPA)
```

### Key Classes

| Class | Purpose |
|---|---|
| `QuantityMeasurementAppApplication` | Spring Boot entry point (`@SpringBootApplication`) |
| `QuantityMeasurementController` | REST controller — exposes API endpoints |
| `QuantityMeasurementServiceImpl` | Service layer with business logic |
| `QuantityMeasurementRepository` | Spring Data JPA repository |
| `QuantityMeasurementEntity` | JPA entity mapped to DB table |
| `QuantityInputDTO` | Input DTO for API requests |
| `QuantityMeasurementDTO` | Output DTO for API responses |
| `GlobalExceptionHandler` | Centralized error handling via `@ControllerAdvice` |
| `SecurityConfig` | Basic Spring Security configuration |
| `OperationType` | Enum for ADD, SUBTRACT, COMPARE, CONVERT, DIVIDE |

### REST API Endpoints

| Method | Endpoint | Operation |
|---|---|---|
| POST | `/api/v1/quantities/compare` | Compare two quantities |
| POST | `/api/v1/quantities/convert` | Convert unit |
| POST | `/api/v1/quantities/add` | Add two quantities |
| POST | `/api/v1/quantities/subtract` | Subtract quantities |
| POST | `/api/v1/quantities/divide` | Divide quantities |
| GET | `/api/v1/quantities/history/operation/{op}` | Get history by operation |
| GET | `/api/v1/quantities/history/type/{type}` | Get history by measurement type |
| GET | `/api/v1/quantities/history/errored` | Get error history |
| GET | `/api/v1/quantities/count/{operation}` | Get operation count |

### Problem solved

```java
// Before UC17 — manual JDBC boilerplate
Connection conn = pool.getConnection();
PreparedStatement ps = conn.prepareStatement("INSERT INTO ...");
ps.setString(1, entity.getOperation());
// ... many more lines ❌

// After UC17 — Spring Data JPA
@Repository
public interface QuantityMeasurementRepository
    extends JpaRepository<QuantityMeasurementEntity, Long> {
    List<QuantityMeasurementEntity> findByOperation(String operation);
} // Zero SQL needed ✅
```

### Maven Commands

```bash
mvn clean compile          # Build project
mvn spring-boot:run        # Run application
mvn test                   # Run all tests
mvn clean package          # Package as JAR
mvn surefire-report:report # Generate test reports
```

### URLs After Starting App

| URL | Purpose |
|---|---|
| `http://localhost:8080/api/v1/quantities/` | REST API |
| `http://localhost:8080/h2-console` | H2 database console |
| `http://localhost:8080/swagger-ui.html` | Swagger API documentation |
| `http://localhost:8080/actuator/health` | Health check |

### Advantages over UC16

- **Zero SQL boilerplate** — Spring Data JPA auto-generates queries from method names
- **Embedded server** — No external Tomcat/server setup needed
- **Auto-configured** — H2, JPA, security all auto-configured via `application.properties`
- **Interactive API docs** — Swagger UI for testing endpoints directly in browser
- **Centralized error handling** — `@ControllerAdvice` returns consistent JSON error responses
- **Declarative transactions** — `@Transactional` replaces manual commit/rollback
- **Spring Security ready** — Foundation for future JWT/OAuth2 authentication
- All UC1–UC16 tests **still pass** — business logic unchanged
---

# 🔐 UC18: Google Authentication & User Management

> **Spring Backend — Quantity Measurement App**
> Concepts covered: **Spring Security · JWT · OAuth 2.0**
> Author: Vishal Bhakare | Mar 20

---

## What we did
- Integrated **Google OAuth 2.0** login — users sign in with their Google account
- Issued a signed **JWT token** after successful Google login
- Created a **JWT filter** that validates the bearer token on every protected request
- Persisted Google user profile (email, name, picture) to the database on first login
- Added **role-based access** (`USER` / `ADMIN`) with stateless session management

---

## What we learned

### Spring Security
> A filter-chain-based security framework that handles authentication (who are you?) and authorization (what can you do?) before the request ever reaches the controller.

```
HTTP Request → JwtAuthFilter → OAuth2LoginFilter → Controller
```

### JWT (JSON Web Token)
> A compact, self-contained token that carries identity claims, digitally signed so it cannot be tampered with.

```
eyJhbGciOiJIUzI1NiJ9 . eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSJ9 . SflKxwRJSMeKK
      HEADER                       PAYLOAD                      SIGNATURE
   (algorithm)             (sub, iat, exp, role, name)       (HMAC-SHA256)
```

### OAuth 2.0
> An authorization framework that lets users grant our app access to their Google profile — without sharing their password.

| Role | Description | In our app |
|------|-------------|------------|
| Resource Owner | The user | Vishal (Google account holder) |
| Client | Our app | Quantity Measurement Backend |
| Authorization Server | Issues tokens | Google OAuth Server |
| Resource Server | Holds user data | Google People API |

---

## Flow

```
User clicks "Login with Google"
        │
        ▼
Google shows consent screen
        │
        ▼
Google returns Auth Code to our callback URL
        │
        ▼
Our app exchanges code → Access Token → fetches profile (email, name, picture)
        │
        ▼
Save/update User in DB → Generate JWT → return to client
        │
        ▼
Client sends JWT in every request:  Authorization: Bearer <token>
        │
        ▼
JwtAuthFilter validates → sets SecurityContext → Controller runs
```

---

## Key implementations

### Security Configuration
```java
http
    .csrf(AbstractHttpConfigurer::disable)
    .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**", "/oauth2/**").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    )
    .oauth2Login(oauth2 -> oauth2
        .userInfoEndpoint(u -> u.userService(oAuth2UserService))
        .successHandler(oAuth2SuccessHandler)
    )
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
```

### JWT Generation
```java
public String generateToken(String email, String role, String name) {
    return Jwts.builder()
        .setSubject(email)
        .claim("role", role)
        .claim("name", name)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
}
```

### JWT Filter
```java
// Runs before every request
String jwt   = authHeader.substring(7);
String email = jwtUtil.extractEmail(jwt);

if (jwtUtil.isTokenValid(jwt, email)) {
    // Set authentication in SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authToken);
}
```

### Save or Create User on Login
```java
public User findOrCreateUser(String email, String name, String picture) {
    return userRepository.findByEmail(email)
        .map(user -> { user.setName(name); return userRepository.save(user); })
        .orElseGet(() -> userRepository.save(new User(email, name, picture, Role.USER)));
}
```

---

## Why JWT over Sessions?

| | Sessions | JWT |
|--|----------|-----|
| **Storage** | Server-side | Client-side |
| **Scalability** | Needs shared store | Stateless — any server |
| **Performance** | DB lookup per request | Local crypto validation |
| **Best for** | Traditional web apps | REST APIs / SPAs |

---

