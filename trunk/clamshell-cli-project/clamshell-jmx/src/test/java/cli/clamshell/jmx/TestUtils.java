/*
 * Copyright 2012 ClamShell-Cli.
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
package cli.clamshell.jmx;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * Utility methods for testing.
 * @author vladimir.vivien
 */
public class TestUtils {
    public static String MBEAN_NAME = "test.jmx:type=bean";
    public static JmxAgent startNewJmxAgent(int port) throws Exception{
        JmxAgent agent = new JmxAgent(port);
        agent.start();
        return agent;
    }
    
    public static void setupJmxConnection(Context ctx) throws Exception{
        MBeanServerConnection conn = ManagementFactory.getPlatformMBeanServer();
        assert conn != null;
        ctx.putValue(Management.KEY_JMX_MBEANSERVER, conn);
    }
    
    public static void setupDefaultMBeanInstance(Context ctx) throws Exception{
        Map<String,ObjectInstance> beanMap = new HashMap<String,ObjectInstance>();
        ctx.putValue(Management.KEY_MBEANS_MAP, beanMap);
        Command cmd = new MBeanCommand();
        cmd.plug(ctx);
        
        Map<String,Object> argsMap = (ctx.getValue(Context.KEY_COMMAND_LINE_ARGS) != null) 
                ? (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS)
                : new HashMap<String,Object>();
        argsMap.put("name", "java.lang:type=Runtime");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        cmd.execute(ctx);
        
        assert beanMap.get(Management.KEY_DEFAULT_MBEANS) != null;
    }
    
    public static void registerMBean(JmxAgent agent, TestJmxMBeanMBean obj, String name) throws Exception{   
        agent.getConnectorServer().getMBeanServer().registerMBean(obj, new ObjectName(name));
    }
    
    public static void unregisterMBean(JmxAgent agent, String name) throws Exception{
        agent.getConnectorServer().getMBeanServer().unregisterMBean(new ObjectName(name));
    }
}
