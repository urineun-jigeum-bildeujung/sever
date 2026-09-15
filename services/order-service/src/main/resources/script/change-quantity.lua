-- change-quantity.lua
-- KEYS[1] = cart key, ARGV[1] = field, ARGV[2] = delta, ARGV[3] = 최소(1), ARGV[4] = 최대(99), ARGV[5] = TTL(초)
local current = redis.call('HGET', KEYS[1], ARGV[1])
if current == false then
    return -1
end

local newValue = tonumber(current) + tonumber(ARGV[2])
local min = tonumber(ARGV[3])
local max = tonumber(ARGV[4])
if newValue < min then
    newValue = min
elseif newValue > max then
    newValue = max
end

redis.call('HSET', KEYS[1], ARGV[1], newValue)
redis.call('EXPIRE', KEYS[1], ARGV[5])
return newValue