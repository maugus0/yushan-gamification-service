CREATE TABLE exp_transactions (
                                  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                  user_id UUID NOT NULL,
                                  amount DOUBLE PRECISION NOT NULL,
                                  reason VARCHAR(255),
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE yuan_transactions (
                                   id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                   user_id UUID NOT NULL,
                                   amount DOUBLE PRECISION NOT NULL,
                                   description TEXT,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE daily_reward_log (
                                  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                  user_id UUID NOT NULL UNIQUE,
                                  last_reward_date DATE NOT NULL,
                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE achievements (
                              id VARCHAR(100) PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              description TEXT,
                              criteria_json JSONB,
                              icon_url TEXT
);

CREATE TABLE user_achievements (
                                   id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                   user_id UUID NOT NULL,
                                   achievement_id VARCHAR(100) NOT NULL REFERENCES achievements(id),
                                   unlocked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE(user_id, achievement_id)
);

CREATE INDEX idx_exp_transactions_user_id ON exp_transactions(user_id);
CREATE INDEX idx_yuan_transactions_user_id ON yuan_transactions(user_id);
CREATE INDEX idx_user_achievements_user_id ON user_achievements(user_id);