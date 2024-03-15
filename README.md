# Description of the project

The project is a web application that allows the user to register and manage subscriptions with music files. Users can subscribe to different types of subscriptions (FREE, OPTIMAL, MAXIMUM) and have access to the corresponding content. There is also an option to manage the automatic renewal of the subscription. There is an administrator page, access to which opens when the ADMIN subscription is enabled (this subscription is not available for selecting a regular user). Admin can perform CRUD operations related to users, subscription, and music files.

## Main components

1. **enums package:** Contains enumerations for auto-renew subscription statuses and subscription types.
2. **Repositories package:** Contains repository interfaces for working with entity objects.
3. **Services package:** Contains services that perform the business logic of the application.
    - *JwtService:* Responsible for generating and validating JWT tokens for authentication.
    - *MusicFileService:* Provides functionality for working with music files, including adding, updating, and deleting them.
    - *SubscriptionService:* Manages users’ subscriptions, including handling subscription expiration and auto-renewal.
    - *UserService:* Provides functionality for registration, authorization, and users’ management.
4. **Mock package:** Contains entities and services that simulate the operation of bank accounts and perform transactions with them.
5. **Validation package:** Contains components for data validation, such as checking the format of the phone number and the presence of a bank card.

## Configuration

- *WebSecurityConfig:* Security configuration, including resource access settings and filters for handling JWT tokens.
- *PasswordEncoderConfig:* Configuration of user password encoding.
- *FinalProjectApplication:* A class to run the application, including scheduler support.

## Project launch

1. Start the FinalProjectApplication class.
2. The application will be available at [http://localhost:8080](http://localhost:8080).

## Important notes

1. To access protected resources, you must provide the correct JWT token in the Authorization header.
2. Ensure correct configuration of `token.signing.key` in `application.properties` file.

The project is partially covered by tests.

## Technologies

- Java 17
- Spring Boot (Data JPA, Web, Security)
- Lombok
- Maven
- Git
- PostgreSQL
