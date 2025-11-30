# Sernova Technical Test — Spring Boot + JPA

This document outlines the minimal fixes applied to get the project running, performance improvements for the one-to-many relationship, and potential future improvements.

---

## 1. Issues Identified

1. **JPA Entities Missing Primary Keys**
   - Both `Person` and `Address` were missing `@Id` and `@GeneratedValue`, preventing Hibernate from mapping them correctly.

2. **Build Failure due to Java Version**
   - `pom.xml` used `<release>22</release>` but Spring Boot 3.3.x supports up to Java 21.

3. **No Embedded Database**
   - The project lacked a runtime database dependency, preventing Spring Boot from auto-configuring a datasource.

4. **N+1 Problem on One-to-Many**
   - Fetching persons with addresses triggered a separate SQL query per person, causing slow response and excessive database load.

---

## 2. Minimal Changes Made

### a) Fixed JPA Entities

**Person.java** and **Address.java**:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

### b) Fixed Build Configuration
**pom.xml**

Updated Java version to 21

Added H2 embedded database dependency

### c) Fixed N+1 Problem

**PersonRepository.java**

```
@Query("select distinct p from Person p left join fetch p.addresses")
java.util.List<Person> findAllWithAddresses();
```

## 3. Performance Improvement

Before: 10,000 persons × 2 addresses each → 10,001 SQL queries.
After: Single JOIN FETCH query loads all persons with addresses, drastically reducing SQL calls and response time.

## 4. Future Improvements (Explained & Implemented in Code)

1. **DTO Projection**
	- Return only required fields instead of full JPA entities.
	- Reduces memory usage and network payload.

2. **Pagination**
	- Return data in pages (e.g., 500 per request) for large datasets.
	- Improves response time and memory usage.
	
3. **@EntityGraph**
	- Declaratively fetch related entities, avoiding N+1 queries.

4. **Caching**
	- Cache frequently accessed data to reduce repeated database queries.
	

