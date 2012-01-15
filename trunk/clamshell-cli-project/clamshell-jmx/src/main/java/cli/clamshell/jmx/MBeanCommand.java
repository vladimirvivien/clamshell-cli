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

/**
 * This command sets an identifier for one or more MBean names for later use with other
 * commands.  User can provide an alias by which to refer to the mbean names. 
 * If no alias is provided, the bean no alias is provided, the beans can be
 * used as default MBean in subsequent commands that need bean names.
 * format:
 * <pre>
 *     mbean name:<mbean_object_expression> as:‘<identifier>’
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
    private Map<String,ObjectInstance[]> mbeanMap;
    
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
                    return "Sets an identifier for one or more MBean names that "
                            + "to be used in other commands.";
                }

                public String getUsage() {
                    return "mbean name:<ObjectName> [as:<identifier>]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("name:<ObjectName> (required)", "ObjectName expression for beans.");
                    args.put("as:<identifier>", "An alias that used to identify the MBeans.");   
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        IOConsole c = ctx.getIoConsole();
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);

        // validate connection
        Management.verifyServerConnection(ctx);
        MBeanServerConnection server = (MBeanServerConnection)ctx.getValue(Management.KEY_JMX_MBEANSERVER);        
        // valdate name param
        if(!isNameArgValid(argsMap)){
            throw new ShellException("Command \"mbean\" "
                    + "requires the 'name:' parameter to specify "
                    + "the ObjectName expression (see help).");
        }
        
        String nameParam = (String)argsMap.get(KEY_ARGS_NAME);
        String asParam = (String)argsMap.get(KEY_ARGS_AS);
        
        ObjectInstance[] objs = Management.getObjectInstances(server,nameParam);
        if(objs == null || objs.length == 0){
            throw new ShellException(String.format("%nNo beans found with name expression %s.%n%n",nameParam));
        }
        
        if(asParam == null){
            mbeanMap.put(Management.KEY_DEFAULT_MBEANS, objs);
            c.writeOutput(String.format("%n%d bean(s) as default (using %s).%n%n",objs.length, nameParam));
        }else{
            mbeanMap.put(asParam, objs);
            c.writeOutput(String.format("%n%d bean(s) set as %s (using %s).%n%n",objs.length, asParam, nameParam));
        }
        
        return null;
    }

    public void plug(Context plug) {
        mbeanMap = new HashMap<String,ObjectInstance[]>();
        plug.putValue(Management.KEY_MBEANS_MAP, mbeanMap);
    }
    
    private boolean isNameArgValid (Map<String,Object> args){
        if(args == null)
            return false;
        String arg = (String)args.get(KEY_ARGS_NAME);
        return (arg != null && !arg.isEmpty());
    }
    
}
