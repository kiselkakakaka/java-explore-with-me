# 🎟 Explore With Me

Афиша событий: пользователи публикуют события, другие подают заявки на участие, администратор модерирует контент и категории. Отдельный сервис собирает статистику просмотров и подсказывает, что популярно.

> Проект курса «Java-разработчик» Яндекс.Практикума.

## Стек

![Java](https://img.shields.io/badge/Java-21-6DB33F?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=flat&logo=spring&logoColor=white)
![JPA / Hibernate](https://img.shields.io/badge/Spring%20Data%20JPA-Hibernate-green?style=flat)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED?style=flat&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-multimodule-C71A36?style=flat&logo=apachemaven&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?style=flat&logo=githubactions&logoColor=white)

## Архитектура

Maven-проект из четырёх модулей:

| Модуль | Назначение |
|---|---|
| `ewm-main-service` | основной сервис: события, категории, подборки, комментарии, заявки на участие |
| `ewm-stats-server` | сервис статистики: принимает и хранит просмотры, отдаёт агрегаты |
| `ewm-stats-client` | HTTP-клиент для обращения основного сервиса к статистике |
| `ewm-stats-dto` | общие DTO для обмена между сервисами |

API делится на три уровня доступа: **admin**, **public**, **private** (личные кабинеты пользователей). Спецификация основного сервиса — [`ewm-main-service-spec.json`](ewm-main-service-spec.json) (OpenAPI).

## Возможности

- Публикация и модерация событий (черновик → публикация → отмена)
- Заявки на участие с подтверждением/отклонением организатором
- Категории, подборки событий, комментарии
- Учёт просмотров через отдельный сервис статистики
- Выборка событий с фильтрацией, сортировкой и пагинацией

## Запуск

```bash
docker compose up --build
# основной сервис: http://localhost:8080
# сервис статистики: http://localhost:9090
```

## Тесты и CI

На pull request запускается reusable workflow: поднимается окружение и гоняются Postman-коллекции (`postman/feature.json`, `postman/sprint.json`) через Newman, плюс статическая проверка Checkstyle.
