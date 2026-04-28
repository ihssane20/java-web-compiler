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
    public void compileWithDifferentClassName() {
        var code = """
      public class Test {
        public static void main(String[] args) {
        }
      }
      """;

        var diagnostics = Compiler.compileInMemory("Test", code);

        assertTrue(diagnostics.isEmpty());
    }

    @Test
    public void compileWithWrongClassNameShouldFail() {
        var code = """
      public class Test {
      }
      """;

        var diagnostics = Compiler.compileInMemory("Main", code);

        assertFalse(diagnostics.isEmpty());
    }

    @Test
    public void compileClassWithoutMainShouldStillCompile() {
        var code = """
        public class Test {
            int x = 10;
        }
        """;

        var diagnostics = Compiler.compileInMemory("Test", code);

        assertTrue(diagnostics.isEmpty());
    }
}
