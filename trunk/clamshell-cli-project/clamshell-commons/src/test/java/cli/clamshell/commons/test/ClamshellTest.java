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
package cli.clamshell.commons.test;

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Plugin;
import cli.clamshell.commons.Clamshell;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        //System.setProperty(Configurator., "../mock-env/plugins");
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
        ClassLoader cl = Clamshell.Runtime.createClassLoaderForPath(
            new File[]{new File("../mock-env/plugins")}, 
            Thread.currentThread().getContextClassLoader()
        );  
        List<Plugin> plugins = Clamshell.Runtime.loadPlugins(cl);
        assert plugins.size() >= 3;
    }
    
    //@Test
    public void testCreateClassLoaderForPath() throws Exception{
        ClassLoader cl = Clamshell.Runtime.createClassLoaderForPath(
            new File[]{new File("./plugins")}, 
            Thread.currentThread().getContextClassLoader());
        
        assert cl != null;
        Object o = cl.loadClass("demo.component.SimplePlugin");
        assert o != null;
    }
}
