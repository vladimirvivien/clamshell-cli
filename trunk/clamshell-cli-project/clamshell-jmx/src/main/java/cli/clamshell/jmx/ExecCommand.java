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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ReflectionException;

/**
 * The Exec command lets user invoke methods on specified mbean.
 * The format for the exec command is:
 * <pre>
 * exec name:<objectName or Id> 
 *      get:<attribName>|[[<attribNameList>]]
 *      set:<attribName>|[[<attribNameList>]]
 *      op:<operationName>|[[<opNameList>]]
 *      params:[<paramValueList>]
 * </pre>
 * @author vladimir.vivien
 */
public class ExecCommand implements Command{
   private static final String CMD_NAME         = "exec";
    private static final String NAMESPACE       = "jmx";
    private static final String KEY_ARGS_NAME   = "name";
    private static final String KEY_ARGS_GET    = "get";
    private static final String KEY_ARGS_SET    = "set";
    private static final String KEY_ARGS_OP     = "op";
    private static final String KEY_ARGS_PARAMS = "params";
    
    private Command.Descriptor descriptor = null;
 
    public Descriptor getDescriptor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object execute(Context ctx) {
        IOConsole c = ctx.getIoConsole();
        Map<String, Object> argsMap = (Map<String, Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        Map<String, ObjectInstance> mbeanMap = (Map<String, ObjectInstance>) ctx.getValue(Management.KEY_MBEANS_MAP);

        // validate connection
        Management.verifyServerConnection(ctx);
        MBeanServerConnection server = (MBeanServerConnection) ctx.getValue(Management.KEY_JMX_MBEANSERVER);

        Object nameParam = (argsMap  != null) ? argsMap.get(KEY_ARGS_NAME) : null;
        Object getParam  = (argsMap  != null) ? argsMap.get(KEY_ARGS_GET) : null;
        Object setParam  = (argsMap  != null) ? argsMap.get(KEY_ARGS_SET) : null;
        Object opParam   = (argsMap  != null) ? argsMap.get(KEY_ARGS_OP) : null;
        Object params    = (argsMap  != null) ? argsMap.get(KEY_ARGS_PARAMS) : null;
        
        // valdate name param
        if(nameParam == null){
            throw new ShellException("Command \"exec\" "
                    + "requires the 'name:' parameter to specify "
                    + "an ObjectName expression (see help).");
        }
        
        // look for obj instance
        ObjectInstance obj = mbeanMap.get((String) nameParam);
        if(obj == null)
            obj = Management.getObjectInstance(server, (String)nameParam);
        if(obj == null){
            throw new ShellException(String.format("Unable to locate MBean with name %s.",nameParam));            
        }
        
        // get Attribute 
        if(getParam != null){
            if(getParam instanceof String){
                Object attrib = getObjectAttribute(server, obj, (String)getParam);
                c.writeOutput(String.format("%n%s.%s %s", obj.getClassName(), getParam, attrib));
            }
            if(getParam instanceof List){
                
            }
        }
        
        return null;
    }

    public void plug(Context plug) {
    }
    
    private Object getObjectAttribute(MBeanServerConnection server, ObjectInstance obj, String attrib) throws ShellException {
        Object result = null;
        try {
            result = server.getAttribute(obj.getObjectName(), attrib);
        } catch (MBeanException ex) {
            throw new ShellException(ex);
        } catch (AttributeNotFoundException ex) {
            throw new ShellException(ex);
        } catch (InstanceNotFoundException ex) {
            throw new ShellException(ex);
        } catch (ReflectionException ex) {
            throw new ShellException(ex);
        } catch (IOException ex) {
            throw new ShellException(ex);
        }
        
        return result;
    }
    
}
