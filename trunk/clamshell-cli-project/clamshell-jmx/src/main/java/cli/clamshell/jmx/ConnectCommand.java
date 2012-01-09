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
package cli.clamshell.jmx;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import cli.clamshell.jmx.Management.VmInfo;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import sun.jvmstat.monitor.MonitoredVm;

/**
 * This Command interface implementation provides the JMX "connect" command-line.
 * Using this command, users can connect to a local or remote JVM running the 
 * JMX MBeanServer.  Format
 * 
 * <code>connect [
 *     [host:<hosturl>] [username:<username> password:<password>]
 * ] [pid:<procid>]
 * 
 * <ul>
 * <li>host: address of host to connect to</li>
 * <li>username: username credential</li>
 * <li>password: password credential</li>
 * <li>pid : connect using JVM pid (from ps command)</li>
 * </ul>
 * 
 * @author vladimir.vivien
 */
public class ConnectCommand implements Command{
    private static final String CMD_NAME = "connect";
    private static final String NAMESPACE = "jmx";
    private static final String KEY_ARGS_UNAME = "username";
    private static final String KEY_ARGS_PWD = "password";
    private static final String KEY_ARGS_PID = "pid";
    
    private Command.Descriptor descriptor = null;
    
    public Descriptor getDescriptor() {
        return (descriptor != null ) ? descriptor : (
            descriptor = new Command.Descriptor() {

                public String getNamespace() {
                    return NAMESPACE;
                }

                public String getName() {
                    return CMD_NAME;
                }

                public String getDescription() {
                    return "Connects to local or remote JVM management server.";
                }

                public String getUsage() {
                    return "connect [options]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("host:<hostUrl>", "Host url, default is localhost.");
                    args.put(CMD_NAME, CMD_NAME);   
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        Map<Integer,VmInfo> localVms = (Map<Integer,VmInfo>) ctx.getValue(Management.KEY_VMINFO_MAP);
        final IOConsole c = ctx.getIoConsole();
 
        MBeanServerConnection serverConnection = null;
        JMXConnector connector = null;
        
        String hostAddr = Management.getHostFromArgs(argsMap);
        String uname = (String)argsMap.get(KEY_ARGS_UNAME);
        String pwd = (String)argsMap.get(KEY_ARGS_PWD);
        String pid = (String)argsMap.get(KEY_ARGS_PID);
        
        // extract JMX URL.
        JMXServiceURL jmxUrl = null;
        try{
            jmxUrl = Management.getJmxUrlFrom("localhost");

            if (pid != null) {
                try{
                    Integer procId = Integer.valueOf(pid);
                    VmInfo info = getVmInfo(procId, localVms);
                    if (info != null) {
                        hostAddr = info.getAddress();
                        jmxUrl = Management.getJmxUrlFrom(hostAddr);
                    }
                    
                    // if vminfo not cached already, try to find and connect 
                    else{
                        MonitoredVm mvm = Management.getMonitoredVmFromId(procId);
                        if(mvm != null){
                            Management.VmInfo vmInfo = new Management.VmInfo(mvm);
                            jmxUrl = Management.getJmxUrlFrom(vmInfo.getAddress());
                            localVms.put(procId, vmInfo);
                        }else{
                            c.writeOutput(String.format("ERROR: Unable to find local VM "
                                + " with pid value: [%s]", pid));            
                            return null;                            
                        }
                    }
                }catch(Exception ex){
                    c.writeOutput(String.format("ERROR: Unable to determine "
                        + "pid value: [%s]", pid));            
                    return null;
                }
            }
            if (hostAddr != null){
                jmxUrl = Management.getJmxUrlFrom(hostAddr);
            }
                        
            ctx.putValue(Management.KEY_JMX_URL, jmxUrl);
            
        }catch(Exception ex){
            c.writeOutput(String.format("ERROR: Unable to determine "
                    + "connection URL: %s", ex.getMessage()));            
        }
        
        // add credentials info
        Map<String, String[]> env =  null;
        if(uname != null && pwd != null){
            env = new HashMap<String,String[]>();
            env.put(JMXConnector.CREDENTIALS, new String[]{uname, pwd});
        }
        
        
        // connect
        try{
            connector = JMXConnectorFactory.connect(jmxUrl, env); 
            
            // if pid nor hostaddr provided, use platform/local mbean server
            if(pid == null && hostAddr == null){
                serverConnection = ManagementFactory.getPlatformMBeanServer();
            }else{           
                serverConnection = connector.getMBeanServerConnection();
            }
            ctx.putValue(Management.KEY_JMX_CONNECTOR, connector);
            ctx.putValue(Management.KEY_JMX_MBEANSERVER, serverConnection);
        }catch(Exception ex){
            c.writeOutput(String.format("ERROR: unable to connect to"
                    + " MBeanServer: %s", ex.getMessage()));
        }
        
        
        return null;
    }

    public void plug(Context plug) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected VmInfo getVmInfo(Integer key, Map<Integer,VmInfo> jvmMap){
        if(jvmMap == null) return null;
        return jvmMap.get(key);
    }    
}
