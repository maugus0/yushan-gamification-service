-- Insert initial achievements data
INSERT INTO achievements (id, name, description, criteria_json, icon_url) VALUES
('WELCOME_TO_YUSHAN', 'Welcome to Yushan', 'Welcome to the Yushan platform! Complete your first login to unlock this achievement.', '{"type": "login", "count": 1}', 'https://example.com/icons/welcome.png'),
('FIRST_CRY', 'First Comment', 'Share your first thoughts by posting a comment.', '{"type": "comment", "count": 1}', 'https://example.com/icons/first-comment.png'),
('ELOQUENT_SPEAKER', 'Comment Enthusiast', 'You are an active participant! Post 10 comments.', '{"type": "comment", "count": 10}', 'https://example.com/icons/comment-enthusiast.png'),
('COMMENT_MASTER', 'Comment Master', 'You are a true comment master! Post 50 comments.', '{"type": "comment", "count": 50}', 'https://example.com/icons/comment-master.png'),
('REVIEW_ROOKIE', 'First Review', 'Share your first review to help others make informed decisions.', '{"type": "review", "count": 1}', 'https://example.com/icons/first-review.png');
