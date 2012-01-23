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
import cli.clamshell.jmx.Management.VmInfo;
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
        ctx.putValue(Management.KEY_JMX_MBEANSERVER, conn);
        Command cmd = new ConnectCommand();
        cmd.plug(ctx);
        
        Map<String,Object> argsMap = new HashMap<String,Object>();
        
        Map<Integer,VmInfo> vms = Management.mapVmInfo("localhost");
        VmInfo vm = null;
        for(Map.Entry<Integer,VmInfo> e : vms.entrySet()){
            if(e.getValue().isAttachable()){
                vm = e.getValue();
                break;
            }
        }
        
        if(vm != null){
            argsMap.put(Management.KEY_ARGS_PID, vm.getMonitoredVm().getVmIdentifier().getLocalVmId());
            ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
            cmd.execute(ctx);
        }
    }
    
    public static void setupDefaultMBeanInstance(Context ctx){
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
    
    public static void registerTestMBean(JmxAgent agent) throws Exception{
        JmxMBean bean = new JmxMBeanImpl();     
        agent.getConnectorServer().getMBeanServer().registerMBean(bean, new ObjectName(MBEAN_NAME));
    }
}
