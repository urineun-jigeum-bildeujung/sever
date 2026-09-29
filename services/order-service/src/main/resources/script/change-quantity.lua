-- change-quantity.lua
-- 저장 값 형식: "{quantity}:{addedAtEpochMillis}" (구버전 "{quantity}"도 허용)
-- KEYS[1] = cart key
-- ARGV[1] = field, ARGV[2] = delta, ARGV[3] = 최소(1), ARGV[4] = 최대(99), ARGV[5] = TTL(초)

local raw = redis.call('HGET', KEYS[1], ARGV[1])
if not raw then
    return -1
end

local q, ts = string.match(raw, '^(%d+):(%d+)$')
local quantity, addedAt
if q then
    quantity = tonumber(q)
    addedAt = ts
else
    quantity = tonumber(raw) or 0
    addedAt = '0'
end

local newQuantity = quantity + tonumber(ARGV[2])
local min = tonumber(ARGV[3])
local max = tonumber(ARGV[4])
if newQuantity < min then
    newQuantity = min
elseif newQuantity > max then
    newQuantity = max
end

redis.call('HSET', KEYS[1], ARGV[1], string.format('%d', newQuantity) .. ':' .. addedAt)
redis.call('EXPIRE', KEYS[1], ARGV[5])
return newQuantity