package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
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
    private class SysInfoDescriptor implements Command.Descriptor {
        private JCommander commander = new JCommander();
        public void setCommandArgs(String[] args){
            commander.addObject(this);
            commander.parse(args);
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
        
        @Parameter
        public List<String> parameters = Lists.newArrayList();
        
        @Parameter(names = "-props", required=false, description = "Displays the JVM's system properties. Usage -props.")
        public boolean props = false;

        @Parameter(names = {"-cp","-classpath"}, required=false, description = "Displays JVM classpath information.")
        public boolean cp = false;      
        
        @Parameter(names = {"-bootcp","-boot-classpath"}, required=false, description = "Displays JVM classpath information.")
        public boolean bootcp = false;     
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
            if(descriptor!=null && descriptor.props){
                c.writeOutput(String.format("%nSystem Properties"));
                c.writeOutput(String.format("%n-----------------"));
                displayAllSysProperties(ctx);
                c.writeOutput(String.format("%n%n"));
                return null;
            }
            
            // >sysinfo -cp [or -classpath]
            if(descriptor != null && descriptor.cp){
                RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                c.writeOutput(String.format("%nClasspath:"));
                c.writeOutput(String.format("%n%s%n%n",bean.getClassPath()));
                return null;
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
