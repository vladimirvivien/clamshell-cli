package cli.clamshell.commons;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vladimir Vivien
 */
public class ShellConfiguratorTest {
    static {
        System.setProperty(ShellConfigurator.KEY_PROP_FILE, "conf/cli.properties");
    }
    ShellConfigurator config;
    
    public ShellConfiguratorTest() {
        config = ShellConfigurator.createNewInstance();
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    public void testInstanceCreation() {
        ShellConfigurator cfg = ShellConfigurator.createNewInstance();
        assert cfg != null : "Factory method not building instance";
    }

    
    @Test
    public void testPropFile() {
        String val1 = (String) config.getProperty("key1");
        assert val1.equals("value1");
        String val2 = (String) config.getProperty("key2");
        assert val2.equals("value2");
    }
}
