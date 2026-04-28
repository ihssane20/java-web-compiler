package com.github.forax.javawebcompiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class ApplicationTest {
  @Test
  public void compileValidCode() {
    var code = """
      public class Main {
        public static void main(String[] args) {
          System.out.println("Hello");
        }
      }
      """;
    var result = Compiler.compileInMemory("Main", code, new MemoryClassLoader());
    assertTrue(result.isEmpty());
  }

  @Test
  public void diagnosticContainsLineAndColumn() {
    var source = """
      public class Main {
        public static void main(String[] args) {
          int x = "ksdjfj";
        }
      }
    """;
    var diagnostics = Compiler.compileInMemory("Main", source, new MemoryClassLoader());
    assertFalse(diagnostics.isEmpty());
    var first = diagnostics.getFirst();
    assertTrue(first.line() > 0);
    assertTrue(first.column() > 0);
    assertNotNull(first.message());
    assertFalse(first.message().isEmpty());
  }

    @Test
    public void runHelloWorld() throws Exception {
      var code = """
        public class Main {
          public static void main(String[] args) {
            System.out.println("Hello");
          }
        }
      """;
      var loader = new MemoryClassLoader();
      Compiler.compileInMemory("Main", code, loader);
      var output = Runner.runFromMemory("Main", loader);
      assertEquals("Hello\n", output);
    }

    @Test
    public void runWithNoCompiledCode() {
      var loader = new MemoryClassLoader();
      assertThrows(ClassNotFoundException.class, () -> Runner.runFromMemory("Main", loader));
    }

    @Test
    public void runMultipleLines() throws Exception {
      var code = """
        public class Main {
          public static void main(String[] args) {
          System.out.println("line1");
          System.out.println("line2");
          }
        }
      """;
      var loader = new MemoryClassLoader();
      Compiler.compileInMemory("Main", code, loader);
      var output = Runner.runFromMemory("Main", loader);
      assertEquals("line1\nline2\n", output);
    }

    @Test
    public void runEmptyOutput() throws Exception {
      var code = """
        public class Main {
          public static void main(String[] args) {}
        }
      """;
      var loader = new MemoryClassLoader();
      Compiler.compileInMemory("Main", code, loader);
      var output = Runner.runFromMemory("Main", loader);
      assertEquals("", output);
    }
}
