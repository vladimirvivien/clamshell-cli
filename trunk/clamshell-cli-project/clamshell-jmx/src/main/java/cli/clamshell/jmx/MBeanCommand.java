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
import cli.clamshell.api.IOConsole;
import cli.clamshell.commons.ShellException;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * This command sets an identifier for one or more MBean names for later use with other
 * commands.  User can provide an alias by which to refer to the mbean names. 
 * If no alias is provided, the bean no alias is provided, the beans can be
 * used as default MBean in subsequent commands that need bean names.
 * format:
 * <pre>
 *     mbean name:<mbean_object> as:‘<identifier>’
 * </pre>
 * <ul>
 * <li>
 *   name (required) - this parameter specifies the expression for MBean names.
 *   The name can refer to one bean or a pattern for more than one bean.
 *   That string must be well-formatted following the MBean ObjectName identifier.
 * </li>
 * <li>
 *   as - this is the alias to be used to identified the MBean(s).
 * </li>
 * </ul>
 * @author vvivien
 */
public class MBeanCommand implements Command{
    private static final String CMD_NAME = "mbean";
    private static final String NAMESPACE = "jmx";
    private static final String KEY_ARGS_NAME = "name";
    private static final String KEY_ARGS_AS   = "as";
    
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
                    return "Sets an identifier for an MBean "
                            + "to be used in other commands.";
                }

                public String getUsage() {
                    return "mbean name:<ObjectName> [as:<identifier>]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("name:<ObjectName>", "ObjectName for bean.");
                    args.put("as:<identifier>", "A user-provided identifier.");   
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        IOConsole c = ctx.getIoConsole();
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        Map<String,ObjectInstance> mbeanMap = (Map<String,ObjectInstance>)ctx.getValue(Management.KEY_MBEANS_MAP);

        // validate connection
        Management.verifyServerConnection(ctx);
        MBeanServerConnection server = (MBeanServerConnection)ctx.getValue(Management.KEY_JMX_MBEANSERVER);        
        
        String nameParam = (argsMap != null) ? (String)argsMap.get(KEY_ARGS_NAME) : null;
        String asParam = (argsMap != null) ? (String)argsMap.get(KEY_ARGS_AS) : null;

        // valdate name param
        if(nameParam == null){
            throw new ShellException("Command \"mbean\" "
                    + "requires the 'name:' parameter to set the MBean (see help). ");
        }
        
        ObjectInstance obj = null;
        try{
            obj = Management.getObjectInstance(server, nameParam);
        }catch(ShellException ex){
            throw new ShellException (String.format("%nError locating MBean %s: "
                    + "%s", nameParam, ex.getMessage()));
        }
                        
        if(asParam == null){
            mbeanMap.put(Management.KEY_DEFAULT_MBEANS, obj);
            c.writeOutput(String.format("%nMBean %s set as default.%n%n",nameParam));
        }else{
            mbeanMap.put(asParam, obj);
            c.writeOutput(String.format("%nMBean %s set as %s.%n%n", nameParam, asParam));
        }
        
        return null;
    }

    public void plug(Context plug) {
    }

}
