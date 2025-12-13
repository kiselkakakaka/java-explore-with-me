INSERT INTO categories (name) VALUES
    ('Концерты'),
    ('Выставки'),
    ('Фестивали');

INSERT INTO users (name, email) VALUES
    ('Иван Петров', 'ivan.petrov@example.com'),
    ('Анна Смирнова', 'anna.smirnova@example.com'),
    ('Матвей Овчаров', 'matvey.ovcharov@example.com');

INSERT INTO events (
    annotation,
    description,
    category_id,
    created_on,
    event_date,
    initiator_id,
    loc_lat,
    loc_lon,
    paid,
    participant_limit,
    published_on,
    request_moderation,
    state,
    title
) VALUES
(
    'Большой рок-концерт',
    'Громкая музыка, живой звук, любимые группы.',
    1,
    NOW() - INTERVAL '2 days',
    NOW() + INTERVAL '5 days',
    1,
    55.7522,
    37.6156,
    FALSE,
    1000,
    NOW() - INTERVAL '1 days',
    TRUE,
    'PUBLISHED',
    'Рок ночь в Москве'
),
(
    'Выставка современного искусства',
    'Инсталляции и картины молодых художников.',
    2,
    NOW() - INTERVAL '1 days',
    NOW() + INTERVAL '10 days',
    2,
    59.9343,
    30.3351,
    TRUE,
    200,
    NULL,
    TRUE,
    'PENDING',
    'Современное искусство Петербурга'
);

INSERT INTO requests (created, event_id, requester_id, status) VALUES
    (NOW() - INTERVAL '12 hours', 1, 2, 'CONFIRMED'),
    (NOW() - INTERVAL '10 hours', 1, 3, 'PENDING');

INSERT INTO compilations (pinned, title) VALUES
    (TRUE,  'Главные события недели'),
    (FALSE, 'Новые события');

INSERT INTO compilation_events (compilation_id, event_id) VALUES
    (1, 1),
    (1, 2),
    (2, 2);