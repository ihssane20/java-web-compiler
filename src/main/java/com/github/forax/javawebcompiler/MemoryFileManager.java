// MemoryFileManager.java
package com.github.forax.javawebcompiler;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public final class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
  private final MemoryClassLoader loader;

  public MemoryFileManager(StandardJavaFileManager fileManager, MemoryClassLoader loader) {
    super(fileManager);
    this.loader = loader;
  }

  @Override
  public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling){
    return new SimpleJavaFileObject(URI.create("mem:///" + className), kind) {
      @Override
      public OutputStream openOutputStream() {
        return new ByteArrayOutputStream() {
          @Override
          public void close() throws IOException {
            super.close();
            loader.addClass(className.replace('/', '.'), toByteArray());
          }
        };
      }
    };
  }
}