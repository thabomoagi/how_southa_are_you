ALTER TABLE thirty_seconds_games
ADD COLUMN winning_player_name VARCHAR(100);

ALTER TABLE thirty_seconds_rounds
ADD COLUMN player_name VARCHAR(100);