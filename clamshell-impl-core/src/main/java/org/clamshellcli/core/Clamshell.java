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
import java.util.logging.Level;
import java.util.logging.Logger;
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
        private static ClassLoader pluginsCl;
        private static List<Plugin> plugins;
        private static File libDir;
        private static File pluginsDir;
        
        public static ShellContext getContext() {
            return (ctx == null) ? ctx = ShellContext.createInstance() : ctx;
        }
        
        public static Configurator getConfigurator () {
            return (config == null) ? config = ShellConfigurator.createNewInstance(): config;
        }
        
        /**
         * Returns the plugins classloader, uses the current thread's a s parent.
         * @return 
         */
        public static ClassLoader getPluginsClassLoader() {
            return getPluginsClassLoader(Thread.currentThread().getContextClassLoader());
        }
        
        /**
         * Returns the class loader for the plugins directory specified by cli.dir.plugins from config.
         * @param parentCl - a parent classloader to use.
         * @return 
         */
        public static ClassLoader getPluginsClassLoader(ClassLoader parentCl) {
            if(pluginsCl != null) return pluginsCl; // return if already loaded.

            if(getPluginsDir().exists() && getPluginsDir().isDirectory()){
                try {
                    pluginsCl = Clamshell.ClassManager.createClassLoaderForPath(
                        new File[]{getPluginsDir()},
                        parentCl
                    );
                } catch (Exception ex) {
                   throw new RuntimeException(ex);
                }
            }else{
                throw new RuntimeException (String.format(
                        "%nUnable to find plugins directory [%s]."
                        + "%nClamshell can not run.  Exiting...", getPluginsDir().getAbsolutePath()));
            }
            return pluginsCl;
        }
        
        
        /**
         * Retrieve all Plugin.class instances from specified plugins classloader.
         * @param cl a ClassLoader that provides a v
         * @return List of Plugin instances.
         */
        public static List<Plugin> getPlugins() {
            if(plugins != null) return plugins;
            
            ServiceLoader<Plugin> pluginClasses = ServiceLoader.load(Plugin.class, getPluginsClassLoader());
            plugins = new ArrayList();
            for (Plugin e : pluginClasses) {
                plugins.add(e);
            }
            
            return plugins;
        }
        
        /**
         * etrieves a list of Class instances using the provided Type.
         * @param <T> type filter
         * @param type type
         * @return list of instances of type <T>
         */
        public static <T> List<T> getPluginsByType(Class<T> type) {
            List<T> result = new ArrayList<T>();
            for (Plugin p : getPlugins()) {
                if (type.isAssignableFrom(p.getClass())) {
                    result.add((T) p);
                }
            }
            return result;
        }
        
        public static void setLibDir(File dir){
            libDir = dir;
        }
        
        public static File getLibDir() {
            return (libDir != null) ? libDir : (libDir = new File(Configurator.VALUE_CONFIG_LIBDIR));
        }
        
        public static void setPluginsDir(File dir){
            pluginsDir = dir;
        }
        
        public static File getPluginsDir() {
            return (pluginsDir != null) ? pluginsDir : (pluginsDir = new File(Configurator.VALUE_CONFIG_PLUGINSDIR));
        }
    }
    
    /**
     * Clamshell ClassManager utility Classes/Methods.
     */
    public static class ClassManager{
        
        /**
         * Creates a class loader object based on a physical locations provided.
         * @param paths an array of File paths where classes are located
         * @param parent the parent loader associated with this call.
         * @return ClassManager instance
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
