-- 1.0
GRANT SELECT, UPDATE ON users_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON users TO @DB_USERNAME@;
GRANT SELECT, UPDATE ON roles_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON roles TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON user_role_link TO @DB_USERNAME@;
GRANT SELECT, UPDATE ON articles_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON articles TO @DB_USERNAME@;
GRANT SELECT, UPDATE ON tags_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON tags TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON article_tag_link TO @DB_USERNAME@;

-- 1.2
GRANT SELECT, UPDATE ON comments_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON comments TO @DB_USERNAME@;

-- 2.0
GRANT SELECT, UPDATE ON cardspace_tokens_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON cardspace_tokens TO @DB_USERNAME@;

GRANT SELECT, UPDATE ON cardspace_seen_tokens_seq TO @DB_USERNAME@;
GRANT SELECT, INSERT, UPDATE, DELETE ON cardspace_seen_tokens TO @DB_USERNAME@;
