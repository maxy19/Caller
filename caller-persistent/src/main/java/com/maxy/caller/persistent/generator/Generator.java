package com.maxy.caller.persistent.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Java 的方式来运行 MBG
 *
 * @see <a href="http://www.mybatis.org/generator/running/runningWithJava.html">Running MyBatis Generator With Java</a>
 */
public class Generator {

    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<>(2);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(getFile("generatorConfig.xml"));
        DefaultShellCallback callback = new DefaultShellCallback(true);

        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    private static File getFile(String fileName) {
        ClassLoader classLoader = Generator.class.getClassLoader();
        /**
         getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
         */
        URL url = classLoader.getResource(fileName);
        /**
         * url.getFile() 得到这个文件的绝对路径
         */
        return new File(url.getFile());
    }
}
