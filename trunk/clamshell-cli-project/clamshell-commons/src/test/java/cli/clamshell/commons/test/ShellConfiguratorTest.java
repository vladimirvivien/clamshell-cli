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

import cli.clamshell.commons.ShellConfigurator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vladimir Vivien
 */
public class ShellConfiguratorTest {
    static {
        System.setProperty(ShellConfigurator.KEY_PROP_FILE, "../mock-env/conf/cli.properties");
    }
    ShellConfigurator config;
    
    public ShellConfiguratorTest() {
        config = ShellConfigurator.createNewInstance();
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    public void testInstanceCreation() {
        ShellConfigurator cfg = ShellConfigurator.createNewInstance();
        assert cfg != null : "Factory method not building instance";
    }

    
    @Test
    public void testPropFile() {
        String val1 = (String) config.getProperty("key1");
        assert val1.equals("value1");
        String val2 = (String) config.getProperty("key2");
        assert val2.equals("value2");
    }
}
