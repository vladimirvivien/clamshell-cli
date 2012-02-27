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
 * @author Vladimir.Vivien
 */
public class DescribeCommandTest {
    Context ctx = MockContext.createInstance();
    DescribeCommand cmd;
    Map<String,String> argsMap;
    JmxAgent agent;
    
    public DescribeCommandTest() {
        cmd = new DescribeCommand();
        argsMap = new HashMap<String,String>();
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
    public void testCommandWithNoBeanNameAndNoDefaultBean() {
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, null);
        try{
            cmd.execute(ctx);
            Assert.fail();
        }catch(ShellException ex){
            Map<String,ObjectInstance[]> mbeanMap = (Map<String,ObjectInstance[]>) ctx.getValue(Management.KEY_MBEANS_MAP);
            assert mbeanMap == null;
        }
    }

    @Test
    public void testCommandWithNoBeanNameAndWithDefaultBeanSet() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        TestUtils.setupDefaultMBeanInstance(ctx);
        Map<String,ObjectInstance[]> mbeanMap = (Map<String,ObjectInstance[]>) ctx.getValue(Management.KEY_MBEANS_MAP);
        
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
            assert mbeanMap.get(Management.KEY_DEFAULT_MBEANS) != null;
        }catch(ShellException ex){
            Assert.fail();
        }
    }
 
    @Test
    public void testCommandWithBeanNameExpression() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        argsMap.put("bean", "java.lang:type=*");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
        }catch(ShellException ex){
            Assert.fail();
        }
    }    

    @Test
    public void testCommandWithBadBeanNameExpression() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        argsMap.put("bean", "badDomain:type=badType");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
            Assert.fail();
        }catch(ShellException ex){
            System.out.println (ex.getMessage());
        }
    }    
    
    @Test
    public void testCommandWithBeanNameAsAlias() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        TestUtils.setupDefaultMBeanInstance(ctx);
        Map<String,ObjectInstance> mbeanMap = (Map<String,ObjectInstance>) ctx.getValue(Management.KEY_MBEANS_MAP);

        ObjectInstance beanInstance = mbeanMap.get(Management.KEY_DEFAULT_MBEANS);
        mbeanMap.put("runtimeBean", beanInstance);
        
        argsMap.put("bean", "runtimeBean");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        try{
            cmd.execute(ctx);
        }catch(ShellException ex){
            Assert.fail();
        }
    }
}
