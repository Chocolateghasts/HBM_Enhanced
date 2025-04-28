local c = require("component")
local rpc = c.RPComponent

local ResearchNode = {}
ResearchNode.__index = ResearchNode

function ResearchNode.fromJava(javaNode)
    if not javaNode then return nil end
    local self = setmetatable({}, ResearchNode)
    self.name = javaNode.name
    self.id = javaNode.id
    self.category = javaNode.category
    self.description = javaNode.description
    self.level = javaNode.level
    self.unlocked = javaNode.unlocked
    self.dependencies = javaNode.dependencies or {}
    self.requirements = javaNode.requirements or {}
    self.templateId = javaNode.templateId
    self.teamUnlocked = {}
    if javaNode.teamUnlocked then
        for team, status in pairs(javaNode.teamUnlocked) do
            self.teamUnlocked[team] = status
        end
    end
    return self
end

function ResearchNode:display()
    print("Research Node: " .. self.name)
    print("ID: " .. self.id)
    print("Category: " .. self.category)
    print("Description: " .. self.description)
    print("Level: " .. tostring(self.level))
    print("Unlocked: " .. tostring(self.unlocked))
    print("Dependencies:")
    for _, dep in ipairs(self.dependencies) do
        print("  - " .. dep)
    end
    print("Requirements:")
    for reqType, points in pairs(self.requirements) do
        print("  - " .. reqType .. ": " .. points)
    end
    print("Template ID: " .. tostring(self.templateId))
    print("Team Unlock Status:")
    local teamCount = 0
    for team, status in pairs(self.teamUnlocked) do
        teamCount = teamCount + 1
        print("  - " .. team .. ": " .. tostring(status))
    end
    if teamCount == 0 then
        print("  No teams unlocked yet")
    end
end

function ResearchNode:unlock(ResearchTree, teamName)
    if not teamName then return end
    if self.teamUnlocked[teamName] then return end

    local succes, rpData = rpc.getRp(teamName)

    if not succes then 
        print("Diddnt workedst")
        return
    end
    for entry, points in pairs(self.requirements) do
        print(string.format("Required: %-15s %5d  Total: %5d", entry, points, rpData[entry]))
        if not (rpData[entry] - points >= 0)  then
            print("Not enough points for ", entry)
            return
        end
    end
    
    print("Trying to unlock for team: " .. teamName)
    for _, nodeId in ipairs(self.dependencies) do
        local dep = ResearchTree.getNode(nodeId)
        if not (dep and dep.teamUnlocked[teamName]) then
            print(dep, "Not Unlocked")
            return
        end
    end
    print("Unlocking node for team: " .. teamName)
    self.teamUnlocked[teamName] = true
    ResearchTree.nodes[self.id] = self
end



return ResearchNode