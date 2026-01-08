DELETE FROM votes;
DELETE FROM menu_items;
DELETE FROM restaurants;
DELETE FROM votes;
DELETE FROM user_role;
DELETE FROM users;

INSERT INTO users (name, email, password, registered)
VALUES ('User', 'user@yandex.ru', '{noop}password', NOW()),
       ('Admin', 'admin@gmail.com', '{noop}admin', NOW());

INSERT INTO user_role (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2);

INSERT INTO restaurants (name, address)
VALUES ('Burger King', '123456 Moscow City'),
       ('McDonalds', '123456 Moscow, Red Square'),
       ('KFC', '123456 Moscow, Paveletskaya');

INSERT INTO menu_items (restaurant_id, name, menu_date, description, price)
VALUES (1, 'Whopper', CURRENT_DATE, 'Big Burger', 500),
       (1, 'Cheeseburger', CURRENT_DATE, 'Cheese Burger', 300),
       (1, 'Fries', CURRENT_DATE, 'French Fries', 200),
       (2, 'Big Mac', CURRENT_DATE, 'Big Mac Burger', 550),
       (2, 'McChicken', CURRENT_DATE, 'Chicken Burger', 450),
       (2, 'Nuggets', CURRENT_DATE, 'Chicken Nuggets', 350),
       (3, 'Original Recipe', CURRENT_DATE, 'Fried Chicken', 600),
       (3, 'Zinger', CURRENT_DATE, 'Spicy Chicken', 650),
       (3, 'Twister', CURRENT_DATE, 'Chicken Wrap', 500),

       -- Yesterday's menu
       (1, 'Whopper', CURRENT_DATE - 1, 'Big Burger', 500),
       (1, 'Cheeseburger', CURRENT_DATE - 1, 'Cheese Burger', 300),
       (2, 'Big Mac', CURRENT_DATE - 1, 'Big Mac Burger', 550),
       (2, 'McChicken', CURRENT_DATE - 1, 'Chicken Burger', 450),
       (3, 'Original Recipe', CURRENT_DATE - 1, 'Fried Chicken', 600),
       (3, 'Zinger', CURRENT_DATE - 1, 'Spicy Chicken', 650);

INSERT INTO votes (user_id, restaurant_id, vote_date)
VALUES (1, 1, CURRENT_DATE),
       (2, 2, CURRENT_DATE);