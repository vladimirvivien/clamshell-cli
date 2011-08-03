package cli.clamshell.commons;

import org.cli.clamshell.api.Configurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vladimir Vivien
 */
public class ShellConfiguratorTest {
    
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
    public void testDefaultProps() {
        String libDir =  (String) config.getProperty(config.KEY_DIR_LIB);
        assert libDir.equals(config.VALUE_DIR_LIB);
    }
    
    @Test
    public void testPropFile() {
        String testVal = (String) config.getProperty("test.key");
        assert testVal.equals("test.value");
    }
}
