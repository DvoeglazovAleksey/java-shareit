# Сервис аренды вещей
## Описание проекта
Это сервис, который позволяет пользователям арендовать и сдавать в аренду различные вещи, такие как книги, игры, одежда и т.д. Пользователи могут создавать объявления о своих вещах, просматривать объявления других пользователей, отправлять и принимать заявки на аренду, оценивать и комментировать арендованные вещи. Аренда вещей имеет следующие атрибуты: id, itemId, userId, startDate, endDate, price, status. Пользователи имеют следующие атрибуты: id, name, email, password.

## Технологии, которые использовались
- Java 11
- Spring Boot
- PostgreSQL
- Lombok
- JUnit

## Архитектура сервиса
Сервис состоит из двух модулей: gateway и server. Gateway - это модуль для приема запросов от пользователей и отправки ответа сервера. Он также выполняет функции аутентификации и валидации запросов. Server - это модуль для обработки запросов пользователей и взаимодействия с базой данных PostgreSQL. Он использует Spring Boot для создания RESTful API и JPA для работы с данными. Для упрощения кода используется Lombok. Для тестирования используется JUnit.

## Эндпоинты сервиса
Сервис может обрабатывать следующие эндпоинты:

- /bookings — для работы с арендой вещей
    - POST /bookings — создает новую заявку на аренду из тела запроса
    - PATCH /bookings/{bookingId} — обновляет статус заявки на аренду по id
    - GET /bookings — возвращает список всех заявок на аренду пользователя
    - GET /bookings/{bookingId} — возвращает заявку на аренду по id
    - GET /bookings/owner — возвращает список всех заявок на аренду по вещам пользователя
- /items — для работы с вещами
    - GET /items/{id} — возвращает вещь по id
    - GET /items — возвращает список всех вещей
    - POST /items — создает новую вещь из тела запроса
    - PATCH /items/{id} — обновляет существующую вещь из тела запроса
    - GET /items/search — возвращает список вещей по поисковому запросу
    - POST /items/{itemId}/comment — создает новый комментарий к вещи из тела запроса
- /requests — для работы с заявками на аренду вещей
    - GET /requests/all — возвращает отсортированный список всех заявок на аренду
    - GET /requests/{requestId} — возвращает заявку на аренду по id
    - GET /requests — возвращает список всех заявок на аренду пользователя
    - POST /requests — создает новую заявку на аренду из тела запроса
- /users — для работы с пользователями
    - GET /users/{id} — возвращает пользователя по id
    - GET /users — возвращает список всех пользователей
    - POST /users — создает нового пользователя из тела запроса
    - PATCH /users/{id} — обновляет существующего пользователя из тела запроса
    - DELETE /users/{id} — удаляет пользователя по id
## Лицензия
Этот сервис распространяется под лицензией MIT. Вы можете свободно использовать, изменять и распространять этот сервис, при условии, что вы указываете авторство и сохраняете эту лицензию.
