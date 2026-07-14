# AlertAid - Disaster Management Platform

AlertAid is a comprehensive disaster management platform built with Spring Boot (backend) and vanilla JavaScript/HTML (frontend). The platform enables citizens to report emergencies, volunteers to respond to disasters, organizations to coordinate relief efforts, and administrators to manage the entire system.

## 🚀 Recent Fixes Applied

This project has been recently updated to fix several critical issues:

- ✅ **Database Configuration**: Flexible support for both MySQL and PostgreSQL
- ✅ **Security**: Improved JWT secret management and CORS configuration
- ✅ **Code Structure**: Cleaned up duplicate entity classes and Maven dependencies
- ✅ **Environment Management**: Added proper .env configuration support
- ✅ **Production Ready**: Enhanced Docker configuration for deployment

## 🏗️ Architecture

- **Backend**: Spring Boot 3.3.4 with MySQL (default) or PostgreSQL
- **Frontend**: Vanilla JavaScript, HTML5, CSS3
- **Authentication**: JWT-based security
- **Database**: MySQL (default) or PostgreSQL with JPA/Hibernate
- **Containerization**: Docker & Docker Compose

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose
- MySQL (for local development) or PostgreSQL (for Docker deployment)

## 🚀 Quick Start

### 1. Clone and Setup Environment

```bash
git clone <repository-url>
cd Alertaid
cp .env.example .env
```

### 2. Configure Environment

Edit `.env` file with your settings:

```bash
# Generate a strong JWT secret
openssl rand -base64 32

# Add the generated secret to .env file
APP_JWT_SECRET=your-generated-secret-here
```

### 3. Run with MySQL Locally (Recommended for Development)

Make sure MySQL is running and create the database:
```sql
CREATE DATABASE alertaid_db;
```

Then run the application:
```bash
cd backend/Alertaid
# Windows / PowerShell (loads .env entries such as OAuth + Razorpay)
.\start-env.ps1

# macOS / Linux
./mvnw spring-boot:run
```

The application will be available at:
- **Frontend**: http://localhost:8080
- **Backend API**: http://localhost:8080/api

### 4. Run with Docker (PostgreSQL)

For PostgreSQL deployment with Docker:

```bash
# For PostgreSQL with Docker
docker-compose up -d

# For MySQL with Docker (alternative)
docker-compose -f docker-compose.mysql.yml up -d

# Check logs
docker-compose logs -f
```

The application will be available at:
- **Frontend**: http://localhost:8080
- **Backend API**: http://localhost:8080/api
- **Database**: localhost:5432 (PostgreSQL) or localhost:3306 (MySQL)

## 🔧 Configuration

### Database Configuration

The application supports both MySQL (default) and PostgreSQL. Configuration can be customized via environment variables:

**MySQL (Default):**
```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/alertaid_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
```

**PostgreSQL (Docker):**
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/alertaid
SPRING_DATASOURCE_USERNAME=alertaid
SPRING_DATASOURCE_PASSWORD=alertaid
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

### Security Configuration

JWT authentication is configured with:

```properties
APP_JWT_SECRET=your-jwt-secret-base64-encoded
APP_JWT_EXPIRATION_MS=86400000  # 24 hours
```

### CORS Configuration

CORS origins can be configured via:

```properties
CORS_ALLOWED_ORIGINS=http://localhost:8080,http://localhost:3000
```

### Payment Gateway Configuration

Monetary donations use Razorpay. Provide your credentials through environment variables (or `.env` + `start-env.ps1`):

```properties
PAYMENT_RAZORPAY_KEY_ID=rzp_live_xxx
PAYMENT_RAZORPAY_KEY_SECRET=your_real_secret
```

If these variables are not set the backend falls back to mock orders, but real payments will fail with `Unable to create payment order (500)` and the frontend will notify you. Run `.\start-env.ps1` so the `.env` values are loaded each time you start Spring Boot.

### Email notifications

Set up SMTP credentials and enable AlertAid’s email notifications to broadcast every new disaster report to all registered users:

```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=alerts@example.com
MAIL_PASSWORD=app-specific-password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
ALERTAID_NOTIFICATIONS_EMAIL_ENABLED=true
ALERTAID_NOTIFICATIONS_FROM=alerts@example.com
ALERTAID_NOTIFICATIONS_REPORT_SUBJECT=New AlertAid report submitted
```

When `ALERTAID_NOTIFICATIONS_EMAIL_ENABLED=true`, each successful report submission triggers a single message that BCCs every user email stored in the database (batched to avoid SMTP limits). Leave the flag `false` to disable this automation in development.

## Deploying on Render

