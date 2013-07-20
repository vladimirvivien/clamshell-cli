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
import org.clamshellcli.core.ShellConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
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
    public void testRuntimeLoadPlugins() throws Exception{  
        List<Plugin> plugins = Clamshell.Runtime.getPlugins();
        assert plugins.size() >= 3;
    }
    
    @Test
    /**
     * This test will fail (classNotFound) if mock-env/plugins/ is empty.
     * Run project clamshellcli-test to put pluings at that location.
     */
    public void testCreateClassLoaderForPath() throws Exception{
        ClassLoader cl = Clamshell.ClassManager.createClassLoaderForPath(
            new File[]{new File("../mock-env/plugins")}, 
            Thread.currentThread().getContextClassLoader());
        
        assert cl != null;
        Object o = cl.loadClass("org.clamshellcli.test.MockShell");
        assert o != null;
    }
}
