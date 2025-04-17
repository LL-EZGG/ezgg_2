INSERT INTO match (riot_match_id, win, kills, deaths, assists, team_position, champion_name)
VALUES ('KR_1234567890', TRUE, 8, 2, 15, 'MID', 'Ahri'),
       ('KR_2345678901', FALSE, 3, 8, 5, 'TOP', 'Garen'),
       ('KR_3456789012', TRUE, 12, 4, 7, 'JUNGLE', 'Lee Sin'),
       ('KR_4567890123', FALSE, 5, 7, 10, 'ADC', 'Jinx'),
       ('KR_5678901234', TRUE, 2, 1, 18, 'SUPPORT', 'Lulu'),
       ('KR_6789012345', FALSE, 10, 5, 8, 'MID', 'Zed'),
       ('KR_7890123456', TRUE, 7, 3, 12, 'TOP', 'Darius'),
       ('KR_8901234567', FALSE, 4, 9, 6, 'JUNGLE', 'Amumu'),
       ('KR_9012345678', TRUE, 9, 2, 14, 'ADC', 'KaiSa'),
       ('KR_0123456789', FALSE, 6, 6, 9, 'SUPPORT', 'Thresh');

INSERT INTO recent_twenty_match (recent_riot_match_id, recent_kills, recent_deaths, recent_assists, champion_stats)
VALUES ('RECENT_KR_1111', 30, 10, 20, '{"ahri": {"championName": "ahri", "kills": 30, "deaths": 10, "assists": 20}}'),
       ('RECENT_KR_2222', 15, 5, 25, '{"yasuo": {"championName": "yasuo", "kills": 25, "deaths": 8, "assists": 15}}'),
       ('RECENT_KR_3333', 40, 15, 30, '{"leesin": {"championName": "Lee Sin", "kills": 18, "deaths": 12, "assists": 22}}'),
       ('RECENT_KR_4444', 22, 7, 18, '{"jinx": {"championName": "Jinx", "kills": 32, "deaths": 5, "assists": 28}}'),
       ('RECENT_KR_5555', 28, 12, 35, '{"darius": {"championName": "Darius", "kills": 20, "deaths": 10, "assists": 15}, "thresh": {"championName": "Thresh", "kills": 8, "deaths": 3, "assists": 40}}'),
       ('RECENT_KR_6666', 17, 9, 24, '{"lux": {"championName": "Lux", "kills": 45, "deaths": 2, "assists": 12}}'),
       ('RECENT_KR_7777', 33, 14, 27, '{"kaisa": {"championName": "Kai''Sa", "kills": 27, "deaths": 7, "assists": 19}}'),
       ('RECENT_KR_8888', 19, 4, 31, '{"amumu": {"championName": "Amumu", "kills": 12, "deaths": 15, "assists": 35}}'),
       ('RECENT_KR_9999', 25, 11, 29, '{"zed": {"championName": "Zed", "kills": 38, "deaths": 9, "assists": 5}, "garen": {"championName": "Garen", "kills": 15, "deaths": 12, "assists": 20}}'),
       ('RECENT_KR_0000', 14, 6, 23, '{"lulu": {"championName": "Lulu", "kills": 5, "deaths": 3, "assists": 45}}');
