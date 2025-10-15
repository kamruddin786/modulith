CREATE TABLE if not exists products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if not exists orders (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    order_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

-- Spring Modulith event publication table
CREATE TABLE if not exists event_publication (
    id UUID PRIMARY KEY,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date TIMESTAMP WITH TIME ZONE,
    event_type VARCHAR(255) NOT NULL,
    serialized_event TEXT NOT NULL,
    listener_id VARCHAR(255) NOT NULL
);

-- Spring Integration distributed lock table
CREATE TABLE if not exists "INT_LOCK" (
    LOCK_KEY VARCHAR(36) NOT NULL PRIMARY KEY,
    REGION VARCHAR(100) NOT NULL,
    CLIENT_ID VARCHAR(36),
    CREATED_DATE TIMESTAMP WITH TIME ZONE NOT NULL
);