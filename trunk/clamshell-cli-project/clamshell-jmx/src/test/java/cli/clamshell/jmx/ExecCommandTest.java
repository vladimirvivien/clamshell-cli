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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class ExecCommandTest {
    Context ctx = MockContext.createInstance();
    ExecCommand cmd;
    Map<String,Object> argsMap;
    Map<String,ObjectInstance> mbeanMap;
    JmxAgent agent;
    TestJmxMBeanMBean bean;
    
    public ExecCommandTest() {
        cmd = new ExecCommand();
        argsMap = new HashMap<String,Object>();
        mbeanMap = new HashMap<String,ObjectInstance>();
        ctx.putValue(Management.KEY_MBEANS_MAP, mbeanMap);
        bean = new TestJmxMBean();
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
    public void testCommandWithNoArgs() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, null);
        try{
            cmd.execute(ctx);
            Assert.fail();
        }catch(ShellException ex){}
    }
    
    @Test
    public void testExecWithGetArg() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        bean.setStringValue("TEST_VALUE");
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);
        
        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("get", "StringValue");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        List<Object> result = (List<Object>)cmd.execute(ctx);
        assert result != null;
        assert result.get(0).equals("TEST_VALUE");
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }

    @Test
    public void testExecWithSetArg() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);
        
        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("set", "StringValue");
        argsMap.put("params", "STRING_VALUE");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        cmd.execute(ctx);

        assert bean.getStringValue().equals("STRING_VALUE");
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
  
    @Test
    public void testExecWithSetAndOpArg() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);
        
        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("set", "StringValue");
        argsMap.put("op", "exec");
        argsMap.put("params", "STRING_VALUE");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        try{
            cmd.execute(ctx);
            Assert.fail("Should not allow both get: and set: parameters.");
        }catch(ShellException ex){
            
        }finally{
            TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
        }
    }
    
    @Test
    public void testExecWithNoParam() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        bean.setStringValue("THIS_VAL_SHOULD_CHANGE_WHEN_EXEC_IS_CALLED");
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);

        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("op", "exec");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        cmd.execute(ctx);

        assert bean.getStringValue().equals("EXEC_VAL");
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
 
    @Test
    public void testExecWithParam() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        bean.setStringValue("THIS_VAL_SHOULD_CHANGE_WHEN_EXEC_IS_CALLED");
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);

        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("op", "exec");
        argsMap.put("params", "EXEC_WITH_PARAM");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        cmd.execute(ctx);

        assert bean.getStringValue().equals("EXEC_WITH_PARAM");
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
    
    @Test
    public void testExecWithTwoParams() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        bean.setStringValue("THIS_VAL_SHOULD_CHANGE_WHEN_EXEC_IS_CALLED");
        bean.setNumericValue(new Integer(5));
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);

        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("op", "execWithParams");
        
        //set op params
        List<Object> args = new ArrayList<Object>(2);
        args.add("EXEC_WITH_PARAMS");
        args.add(Integer.MAX_VALUE);
        argsMap.put("params", args);
        
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        cmd.execute(ctx);

        assert bean.getStringValue().equals("EXEC_WITH_PARAMS");
        assert bean.getNumericValue() == Integer.MAX_VALUE;
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
    
    @Test
    public void testExecOverloadedOps() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        bean.setStringValue("THIS_VAL_SHOULD_CHANGE_WHEN_EXEC_IS_CALLED");
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);

        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("op", "execWithParam");
        argsMap.put("params", Integer.valueOf("3"));
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        try{
            cmd.execute(ctx);
        }catch(ShellException ex){
            
        }

        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
    
    @Test
    public void testExecWithReturnValue() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        bean.setStringValue("RETURN_VALUE");
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);

        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("op", "retrieveValue");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        List<Object> result = (List<Object>) cmd.execute(ctx);

        assert result != null;
        assert result.get(0).equals("RETURN_VALUE");
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
    
    
    @Test
    public void testExecWithBadMethod() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);

        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("op", "retrieveValue_BAD_METHOD");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        try{
            cmd.execute(ctx);
            Assert.fail("Method should not be found.");
        }catch(ShellException ex){
            
        }
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
    

    @Test
    public void testExecSetBoolean() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        TestJmxMBeanMBean bean = new TestJmxMBean();
        TestUtils.registerMBean(agent, bean, TestJmxMBeanMBean.NAME);
        
        argsMap.put(ExecCommand.KEY_ARGS_BEAN, TestJmxMBeanMBean.NAME);
        argsMap.put("set", "BooleanValue");
        argsMap.put("params", new Boolean("True"));
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
        
        cmd.execute(ctx);

        assert bean.getBooleanValue() == true;
        
        TestUtils.unregisterMBean(agent, TestJmxMBeanMBean.NAME);
    }
}
