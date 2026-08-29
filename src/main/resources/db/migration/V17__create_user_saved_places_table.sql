-- Create join table for user saved places (many-to-many relationship)
CREATE TABLE IF NOT EXISTS registration.user_saved_places (
    user_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, place_id),
    FOREIGN KEY (user_id) REFERENCES registration.user(id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES registration.place(id) ON DELETE CASCADE
);

-- Create indexes for faster lookups
CREATE INDEX idx_user_saved_places_user_id ON registration.user_saved_places(user_id);
CREATE INDEX idx_user_saved_places_place_id ON registration.user_saved_places(place_id);
