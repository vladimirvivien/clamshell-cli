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
    public static final String KEY_ARGS_OPTIONS = "o";
    
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
                    args.put("o:v", "Option for verbose output");
                    
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        IOConsole c = ctx.getIoConsole();
        String options = getOptions(argsMap);
        String hostName = getHostName(argsMap);
        c.writeOutput(String.format("%nConnecting to %s ...", hostName));
        
        try {
            hostIdentifier = getHostIdentifier(hostName);
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostIdentifier);
            Set<Integer> jvmIds = monitoredHost.activeVms();
            for(Integer jvmId : jvmIds){
                MonitoredVm monitoredVm = getMonitoredVm(monitoredHost, jvmId);
                c.writeOutput(String.format("%n%d\t%s",jvmId, getVmInfo(monitoredVm, options)));
            }
        } catch (Exception ex) {
            c.writeOutput(String.format("%nERROR: %s%n%n", ex.getMessage()));
            return null;
        }
        
        c.writeOutput(String.format("%n%n"));
        
        return null;
    }

    public void plug(Context plug) {
    }
    
    
    protected String getHostName(Map<String,Object> argsMap){
        return (argsMap != null && argsMap.get(KEY_ARGS_HOST) != null) ?
            (String)argsMap.get(KEY_ARGS_HOST) : "localhost";
    }
    
    protected String getOptions(Map<String,Object> argsMap){
        return (argsMap != null && argsMap.get(KEY_ARGS_OPTIONS) != null) ?
            (String)argsMap.get(KEY_ARGS_OPTIONS) : "q";
    }
    
    protected HostIdentifier getHostIdentifier(String hostName){
        try {
            hostIdentifier = new HostIdentifier((hostName != null) ? hostName : "localhost");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return hostIdentifier;
    }
    
    protected MonitoredVm getMonitoredVm(MonitoredHost mHost, Integer jvmId) throws Exception{
        String vmUri = "//" + jvmId + "?mode=r";
        VmIdentifier vmId = new VmIdentifier(vmUri);
        return mHost.getMonitoredVm(vmId,0);
    }
    
    private String getVmInfo(MonitoredVm vm, String options) throws Exception {
        String vmInfo = MonitoredVmUtil.mainClass(vm, true);;
        if (options != null && options.toLowerCase().equals("v")) {
            vmInfo = String.format(
                "%s %s",
                MonitoredVmUtil.mainClass(vm, true),
                MonitoredVmUtil.commandLine(vm)
            );
        }

        return vmInfo;
    }
    
}
