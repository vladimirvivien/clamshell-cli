/*
 * Copyright 2011 ClamShell-Cli.
 *
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
 */
package cli.clamshell.test;

import cli.clamshell.api.Configurator;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for MockConfigurator class.
 * @author vladimir.vivien
 */
public class MockConfiguratorTest {
    private MockConfigurator mock;

    public MockConfiguratorTest() {
        mock = new MockConfigurator();
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
    
    @Test
    public void testConstructor(){
        Configurator cfg = new MockConfigurator();
        assert cfg != null;
        assert cfg.getConfigMap() != null;
        assert cfg.getControllersMap() == null;
        assert cfg.getPropertiesMap() == null;
    }
    
    @Test
    public void testSetConfigMap() {
        Map<String, Map<String, ?>> config = new HashMap<String,Map<String,?>>();
        Map<String, String> props = new HashMap<String,String>();
        props.put("key", "Hello");
        config.put("props", props);
        mock.setConfigMap(config);
        assert mock.getConfigMap() != null;
        assert mock.getConfigMap().get("props") != null;
        assert mock.getConfigMap().get("props").get("key") != null;
        assert mock.getConfigMap().get("props").get("key").equals("Hello");
    }
    
    @Test
    public void testAddConfigMap(){
        Map<String, String> props = new HashMap<String,String>();
        props.put("key1", "Hello");
        props.put("key2", "World");
        
        mock.addConfigMap("config", props);
        assert mock.getConfigMap().get("config") != null;
        assert mock.getConfigMap().get("config").get("key1").equals("Hello");
        assert mock.getConfigMap().get("config").get("key2").equals("World");
    }
    
    @Test
    public void testPropertiesMap(){
        Map<String, String> props = new HashMap<String,String>();
        props.put("key1", "Hello");
        props.put("key2", "World");
        
        mock.setPropertiesMap(props);
        
        assert mock.getPropertiesMap() != null;
        assert mock.getPropertiesMap().get("key1").equals("Hello");
        assert mock.getPropertiesMap().get("key2").equals("World");
        
        mock.addProperty("key3", "Foo");
        assert mock.getPropertiesMap().get("key3").equals("Foo");
    }
    
}
