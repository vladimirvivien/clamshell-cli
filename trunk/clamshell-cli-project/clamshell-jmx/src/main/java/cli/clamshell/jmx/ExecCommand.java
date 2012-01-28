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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
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
        return (descriptor != null ) ? descriptor : (
            descriptor = new Command.Descriptor() {

                public String getNamespace() {
                    return NAMESPACE;
                }

                public String getName() {
                    return CMD_NAME;
                }

                public String getDescription() {
                    return "Execute MBean attribute getters/setters and operations.";
                }

                public String getUsage() {
                    return "exec name:<ObjectNamePattern> [get:<AttributeName>] "
                            + "[set:<AttributeName>] [op:<OperationName>] "
                            + "[params:<ParamValue>|[<ListOfParamValue>]]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("name:<ObjectNamePattern>", "An MBean ObjectName or name pattern.");
                    args.put("get:<AttributeName>", "An attribute to retrieve.");  
                    args.put("set:<AttributeName>", "Name of attribute to set (must provide params:).");
                    args.put("op:<OperationName>", "Name of an MBean operation to invoke.");
                    args.put("params:<ParamValue>|[<ListOfParamValue>]", "One or more parameter values used to attribute setter or operation.");
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        IOConsole c = ctx.getIoConsole();
        List<Object> result = null;
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

        if (opParam != null && setParam != null) {
            throw new ShellException("You cannot specify both 'op:' "
                    + "and 'set:' parameters at the same time (see help).");
        }
   
        
        ObjectInstance[] objs = null;
        result = new ArrayList<Object>();
        if (nameParam != null) {

            // find object instances
            ObjectInstance cachedInstance = mbeanMap.get((String) nameParam);
            if (cachedInstance != null) {
                objs = new ObjectInstance[]{cachedInstance};
            }else{
                objs = Management.getObjectInstances(server, (String) nameParam);
            }
            for (ObjectInstance obj : objs) {
                // get attribute
                if(getParam != null){
                    result.add(getObjectAttribute(server, obj, (String) getParam));
                    c.writeOutput(String.format("%n%s.%s = %s", obj.getClassName(), getParam, result));
                }
                
                // set attribute
                if(setParam != null){
                    if(params instanceof List){
                        params = ((List)params).toArray();
                    }
                    this.setObjectAttribute(server, obj, (String)setParam, params);
                    c.writeOutput(String.format("%n%s.%s set to %s OK", obj.getClassName(), setParam, params));
                }
                
                if(opParam != null){
                    Object[] paramVals = null;
                    if(params instanceof List){
                        paramVals = ((List)params).toArray();
                    }else{
                        paramVals = (params != null) ? new Object[]{params} : null;
                    }
                    // find all matching operation from object
                    List<MBeanOperationInfo> ops = null;
                    try {
                        ops = findOpsBySignature(server, obj, (String)opParam, paramVals);
                    } catch (Exception ex) {
                        throw new ShellException(ex);
                    }

                    // invoke all matching op
                    if(ops != null && ops.size() > 0){
                        for(MBeanOperationInfo op : ops){
                            try{
                                Object val = invokeObjectOperation(server, obj, op, paramVals);
                                result.add(val);
                                c.writeOutput(String.format(
                                    "%nInvoked %s.%s(%s) OK [result %s]", 
                                    obj.getClassName(), 
                                    opParam, 
                                    (paramVals != null) ? Arrays.asList(paramVals) : "", 
                                    val));
                            }catch(ShellException ex){
                                c.writeOutput(String.format(
                                    "Operation %s.%s(%s) failed: %s", 
                                    obj.getClassName(), 
                                    opParam, 
                                    Arrays.asList(getOpSignature(op)),
                                    ex.getMessage()));
                            }
                        }
                    }else{
                        throw new ShellException(String.format(
                            "Method %s.%s() not found.", 
                            obj.getClassName(), 
                            opParam));
                    }
                }
            }
         }
        
        c.writeOutput(String.format("%n%n"));
        
        return result;
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
        }catch(Exception ex){
            throw new ShellException(ex);
        }
        
        return result;
    }
    
    private void setObjectAttribute(MBeanServerConnection server, ObjectInstance obj, String attrib, Object value){
        try {
            server.setAttribute(obj.getObjectName(), new Attribute(attrib,value));
        } catch (InstanceNotFoundException ex) {
            throw new ShellException(ex);
        } catch (AttributeNotFoundException ex) {
            throw new ShellException(ex);
        } catch (InvalidAttributeValueException ex) {
            throw new ShellException(ex);
        } catch (MBeanException ex) {
            throw new ShellException(ex);
        } catch (ReflectionException ex) {
            throw new ShellException(ex);
        } catch (IOException ex) {
            throw new ShellException(ex);
        }catch(Exception ex){
            throw new ShellException(ex);
        }
    }
    
    /**
     * Invokes one or more operation that matches the specified op name and
     * parameter signature.
     * @param server
     * @param obj
     * @param opName
     * @param params
     * @return
     * @throws ShellException 
     */
    private Object invokeObjectOperation(MBeanServerConnection server, ObjectInstance obj, MBeanOperationInfo op, Object[] params) throws ShellException{
        String[] signature = getOpSignature(op);
        Object result = null;
        try {
            result = server.invoke(obj.getObjectName(), op.getName(), params, signature);
        } catch (InstanceNotFoundException ex) {
            throw new ShellException(ex);
        } catch (MBeanException ex) {
            throw new ShellException(ex);
        } catch (ReflectionException ex) {
            throw new ShellException(ex);
        } catch (IOException ex) {
            throw new ShellException(ex);
        }catch(Exception ex){
            throw new ShellException(ex);
        }
        
        return result;
    }
    
    /**
     * Return all operations which have the same name and signature size.
     * @param server
     * @param obj
     * @param opName
     * @param params
     * @return
     * @throws Exception 
     */
    private List<MBeanOperationInfo> findOpsBySignature(MBeanServerConnection server, ObjectInstance obj, String opName, Object[] params) throws Exception{
        MBeanOperationInfo[] ops = server.getMBeanInfo(obj.getObjectName()).getOperations();
        if(ops == null)
            return null;
        
        List<MBeanOperationInfo> result = new ArrayList<MBeanOperationInfo>(ops.length);
        for(MBeanOperationInfo op : ops){
            int paramLen = (params != null) ? params.length : 0;            
            if (op.getName().equals(opName) && op.getSignature().length == paramLen) {
                result.add(op);
            }
        }
        
        return result;
    }
    
    private String[] getOpSignature(MBeanOperationInfo op){
        MBeanParameterInfo[] sig = op.getSignature();
        String[] result = new String[sig.length];
        for(int i = 0; i < sig.length; i++){
            result[i] = sig[i].getType();
        }
        return result;
    }
}
