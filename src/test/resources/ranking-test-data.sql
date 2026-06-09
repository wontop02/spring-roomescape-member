INSERT INTO theme (id, name, description, thumbnail_url)
VALUES (1, '우테코 방탈출', '재미있는 방탈출', 'thumb.png');

INSERT INTO reservation_time (id, start_at)
VALUES (1, '10:00');

INSERT INTO reservation (id, name, date, time_id, theme_id)
VALUES (1, '왕국_01', '2026-04-29', 1, 1),
       (2, '왕국_02', '2026-05-01', 1, 1);
