package fkjava.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PackageScanner {

    public PackageScanner() {
    }

    /**
     * @param basePackages 目前只考虑一个包名的情况
     * @return
     */
    public Set<Class<?>> scan(String basePackages) {
        Set<Class<?>> classes = new HashSet<>();
        // 使用类加载器找到包的URL，一般来讲这里需要自定义类加载器，但是由于这个只是实验，所以不理不加
        // 同样，这里也不考虑加载jar文件里面的类，只考虑当前类路径的情况

        try {
            // 获取当前类所在的目录
            URL url = this.getClass().getResource(this.getClass().getSimpleName() + ".class");
            URI uri = url.toURI();
            File file = new File(uri);

            // 根据包名的段数，找到类路径的根目录
            String[] args = basePackages.split("\\.");
            for (String p : args) {
                file = new File(file, "..");
            }
            File dir = file.getCanonicalFile();
            String dirPath = dir.getAbsolutePath();

            // 扫描类路径下，所有类文件的路径
            List<String> classFilePaths = findClasses(dir);
//            System.out.println(classFilePaths);

            classFilePaths.forEach(filePath -> {
                String path = filePath.substring(dirPath.length());
                path = path.substring(0, path.indexOf(".class"));
                path = path.replace("\\", "/");
                if(path.startsWith("/")){
                    path = path.substring(1);
                }
                String className = path.replace("/", ".");
                try {
                    Class cla = Class.forName(className);
                    classes.add(cla);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("包扫描出现问题：" + e.getLocalizedMessage(), e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("包扫描出现问题：" + e.getLocalizedMessage(), e);
        }
        return classes;
    }

    private List<String> findClasses(File dir) throws IOException {
        List<String> paths = new LinkedList<>();
        Files.walk(dir.toPath())
                .filter(path -> path.toFile().isFile() && path.toString().endsWith(".class"))
                .forEach(path -> paths.add(path.toFile().getAbsolutePath()));
        return paths;
    }
}
