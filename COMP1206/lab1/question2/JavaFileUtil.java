import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.io.ConcatenateJavaFiles;

import java.io.*;

public class JavaFileUtil implements ConcatenateJavaFiles {
  @Override
  public void concatenateJavaFiles(String s, String s1) throws IOException {
    File f1 = new File(s);
    File f2 = new File(s,s1);
    if(f1.isDirectory()){
      File[] arr = f1.listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
          return pathname.isFile() && pathname.getName().endsWith(".java");
        }
      });
      for (File f :arr) {
        int a;
        FileInputStream fis = new FileInputStream(f);
        FileOutputStream fos = new FileOutputStream(f2,true);
        while((a = fis.read())!=-1){
          fos.write(a);
        }
      fos.close();
      fis.close();
      }
    }else{
      throw new IllegalArgumentException();
    }
  }
}
