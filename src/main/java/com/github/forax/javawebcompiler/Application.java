package com.github.forax.javawebcompiler;

import module java.base;
import module java.compiler;

import tools.jackson.databind.ObjectMapper;

import javax.tools.ToolProvider;

public class Application {

  private record CompileRequest(String code) {
    private CompileRequest {
      Objects.requireNonNull(code);
    }
  }

  private record SourceFile(String className, String sourceCode) {}

  private static final Pattern CLASSNAME_PATTERN = Pattern.compile("class\\s+(\\w+)");

  // Dynamic class name extraction
  private static String classNameExtractor(String code) {
    var m = CLASSNAME_PATTERN.matcher(code);
    return m.find() ? m.group(1) : "Main";
  }

  static void main(String[] args) {
    var app = JExpress.express();

    app.use(JExpress.staticFiles(Path.of("public")));

    var objectMapper = new ObjectMapper();

    app.post("/compile", (req, res) -> {
      try {
        var body = req.bodyText();
        var compileRequest = objectMapper.readValue(body, CompileRequest.class);

        var sourceCode = compileRequest.code();

        var className = classNameExtractor(sourceCode);

        var sourceFile = new SourceFile(className, sourceCode);

        var diagnostics = Compiler.compileInMemory(
                sourceFile.className(),
                sourceFile.sourceCode()
        );

        res.send(objectMapper.writeValueAsString(diagnostics));

      } catch (Exception e) {
        res.status(500).json("""
            {"error": "Internal Server Error"}
            """);
      }
    });

    app.listen(8080);
    System.out.println("Web site on http://localhost:8080/index.html");
  }
}