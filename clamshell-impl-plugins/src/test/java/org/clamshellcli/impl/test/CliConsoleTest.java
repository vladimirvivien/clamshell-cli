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
package org.clamshellcli.impl.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import jline.console.history.History;
import junit.framework.Assert;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.impl.CliConsole;
import org.clamshellcli.test.MockContext;
import org.junit.After;
import org.junit.Before; 
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class CliConsoleTest {
    private static final File CLI_USERDIR = new File(Configurator.VALUE_USERHOME,".cli");
    private Context ctx;
    private CliConsole c;
    
    @Before
    public void setup() {
        ctx = MockContext.createInstance();
        c = new CliConsole();
        c.plug(ctx);
    }
    
    @After
    public void teardown() {
        ctx = null;
    }
    
    @Test
    public void testConsolePlugIn() {
        
        Assert.assertNotNull(c.getWriter());
        Assert.assertNotNull(c.getHistoryFile());
        
        // ensure default CLI dir
        Assert.assertTrue("CLI User directory not created", CLI_USERDIR.exists());
        Assert.assertNotNull(c.getHistoryFile());
        Assert.assertEquals (c.getHistoryFile(), new File(CLI_USERDIR,"history.log"));
    }
    
    @Test 
    public void testSetHistoryEnabled() {
        c.setHistoryEnabled(true);
        Assert.assertTrue(c.isHistoryEnabled());
        Assert.assertTrue(c.getReader().isHistoryEnabled());
        c.setHistoryEnabled(false);
        Assert.assertFalse (c.isHistoryEnabled());
        Assert.assertFalse(c.getReader().isHistoryEnabled());
    }
    
    @Test
    public void testSetHistoryFile() {
        File f = new File (CLI_USERDIR, "test.hist");
        c.setHistoryFile(f);
        Assert.assertEquals(c.getHistoryFile(), new File (CLI_USERDIR, "test.hist"));
        f.deleteOnExit();
    }
    
    @Test
    public void testAddToHistory() throws Exception{
        History h = c.getReader().getHistory();
        c.addToHistory("Test");
        Assert.assertEquals("Test", h.get(0));
    }
    
    @Test
    public void testSaveHistory() throws Exception{
        File f = new File (CLI_USERDIR, "test-history.log");
        c.setHistoryFile(f);
        c.addToHistory("Test");
        c.saveHistory();
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = reader.readLine();
        reader.close();
        Assert.assertEquals("Test",line);
        f.deleteOnExit();
    }
    
    @Test
    public void loadHistory() throws Exception {
        File f = new File (CLI_USERDIR, "test-history.log");
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        writer.write("Test");
        writer.newLine();
        writer.write("Test2");
        writer.close();

        c.getReader().getHistory().clear();
        c.setHistoryFile(f);
        
        Assert.assertTrue(c.getReader().getHistory().size() == 2);
        
        f.deleteOnExit();
    }
}
