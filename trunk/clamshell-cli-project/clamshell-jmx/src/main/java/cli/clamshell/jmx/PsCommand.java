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
import java.util.LinkedHashMap;
import java.util.Map;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

/**
 * This is an implementation of the Command interface to handle JMX command-line
 * "ps".  This command displays Java VM process information similar to the JDK
 * command-line tool Jps.  The command format:
 * <code>ps [host:"hostUri" o:q|v]</code>
 * Parameter "host" specifies the address of the JVM. Parameter "o" specifies 
 * additional options q = quiet (default), v = verbose.
 * @author vladimir.vivien
 */
public class PsCommand implements Command{
    public static final String CMD_NAME  = "ps";
    public static final String NAMESPACE = "jmx";
    public static final String KEY_ARGS_HOST = "host";
    public static final String KEY_ARGS_OPTION = "option";
    
    
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
                    return "Displays a list of running JVM processes (similar to jps tool)";
                }

                public String getUsage() {
                    return "ps [host:<HostUrl> option:<v|q>]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new LinkedHashMap<String,String>();
                    args.put(KEY_ARGS_HOST      + ":<hostUrl>", "Host url, default is localhost");
                    args.put(KEY_ARGS_OPTION    + ":v", "Option for verbose output");
                    args.put(KEY_ARGS_OPTION    + ":q", "Option for quitter output (default)");
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        IOConsole c = ctx.getIoConsole();
        String options = getOptions(argsMap);
        String hostAddr = Management.getHostFromArgs(argsMap);
        c.writeOutput(String.format("%nConnecting to %s ...", hostAddr));

        try {
            Map<Integer, Management.VmInfo> vMs = Management.mapVmInfo(hostAddr);
            ctx.putValue(Management.KEY_VMINFO_MAP, vMs);
            for(Map.Entry<Integer,Management.VmInfo> vmInfo : vMs.entrySet()){
                Integer id = vmInfo.getKey();
                MonitoredVm vm = vmInfo.getValue().getMonitoredVm();
                c.writeOutput(String.format("%n%d\t%s",id, formatVmInfo(vm, options)));
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
    
    protected String getOptions(Map<String,Object> argsMap){
        return (argsMap != null && argsMap.get(KEY_ARGS_OPTION) != null) ?
            (String)argsMap.get(KEY_ARGS_OPTION) : "q";
    }
        
    private String formatVmInfo(MonitoredVm vm, String options) throws Exception {
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
