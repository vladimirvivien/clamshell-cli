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

/**
 *
 * @author vvivien
 */
public class TestJmxMBean implements TestJmxMBeanMBean{
    private String strVal;
    private Integer intVal;
    public void setStringValue(String val) {
        strVal = val;
    }

    public String getStringValue() {
        return strVal;
    }

    public Integer getNumericValue() {
        return intVal;
    }

    public void setNumericValue(Integer val) {
        intVal = val;
    }

    public void exec() {
        strVal = "EXEC_VAL";
    }
    public void exec(String val) {
        strVal = val;
    }
    public void execWithParam(String val) {
        strVal = val;
    }

    public void execWithParam(Integer val) {
        intVal = val;
    }

    public void execWithParams(String val1, Integer val2) {
        strVal = val1;
        intVal = val2;
    }
    
    public String retrieveValue() {
        return strVal;
    }

}
