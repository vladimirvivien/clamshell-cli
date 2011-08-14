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
package cli.clamshell.commons;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import cli.clamshell.api.Plugin;

/**
 * Utility class.
 * @author vvivien
 */
public final class Clamshell {
    private static final Logger log = Logger.getLogger(Clamshell.class.getName());
    
    private Clamshell(){}
    
    /**
     * Clamshell Runtime utility methods.
     */
    public static class Runtime{
        
        /**
         * Creates a collection of all classes that implements Plugin using the 
         * ServiceLoader API.
         * @param cl a ClassLoader that provides a valid classpath
         * @return List of Plugin instances.
         */
        public static List<Plugin> loadPlugins(ClassLoader cl) {
            ServiceLoader<Plugin> pluginClasses = ServiceLoader.load(Plugin.class, cl);
            List<Plugin> plugins = new ArrayList();
            for (Plugin e : pluginClasses) {
                plugins.add(e);
            }
            return plugins;
        }
        
        /**
         * Creates a class loader object based on a physical locations provided.
         * @param paths an array of File paths where classes are located
         * @param parent the parent loader associated with this call.
         * @return ClassLoader instance
         * @throws Exception if something goes horribly wrong.
         */
        public static ClassLoader createClassLoaderForPath(File[] paths, ClassLoader parent) throws Exception {
            File[] filePaths = correctPaths(paths);
            List<URL> classpath = new ArrayList<URL>();
            for(int i = 0; i < filePaths.length; i++){
                File filePath = filePaths[i].getCanonicalFile();
                if (!filePath.exists()) {
                    log.log(Level.WARNING,"Path [{0}] does not exist."
                            + "  It will not be added to classpath", filePath.getCanonicalPath());
                    continue;
                }
                if (filePath.exists() && !filePath.isDirectory()) {
                    log.log(Level.WARNING,"Path [{0}] is not a directory."
                            + "  It will not be added to classpath", filePath.getCanonicalPath());
                    continue;
                }                
                
                // retrieve jar files from direction i
                File[] files = filePath.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.getName().endsWith(".jar");
                    }
                });

                for (int j = 0; j < files.length; j++) {
                    URL url = files[j].toURI().toURL();
                    log.log(Level.FINE, "Added file {0} to classpath.", url);
                    classpath.add(url);
                }
            }

            URL[] urls = new URL[classpath.size()];
            ClassLoader cl = new URLClassLoader(classpath.toArray(urls), parent);
            return cl;
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
