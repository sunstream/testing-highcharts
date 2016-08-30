package core;

import org.testng.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestDataReader {
    Properties dataProperties;
    String dataFieldName;
    String separator;
    String fileName;
    public TestDataReader(String filename){
        this.fileName = filename;
        dataProperties = new Properties();
        FileInputStream propFile;
        try {
            propFile = new FileInputStream(filename);
            dataProperties.load(propFile);
        } catch (IOException e) {
            throw new Error("Failed to read data properties file '" + filename+"': "+e.getMessage(), e);
        }
        separator = dataProperties.getProperty("separator");
    }

    public void setDataFieldName(String dataFieldName){
        this.dataFieldName = dataFieldName;
    }

    public String[] getDataFieldValuesByIndex(int index) {
        Assert.assertNotNull(dataFieldName, "No test data field name is specified. No data can be fetched.");
        String key = dataFieldName + "." + index;
        try {
            String value = dataProperties.getProperty(key);
            return value.split("\\s*"+separator+"\\s*");
        } catch (NullPointerException e) {
            throw new Error("Failed to obtain test data property by key '"+key+" from file '"+fileName + "'.");
        }

    }
}
