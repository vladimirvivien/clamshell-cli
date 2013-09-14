/*
 * #%L
 * clamshell-commons
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.clamshellcli.core;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.clamshellcli.api.Command;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Plugin;

/**
 * Utility class.
 * @author Vladimir Vivien
 */
public final class Clamshell {
    private static final Logger log = Logger.getLogger(Clamshell.class.getName());
    private Clamshell(){}
    
    public static class Runtime {
        private static ShellContext ctx;
        private static Configurator config;
        
        public static ShellContext getContext() {
            return (ctx == null) ? ctx = ShellContext.createInstance() : ctx;
        }
        
        public static Configurator getConfigurator () {
            return (config == null) ? config = ShellConfigurator.createNewInstance(): config;
        }
                
        /**
         * This function loads/returns all Classes of type T from classpath.
         * It uses Java's ServiceProvider architecture to locate specified type.
         * @param <T>
         * @param type
         * @param parent
         * @return 
         */
        public static <T> List<T> loadServicePlugins(Class<T> type, ClassLoader parent) {
             ServiceLoader<T> loadedTypes = ServiceLoader.load(type, parent);
             List<T> result = new ArrayList<T>();
             for(T t : loadedTypes){
                 result.add(t);
             }
             return result;
        }

        /**
         * Filters the provided list using the specified type.
         * @param <T> The type provided.
         * @param services
         * @param type
         * @return 
         */
        public static <T> List<T> filterPluginsByType(List<? extends Plugin> services, Class<T> type) {
            List<T> result = new ArrayList<T>();
            for (Plugin p : services) {
                if (type.isAssignableFrom(p.getClass())) {
                    result.add((T) p);
                }
            }
            return result;
        }
        
        public static File getLibDir() {
            return new File(Configurator.VALUE_CONFIG_LIBDIR);
        }
        
        public static File getPluginsDir() {
            return new File(Configurator.VALUE_CONFIG_PLUGINSDIR);
        }
    }
    
    /**
     * Clamshell ClassManager utility Classes/Methods.
     */
    public static class ClassManager{
        
        /**
         * Creates a classloader by searching for specified files in given
         * search directories.
         * @param searchPaths directory or files to add to class loaer.  If DIR 
         * search content of the dir that maches expression.  if FILE and matches
         * expression, add to classloader.
         * @param filePattern regex pattern used to match filename.
         * @param parent parent class loader
         * @return
         * @throws Exception 
         */
        public static ClassLoader getClassLoaderFromFiles(final File[] filePaths, final Pattern filePattern, final ClassLoader parent) throws Exception {
            List<URL> classpath = new ArrayList<URL>();
            
            for(int i = 0; i < filePaths.length; i++){
                File filePath = filePaths[i].getCanonicalFile();
                
                // if file is FILE and matches search, add to classloader
                if (filePath.isFile() && 
                    filePattern.matcher(filePath.getName()).matches()) {
                    classpath.add(filePath.toURI().toURL());
                    continue;
                }                
                
                // if directory, search all matching files to add to classloader
                if(filePath.isDirectory()){
                    File[] files = filePath.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return filePattern.matcher(file.getName()).matches();
                        }
                    });

                    for (int j = 0; j < files.length; j++) {
                        URL url = files[j].toURI().toURL();
                        classpath.add(url);
                    }
                }
            }

            URL[] urls = new URL[classpath.size()];
            ClassLoader cl = new URLClassLoader(classpath.toArray(urls), parent);
            return cl;
        }
        
        
        /**
         * Creates classloader from directories.  The specified directory must
         * contain class files that will be searched by the ClassLoader
         * @param dirs directories to be used for class loading
         * @param parent parent directory
         * @return ClassLoader
         * @throws Exception 
         */
        public static ClassLoader getClassLoaderFromDirs(File[] dirs, ClassLoader parent) throws Exception {
            File[] fileDirs = correctPaths(dirs);
            List<URL> classpath = new ArrayList<URL>();
            for(int i = 0 ; i < fileDirs.length; i++){
                File f = fileDirs[i];
                if(f.isDirectory()){
                    classpath.add(fileDirs[i].toURI().toURL());
                }
            }
            return new URLClassLoader(classpath.toArray(new URL[classpath.size()]), parent);
        }
        
        /**
         * Creates ClassLoader instance from files searched in provided directories. 
         * @param paths directories to search for files to include in ClassLoader
         * @param parent parent class loader
         * @return ClassLoader
         * @throws Exception 
         */
        public static ClassLoader createClassLoaderFromFiles(File[] paths, ClassLoader parent) throws Exception{
            return getClassLoaderFromFiles(paths, Pattern.compile(".*\\.jar"),parent);
        }

        /**
         * Adds the trailing slash in the path name.
         * @param paths
         * @return 
         */
        private static File[] correctPaths(File[] paths) throws Exception{
            File[] correctedPaths = new File[paths.length];
            for(int i = 0; i < paths.length; i++){
                String pathName = paths[i].getName();
                if(!pathName.endsWith(System.getProperty("file.separator"))){
                    String fullPath = paths[i].getCanonicalPath();
                    correctedPaths[i] = new File(fullPath + System.getProperty("file.separator"));
                }else{
                    correctedPaths[i] = paths[i];
                }
            }
            return correctedPaths;
        }
    }
}
