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

import cli.clamshell.api.Context;
import cli.clamshell.commons.ShellException;
import cli.clamshell.test.MockContext;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author vladimir.vivien
 */
public class ListCommandTest {
    Context ctx = MockContext.createInstance();
    ListCommand cmd;
    Map<String,Object> argsMap;
    JmxAgent agent;

    public ListCommandTest() {
        cmd = new ListCommand();
        argsMap = new HashMap<String,Object>();
    }

    
    @Before
    public void setUp() throws Exception{
        agent = TestUtils.startNewJmxAgent(1999);
    }
    
    @After
    public void tearDown() throws Exception{
        agent.stop();
    }
    
    @Test
    public void testCommandWithNoParams() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, null);
        try{
            cmd.execute(ctx);
        }catch(ShellException ex){
            Assert.fail();
        }
    }
    
    @Test
    public void testCommandWithPatternParam() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        argsMap.put("pattern", "java.lang:type=*");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        try{
            cmd.execute(ctx);
        }catch(ShellException ex){
            Assert.fail();
        }
    }
    
    @Test
    public void testCommandWithPatternAndSetIdParams() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        String namePattern = "java.lang:type=*";
        
        MBeanServerConnection conn = (MBeanServerConnection) ctx.getValue(Management.KEY_JMX_MBEANSERVER);
        Set<ObjectInstance> objs = conn.queryMBeans(new ObjectName(namePattern), null);
        
        argsMap.put("pattern", namePattern);
        argsMap.put("setid", "true");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        try{
            cmd.execute(ctx);
            Map<String,ObjectInstance[]> mbeanMap = (Map<String,ObjectInstance[]>) ctx.getValue(Management.KEY_MBEANS_MAP);
            assert mbeanMap.size() >= objs.size();
        }catch(ShellException ex){
            Assert.fail();
        }
    }
    
    @Test
    public void testCommandWithMultiplePatterns() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        
        List<String> arr = new ArrayList<String>(2);
        arr.add("java.lang:type=*");
        arr.add("java.util.logging:*");
        argsMap.put("pattern", arr);
        argsMap.put("setid", "true");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        try{
            cmd.execute(ctx);
            Map<String,ObjectInstance[]> mbeanMap = (Map<String,ObjectInstance[]>) ctx.getValue(Management.KEY_MBEANS_MAP);
            assert mbeanMap.size() >= 1;
        }catch(ShellException ex){
            Assert.fail();
        }
    }
}
