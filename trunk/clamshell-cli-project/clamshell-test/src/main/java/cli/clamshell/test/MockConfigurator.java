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

/**
 * This is a mock class for the Configurator class.
 * It is intended to be used for testing.
 * @author vladimir
 */
public class MockConfigurator implements Configurator{
    private Map<String, Map<String, ? extends Object>> configMap;
    
    public MockConfigurator() {
        configMap = new HashMap<String,Map<String, ? extends Object>>();
    }
    
    public Map<String, Map<String, ? extends Object>> getConfigMap() {
        return configMap;
    }
    public void setConfigMap(Map<String,Map<String,? extends Object>> map){
        configMap = map;
    }
    public void addConfigMap(String cfgName, Map<String, ? extends Object> val){
        configMap.put(cfgName, val);
    }

    public Map<String, String> getPropertiesMap() {
        return (Map<String, String>) configMap.get(Configurator.KEY_CONFIG_PROPS);
    }    
    public void setPropertiesMap(Map<String,String> map){
        configMap.put(Configurator.KEY_CONFIG_PROPS, map);
    }
    
    public void addProperty(String name, String val){
        if(getPropertiesMap() != null)
            getPropertiesMap().put(name, val);
        else{
            this.setPropertiesMap(new HashMap<String,String>());
            getPropertiesMap().put(name, val);
        }
            
    }

    public Map<String, Map<String, ? extends Object>> getControllersMap() {
        return (Map<String, Map<String, ? extends Object>>) configMap.get(Configurator.KEY_CONFIG_CTRLS);
    }
    
    public void setControllersMap(Map<String, Map<String, ? extends Object>> map){
        configMap.put(Configurator.KEY_CONFIG_CTRLS, map);
    }
    
    public void addControllerMap(String name, Map<String,? extends Object> attribs){
        if(getControllersMap() != null){
            getControllersMap().put(name, attribs);
        }else{
            this.setControllersMap(new HashMap<String, Map<String, ? extends Object>>());
            this.getControllersMap().put(name, attribs);
        }
    }
}
