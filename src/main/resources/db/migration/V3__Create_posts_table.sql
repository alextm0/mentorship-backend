CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       content TEXT,
                       user_id INTEGER NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);