The repository now ships with a turnkey blueprint at `render.yaml`. It provisions a Web Service plus a managed MySQL instance (`alertaid-mysql`), wires the JDBC URL automatically, configures the build (`./mvnw clean package -DskipTests`), and sets `/actuator/health` as the health check.

**Important:** The application automatically detects MySQL from the `DATABASE_URL` and sets the correct Hibernate dialect (`org.hibernate.dialect.MySQLDialect`) to prevent SQL dialect errors. The `RenderDatabaseEnvironmentPostProcessor` handles parsing the connection string.

### Option 1: Apply the blueprint

1. In Render, click **New ➜ Blueprint** and select this repository. Render will automatically discover the `render.yaml`.
2. Accept the staged MySQL instance (database name `alertaid_db`) and the Web Service named `alertaid-backend`.
3. Supply the required secrets (see the list below). The blueprint auto-generates `APP_JWT_SECRET`, but you can overwrite it with your own value at any time.
4. **Update environment variables** in the Render dashboard:
   - Replace `REPLACE_WITH_RAZORPAY_KEY` with your Razorpay Key ID
   - Replace `REPLACE_WITH_RAZORPAY_SECRET` with your Razorpay Key Secret
   - Replace `REPLACE_WITH_SMTP_USERNAME` with your SMTP username
   - Replace `REPLACE_WITH_SMTP_PASSWORD` with your SMTP password
   - Update `CORS_ALLOWED_ORIGINS` with your actual frontend URL (e.g., `https://alertaid-backend.onrender.com`)
5. Deploy. The backend binds to Render's `$PORT` and auto-configures the datasource from `DATABASE_URL`.

### Option 2: Manual Web Service

If you prefer to configure things in the dashboard:

1. Point the service to this repo and set the **Root Directory** to `backend/Alertaid`.
2. Build command: `./mvnw clean package -DskipTests`
3. Start command: `java -jar target/Alertaid-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
4. Health check path: `/actuator/health`
5. Attach a managed MySQL instance so `DATABASE_URL` is injected, or provide the `SPRING_DATASOURCE_*` variables manually.
6. Set `HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect` if using MySQL (though this is auto-detected from `DATABASE_URL`).

## Deploying on Railway

Railway reads the `railway.json` file that ships with the repo. It forces the builder to use the Dockerfile located at `backend/Alertaid/Dockerfile`, enables the `/actuator/health` probe, and restarts on failure. The Dockerfile was updated to work from either the module directory or the repository root, so no extra shims are required.

1. Install the [Railway CLI](https://docs.railway.com/reference/cli) and run `railway up` (or create the service from the dashboard) pointing at this repository.
2. Accept the managed PostgreSQL plugin so Railway injects `DATABASE_URL`. The backend will auto-map it to `spring.datasource.*` the same way it does on Render.
3. Under **Variables**, add the required secrets listed below (same set as Render). Because the build relies on Docker, no build/start commands are required—the service will use the image entrypoint.
4. Deploy and verify the health check at `/actuator/health`.

## Cloud environment variables (Render & Railway)

These keys must be provided through the respective dashboard/CLI before marking the deployment live:

- `SPRING_PROFILES_ACTIVE=prod`
- `APP_JWT_SECRET=<base64 32+ byte secret>`
- `APP_JWT_EXPIRATION_MS=86400000` (tweak if you need shorter-lived tokens)
- `CORS_ALLOWED_ORIGINS=https://<your-ui-domain>`
- `PAYMENT_GATEWAY_PROVIDER=razorpay`
- `PAYMENT_RAZORPAY_KEY_ID=<live-key>`
- `PAYMENT_RAZORPAY_KEY_SECRET=<live-secret>`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, optional `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS`
- `ALERTAID_NOTIFICATIONS_EMAIL_ENABLED=true` plus optional `ALERTAID_NOTIFICATIONS_FROM`, `ALERTAID_NOTIFICATIONS_REPORT_SUBJECT`, `ALERTAID_NOTIFICATIONS_BATCH_SIZE`
- Optional: `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` (only if you enable Google OAuth)
- Optional (only when you do **not** attach a managed DB): `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_DATASOURCE_DRIVER`, and `HIBERNATE_DIALECT`

> The backend ships with `RenderDatabaseEnvironmentPostProcessor`, so any platform that exposes `DATABASE_URL`, `JAWSDB_URL`, `CLEARDB_DATABASE_URL`, or `DATABASE_JDBC_URL` will be parsed automatically. Managed services on both Render and Railway provide the correct variable, letting you omit every `SPRING_DATASOURCE_*` entry unless you are targeting an external database.

## 📁 Project Structure

