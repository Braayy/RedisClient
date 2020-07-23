# RedisClient
A Simple Redis Client

```Java
RedisClient redis = new RedisClient("localhost", 6379);
redis.connect();

// Sets the "key" to "value"
redis.set("key", "value"); 

// Returns the "key"'s value
String value = redis.get("key");

// Pushes values to a list
// Returns the current size of the list
int size = redis.lpush("key", "values", "values");

// Returns a list with all ranged elements
// start = 0, end = -1 to get all elements
List<String> list = redis.lrange("key", start, end);

// Pops the first element of the list
// Returns the poped value
String popedValue = redis.lpop("key");

// Returns the size of the element
int size = redis.llen("key");

// Close the socket connection
redis.close();
```