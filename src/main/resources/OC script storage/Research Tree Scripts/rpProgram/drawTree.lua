local component = require("component")
local gpu = component.gpu
local term = require("term")
local event = require("event")
local keyboard = require("keyboard")
local ResearchTree = require("Manager")
local rpc = component.RPComponent
ResearchTree.loadAllNodes(rpc.getNodes())
local screen = component.screen
term.clear()
local unicode = require("unicode")
local colors = require("colors")
local team = rpc.getTeam(6)
local isDisplaying = true

local scrollX = 0
local scrollY = 0

local nodePos = {}

local function drawCircle2(cx, cy, r, color)
    local x = 0
    local y = r
    local aspect = 0.5

    if color == "green" then
        gpu.setForeground(0x00FF00)
    elseif color == "white" then
        gpu.setForeground(0xFFFFFF)
    end

    local p = 1 - r

    while x <= y do
        
        local ay = (y * aspect + 0.5) // 1
        local ax = (x * aspect + 0.5) // 1

        gpu.set(cx + x, cy + ay, "█")
        gpu.set(cx + y, cy + ax, "█")
        gpu.set(cx - y, cy + ax, "█")
        gpu.set(cx - x, cy + ay, "█")
        gpu.set(cx - x, cy - ay, "█")
        gpu.set(cx - y, cy - ax, "█")
        gpu.set(cx + y, cy - ax, "█")
        gpu.set(cx + x, cy - ay, "█")

        x = x + 1

        if p < 0 then
            p = p + 2 * x + 1
        else
            y = y - 1
            p = p + 2 * (x - y) + 1
        end
    end
end

local function drawLine(x1, y1, x2, y2, char)
    local dx = math.abs(x2 - x1)
    local dy = math.abs(y2 - y1)
    local sx = (x1 < x2) and 1 or -1
    local sy = (y1 < y2) and 1 or -1
    local err = dx - dy

    while true do
        gpu.set(x1, y1, char)
        if x1 == x2 and y1 == y2 then break end
        local e2 = err * 2
        if e2 > -dy then
            err = err - dy
            x1 = x1 + sx
        end
        if e2 < dx then
            err = err + dx
            y1 = y1 + sy
        end
    end
end

local function drawTree()
    for IteratorNode in pairs(ResearchTree.nodes) do
        local node = ResearchTree.getNode(IteratorNode)
        local x = math.floor(node.xPos - scrollX)
        local y = math.floor(node.yPos - scrollY)

        if node.teamUnlocked[team] then
            drawCircle2(x, y, 5, "green")
        else
            drawCircle2(x, y, 5, "white")
        end
        local text = "[" .. tostring(node.type) .. "]"
        gpu.set(x - math.floor(#text / 2), y - 5, text)
        gpu.set(x - math.floor(#node.name / 2), y - 6, node.name)

        for _, depId in ipairs(node.dependencies) do
            local depNode = ResearchTree.getCachedNode(depId)
            if depNode then
                local depX = math.floor(depNode.xPos - scrollX)
                local depY = math.floor(depNode.yPos - scrollY)
                drawLine(x, y, depX, depY, "─")
            end
        end

        nodePos[IteratorNode] = { x = x, y = y }
    end
end

local tolerance = 5

local function handleUnlock(selectedNode, plr)

    if selectedNode.teamUnlocked[team] then
        local weee = rpc.giveTemplate(selectedNode.id, plr)
        print(weee)
    else
        print("Unlock? Y/N")
        local input = string.lower(term.read():gsub("\n", ""))
        if input == "y" or input == "yes" then
            local succes, output = selectedNode:unlock(ResearchTree, team)
            ResearchTree.saveAllNodes()
            print(output)
            os.sleep(2)
            term.clear()
            isDisplaying = true
        else
            term.clear()
            isDisplaying = true
        end
    end
end

local function onTouch(_, address, x, y, button, player)
    if not isDisplaying then return end
    --print("Touch at X:", x, "Y:", y, "Button:", button, "By:", player)
    for node, pos in pairs(nodePos) do
        local xPos, yPos = pos.x, pos.y
        if math.abs(xPos - x) <= tolerance and math.abs(yPos - y) <= tolerance then
            local displayNode = ResearchTree.getCachedNode(node)
            isDisplaying = false
            term.clear()
            displayNode:display()
            handleUnlock(displayNode, player)
        end
    end
end

event.listen("touch", onTouch)

while isDisplaying == true do
    local scrollSpeedX = 6
    local scrollSpeedY = 3

    if keyboard.isKeyDown(keyboard.keys.up) then
        scrollY = scrollY - scrollSpeedY
        term.clear()
    end
    if keyboard.isKeyDown(keyboard.keys.down) then
        scrollY = scrollY + scrollSpeedY
        term.clear()
    end
    if keyboard.isKeyDown(keyboard.keys.left) then
        scrollX = scrollX - scrollSpeedX
        term.clear()
    end
    if keyboard.isKeyDown(keyboard.keys.right) then
        scrollX = scrollX + scrollSpeedX
        term.clear()
    end
    if keyboard.isKeyDown(keyboard.keys.q) then
        event.ignore("touch", onTouch)
        term.clear()
        isDisplaying = false
        os.sleep(1)
        term.clear()
    end
    if not isDisplaying then return end
    drawTree()
    os.sleep(0.5)
end