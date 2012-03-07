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

import org.clamshellcli.core.ShellConfigurator;
import java.util.Map;
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
        System.setProperty(ShellConfigurator.KEY_CONFIG_FILE, "../mock-env/conf/cli.config");
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
    public void testLoadConfigMap() {
        Map<String,Map<String,?>> configMap = config.getConfigMap();
        assert configMap != null;
        assert configMap.size() == 2;
        assert configMap.get(ShellConfigurator.KEY_CONFIG_PROPS) != null;
        assert configMap.get(ShellConfigurator.KEY_CONFIG_PROPS).size() == 2;
    }
    
    @Test
    public void testGetPropertiesMap() {
        Map<String,String> props = config.getPropertiesMap();
        assert props != null;
        assert props.size() == 2;
        assert props.containsKey(ShellConfigurator.KEY_CONFIG_LIBIDR);
        assert props.containsKey(ShellConfigurator.KEY_CONFIG_PLUGINSDIR);
    }
    
    @Test
    public void testGetControllersMap(){
        Map<String,?> ctrls = config.getControllersMap();
        assert ctrls != null;
        assert ctrls.size() == 2;
        assert ctrls.containsKey("cli.clamshell.impl.CmdController");
        Map<String,String> ctrl1 = (Map<String,String>) ctrls.get("cli.clamshell.impl.CmdController");
        assert ctrl1 != null;
        assert ctrl1.get("activated").equals("true");
    }
}
