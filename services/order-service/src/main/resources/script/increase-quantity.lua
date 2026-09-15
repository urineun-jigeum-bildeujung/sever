-- increase-quantity.lua
-- KEYS[1] = cart key, ARGV[1] = field, ARGV[2] = 더할 수량, ARGV[3] = 최대치(99), ARGV[4] = TTL(초)
local current = tonumber(redis.call('HGET', KEYS[1], ARGV[1]))
if current == nil then
    current = 0
end

local newValue = current + tonumber(ARGV[2])
local max = tonumber(ARGV[3])
if newValue > max then
    newValue = max
end

redis.call('HSET', KEYS[1], ARGV[1], newValue)
redis.call('EXPIRE', KEYS[1], ARGV[4])
return newValue