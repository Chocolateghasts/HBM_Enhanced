local c = require("component")
local rpc = c.RPComponent
local ResearchNode = require("node")

local ResearchTree = {
    nodes = {}
}



function ResearchTree.loadAllNodes(list)
    for _, javaNode in ipairs(list) do
        local fullNodeData = rpc.getNode(javaNode.id)
        if fullNodeData then
            local node = ResearchNode.fromJava(fullNodeData)
            ResearchTree.nodes[node.id] = node
        end
    end
end

function ResearchTree.getNode(id)
    local javaNode = rpc.getNode(id)
    if javaNode then
        local node = ResearchNode.fromJava(javaNode)
        ResearchTree.nodes[id] = node
        return node
    end
    return nil
end
function ResearchTree.getCachedNode(id)
    local node = ResearchTree.nodes[id]
    return node
end
function ResearchTree.saveAllNodes()
    local nodesToSave = {}
    
    for id, node in pairs(ResearchTree.nodes) do
        local deps = {}
        if type(node.dependencies) == "table" then
            for i, dep in ipairs(node.dependencies) do
                deps[tostring(i-1)] = tostring(dep)
            end
        end

        local reqs = {}
        if type(node.requirements) == "table" then
            for k, v in pairs(node.requirements) do
                reqs[tostring(k)] = tonumber(v) or 0
            end
        end

        local theTemplates = {}
        for templateType, templateId in pairs(node.templates) do
            table.insert(theTemplates, {
                type = tostring(templateType),
                id = tonumber(templateId)
            })
        end
        
        local teamUnlocked = {}
        if type(node.teamUnlocked) == "table" then
            for team, status in pairs(node.teamUnlocked) do
                teamUnlocked[tostring(team)] = status == true
            end
        end

        nodesToSave[tostring(id)] = {
            name = tostring(node.name or ""),
            id = tostring(node.id or ""),
            category = tostring(node.category or ""),
            description = tostring(node.description or ""),
            level = tonumber(node.level) or 0,
            unlocked = node.unlocked == true,
            dependencies = deps,
            requirements = reqs,
            teamUnlocked = teamUnlocked,
            xPos = tonumber(node.xPos) or 0.0,
            yPos = tonumber(node.yPos) or 0.0,
            type = node.type,
            templates = theTemplates
        }
    end

    local success, result = rpc.saveNodes(nodesToSave)
    return success
end
return ResearchTree