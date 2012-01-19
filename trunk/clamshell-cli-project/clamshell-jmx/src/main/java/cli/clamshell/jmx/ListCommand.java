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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * This Command lists the MBeans registered witht JMX MBean server.  The 
 * command uses the pattern: parameter is used to provide one or more filter patterns
 * used to generate the list.  The setid: parameter will cause the list to 
 * cache the result and assign each entry a generated-identifier that can be
 * used to refer to the MBean in other commands (see mbean command).
 * <p>
 * The List command format is:
 *<pre>
 * list [pattern:"java.lang:* | ["pattern1","pattern2", "pattern3,...,patternN]] [setid:true|false]"
 *</pre>
 * </p>
 * 
 * @author vladimir.vivien
 */
public class ListCommand implements Command{
    private static final String CMD_NAME = "list";
    private static final String NAMESPACE = "jmx";
    private static final String KEY_ARGS_PATTERN = "pattern";
    private static final String KEY_ARGS_SETID = "setid";
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
                    return "Lists registered JMX MBeans.";
                }

                public String getUsage() {
                    return "list [pattern:<namePatter>|[<namePatternList>]] [setid:<true|false>]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("pattern", "One or more ObjectName pattern used as filter.");
                    args.put("setid", "Assigns identifiers to each bean in list that can be used in other commands.");   
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        IOConsole c = ctx.getIoConsole();
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        Map<String,ObjectInstance[]> mbeanMap = (Map<String,ObjectInstance[]>) ctx.getValue(Management.KEY_MBEANS_MAP);
        if(mbeanMap == null){
            mbeanMap = new HashMap<String,ObjectInstance[]>();
            ctx.putValue(Management.KEY_MBEANS_MAP, mbeanMap);
        }
                
        // validate connection
        Management.verifyServerConnection(ctx);
        MBeanServerConnection server = (MBeanServerConnection)ctx.getValue(Management.KEY_JMX_MBEANSERVER);
                
        Object patternParam = (argsMap  != null) ? argsMap.get(KEY_ARGS_PATTERN) : null;
        Object setidParam = (argsMap != null) ? argsMap.get(KEY_ARGS_SETID) : null;
        Boolean setid = Boolean.FALSE;
        if(setidParam instanceof String){
            setid = Boolean.valueOf((String)setidParam);
        }
        if(setidParam instanceof Boolean){
            setid = (Boolean)setidParam;
        }
       
        Set<ObjectInstance> objs = null;
        try {
            if (patternParam == null) {
                c.writeOutput(String.format("%nMBean list [all]"));
               objs = server.queryMBeans(null, null);
            }else{
                c.writeOutput(String.format("%nMBean list [%s]", patternParam));
                if(patternParam instanceof String){
                    String pattern = (String) patternParam;
                    if(pattern.equals("*")){
                        objs = server.queryMBeans(null, null);
                    }else{
                        objs = server.queryMBeans(new ObjectName(pattern), null);
                    }
                }
                if(patternParam instanceof List){
                    List<String> patterns = (List<String>) patternParam;
                    objs = new HashSet<ObjectInstance>();
                    for(String pattern : patterns){
                        objs.addAll(server.queryMBeans(new ObjectName(pattern), null));
                    }
                }
            }

        } catch (Exception ex) {
            throw new ShellException(ex);
        }
        
        // print it all
        long counter = 0;
        for (ObjectInstance obj : objs) {
            if (setid) {
                String beanId = String.format("$%d", counter);
                mbeanMap.put(beanId, new ObjectInstance[]{obj});
                c.writeOutput(getInstanceDesc(obj, true, beanId));
            } else {
                c.writeOutput(getInstanceDesc(obj, false, null));
            }
            
            counter++;
        }
        
        c.writeOutput(String.format("%n%n %d objects found.", counter));
        c.writeOutput(String.format("%n%n"));
        
        return null;
    }

    public void plug(Context plug) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String getInstanceDesc(ObjectInstance obj, boolean setId, String id) {
        StringBuilder s = new StringBuilder();
        if (setId) {
            s.append(String.format(
            "%n%5s[%s] %s (%s)",
                " ",
                id,
                obj.getObjectName().getCanonicalName(),
                obj.getClassName())
            );
        }else{
            s.append(String.format(
                "%n%5s %s (%s)",
                " ",
                obj.getObjectName().getCanonicalName(),
                obj.getClassName())
            );
            
        }
        return s.toString();
    }
    
}
