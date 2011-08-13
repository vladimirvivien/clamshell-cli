package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.internal.Lists;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a Command implementation that returns runtime system information.
 * The implemented command has the command-line format of:
 * <pre>
 * sysinfo [options] [option params]
 * </pre>
 * 
 * <b>Options</b><br/>
 * <ul>
 * <li>-props: returns a list of all system properties</li>
 * </ul>
 * 
 * @author vvivien
 */
public class SysInfoCmd implements Command{
    private static final String CMD_NAME = "sysinfo";
    private SysInfoDescriptor descriptor;
    
    private class SysInfoParams {
        @Parameter
        public List<String> parameters = Lists.newArrayList();
        
        @Parameter(names = {"-props"}, required=false, description = "Displays the JVM's system properties. Usage -props.")
        public boolean props = false;

        @Parameter(names = {"-cp","-classpath"}, required=false, description = "Displays JVM classpath information.")
        public boolean cp = false;      
        
        @Parameter(names = {"-mem","-memory"}, required=false, description = "Displays memory inforamtion about current JVM.")
        public boolean mem = false;        
    }
    
    private class SysInfoDescriptor implements Command.Descriptor {
        private JCommander commander;
        SysInfoParams parameters;
        
        public void setCommandArgs(String[] args){
            commander = new JCommander((parameters=new SysInfoParams()), args);
        }
            
        public String getName() {
            return CMD_NAME;
        }

        public String getDescription() {
            return "Displays current JVM runtime information.";
        }

        public String getUsage() {
            return "sysinfo [options]";
        }

        public Map<String, String> getArgsDescription() {
            if(commander == null) commander = new JCommander(new SysInfoParams());
            Map<String, String> result = new HashMap<String,String>();
            List<ParameterDescription> params = commander.getParameters();
            for(ParameterDescription param : params){
                result.put(param.getNames(), param.getDescription());
            }
            
            return result;
        }
        
    }
    
    public Descriptor getDescriptor() {
        return descriptor;
    }

    public Object execute(Context ctx) {
        String[] args = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        IOConsole c = ctx.getIoConsole();
        if(args != null){
            try{
                descriptor.setCommandArgs(args);
            }catch(RuntimeException ex){
                c.writeOutput(String.format("%nUnable execute command: %s%n%n", ex.getMessage()));
                return null;
            }
            
            // decipher args
            
            // >sysinfo -props
            if(descriptor!=null && descriptor.parameters.props){
                c.writeOutput(String.format("%nSystem Properties"));
                c.writeOutput(String.format("%n-----------------"));
                displayAllSysProperties(ctx);
                c.writeOutput(String.format("%n%n"));
            }
            
            // >sysinfo -cp [or -classpath]
            if(descriptor != null && descriptor.parameters.cp){
                RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                c.writeOutput(String.format("%nClasspath: %s", bean.getClassPath()));
                c.writeOutput(String.format("%nBoot Classpath: %s%n%n", bean.getBootClassPath()));
            }
            
            
            // >sysinfo -mem
            if(descriptor != null && descriptor.parameters.mem){
                MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
                c.writeOutput(String.format("%nHeap Memory Usage:%n"));
                c.writeOutput(String.format("\t-Initial: %d%n", bean.getHeapMemoryUsage().getInit()));
                c.writeOutput(String.format("\t-Max: %d%n", bean.getHeapMemoryUsage().getMax()));
                c.writeOutput(String.format("\t-Committed: %d%n", bean.getHeapMemoryUsage().getCommitted()));
                c.writeOutput(String.format("\t-Used: %d", bean.getHeapMemoryUsage().getUsed()));
                c.writeOutput(String.format("%n%n"));
            }

        }
        
        return null;
    }

    public void plug(Context plug) {
        descriptor = new SysInfoDescriptor();
    }
    
    
    private void displaySystemProperty(Context ctx, String propName){
        IOConsole c = ctx.getIoConsole();
        if(propName == null || propName.isEmpty()){
            c.writeOutput(String.format("%n Property name is missing. Provide a property name.%n%n"));
            return;
        }
        String propVal = System.getProperty(propName);
        if(propVal != null){
            c.writeOutput(String.format(
                "%n%1$30s %2$5s %3$s", 
                propName, 
                " ", 
                propVal
            ));             
        }
    }
    
    private void displayAllSysProperties(Context ctx){
        IOConsole c = ctx.getIoConsole();
        for(Entry<Object,Object> entry: System.getProperties().entrySet()){
            displaySystemProperty(ctx, (String)entry.getKey());
        }
    }
    
}
