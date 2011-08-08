package cli.clamshell.commons.test;

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Plugin;
import cli.clamshell.commons.Clamshell;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class ClamshellTest {
    public ClamshellTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty(Configurator.KEY_DIR_CONF, "../mock-env/conf");
        System.setProperty(Configurator.KEY_DIR_PLUGINS, "../mock-env/plugins");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testRuntimeLoadPlugins() throws Exception{
        ClassLoader cl = Clamshell.Runtime.createClassLoaderForPath(
            new File[]{new File(Configurator.VALUE_DIR_PLUGINS)}, 
            Thread.currentThread().getContextClassLoader()
        );  
        List<Plugin> plugins = Clamshell.Runtime.loadPlugins(cl);
        assert plugins.size() >= 3;
    }
    
    //@Test
    public void testCreateClassLoaderForPath() throws Exception{
        ClassLoader cl = Clamshell.Runtime.createClassLoaderForPath(
            new File[]{new File("./plugins")}, 
            Thread.currentThread().getContextClassLoader());
        
        assert cl != null;
        Object o = cl.loadClass("demo.component.SimplePlugin");
        assert o != null;
    }
}
