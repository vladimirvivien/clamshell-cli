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
import java.util.HashMap;
import java.util.Map;
import javax.management.ObjectInstance;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class MBeanCommandTest {
    Context ctx = MockContext.createInstance();
    MBeanCommand cmd;
    Map<String,String> argsMap;
    Map<String,ObjectInstance> mbeanMap;
    
    public MBeanCommandTest() {
        cmd = new MBeanCommand();
        argsMap = new HashMap<String,String>();
        plugCommand();
    }

    
    private void plugCommand(){
        mbeanMap = new HashMap<String, ObjectInstance>();
        ctx.putValue(Management.KEY_MBEANS_MAP, mbeanMap);
    }
    
    JmxAgent agent;
    @Before
    public void startAgent() throws Exception{
        agent = TestUtils.startNewJmxAgent(1999);
    }
    
    @After
    public void stopAgent() throws Exception{
        agent.stop();
    }
    
    @Test
    public void testIfMBeanMapExists(){
        assert ctx.getValue(Management.KEY_MBEANS_MAP) != null;
    }
    
    @Test
    public void testCommandWithNoParams(){
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
            Assert.fail();
        }catch(ShellException ex){}
    }
    
    @Test
    public void testCommandWithBadObjectName() {
        argsMap.put("name", "domain:type=object,status=bad");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
            Assert.fail();
        }catch(ShellException ex){
            Map<String,ObjectInstance> mbeans = (Map<String,ObjectInstance>)ctx.getValue(Management.KEY_MBEANS_MAP);
            assert mbeans.get(Management.KEY_DEFAULT_MBEANS) == null;
        }
    }
    
    @Test
    public void testCommandWithDefaultNameOK() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        argsMap.put("name", "java.lang:type=Runtime");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
            Map<String,ObjectInstance> mbeans = (Map<String,ObjectInstance>)ctx.getValue(Management.KEY_MBEANS_MAP);
            ObjectInstance obj = mbeans.get(Management.KEY_DEFAULT_MBEANS);
            assert obj != null;
        }catch(ShellException ex){
            Assert.fail(ex.getMessage());
        }
    }
    
    @Test
    public void testCommandWithAlias() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        argsMap.put("name", "java.lang:type=Runtime");
        argsMap.put("as", "runtimeMBean");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
            Map<String,ObjectInstance> mbeans = (Map<String,ObjectInstance>)ctx.getValue(Management.KEY_MBEANS_MAP);
            ObjectInstance obj = mbeans.get("runtimeMBean");
            assert obj != null;
        }catch(ShellException ex){
            Assert.fail(ex.getMessage());
        }
    }
    
}
