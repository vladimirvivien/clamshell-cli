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
import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

/**
 *
 * @author vvivien
 */
public class PsCommand implements Command{
    public static final String KEY_ARGS_HOST = "host";
    public static final String KEY_ARGS_ARGS = "args";
    
    private static final String NAMESPACE = "jmx";
    private static final String CMD_NAME  = "ps";
    private Command.Descriptor descriptor;
    private HostIdentifier hostIdentifier;
            
    public Descriptor getDescriptor() {
        return (descriptor != null) ? 
            descriptor : (
                descriptor = new Command.Descriptor() {

                public String getNamespace() {
                    return NAMESPACE;
                }

                public String getName() {
                    return CMD_NAME;
                }

                public String getDescription() {
                    return "Displays the list of running JVM processes.";
                }

                public String getUsage() {
                    StringBuilder result = new StringBuilder();
                    result
                        .append(Configurator.VALUE_LINE_SEP)
                        .append("ps [options]").append(Configurator.VALUE_LINE_SEP);

                    for(Map.Entry<String,String> entry : getArguments().entrySet()){
                        result.append(
                            String.format("%n%1$15s %2$2s %3$s", 
                                entry.getKey(), 
                                " ", 
                                entry.getValue()
                            )
                        );
                    }

                    return result.toString();
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("host:<hostUrl>", "Specifies a remote host url. Default is localhost.");
                    args.put("args:v", "Arguments for verbose output");
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        IOConsole c = ctx.getIoConsole();
        if(argsMap != null){
            hostIdentifier = getHostIdentifier((String)argsMap.get(KEY_ARGS_HOST));
        }else{
            hostIdentifier = getHostIdentifier("localhost");
        }
        
        c.writeOutput(String.format("%nJVM Processes"));
        c.writeOutput(String.format("%n-------------"));
        c.writeOutput(String.format("%nHost - %s", hostIdentifier.getHost()));
        
        try {
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostIdentifier);
            Set<Integer> jvmIds = monitoredHost.activeVms();
            for(Integer jvmId : jvmIds){
                String vmUri = "//" + jvmId + "?mode=r";
                VmIdentifier vmId = new VmIdentifier(vmUri);
                MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId,0);
                c.writeOutput(String.format("%n%d %s",jvmId, MonitoredVmUtil.commandLine(monitoredVm)));
            }
        } catch (Exception ex) {
            c.writeOutput(String.format("%ERROR: %s%n%n", ex.getMessage()));
            return null;
        }
        
        c.writeOutput(String.format("%n%n"));
        
        return null;
    }

    public void plug(Context plug) {
    }
    
    protected HostIdentifier getHostIdentifier(String hostName){
        try {
            hostIdentifier = new HostIdentifier((hostName != null) ? hostName : "localhost");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return hostIdentifier;
    }
    
}
