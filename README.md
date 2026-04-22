# Golden Horse — VIP Тотализатор

**Golden Horse** — это современное backend-приложение для системы онлайн-тотализатора на виртуальные конные забеги. Проект реализует логику динамических игровых комнат, управления балансом в реальном времени и автоматического расчета выигрышей.

Приложение построено на событийной архитектуре с использованием WebSocket, что обеспечивает мгновенный отклик и живое взаимодействие без перезагрузки страниц.

---

## Особенности

* **Система комнат:** Динамическое создание забегов с гибкой настройкой: цена входа, лимит мест (до 10), процент призового фонда и время таймера.
* **Продвинутая фильтрация:** Мощный поиск комнат через **JPA Specification API** по диапазону цен, количеству мест и проценту отдачи (RTP).
* **Финансовая безопасность:** Механика двухэтапного списания (`reservedBalance`) исключает "двойные траты" и обеспечивает корректность выплат при любых сценариях.
* **Real-time Engine:** Интеграция с **WebSocket (STOMP)** для мгновенной трансляции старта, финиша и результатов всем участникам.
* **Автоматизация:** Интеллектуальный планировщик событий (`@Scheduled`), работающий с точностью до секунды.
* **Механика "Буст":** Уникальный алгоритм повышения вероятности победы (множитель шанса) за дополнительные игровые ресурсы.

---

## Технологический стек

* **Core:** Java 17 / Spring Boot 3.x
* **Database:** PostgreSQL (Spring Data JPA + Specification API)
* **Messaging:** Spring WebSocket (STOMP + SockJS)
* **Safety:** Jakarta Persistence & Lombok
* **Frontend:** HTML5, Bootstrap 5, JS (Async/Await API)

---

## Структура проекта

```text
src/main/java/com/example/backendapp/
├── controllers/    # REST Эндпоинты (Game API, Admin API)
├── models/         # Сущности БД (User, Room, Participation)
├── repositories/   # Интерфейсы доступа к данным (+ Query Specifications)
├── services/       # Бизнес-логика, Расчеты и Scheduler
└── BackendAppApplication.java # Точка входа в приложение
```

## Установка и запуск

### Локальный запуск
1. **Клонируйте репозиторий:**
   ```bash
   git clone https://github.com/SkeletonGuns/golden-horse.git
   ```
2. **Настройте application.properties:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/horse_db
   spring.datasource.username=your_user
   spring.datasource.password=your_password
   ```
   
3. **Сборка и запуск:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
