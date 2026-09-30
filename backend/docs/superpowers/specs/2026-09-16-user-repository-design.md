# User Repository Design Spec

## Overview
Create the initial repository layer focusing strictly on the Authentication & User Management module, following the Ponytail YAGNI principles.

## Components

### 1. `UserRepository` Interface
**Location**: `com.phungvanlong.booking_hotel.repository.UserRepository`
**Dependencies**: `org.springframework.data.jpa.repository.JpaRepository`, `com.phungvanlong.booking_hotel.entity.User`

**Methods**:
- `Optional<User> findByEmail(String email)`: Used for Spring Security login and loading user details by username (email).
- `boolean existsByEmail(String email)`: Used for validation during user registration to prevent duplicate emails.
- `boolean existsByPhone(String phone)`: Used for validation during user registration to prevent duplicate phone numbers.

## Data Flow
- `AuthController` -> `AuthService` (or `UserService`) -> `UserRepository` -> Database

## Adherence to Ponytail Rules
- **YAGNI**: Only `UserRepository` is being created right now. Other repositories will be created when their specific features are built.
- **Framework Leverage**: Utilizes Spring Data JPA's derived query methods instead of writing manual JPQL.
