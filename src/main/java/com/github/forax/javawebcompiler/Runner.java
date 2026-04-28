package com.github.forax.javawebcompiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

public class Runner {
  private Runner(){
      throw new AssertionError("no instances");
  }

  static String runFromMemory(String className, MemoryClassLoader loader) throws Exception {
    Objects.requireNonNull(className);
    Objects.requireNonNull(loader);
    var runClass = loader.loadClass(className);
    var method = runClass.getMethod("main", String[].class);

    var out = new ByteArrayOutputStream();
    var old = System.out;
    System.setOut(new PrintStream(out));

    try {
        method.invoke(null, (Object) new String[]{});
    } finally {
        System.setOut(old);
    }

    return out.toString();
  }
}