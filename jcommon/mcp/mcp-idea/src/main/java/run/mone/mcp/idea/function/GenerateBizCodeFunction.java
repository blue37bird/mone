package run.mone.mcp.idea.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class GenerateBizCodeFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    public GenerateBizCodeFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "generateBizCode";

    private String desc = "根据需求，生成业务代码";
    private String ideaPort;

    private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "requirement": {
                            "type": "string",
                            "description":"需求描述，用户输入什么就传什么，不要有任何更改，否则会有不好的事情发生"
                        },
                        "fileLists": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            },
                            "description":"文件列表，根据需求分析出来要操作的文件数组"
                        },
                        "projectName": {
                            "type": "string",
                            "description":"需要操作的项目，你不应该假设项目名称，如果不知道填什么，请询问用户，否则会有不好的事情发生!"
                        }
                    },
                    "required": ["requirement","projectName"]
                }
                """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "writeCodeMethod");
        req.addProperty("from", "idea_mcp");
        req.addProperty("requirement", (String) arguments.get("requirement"));
        req.addProperty("projectName", (String) arguments.get("projectName"));
        req.addProperty("athenaPluginHost", "127.0.0.1:" + ideaPort);
        JsonObject res = IdeaFunctions.callAthena(ideaPort, req);
        if (res.get("code") != null && res.get("code").getAsInt() == 0) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("已完成")), false);
        }
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), true);
    }
}
