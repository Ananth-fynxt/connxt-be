# Connxt Backend

A Spring Boot-based backend application built using Gradle with a modular architecture and centralized environment configuration via .env file.

## 🏗️ Project Structure

```
connxt-backend/
├── buildSrc/                          # Build logic and conventions
│   └── src/main/kotlin/               # Gradle build scripts
├── gradle/                            # Gradle configuration and wrapper
│   └── libs.versions.toml             # Dependency version management
├── libs/                              # Shared reusable libraries
│   ├── integration/                   # External service integration
│   └── shared/                        # Shared components (SecurityConfig, constants, utils, config)
├── services/                          # Service modules
│   ├── core/                          # Main application service
│   │   ├── controller/                # REST controllers
│   │   ├── service/                   # Business logic services
│   │   ├── repository/                # Data access layer
│   │   ├── entity/                    # JPA entities
│   │   ├── dto/                       # Data transfer objects
│   │   └── resources/                 # Application configuration
│   └── migration/                     # Database migration service
│       └── src/main/resources/db/migration/  # Flyway migration scripts
├── .env                                # Centralized environment configuration
├── gradlew                            # Gradle wrapper script (Unix)
├── gradlew.bat                        # Gradle wrapper script (Windows)
├── settings.gradle.kts                # Gradle settings
├── build.gradle.kts                   # Main build configuration
└── README.md                          # This file
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Gradle 8.7** or higher (included via wrapper)
- **PostgreSQL** database
- **.env** file configuration (environment variables)

### Setup & Run

1. **Clone and build:**
   ```bash
   git clone <repository-url>
   cd connxt-backend
   cp .env.example .env  # Copy and configure environment variables
   ./gradlew clean build
   ```

2. **Start the application:**
   ```bash
   ./gradlew :services:core:bootRun
   ```

3. **Application will be available at:**
   - Server: http://localhost:8001
   - Health Check: http://localhost:8001/actuator/health
   - Swagger: http://localhost:8001/swagger-ui/index.html
   - JobRunr Dashboard: http://localhost:8000/jobrunr-dashboard

## 🔧 Development Commands

### **Build & Compilation**
```bash
# Clean and build entire project
./gradlew clean build

# Compile Java sources only
./gradlew compileJava

# Build specific module
./gradlew :services:core:build
```

### **Running**
```bash
# Start the main application
./gradlew :services:core:bootRun

# Run this before debugger mode
./gradlew :services:core:bootRun --debug-jvm

# Stop the application
pkill -f "connxt.ConnxtApplication"
```

### **Kill Port**
```bash
# Terminal command
lsof -ti:8000,8001 | xargs kill -9
```

### **Database Operations**
```bash
# Clean database migrations
./gradlew :services:migration:clean

# Run database migrations
./gradlew :services:migration:run
```

### **Project Information**
```bash
# List all projects/modules
./gradlew projects

# Show dependencies
./gradlew dependencies
```

### **Formatting**
```bash
# Format all projects/modules
./gradlew spotlessApply
```

## 🐳 Docker Setup

### 🚀 Essential Commands

```bash
# Start everything
docker-compose up --build

# Start everything detached mode
docker-compose up -d

# Stop everything  
docker-compose down

# View logs
docker-compose logs -f
```

## 🤝 Contributing

1. Follow the existing code structure and patterns
2. Write tests for new functionality
3. Update documentation as needed
4. Use conventional commit messages
5. Ensure all tests pass before submitting

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details. 
