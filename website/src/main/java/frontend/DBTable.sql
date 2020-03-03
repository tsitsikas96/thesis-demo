SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

CREATE TABLE orders (
  id SERIAL PRIMARY KEY NOT NULL,
  head_cushion boolean NOT NULL,
  quantity int NOT NULL,
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)

-- events sql --
CREATE OR REPLACE FUNCTION queue_event() RETURNS TRIGGER AS $$

DECLARE
data json;
notification json;

BEGIN

-- Convert the old or new row to JSON, based on the kind of action.
-- Action = DELETE? -&gt; OLD row
-- Action = INSERT or UPDATE? -&gt; NEW row
IF (TG_OP = 'DELETE') THEN
data = row_to_json(OLD);
ELSE
data = row_to_json(NEW);
END IF;

-- Contruct the notification as a JSON string.
notification = json_build_object(
'table',TG_TABLE_NAME,
'action', TG_OP,
'data', data);

-- Execute pg_notify(channel, notification)
PERFORM pg_notify('q_event',notification::text);

-- Result is ignored since this is an AFTER trigger
RETURN NULL;
END;

$$ LANGUAGE plpgsql

CREATE TRIGGER queue_notify_event
AFTER INSERT ON orders
FOR EACH ROW EXECUTE PROCEDURE queue_event();

DELETE FROM orders WHERE id in (SELECT id from orders ORDER BY time ASC LIMIT 1);