```
Alertaid/
├── backend/
│   └── Alertaid/           # Spring Boot application
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/alertaid/
│       │   │   │   ├── config/       # Configuration classes
│       │   │   │   ├── controller/   # REST controllers
│       │   │   │   ├── model/        # JPA entities
│       │   │   │   ├── repository/   # Data repositories
│       │   │   │   ├── security/     # Security components
│       │   │   │   └── service/      # Business logic
│       │   │   └── resources/
│       │   │       ├── application.properties
│       │   │       └── application-prod.properties
│       │   └── test/
│       ├── Dockerfile
│       └── pom.xml
├── frontend/              # Static HTML/JS/CSS files
│   ├── index.html        # Landing page
│   ├── login.html        # Authentication
│   ├── *_dashboard.html  # Role-specific dashboards
│   ├── auth-client.js    # Authentication utilities
│   └── role-router.js    # Client-side routing
├── database/
│   ├── alertaid_schema.sql    # Database schema
│   └── quick_setup.sql        # Sample data
├── docker-compose.yml     # Docker orchestration
├── .env.example          # Environment template
└── README.md
```

## 🎭 User Roles

The platform supports four user roles:

1. **Citizens**: Report emergencies, request help, donate
2. **Volunteers**: Respond to calls for help, mark availability
3. **Organizations**: Coordinate relief efforts, manage resources
4. **Administrators**: Oversee the entire platform, manage users

## 🔐 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Reports
- `GET /api/reports/public` - Public emergency reports
- `POST /api/reports` - Create new report (authenticated)
- `GET /api/reports/accepted` - Accepted reports

### Real-time Updates
- `GET /api/stream/reports/public` - SSE stream for public reports
- `GET /api/stream/reports/accepted` - SSE stream for accepted reports

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

## 🔒 Security Features

- JWT-based authentication
- Role-based access control (RBAC)
- CORS protection
- SQL injection prevention via JPA
- Password encryption with BCrypt
- Secure session management

## 🌐 Frontend Features

- Responsive design
- Role-based navigation
- Real-time updates via Server-Sent Events
- Interactive dashboards
- Form validation
- Mobile-friendly interface

## 🐳 Docker Deployment

The application includes production-ready Docker configurations:

**PostgreSQL (docker-compose.yml):**
```yaml
# PostgreSQL database with persistent storage
# Spring Boot application container
# Environment variable configuration
# Health checks and restart policies
```

**MySQL (docker-compose.mysql.yml):**
```yaml
# MySQL 8.0 database with persistent storage
# Spring Boot application container
# Environment variable configuration
# Health checks and restart policies
```

## 🔧 Development

### Adding New Features

1. **Backend**: Add controllers, services, and models in respective packages
2. **Frontend**: Create new HTML pages and update role-router.js for navigation
3. **Database**: Use JPA annotations for schema changes (ddl-auto=update)

### Running Tests

```bash
cd backend/Alertaid
./mvnw test
```

### Building for Production

```bash
# Build Docker image
docker build -t alertaid-backend backend/Alertaid/

# Or use Docker Compose
docker-compose build
```

## 🚨 Important Security Notes

1. **Always change the default JWT secret in production**
2. **Use environment variables for sensitive configuration**
3. **Enable HTTPS in production environments**
4. **Regularly update dependencies for security patches**
5. **Use strong database passwords**
6. **Configure firewall rules appropriately**

## 🐛 Troubleshooting

### Common Issues

**Application won't start - Database connection failed**
```bash
# For MySQL (local development)
# Check if MySQL is running:
sudo systemctl status mysql  # Linux
brew services list | grep mysql  # macOS
# Ensure database exists: CREATE DATABASE alertaid_db;

# For PostgreSQL (Docker)
# Check if PostgreSQL container is running:
docker-compose logs db

# Verify environment variables
cat .env
```

**JWT Authentication errors**
```bash
# Ensure APP_JWT_SECRET is set and properly encoded
echo $APP_JWT_SECRET | base64 -d
```

**CORS errors in browser**
```bash
# Check allowed origins in application properties
# Ensure frontend URL is in CORS_ALLOWED_ORIGINS
```

### Logs

```bash
# Application logs
docker-compose logs alertaid-backend

# Database logs
docker-compose logs db

# All services
docker-compose logs
```

## 📊 Monitoring

The application provides several endpoints for monitoring:

- `/actuator/health` - Application health check
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section above
- Review the application logs for error details

## 🗺️ Roadmap

- [ ] Mobile application (React Native/Flutter)
- [ ] Advanced analytics and reporting
- [ ] Integration with weather APIs
- [ ] SMS/Email notification system
- [ ] Multi-language support
- [ ] Advanced volunteer matching algorithms
- [ ] Integration with government emergency systems

---

**Version**: 1.0.0
**Last Updated**: December 2024
