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
package org.clamshellcli.commons.test;

import org.clamshellcli.api.Plugin;
import org.clamshellcli.core.Clamshell;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import org.clamshellcli.api.Command;
import org.clamshellcli.api.Prompt;
import org.clamshellcli.api.Shell;
import org.clamshellcli.core.Clamshell.ClassManager;
import org.clamshellcli.core.ShellConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests are dependent on project clamshell-test.
 * Ensure that clamshell-test is packaged to produce 
 * file ./mock-env/clamshell-cli-***.jar
 * 
 * @author vvivien
 */
public class ClamshellTest {
    public ClamshellTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty(ShellConfigurator.KEY_CONFIG_FILE, "../mock-env/conf/cli.config");
        Clamshell.Runtime.setLibDir(new File("../mock-env/lib"));
        Clamshell.Runtime.setPluginsDir(new File("../mock-env/plugins"));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
        
    @Test
    public void testClassManager_GetClassLoaderFromFiles() throws Exception{
        ClassLoader cl = ClassManager.getClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Pattern.compile(".*\\.jar"), 
            Thread.currentThread().getContextClassLoader());
        
        Assert.assertNotNull(cl);
        Assert.assertNotNull(cl.loadClass("org.clamshellcli.test.MockShell"));
    }
    
    @Test
    public void testClassManager_GetClassLoaderFromDirs() throws Exception{
        ClassLoader cl = ClassManager.getClassLoaderFromDirs(
            new File[]{new File("./target/classes")},
            Thread.currentThread().getContextClassLoader()
        );
        
        Assert.assertNotNull(cl);
        Assert.assertNotNull(cl.loadClass("org.clamshellcli.core.Clamshell"));
    }
    
//    @Test
//    public void testRuntimeLoadPlugins() throws Exception{  
//        List<Plugin> plugins = Clamshell.Runtime.getPlugins();
//        Assert.assertEquals(plugins.size(),5);
//    }
    
    @Test
    public void test() throws Exception{
        ClassLoader cl = Clamshell.ClassManager.createClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Thread.currentThread().getContextClassLoader());
        
        assert cl != null;
        Object o = cl.loadClass("org.clamshellcli.test.MockShell");
        assert o != null;
    }
    
    
    @Test
    public void testLoadServicePlugins_Plugins() throws Exception{
        ClassLoader parent = ClassManager.getClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Pattern.compile(".*\\.jar"), 
            Thread.currentThread().getContextClassLoader()
        );
        List<Plugin> plugins = Clamshell.Runtime.loadServicePlugins(Plugin.class, parent);
        Assert.assertTrue(!plugins.isEmpty());
        Assert.assertEquals(5, plugins.size());
    }
    
    @Test
    public void testLoadServicePlugins_Shells() throws Exception{
        ClassLoader parent = ClassManager.getClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Pattern.compile(".*\\.jar"), 
            Thread.currentThread().getContextClassLoader()
        );
        List<Shell> shells = Clamshell.Runtime.loadServicePlugins(Shell.class, parent);
        Assert.assertTrue(!shells.isEmpty());
        Assert.assertEquals(1, shells.size());
    }
    
    @Test
    public void testLoadServicePlugins_Prompts() throws Exception{
        ClassLoader parent = ClassManager.getClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Pattern.compile(".*\\.jar"), 
            Thread.currentThread().getContextClassLoader()
        );
        List<Prompt> prompts = Clamshell.Runtime.loadServicePlugins(Prompt.class, parent);
        Assert.assertTrue(!prompts.isEmpty());
        Assert.assertEquals(1, prompts.size());
    }
    
    @Test
    public void testLoadServicePlugins_Commands() throws Exception{
        ClassLoader parent = ClassManager.getClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Pattern.compile(".*\\.jar"), 
            Thread.currentThread().getContextClassLoader()
        );
        List<Command> commands = Clamshell.Runtime.loadServicePlugins(Command.class, parent);
        Assert.assertTrue(!commands.isEmpty());
        Assert.assertEquals(2, commands.size());
    }

}
