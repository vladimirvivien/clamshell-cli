package cli.clamshell.jmx;


import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import cli.clamshell.commons.ShellException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

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

/**
 * This implementation of Command describes an MBean instance from information
 * pulled from the MBeanInfo.
 * @author vvivien
 */
public class DescribeCommand implements Command{
    private static final String CMD_NAME = "desc";
    private static final String NAMESPACE = "jmx";
    private static final String KEY_ARGS_NAME = "name";
    private static final String KEY_ARGS_ATTRIBS   = "attribs";
    private static final String KEY_ARGS_OPS   = "ops";

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
                    return "Prints description for specified mbean(s).";
                }

                public String getUsage() {
                    return "desc name:<ObjectName> [attribs:<attributes> ops:<operations>]";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("name:<ObjectName>", "ObjectName expression for beans.");
                    args.put("attribs:<attributes>", "Specify attribute info to print.");   
                    args.put("ops:<operations>", "Specify operation info to print."); 
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        IOConsole c = ctx.getIoConsole();
        Map<String,Object> argsMap = (Map<String,Object>) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        Map<String,ObjectInstance[]> mbeanMap = (Map<String,ObjectInstance[]>)ctx.getValue(Management.KEY_MBEANS_MAP);
        
        // validate connection
        Management.verifyServerConnection(ctx);
        MBeanServerConnection server = (MBeanServerConnection)ctx.getValue(Management.KEY_JMX_MBEANSERVER);        
        
        String mbeanParam = (argsMap  != null) ? (String)argsMap.get(KEY_ARGS_NAME) : null;
        Object attribsParam = (argsMap  != null) ? argsMap.get(KEY_ARGS_ATTRIBS) : null;
        Object ops = (argsMap != null) ? argsMap.get(KEY_ARGS_OPS) : null;
        
        ObjectInstance[] objs = getObjectInstances(ctx, mbeanParam);
        if(objs == null || objs.length == 0){
            throw new ShellException(String.format("No beans found with name expression %s.",mbeanParam));
        }
        
        try{
            for(ObjectInstance obj : objs){
                MBeanInfo beanInfo = server.getMBeanInfo(obj.getObjectName());
                printInstanceMBeanInfo(ctx, obj);

                // print attribs
                if(attribsParam != null){
                    c.writeOutput(String.format("%n%nAttributes:"));
                    if(attribsParam instanceof String){
                        String val = (String) attribsParam;
                        if(val.equals("*")){
                            printInstanceAttribInfo(ctx, obj);
                        }else{
                            MBeanAttributeInfo attrib = retrieveAttribInfoByName(beanInfo, val);
                            printAttribInfo(ctx, attrib);
                        }
                    }
                    if(attribsParam instanceof List){
                        List<String> attribNames = (List<String>)attribsParam;
                        for(String name : attribNames){
                            if(name != null){
                                MBeanAttributeInfo attrib = retrieveAttribInfoByName(beanInfo,name);
                                printAttribInfo(ctx, attrib);    
                            }
                        }
                    }
                }
            }
        }catch(Exception ex){
            throw new ShellException(ex);
        }
        c.writeOutput(String.format("%n%n"));
        return null;
    }

    public void plug(Context plug) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private ObjectInstance[] getObjectInstances(Context ctx, String mbeanParam) throws ShellException {
        Map<String, ObjectInstance[]> map = (Map<String, ObjectInstance[]>) ctx.getValue(Management.KEY_MBEANS_MAP);

        if (mbeanParam == null) {
            if (map == null || map.get(Management.KEY_DEFAULT_MBEANS) == null) {
                throw new ShellException(String.format("You must specify the beans to use for this command.%n"
                        + "Use the 'name' parameter or set the mbeans with 'mbean' command (see help)."));
            }
            return map.get(Management.KEY_DEFAULT_MBEANS);
        } else {
            ObjectInstance[] objs = (map != null) ? map.get(mbeanParam) :  null;
            if (objs == null) {
                MBeanServerConnection conn = (MBeanServerConnection) ctx.getValue(Management.KEY_JMX_MBEANSERVER);
                objs = Management.getObjectInstances(conn, mbeanParam);
            }
            return objs;
        }
    }
    
    
    private void printInstanceMBeanInfo(Context ctx, ObjectInstance obj) throws Exception{
        IOConsole c = ctx.getIoConsole();
        MBeanServerConnection server = (MBeanServerConnection)ctx.getValue(Management.KEY_JMX_MBEANSERVER); 
        
        ObjectName name = obj.getObjectName();
        c.writeOutput(String.format("%nMBean: %s (%s)", name.getCanonicalName(), obj.getClassName()));
        MBeanInfo info = server.getMBeanInfo(name);
        c.writeOutput(String.format("%n%s", info.getDescription()));
    }
    
    private void printInstanceAttribInfo(Context ctx, ObjectInstance obj) throws Exception {
        IOConsole c = ctx.getIoConsole();
        MBeanServerConnection server = (MBeanServerConnection)ctx.getValue(Management.KEY_JMX_MBEANSERVER); 

        MBeanInfo info = server.getMBeanInfo(obj.getObjectName());
        MBeanAttributeInfo[] attribs = info.getAttributes();
        for(MBeanAttributeInfo attrib : attribs){
            printAttribInfo(ctx, attrib);
        }
    }
    
    private void printAttribInfo(Context ctx, MBeanAttributeInfo attrib){
        IOConsole c = ctx.getIoConsole();
        c.writeOutput(String.format("%n%5s%s (%s)", " ", attrib.getName(), attrib.getType()));
        c.writeOutput("(");
        if (attrib.isReadable()) {
            c.writeOutput("R");
        }
        if (attrib.isWritable()) {
            c.writeOutput("W");
        }
        c.writeOutput(")");
    }
    
    private MBeanAttributeInfo retrieveAttribInfoByName(MBeanInfo mbeanInfo, String attribName){
        MBeanAttributeInfo[] attribs = mbeanInfo.getAttributes();
        for(MBeanAttributeInfo attrib : attribs){
            if(attrib.getName().equalsIgnoreCase(attribName)){
                return attrib;
            }
        }
        return null;
    }
}
