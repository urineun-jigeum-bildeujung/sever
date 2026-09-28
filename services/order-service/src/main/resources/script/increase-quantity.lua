-- increase-quantity.lua
-- 저장 값 형식: "{quantity}:{addedAtEpochMillis}"  예) "3:1758990000123"
-- 구버전 값("3", 수량만 있음)도 읽을 수 있으며 이 경우 addedAt은 '0'으로 취급한다.
-- KEYS[1] = cart key
-- ARGV[1] = field(itemType:itemId), ARGV[2] = 더할 수량, ARGV[3] = 최대 수량(99), ARGV[4] = TTL(초)

local raw = redis.call('HGET', KEYS[1], ARGV[1])
local quantity = 0
local addedAt

if raw then
    local q, ts = string.match(raw, '^(%d+):(%d+)$')
    if q then
        quantity = tonumber(q)
        addedAt = ts
    else
        quantity = tonumber(raw) or 0
        addedAt = '0'
    end
else
    local t = redis.call('TIME')
    addedAt = string.format('%.0f', tonumber(t[1]) * 1000 + math.floor(tonumber(t[2]) / 1000))
end

local newQuantity = quantity + tonumber(ARGV[2])
local max = tonumber(ARGV[3])
if newQuantity > max then
    newQuantity = max
end

redis.call('HSET', KEYS[1], ARGV[1], string.format('%d', newQuantity) .. ':' .. addedAt)
redis.call('EXPIRE', KEYS[1], ARGV[4])
return newQuantity